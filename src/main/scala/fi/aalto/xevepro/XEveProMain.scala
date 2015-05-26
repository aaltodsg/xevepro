package fi.aalto.xevepro

import java.io.{File, PrintWriter}

import fi.aalto.xevepro.helper.MyLog

import scala.collection.mutable.Buffer

/**
 * XEvePro - An XML Event Processor
 * Copyright (C) 2015 Mikko Rinne
 *
 * Entry (main) object for the XML Event Processor
 *
 * Parses command line arguments, runs through the input parameters.
 *
 * Created by mikko.rinne@aalto.fi on 3.3.2015 as "supplychain-v2"
 * 07.05.2015 MJR Upgraded and generalized as a part of transition to xevepro
 */

object XEveProMain extends MyLog {

  val argInfo =
    s"""
       |Arguments: -h -v --csv-output=csvfile -r rulefile --time=timefile -i eventfile
       |
       |-h: Display this help text and terminate processing.
       |-v: Display build info.
       |-r rulefile: Name of an EPL module file.
       |-i eventfile: Name of a file with XML-format events.
       |--csv-output=csvfile: Name of the file, where CSV output will be written.
       |   Defaults to '/dev/stdout'.
       |--time=timefile: Starts a timer from the parameter onwards. '--time=-' outputs to /dev/stdout
       |
       |Note1: Command line arguments are processed sequentially and the order counts!
       |
       |Note2: --csv-output and --time can also be separated by space. If your path contains spaces,
       |       use '--csv-output "my file with spaces.csv"'
    """.stripMargin

  // private var sampleMemory = false

  def main(args: Array[String]): Unit = {

    // Initiate Esper
    EsperHandler
    EsperHandler.openCSVOutput("/dev/stdout")

    var tStart: Long = 0L
    var timerFileHandle: PrintWriter = null

    // Force garbage collection
    System.gc()

    // Sleep a bit to have everything initialized and ready
    // TODO: Open this when everything else is ready.
    // Thread.sleep(5000)

    if (args.length > 0) {
      val argBuffer = Buffer[String]()

      // Expand args with "--"
      for (arg <- args) {
        if (arg.startsWith("--")) {
          if (arg.contains('=')) {
            argBuffer ++= arg.split("=")
          } else {
            argBuffer += arg // accept both space and = because SBT messes up filenames with spaces otherwise
            // error("Don't know how to parse: "+arg)
            // println(argInfo)
          }
        } else {
          argBuffer += arg
        }
      }

      var argList = argBuffer.toList

      do {
        argList match {
          case "-r" :: string :: tail => {
            printTimer("Command: -r, Parameter: "+string)
            EsperHandler.deployModule(string)
            argList = tail
          }
          case "-i" :: string :: tail => {
            printTimer("Command: -i, Parameter: "+string)
            this.runEvents(string)
            argList = tail
          }
          case "--csv-output" :: string :: tail => {
            EsperHandler.closeCSVOutput()
            // println("Opening file: "+string+" for csv-output")
            EsperHandler.openCSVOutput(string)
            argList = tail
          }
          case "--time" :: string :: tail => {
            if (tStart > 0L) {
              printTimer("Re-starting timer to "+string+". Old value.")
              timerFileHandle.close()
            }
            var writeFileName = ""
            if (string == "-") writeFileName="/dev/stdout" else writeFileName=string
            timerFileHandle = {
              try {
                new PrintWriter(new File(writeFileName))
              } catch {
                case ex: Exception => { error(ex)
                  null }
              }
            }
            tStart = System.nanoTime()
            argList = tail
          }
          case "-v" :: tail => {
            // MJR Note: Doesn't currently update with IDEA, need to use command line SBT & compile instead
            println(fi.aalto.xevepro.helper.buildinfo.BuildInfo)
            argList = tail
          }
          case "-h" :: tail => {
            println(argInfo)
            argList = Nil
          }
          case somethingElse => {
            error("Don't know how to parse: "+somethingElse)
            println(argInfo)
            argList = Nil
          }
        }
      } while (argList != Nil)
    } else {
      println(argInfo)
    }

    EsperHandler.advanceFinalEsperTime() // Push time forward by esper variable at the end of processing
    EsperHandler.closeCSVOutput()

    printTimer("Done")
    if (tStart > 0L) timerFileHandle.close()

    def printTimer(descr: String) = {
      if (tStart > 0L) timerFileHandle.write("At " + (System.nanoTime() - tStart)*.000000001 + ": " + descr + "\n")
    }
  }

  private def runEvents(eventFileName: String) = {
    debug("Starting eventfile: "+eventFileName)

    // Open event file for reading
    val myReader = new EventReader(eventFileName)

    // Dispatch events
    var inEvent: String = null
    do {
      inEvent = myReader.readNext()
      if (inEvent != null) EsperHandler.insertEvent(inEvent)
    } while (inEvent != null)
    myReader.closeER()

    // if (sampleMemory) println(mS)
    debug("Finished eventFile: "+eventFileName)
  }

}
