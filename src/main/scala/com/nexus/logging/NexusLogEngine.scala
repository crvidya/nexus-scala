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

import java.io.ByteArrayOutputStream
import java.io.File
import java.io.PrintStream
import java.util.logging.FileHandler
import java.util.logging.Handler
import java.util.logging.Level
import java.util.logging.LogManager
import java.util.logging.LogRecord
import java.util.logging.Logger
import scala.util.Properties
import java.util.concurrent.LinkedBlockingQueue

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
		override def close(){}
		override def flush(){}
	}
	
	private object ConsoleLogThread extends Thread {
		val stdOutHandler = new ConsoleStreamHandler(System.out)
		val stdErrHandler = new ConsoleStreamHandler(System.err)
		val recordQueue = new LinkedBlockingQueue[LogRecord]
		this.setName("Logger")
		this.setDaemon(true)
		override def run(){
			while(true){
				try{
					val record = ConsoleLogThread.recordQueue.take()
					if(record != null){
						if(record.getLevel.equals(Level.SEVERE)) this.stdErrHandler.publish(record)
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
		private final val currentMessage:StringBuilder = new StringBuilder()
		
		override def flush(){
			var record:String = null
			this.synchronized{
				super.flush()
				record = this.toString
				super.reset()
				currentMessage.append(record.replace(NexusLogFormatter.LINE_SEPERATOR, "\n"))
				var lastidx = -1
				var idx = currentMessage.indexOf("\n", lastidx + 1)
				while(idx >= 0){
					log.log(this.level, currentMessage.substring(lastidx + 1, idx))
					lastidx = idx
					idx = currentMessage.indexOf("\n", lastidx + 1)
				}
				if(lastidx >= 0) currentMessage.setLength(0)
			}
		}
	}
	
	private var isConfigured = false
	private var stdErrCache:PrintStream = null
	private var stdOutCache:PrintStream = null
	private var FileHandler:FileHandler = null
	private var Formatter:NexusLogFormatter = null
	private var MinLevel:Level = Level.FINER
	
	def configureLogging(){
		System.out.println("Configuring the nexus logger")
		LogManager.getLogManager.reset()
		val globalLogger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME)
		globalLogger.setLevel(Level.OFF)
		NexusLogEngine.NexusLogger = Logger.getLogger("Nexus")
		val stdOutLogger = Logger.getLogger("STDOUT")
		val stdErrLogger = Logger.getLogger("STDERR")
		stdOutLogger.setParent(NexusLogEngine.NexusLogger)
		stdErrLogger.setParent(NexusLogEngine.NexusLogger)
		NexusLogEngine.NexusLogger.setLevel(Level.ALL)
		NexusLogEngine.NexusLogger.setUseParentHandlers(false)
		ConsoleLogThread.start()
		this.Formatter = new NexusLogFormatter
		try{
			this.FileHandler = new FileHandler(new File("nexus-log-%g.log").getPath, 0, 3){
				override def close(){}
			}
		}
		this.resetLoggingHandlers()
		this.stdErrCache = System.err
		this.stdOutCache = System.out
		System.setErr(new PrintStream(new LoggingOutStream(stdErrLogger, Level.SEVERE), true))
		System.setOut(new PrintStream(new LoggingOutStream(stdOutLogger, Level.INFO), true))
		this.isConfigured = true
	}
	private def resetLoggingHandlers(){
		val level = Level.parse(Properties.propOrElse("nexus.log.level", this.MinLevel.toString))
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
	def setMinLevel(l:Level) = this.MinLevel = Level.parse(sys.props.get("nexus.log.level").getOrElse(l.toString))
	def getMinLevel = this.MinLevel
	
	def makeLog(channel:String):Logger = {
		val l = Logger.getLogger(channel)
		l.setParent(NexusLogEngine.NexusLogger)
		l
	}
	
	def log(channel:String, level:Level, data: String){
		if(!this.isConfigured) this.configureLogging()
		this.makeLog(channel).log(level, data)
	}
	
	def log(level:Level, data:String){
		if(!this.isConfigured) this.configureLogging()
		NexusLogEngine.NexusLogger.log(level, data)
	}
	
	def log(channel:String, level:Level, ex:Throwable, data:String){
		if(!this.isConfigured) this.configureLogging()
		this.makeLog(channel).log(level, data, ex)
	}
	
	def log(level:Level, ex:Throwable, data:String){
		if(!this.isConfigured) this.configureLogging()
		NexusLogEngine.NexusLogger.log(level, data, ex)
	}
	
	def severe(format:String) = this.log(Level.SEVERE, format)
	def warning(format:String) = this.log(Level.WARNING, format)
	def info(format:String) = this.log(Level.INFO, format)
	def fine(format:String) = this.log(Level.FINE, format)
	def finer(format:String) = this.log(Level.FINER, format)
	def finest(format:String) = this.log(Level.FINEST, format)
	def getLogger = this.NexusLogger
	def getOutCache = this.stdOutCache
	def getErrCache = this.stdErrCache
}
