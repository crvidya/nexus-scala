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
	def log(channel:String, level:Level, format:String, data:Any*) = NexusLogEngine.log(channel, level, format, data);
	def log(level:Level, format:String, data:Any*) = NexusLogEngine.log(level, format, data);
	def log(channel:String, level:Level, ex:Throwable, format:String, data:Any*) = NexusLogEngine.log(channel, level, ex, format, data);
	def log(level:Level, ex:Throwable, format:String, data:Any*) = NexusLogEngine.log(level, ex, format, data);
	def severe(format:String, data:Any*) = NexusLogEngine.log(Level.SEVERE, format, data);
	def warning(format:String, data:Any*) = NexusLogEngine.log(Level.WARNING, format, data);
	def info(format:String, data:Any*) = NexusLogEngine.log(Level.INFO, format, data);
	def fine(format:String, data:Any*) = NexusLogEngine.log(Level.FINE, format, data);
	def finer(format:String, data:Any*) = NexusLogEngine.log(Level.FINER, format, data);
	def finest(format:String, data:Any*) = NexusLogEngine.log(Level.FINEST, format, data);
	def off(format:String, data:Any*) = NexusLogEngine.log(Level.OFF, format, data);
	def all(format:String, data:Any*) = NexusLogEngine.log(Level.ALL, format, data);
	def getLogger:Logger = NexusLogEngine.getLogger;
	def MakeLogger(channel:String):Logger = NexusLogEngine.makeLog(channel);
	def MakeLogger(c:Any):Logger = NexusLogEngine.makeLog(c.getClass.getSimpleName);
}