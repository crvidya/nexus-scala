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

package com.nexus.logging

import java.io.PrintWriter
import java.io.StringWriter
import java.text.SimpleDateFormat
import java.util.logging.Formatter
import java.util.logging.Level
import java.util.logging.LogRecord

import scala.util.Properties

import com.nexus.util.ConsoleColors

object NexusLogFormatter {
	final val LINE_SEPERATOR:String = Properties.lineSeparator
	final var UseColors:Boolean = false
	
	private def getColor(color:String):String = if(this.UseColors) color else ""
	private def getColor(level:Level):String = this.getColor(level match{
      case Level.FINEST | Level.FINER | Level.FINE => ConsoleColors.GREEN
      case Level.INFO | Level.ALL => ConsoleColors.BLUE
      case Level.WARNING => ConsoleColors.YELLOW
      case Level.SEVERE => ConsoleColors.RED
      case Level.CONFIG => ConsoleColors.CYAN
      case Level.OFF => ConsoleColors.CYAN
      case _ => ConsoleColors.WHITE
    })
}

class NexusLogFormatter extends Formatter {

	private final val dateFormat:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
	
	override def format(record:LogRecord):String = {
		val msg = new StringBuilder()
		msg.append(this.dateFormat.format(record.getMillis))
		val lvl = record.getLevel
		
		msg.append(" [")
		msg.append(NexusLogFormatter.getColor(lvl))
		msg.append(lvl.toString)
		msg.append(NexusLogFormatter.getColor(ConsoleColors.WHITE))
		msg.append("] ")
		
		msg.append("[")
		msg.append(NexusLogFormatter.getColor(ConsoleColors.YELLOW))
		if(record.getLoggerName != null){
			msg.append(record.getLoggerName)
		}
		msg.append(NexusLogFormatter.getColor(ConsoleColors.WHITE))
		msg.append("] ")
		
		msg.append(record.getMessage)
		msg.append(NexusLogFormatter.LINE_SEPERATOR)
		val thr = record.getThrown
		
		if(thr != null){
			val thrDump = new StringWriter()
			thr.printStackTrace(new PrintWriter(thrDump))
			msg.append(thrDump.toString)
		}
		msg.toString()
	}
}
