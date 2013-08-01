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

package com.nexus

import com.google.common.collect.Lists
import java.util.{List => JList}
import com.nexus.traits.TLoader
import scala.collection.JavaConversions._
import com.nexus.network.WebServer
import com.nexus.network.WebServerHandlerLoader
import com.nexus.network.SslContextProvider
import com.nexus.network.NetworkHandler
import com.nexus.logging.NexusLog

object Nexus {

	private final val loaders:JList[TLoader] = Lists.newArrayList()
	
	this.loaders.add(WebServerHandlerLoader)
	this.loaders.add(SslContextProvider)
	this.loaders.add(WebServer)
	
	def start{
		NexusLog.info("Starting nexus-scala, version %s (build %d)".format(Version.version, Version.build))
		this.loaders.foreach(l=>l.load)
	}
}
