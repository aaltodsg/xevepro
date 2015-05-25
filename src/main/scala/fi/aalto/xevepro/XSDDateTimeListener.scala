package fi.aalto.xevepro

import com.espertech.esper.client.UpdateListener
import com.espertech.esper.client.EventBean
import com.espertech.esper.client.time.CurrentTimeEvent
import fi.aalto.xevepro.helper.MyLog
import org.apache.commons.logging.{LogFactory, Log}
import org.joda.time.format.{DateTimeFormatter, ISODateTimeFormat}

/**
 * XEvePro - An XML Event Processor
 * Copyright (C) 2015 Mikko Rinne
 *
 * Extracts time information from incoming events and sets
 * Esper time accordingly
 *
 * Also maintains the latestTime sent to Esper
 *
 * Created by mikko.rinne@aalto.fi on 6.3.2015.
 *
 * MJR 17.04.2015 Modified to use "eventOccurredAt" instead of "eventRecordedAt"
 * MJR 07.05.2015 Renamed from TimeListener to XSDDateTimeListener, modified to extract generic "XSDDateTime"
 *                Delegated timekeeping to EsperHandler in preparation of new timelisteners
 */
class XSDDateTimeListener extends UpdateListener with MyLog {

  private val fmt = ISODateTimeFormat.dateTime()

  override def update(newEvents: Array[EventBean], oldEvents: Array[EventBean]) {
    if (newEvents != null) {
      newEvents foreach (evt => {
        extractTime(evt.get("XSDDateTime").asInstanceOf[String])
      })
    }
  }

  private def extractTime(inDateTime: String) = {
    EsperHandler.newEsperTime(fmt.parseDateTime(inDateTime).toInstant.getMillis)
  }

}

