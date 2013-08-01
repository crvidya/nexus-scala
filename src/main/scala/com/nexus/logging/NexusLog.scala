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