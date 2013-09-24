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

import java.io.File
import java.text.SimpleDateFormat
import com.nexus.logging.NexusLog
import com.nexus.time.NexusTime

/**
 * No description given
 *
 * @author jk-5
 */
object ErrorHandler {

  private final val output = sys.props.get("nexus.errorHandler.outputErrors").getOrElse("false").equalsIgnoreCase("true")

  def unexpectedException(throwable: Throwable) = if(this.output) throwable.printStackTrace() else {
    val report = throwable match {
      case rep: ReportedException => rep.getReport
      case t => new ErrorReport("Unknown error cause", t)
    }
    val file = new File("error-reports", "error-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(NexusTime.getCurrentDate) + ".txt")
    if (report.saveToFile(file)) NexusLog.severe("An error occured. Log has been saved to: " + file.getAbsolutePath)
    else NexusLog.severe("An error occured, but we were not able to save the log to disk")
  }
}
