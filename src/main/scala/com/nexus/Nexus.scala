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

import com.nexus.traits.TLoader
import com.nexus.logging.NexusLog
import com.nexus.webserver.{WebServerHandlerRegistry, SslContextProvider}
import com.nexus.webserver.netty.WebServer
import java.io.File
import com.nexus.data.config.ConfigFile
import com.nexus.concurrent.WorkerPool
import com.nexus.data.couchdb.CouchDB
import com.nexus.time.synchronisation.TimeSynchronisationHandler
import com.nexus.errorhandling.{ErrorReport, ReportedException, ErrorHandler}

object Nexus {

  private final val CONFIG_DIR = new File("config")
  private var config: ConfigFile = null
  private final val loaders = Array[TLoader](
    WorkerPool,
    CouchDB,
    TimeSynchronisationHandler,
    LoadClass(WebServerHandlerRegistry),
    SslContextProvider,
    WebServer
  )

	def start(){
		NexusLog.info("Starting nexus-scala, version %s (build %d)".format(Version.version, Version.build))

    if(!this.CONFIG_DIR.exists()) this.CONFIG_DIR.mkdirs()
    this.config = new ConfigFile(new File(this.CONFIG_DIR, "Nexus.cfg")).setComment("Nexus main configuration file")

		this.loaders.foreach(l=>l.load())
	}

  def getConfig = this.config
}

object LoadClass {
  def apply(toload: Any): LoadClass = new LoadClass(toload)
}
class LoadClass(toLoad: Any) extends TLoader {
  def load() = toLoad.getClass
}
