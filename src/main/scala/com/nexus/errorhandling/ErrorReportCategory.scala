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

import java.util.concurrent.Callable
import scala.collection.mutable.ArrayBuffer

/**
 * No description given
 *
 * @author jk-5
 */
class ErrorReportCategory(private final val report: ErrorReport, private final val description: String) {
  private final val entries = ArrayBuffer[ErrorReportCategoryEntry]()
  private final val stacktrace = Array[StackTraceElement]()

  def addSection(name: String, c: Callable[_ <: Any]): Unit = try{
    this.addSection(name, c.call())
  }catch{
    case t: Throwable => this.addSection(name, t)
  }
  def addSection(name: String, element: Any) = this.entries += new ErrorReportCategoryEntry(name, element)

  def appendTo(builder: StringBuilder){
    builder.append("-- ").append(this.description).append(" --\n")
    builder.append("Details:")
    for(entry <- this.entries){
      builder.append("\n\t")
      builder.append(entry.getName).append(": ").append(entry.getElement)
    }
    if(this.stacktrace != null && this.stacktrace.length > 0){
      builder.append("\nStacktrace:")
      for(row <- this.stacktrace){
        builder.append("\n\tat ").append(row.toString)
      }
    }
  }
}
