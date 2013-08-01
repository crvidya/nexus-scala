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

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel
import io.netty.handler.codec.http.HttpContentCompressor
import io.netty.handler.codec.http.HttpResponseEncoder
import io.netty.handler.codec.http.HttpRequestDecoder
import io.netty.handler.ssl.SslHandler
import com.nexus.logging.NexusLog

class Pipeline extends ChannelInitializer[SocketChannel] {
	override def initChannel(ch: SocketChannel) {
		val pipeline = ch.pipeline()
		pipeline.addLast(new PortUnificationHandler)
		/*if(SslContextProvider.isValid) {
			val sslEngine = SslContextProvider.getContext.createSSLEngine()
			sslEngine.setUseClientMode(false)
			pipeline.addLast("ssl", new SslHandler(sslEngine))
		}
		pipeline.addLast("decoder", new HttpRequestDecoder())
		//pipeline.addLast("aggregator", new HttpChunkAggregator(65536))
		pipeline.addLast("encoder", new HttpResponseEncoder())
		pipeline.addLast("deflater", new HttpContentCompressor(1))
		pipeline.addLast("handler", new NetworkHandler())*/
	}
}
