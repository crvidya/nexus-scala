/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Jeffrey Kog (jk-5), Martijn Reening (martijnreening)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
	final val LINE_SEPERATOR:String = Properties.lineSeparator;
	final var UseColors:Boolean = false;
	
	private def getColor(color:String):String = if(this.UseColors) color else "";
	private def getColor(level:Level):String = {
		var color = ConsoleColors.WHITE;
		if(level == Level.FINEST || level == Level.FINER || level == Level.FINE){
			color = ConsoleColors.GREEN;
		}else if(level == Level.INFO || level == Level.ALL){
			color = ConsoleColors.BLUE;
		}else if(level == Level.WARNING){
			color = ConsoleColors.YELLOW;
		}else if(level == Level.SEVERE){
			color = ConsoleColors.RED;
		}else if(level == Level.CONFIG){
			color = ConsoleColors.CYAN;
		}else if(level == Level.OFF){
			color = ConsoleColors.CYAN;
		}
		return this.getColor(color);
	}
}

class NexusLogFormatter extends Formatter {

	private final val dateFormat:SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	override def format(record:LogRecord):String = {
		val msg = new StringBuilder();
		msg.append(this.dateFormat.format(record.getMillis()));
		val lvl = record.getLevel();
		
		msg.append(" [");
		msg.append(NexusLogFormatter.getColor(lvl));
		msg.append(lvl.toString());
		msg.append(NexusLogFormatter.getColor(ConsoleColors.WHITE));
		msg.append("] ");
		
		msg.append("[");
		msg.append(NexusLogFormatter.getColor(ConsoleColors.YELLOW));
		if(record.getLoggerName() != null){
			msg.append(record.getLoggerName());
		}
		msg.append(NexusLogFormatter.getColor(ConsoleColors.WHITE));
		msg.append("] ");
		
		msg.append(record.getMessage());
		msg.append(NexusLogFormatter.LINE_SEPERATOR);
		val thr = record.getThrown();
		
		if(thr != null){
			val thrDump = new StringWriter();
			thr.printStackTrace(new PrintWriter(thrDump));
			msg.append(thrDump.toString());
		}
		return msg.toString();
	}
}
