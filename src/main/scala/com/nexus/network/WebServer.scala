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

package com.nexus.network

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import com.nexus.traits.TLoader
import com.nexus.logging.NexusLog

object WebServer extends TLoader {
	def load = new WebServer(9001).run
}

class WebServer(private final val port:Int) {

	def run{
		val bossGroup = new NioEventLoopGroup()
    val workerGroup = new NioEventLoopGroup()
    try{
      val b = new ServerBootstrap()
      b.group(bossGroup, workerGroup).channel(classOf[NioServerSocketChannel]).childHandler(new Pipeline)
      val chan = b.bind(this.port).sync().channel()
      NexusLog.info("WebServer is running on port " + this.port)
      chan.closeFuture().sync()
    }finally{
      bossGroup.shutdownGracefully()
      workerGroup.shutdownGracefully()
    }
    println("webserver closed!")
	}
}
