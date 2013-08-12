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

import io.netty.channel.socket.SocketChannel
import io.netty.channel.ChannelInitializer
import io.netty.handler.ssl.SslHandler
import io.netty.handler.codec.http.{HttpResponseEncoder, HttpObjectAggregator, HttpRequestDecoder}
import io.netty.handler.stream.ChunkedWriteHandler
import com.nexus.webserver.SslContextProvider

/**
 * TODO: Enter description
 * 
 * @author jk-5
 */
object Pipeline extends ChannelInitializer[SocketChannel] {
  override def initChannel(channel: SocketChannel){
    val pipe = channel.pipeline()
    if(SslContextProvider.isValid){
      val engine = SslContextProvider.getContext.createSSLEngine()
      engine.setUseClientMode(false)
      pipe.addLast("ssl", new SslHandler(engine))
    }
    pipe.addLast("decoder", new HttpRequestDecoder)
    pipe.addLast("aggregator", new HttpObjectAggregator(65536))
    pipe.addLast("encoder", new HttpResponseEncoder)
    //pipe.addLast("deflater", new HttpContentCompressor(1))   //FIXME: gzip
    pipe.addLast("chunkedWriter", new ChunkedWriteHandler)
    pipe.addLast("handler", new WebServerHandler)
  }
}
