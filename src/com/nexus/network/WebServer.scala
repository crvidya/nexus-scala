package com.nexus.network

import io.netty.bootstrap.ServerBootstrap
import io.netty.channel.nio.NioEventLoopGroup
import io.netty.channel.socket.nio.NioServerSocketChannel
import com.nexus.traits.TLoader

object WebServer extends TLoader {
	def load = new WebServer(9001).run
}

class WebServer(private final val port:Int) {

	def run{
		val bossGroup = new NioEventLoopGroup()
        val workerGroup = new NioEventLoopGroup()
        try{
            val b = new ServerBootstrap()
            b.group(bossGroup, workerGroup).channel(classOf[NioServerSocketChannel]).childHandler(new WebServerPipeline)
            b.bind(this.port).sync().channel().closeFuture().sync()
        }finally{
            bossGroup.shutdownGracefully()
            workerGroup.shutdownGracefully()
        }
	}
}
