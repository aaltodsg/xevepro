package fi.aalto.xevepro

import java.io.{File, PrintWriter}

import com.espertech.esper.client.UpdateListener
import com.espertech.esper.client.EventBean
import fi.aalto.xevepro.EsperHandler._

/**
 * XEvePro - An XML Event Processor
 * Copyright (C) 2015 Mikko Rinne
 *
 * A general purpose listener to print to console a CSV representation obtained via "getUnderlying"
 * conversion to string by Esper.
 *
 * Created by mikko.rinne@aalto.fi on 3.3.2015.
 */

class CSVListener() extends UpdateListener {

  private var writeHandle: PrintWriter = null

  override def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]) {
    if (newEvents != null) {
      // print("New events: ")
      newEvents foreach (evt => displayEvent(evt))
    }
    if (oldEvents != null) {
      // print("Old events: ")
      oldEvents foreach (evt => displayEvent(evt))
    }
  }

  private def displayEvent(theEvent: EventBean) {
    // println ("EventType: " + theEvent.getEventType)
    // println (stmtName + " received: " + theEvent.getUnderlying)
    // MJR Note: Due to freeriding with toString, '=' or ' ' in string values will interfere
    val inArray = theEvent.getUnderlying.toString.split('=')
    for (col <- inArray.drop(1).dropRight(1)) {
      writeHandle.write(col.split(' ')(0))
    }
    writeHandle.write(inArray.last.dropRight(1) + "\n")
    // writeHandle.write(theEvent.get("A").asInstanceOf[String] + "," + theEvent.get("B").asInstanceOf[String] + "," + theEvent.get("C").asInstanceOf[String] + "\n")
  }

  def openOutput(writeFileName: String) = {
    writeHandle = {
      try {
        new PrintWriter(new File(writeFileName))
      } catch {
        case ex: Exception => { error(ex)
          null }
      }
    }
  }

  def closeOutput() = writeHandle.close()

}

