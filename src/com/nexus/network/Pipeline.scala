package com.nexus.network

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpContentCompressor
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.ssl.SslHandler

class WebServerPipeline extends ChannelInitializer[SocketChannel] {

	override def initChannel(ch:SocketChannel){
		val pipeline = ch.pipeline()
		if(SslContextProvider.isValid){
			val sslEngine = SslContextProvider.getContext.createSSLEngine()
	        sslEngine.setUseClientMode(false)
	        pipeline.addLast("ssl", new SslHandler(sslEngine))
		}
		pipeline.addLast("decoder", new HttpRequestDecoder())
		//pipeline.addLast("aggregator", new HttpChunkAggregator(65536))
        pipeline.addLast("encoder", new HttpResponseEncoder())
        pipeline.addLast("deflater", new HttpContentCompressor(1))
        pipeline.addLast("handler", new NetworkHandler())
	}
}
