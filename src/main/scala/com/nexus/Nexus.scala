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

package com.nexus

import com.google.common.collect.Lists
import java.util.{List => JList}
import com.nexus.traits.TLoader
import scala.collection.JavaConversions._
import com.nexus.logging.NexusLog
import com.nexus.webserver.{WebServerHandlerRegistry, SslContextProvider}
import com.nexus.webserver.netty.WebServer

object Nexus {

	private final val loaders:JList[TLoader] = Lists.newArrayList()
	
	this.loaders.add(WebServerHandlerRegistry)
	this.loaders.add(SslContextProvider)
	this.loaders.add(WebServer)
	
	def start{
		NexusLog.info("Starting nexus-scala, version %s (build %d)".format(Version.version, Version.build))
		this.loaders.foreach(l=>l.load)
	}
}
