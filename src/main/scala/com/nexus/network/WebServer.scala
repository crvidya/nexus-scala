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

package com.nexus.network

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import com.nexus.traits.TLoader
import com.nexus.logging.NexusLog
import java.net.BindException

object WebServer extends TLoader {
	def load = new WebServer(9001).start()
}

class WebServer(private final val port:Int) extends Thread {
	this.setName("WebServer")

	override def run{
		NexusLog.info("Starting WebServer")
		val bossGroup = new NioEventLoopGroup()
    val workerGroup = new NioEventLoopGroup()
    try{
      val b = new ServerBootstrap()
      b.group(bossGroup, workerGroup).channel(classOf[NioServerSocketChannel]).childHandler(new Pipeline)
      val chan = b.bind(this.port).sync().channel()
      NexusLog.info("WebServer is running on port " + this.port)
      chan.closeFuture().sync()
    }catch{
	    case e: BindException => NexusLog.severe("WebServer was not able to start! Address is already in use!")
    }finally{
      bossGroup.shutdownGracefully()
      workerGroup.shutdownGracefully()
    }
	}
}
