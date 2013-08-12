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

package com.nexus.webserver.netty

import com.nexus.traits.TLoader
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.socket.nio.NioServerSocketChannel
import com.nexus.logging.NexusLog
import java.net.BindException

/**
 * TODO: Enter description
 * 
 * @author jk-5
 */
object WebServer extends TLoader {
	override def load = new WebServer(9001).start
}

class WebServer(private final val port: Int) extends Thread {
	override def run {
		val boss = new NioEventLoopGroup
		val worker = new NioEventLoopGroup
		try{
      val srv = new ServerBootstrap
      srv.group(boss, worker).channel(classOf[NioServerSocketChannel]).childHandler(Pipeline)
      val ch = srv.bind(this.port).sync().channel()
      NexusLog.info("Webserver is running on port " + this.port)
      ch.closeFuture().sync()
		}catch{
      case e: BindException => NexusLog.severe("Webserver was not able to listen on port " + this.port)
    }finally{
      boss.shutdownGracefully()
      worker.shutdownGracefully()
    }
	}
}
