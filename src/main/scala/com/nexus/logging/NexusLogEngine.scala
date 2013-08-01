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

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.io.PrintStream
import java.util.Queue
import java.util.logging.FileHandler
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.LogRecord
import java.util.logging.Logger
import scala.util.Properties
import com.google.common.collect.Queues
import scala.actors.threadpool.LinkedBlockingQueue

object NexusLogEngine {

	private var NexusLogger:Logger = _
	
	private class ConsoleLogWrapper extends Handler {
		override def publish(record:LogRecord){
			val interrupted = Thread.interrupted()
			try{
				ConsoleLogThread.recordQueue.put(record)
			}catch{
				case e:InterruptedException => e.printStackTrace(NexusLogEngine.stdErrCache)
			}
			if(interrupted) Thread.currentThread().interrupt()
		}
		override def close{}
		override def flush{}
	}
	
	private object ConsoleLogThread extends Thread {
		val stdOutHandler = new ConsoleStreamHandler(System.out)
		val stdErrHandler = new ConsoleStreamHandler(System.err)
		val recordQueue = new LinkedBlockingQueue[LogRecord]
		this.setName("Logger")
		this.setDaemon(true)
		override def run{
			while(true){
				try{
					val record = ConsoleLogThread.recordQueue.take()
					if(record != null){
						if(record.getLevel().equals(Level.SEVERE)) this.stdErrHandler.publish(record)
						else this.stdOutHandler.publish(record)
					}
				}catch{
					case e:InterruptedException => {
						e.printStackTrace(NexusLogEngine.stdErrCache)
						Thread.interrupted()
					}
				}
			}
		}
	}
	
	private class LoggingOutStream(log:Logger, level:Level) extends ByteArrayOutputStream {
		private final val currentMessage:StringBuilder = new StringBuilder();
		
		@throws[IOException]
		override def flush(){
			var record:String = null;
			this.synchronized{
				super.flush;
				record = this.toString;
				super.reset;
				currentMessage.append(record.replace(NexusLogFormatter.LINE_SEPERATOR, "\n"));
				var lastidx = -1;
				var idx = currentMessage.indexOf("\n", lastidx + 1);
				while(idx >= 0){
					log.log(this.level, currentMessage.substring(lastidx + 1, idx));
					lastidx = idx;
					idx = currentMessage.indexOf("\n", lastidx + 1);
				}
				if(lastidx >= 0) currentMessage.setLength(0);
			}
		}
	}
	
	private var isConfigured = false
	private var stdErrCache:PrintStream = null
	private var stdOutCache:PrintStream = null
	private var FileHandler:FileHandler = null
	private var Formatter:NexusLogFormatter = null
	private var MinLevel:Level = Level.FINER
	
	def configureLogging{
		System.out.println("Configuring the nexus logger")
		LogManager.getLogManager().reset()
		val globalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
		globalLogger.setLevel(Level.OFF)
		NexusLogEngine.NexusLogger = Logger.getLogger("Nexus")
		val stdOutLogger = Logger.getLogger("STDOUT")
		val stdErrLogger = Logger.getLogger("STDERR")
		NexusLogEngine.NexusLogger.setLevel(Level.ALL)
		NexusLogEngine.NexusLogger.setUseParentHandlers(false)
		ConsoleLogThread.start()
		this.Formatter = new NexusLogFormatter
		try{
			val logPath = new File("nexus-log-%g.log")
			this.FileHandler = new FileHandler(new File("nexus-log-%g.log").getPath(), 0, 3){
				override def close{}
			}
		}
		this.resetLoggingHandlers
		this.stdErrCache = System.err
		this.stdOutCache = System.out
		System.setErr(new PrintStream(new LoggingOutStream(stdErrLogger, Level.SEVERE), true))
		System.setOut(new PrintStream(new LoggingOutStream(stdOutLogger, Level.INFO), true))
		this.isConfigured = true
	}
	private def resetLoggingHandlers{
		val level = Level.parse(Properties.propOrElse("nexus.log.level", this.MinLevel.toString()))
		ConsoleLogThread.stdOutHandler.setLevel(level)
		ConsoleLogThread.stdErrHandler.setLevel(level)
		NexusLogFormatter.UseColors = Properties.propOrElse("nexus.log.nocolor", "false").equals("false")
		NexusLogEngine.NexusLogger.addHandler(new ConsoleLogWrapper())
		ConsoleLogThread.stdOutHandler.setFormatter(this.Formatter)
		ConsoleLogThread.stdErrHandler.setFormatter(this.Formatter)
		this.FileHandler.setLevel(Level.ALL)
		this.FileHandler.setFormatter(this.Formatter)
		NexusLogEngine.NexusLogger.addHandler(this.FileHandler)
		/*val NexusHandler = new NexusLogHandler  //TODO: Enable
		NexusHandler.setLevel(Level.ALL)
		NexusHandler.setFormatter(this.Formatter)
		NexusLogEngine.NexusLogger.addHandler(NexusHandler)*/
	}
	def setUseColors(c:Boolean) = NexusLogFormatter.UseColors = c
	def setMinLevel(l:Level) = this.MinLevel = Level.parse(sys.props.get("nexus.log.level").getOrElse(l.toString()))
	def getMinLevel = this.MinLevel
	
	def makeLog(channel:String):Logger = {
		val l = Logger.getLogger(channel)
		l.setParent(NexusLogEngine.NexusLogger)
		return l
	}
	
	def log(channel:String, level:Level, format:String, data:Any*){
		if(!this.isConfigured) this.configureLogging
		this.makeLog(channel).log(level, String.format(format, data))
	}
	
	def log(level:Level, format:String, data:Any*){
		if(!this.isConfigured) this.configureLogging
		NexusLogEngine.NexusLogger.log(level, String.format(format, data))
	}
	
	def log(channel:String, level:Level, ex:Throwable, format:String, data:Any*){
		if(!this.isConfigured) this.configureLogging
		this.makeLog(channel).log(level, String.format(format, data), ex)
	}
	
	def log(level:Level, ex:Throwable, format:String, data:Any*){
		if(!this.isConfigured) this.configureLogging
		NexusLogEngine.NexusLogger.log(level, String.format(format, data), ex)
	}
	
	def severe(format:String, data:Any*) = this.log(Level.SEVERE, format, data)
	def warning(format:String, data:Any*) = this.log(Level.WARNING, format, data)
	def info(format:String, data:Any*) = this.log(Level.INFO, format, data)
	def fine(format:String, data:Any*) = this.log(Level.FINE, format, data)
	def finer(format:String, data:Any*) = this.log(Level.FINER, format, data)
	def finest(format:String, data:Any*) = this.log(Level.FINEST, format, data)
	def getLogger = this.NexusLogger
	def getOutCache = this.stdOutCache
	def getErrCache = this.stdErrCache
}
