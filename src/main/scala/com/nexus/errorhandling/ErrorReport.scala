/*
 * Copyright 2013 TeamNexus
 *
 * TeamNexus Licenses this file to you under the MIT License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License
 */

package com.nexus.errorhandling

import scala.collection.mutable.ArrayBuffer
import java.text.SimpleDateFormat
import com.nexus.time.NexusTime
import java.io.{FileWriter, File, PrintWriter, StringWriter}
import com.nexus.errorhandling.callables._
import com.nexus.logging.NexusLog

/**
 * No description given
 *
 * @author jk-5
 */
object ErrorReport {
  def create(throwable: Throwable, desc: String): ErrorReport = throwable match{
    case t: ReportedException => t.getReport
    case t => new ErrorReport(desc, throwable)
  }
}
class ErrorReport(private final val description: String, private final val cause: Throwable) {
  private final val categories = ArrayBuffer[ErrorReportCategory]()
  private final val systemDetailsCategory = new ErrorReportCategory(this, "System Details")
  private final val stacktrace = Array[StackTraceElement]()
  private var reportFile: File = _

  this.populateDefaultEntries()

  private def populateDefaultEntries(){
    this.systemDetailsCategory.addSection("Version", CallableVersion)
    this.systemDetailsCategory.addSection("Build", CallableBuild)
    this.systemDetailsCategory.addSection("Operating System", CallableOSInfo)
    this.systemDetailsCategory.addSection("Java Version", CallableJavaInfo)
    this.systemDetailsCategory.addSection("Java VM Version", CallableJavaVMInfo)
    this.systemDetailsCategory.addSection("Current Thread", CallableCurrentThread)
    this.systemDetailsCategory.addSection("Memory", CallableMemory)
    this.systemDetailsCategory.addSection("JVM Flags", CallableJVMFlags)
  }

  def getDescription = this.description
  def getCause = this.cause

  def appendSections(builder: StringBuilder){
    if(this.stacktrace != null && this.stacktrace.length > 0){
      builder.append("-- Main --\n")
      builder.append("Stacktrace:\n")
      for(row <- this.stacktrace){
        builder.append("\t").append("at ").append(row.toString).append("\n")
      }
      builder.append("\n")
    }
    for(section <- this.categories){
      section.appendTo(builder)
      builder.append("\n\n")
    }
    this.systemDetailsCategory.appendTo(builder)
  }

  def getStackTrace: String = {
    var stringWriter: StringWriter = null
    var writer: PrintWriter = null
    var desc = this.cause.toString
    try{
      stringWriter = new StringWriter
      writer = new PrintWriter(stringWriter)
      this.cause.printStackTrace(writer)
      desc = stringWriter.toString
    }finally{
      try{
        if(stringWriter != null) stringWriter.close()
        if(writer != null) writer.close()
      }
    }
    desc
  }

  def reportAsString: String = {
    val builder = new StringBuilder
    builder.append("------ Nexus Error Report ------\n")
    builder.append("//This will automaticly be reported by TeamNexus\n\n")
    builder.append("Current time: ")
    builder.append(new SimpleDateFormat().format(NexusTime.getCurrentDate))
    builder.append("\n")
    builder.append("Description: ")
    builder.append(this.description)
    builder.append("\n\n")
    builder.append(this.getStackTrace)
    builder.append("\n\n")
    builder.append("More information about this error is listed below\n")
    builder.append("-------------------------------------------------\n\n")
    this.appendSections(builder)
    builder.toString()
  }

  def saveToFile(file: File): Boolean = {
    if(this.reportFile != null) false
    else try{
      if(file.getParentFile != null) file.getParentFile.mkdirs()
      val writer = new FileWriter(file)
      writer.write(this.reportAsString)
      writer.close()
      this.reportFile = file
      true
    }catch{
      case t: Throwable => {
        NexusLog.severe("Was not able to save error report to " + file, t)
        return false
      }
    }
  }

  def addCategory(c: ErrorReportCategory) = this.categories += c
}
