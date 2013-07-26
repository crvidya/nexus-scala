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