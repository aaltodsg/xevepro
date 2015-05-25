package fi.aalto.xevepro.helper

import scala.math.{min, max}

/**
 * XEvePro - An XML Event Processor
 * Copyright (C) 2015 Mikko Rinne
 *
 * Connects to Java Runtime and samples minimum and maximum values of:
 * TotalMemory
 * MaxMemory
 * FreeMemory
 *
 * Currently not used.
 *
 * Created by mikko.rinne@aalto.fi on 20.3.2015.
 */
class MemorySampler {

  private val runtime = Runtime.getRuntime

  var minTotalMemory = runtime.totalMemory()
  var maxTotalMemory = minTotalMemory
  var minFreeMemory = runtime.freeMemory()
  var maxFreeMemory = minFreeMemory
  var minMaxMemory = runtime.maxMemory()
  var maxMaxMemory = minMaxMemory

  def updateMemMinMax(): Unit = {
    val newTotalMemory = runtime.totalMemory()
    minTotalMemory = min(minTotalMemory,newTotalMemory)
    maxTotalMemory = max(maxTotalMemory,newTotalMemory)
    val newFreeMemory = runtime.freeMemory()
    minFreeMemory = min(minFreeMemory,newFreeMemory)
    maxFreeMemory = max(maxFreeMemory,newFreeMemory)
    val newMaxMemory = runtime.maxMemory()
    minMaxMemory = min(minMaxMemory,newMaxMemory)
    maxMaxMemory = max(maxMaxMemory,newMaxMemory)
  }

  override def toString = {
    "TotalMemory diff: " + (maxTotalMemory-minTotalMemory).toString + " B\n" +
      "FreeMemory diff: " + (maxFreeMemory-minFreeMemory).toString + " B\n" +
      "MaxMemory diff: " + (maxMaxMemory-minMaxMemory).toString + " B\n"
  }
}
