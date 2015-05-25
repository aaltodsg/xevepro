package fi.aalto.xevepro

import java.io.{File, StringReader}
import javax.xml.parsers.DocumentBuilderFactory

import com.espertech.esper.client._
import com.espertech.esper.client.annotation.Name
import com.espertech.esper.client.time.CurrentTimeEvent
import fi.aalto.xevepro.helper.MyLog
import org.xml.sax.InputSource

import scala.collection.JavaConversions._
import scala.util.{Failure, Success, Try}

/**
 * XEvePro - An XML Event Processor
 * Copyright (C) 2015 Mikko Rinne
 *
 * Encapsulate the interface to Esper.
 *
 * Created by mikko.rinne@aalto.fi on 3.3.2015.
 * 07.05.2015 MJR Delegated file handling to CSVListener, took over timekeeping in EsperHandler, added name-based listener addition to deployModule,
 *            General cleanup of unused methods
 */

object EsperHandler extends MyLog {

  lazy val epService = init("esper_config.xml")
  lazy val epRuntime = epService.getEPRuntime
  lazy val deployAdmin = epService.getEPAdministrator.getDeploymentAdmin

  // set up our DOM parser
  val builderFactory = DocumentBuilderFactory.newInstance
  builderFactory.setNamespaceAware(true)
  val documentBuilder = builderFactory.newDocumentBuilder


  private val xSDDTListener = new XSDDateTimeListener
  private var latestTime: Long = 0
  def currentEsperTime = latestTime

  private val cSVListener = new CSVListener


  // val mS = new MemorySampler

  def openCSVOutput(writeFileName: String) = {
    cSVListener.openOutput(writeFileName)
  }
  
  def closeCSVOutput() = cSVListener.closeOutput()

  private def init(configFile: String) = {
    // Load configuration
    val configURL = this.getClass.getClassLoader.getResource(configFile)
    if (configURL == null) {
      error("Error loading configuration file '" + configFile + "' from classpath")
    }
    val config = new Configuration
    config.configure(configURL)

    EPServiceProviderManager.getDefaultProvider(config)
  }

  def deployModule(fileName: String) {
    try {
      val myModule = deployAdmin.read(new File(fileName))
      val deploymentResult = deployAdmin.deploy(deployAdmin.add(myModule), null)
      deploymentResult.getStatements.foreach {s =>
        s.getAnnotations.filter(a => a.annotationType == classOf[Name]).foreach {a =>
          val nameStr = a.asInstanceOf[Name].value
          if (nameStr.startsWith("CSV:")) s.addListener(cSVListener)
          if (nameStr.startsWith("XSDDateTime:")) s.addListener(xSDDTListener)
        }
      }
    } catch {
      case ex: Exception => error(ex)
    }
  }

  /* Currently not used, kept as a spare
def registerQuery(epl:String, dbg_listener:Boolean): Try[EPStatement] = {
  try {
    val newQuery = epService.getEPAdministrator.createEPL(epl)
    if (dbg_listener) newQuery.addListener(cSVListener)
    Success(newQuery)
  } catch {
    case x: EPException => Failure(x)
  }
} */

  /* Currently unused
  def registerListener(stmtName: String): Unit = {
    try {
      epService.getEPAdministrator.getStatement(stmtName).addListener(cSVListener)
    } catch {
      case x: EPException => error(x)
    }
  } */

  def insertEvent(eventstr: String) {
    try {
      val eventDoc = documentBuilder.parse(new InputSource(new StringReader(eventstr)))
      epRuntime.sendEvent(eventDoc)
      // if (sampleMemory) mS.updateMemMinMax
    }
    catch {
      case ex: Exception => error(ex)
    }
  }

  def advanceFinalEsperTime() = {
    try {
      val advanceSecs = epRuntime.getVariableValue("finalTimeAdvance").asInstanceOf[Long]
      latestTime += advanceSecs*1000L // to ms
      epRuntime.sendEvent(new CurrentTimeEvent(latestTime))
      debug("Esper time advanced to: "+latestTime)
    } catch {
      case ex: Exception => info("No final time advance available")
        info("To define in EPL: create constant variable long finalTimeAdvance = 32400L;")
    }
  }

  def newEsperTime(newTime: Long) = {
    if (newTime > latestTime) {
      latestTime = newTime
      epRuntime.sendEvent(new CurrentTimeEvent(latestTime))
      debug("Esper time advanced to: "+latestTime)
    }
  }

}
