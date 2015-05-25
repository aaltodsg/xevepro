package fi.aalto.xevepro

import java.io._

import fi.aalto.xevepro.helper.MyLog

/**
 * XEvePro - An XML Event Processor
 * Copyright (C) 2015 Mikko Rinne
 *
 * Reads successive XML-format events from a file
 *
 * Currently assuming no XML-header in events, i.e. the tag found in the first line starting with '<'
 * is used to construct the end-tag.
 *
 * Created by mikko.rinne@aalto.fi on 4.3.2015.
 */
class EventReader(fileName: String) extends MyLog {

  private var fileEnded = false

  val fileHandle = {
    try {
      new BufferedReader(new FileReader(fileName))
    } catch {
      case ex: Exception => { error(ex)
        null }
    }
  }

  /*
      Loads the next XML-format event from the currently open file

      Return (result) values:
      null: File finished.
      String: An XML-format event
   */
  def readNext(): String = {
    var result: String = null
    if (!fileEnded) {
      // Find the beginning of an event
      var doLoop = true
      do {
        result = fileHandle.readLine()
        if (result == null) {
          doLoop = false
          fileEnded = true
        } else {
          if (result.startsWith("<")) doLoop = false
        }
      } while (doLoop)

      if (!fileEnded) {
        // Construct the end marker
        val endLine = (result.take(1) + "/" + result.split(" ")(0).drop(1) + ">").trim
        var line = ""
        doLoop = true
        // Read lines until end-event marker is found or file ends
        do {
          line = fileHandle.readLine()
          if (line == null) {
            fileEnded = true
            doLoop = false
          } else {
            result += "\n"+line
            if (line.trim == endLine) doLoop = false
          }
        } while (doLoop)
      }
    }
    result
  }

  def closeER(): Unit = {
    fileHandle.close()
  }

}
