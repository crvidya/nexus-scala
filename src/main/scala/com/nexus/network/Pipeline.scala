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

import io.netty.channel.ChannelInitializer
import io.netty.channel.socket.SocketChannel

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
