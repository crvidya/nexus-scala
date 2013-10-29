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

import java.util.logging.Level
import java.util.logging.Logger

object NexusLog {
	def log(channel:String, level:Level, data:String, format: Any *) = NexusLogEngine.log(channel, level, data.format(format: _*))
	def log(channel:String, level:Level, data:String) = NexusLogEngine.log(channel, level, data)
	def log(level:Level, data:String, format: Any *) = NexusLogEngine.log(level, data.format(format: _*))
	def log(level:Level, data:String) = NexusLogEngine.log(level, data)
	def log(channel:String, level:Level, ex:Throwable, data:String, format: Any *) = NexusLogEngine.log(channel, level, ex, data.format(format: _*))
	def log(channel:String, level:Level, ex:Throwable, data:String) = NexusLogEngine.log(channel, level, ex, data)
	def log(level:Level, ex:Throwable, data:String) = NexusLogEngine.log(level, ex, data)
	def log(level:Level, ex:Throwable, data:String, format: Any *) = NexusLogEngine.log(level, ex, data.format(format: _*))
	def severe(data:String, t: Throwable) = NexusLogEngine.log(Level.SEVERE, t, data)
	def severe(data:String, t: Throwable, format: Any *) = NexusLogEngine.log(Level.SEVERE, t, data.format(format: _*))
	def severe(data:String) = NexusLogEngine.log(Level.SEVERE, data)
	def severe(data:String, format: Any *) = NexusLogEngine.log(Level.SEVERE, data.format(format: _*))
	def warning(data:String) = NexusLogEngine.log(Level.WARNING, data)
	def warning(data:String, format: Any *) = NexusLogEngine.log(Level.WARNING, data.format(format: _*))
	def info(data:String) = NexusLogEngine.log(Level.INFO, data)
	def info(data:String, format: Any *) = NexusLogEngine.log(Level.INFO, data.format(format: _*))
	def fine(data:String) = NexusLogEngine.log(Level.FINE, data)
	def fine(data:String, format: Any *) = NexusLogEngine.log(Level.FINE, data.format(format: _*))
	def finer(data:String) = NexusLogEngine.log(Level.FINER, data)
	def finer(data:String, format: Any *) = NexusLogEngine.log(Level.FINER, data.format(format: _*))
	def finest(data:String) = NexusLogEngine.log(Level.FINEST, data)
	def finest(data:String, format: Any *) = NexusLogEngine.log(Level.FINEST, data.format(format: _*))
	def off(data:String) = NexusLogEngine.log(Level.OFF, data)
	def off(data:String, format: Any *) = NexusLogEngine.log(Level.OFF, data.format(format: _*))
	def all(data:String) = NexusLogEngine.log(Level.ALL, data)
	def all(data:String, format: Any *) = NexusLogEngine.log(Level.ALL, data.format(format: _*))
	def getLogger:Logger = NexusLogEngine.getLogger
	def makeLogger(channel:String):Logger = NexusLogEngine.makeLog(channel)
	def makeLogger(c:Any):Logger = NexusLogEngine.makeLog(c.getClass.getSimpleName)
}