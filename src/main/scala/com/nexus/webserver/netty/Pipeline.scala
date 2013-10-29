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
import io.netty.handler.codec.http._
import com.nexus.webserver.SslContextProvider
import com.nexus.network.codec.{PacketJsonDecoder, JsonObjectDecoder, JsonObjectEncoder, PacketJsonEncoder}
import com.nexus.network.PacketHandler
import io.netty.handler.stream.ChunkedWriteHandler
import io.netty.handler.timeout.IdleStateHandler

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
      pipe.addLast("ssl", new SslHandler(engine))                       //Upstream & Downstream
    }

    val websocketHandler = new WebSocketHandler
    val webserverHandler = new WebServerHandler
    webserverHandler.setWebSocketHandler(websocketHandler)

    pipe.addLast("httpDecoder", new HttpRequestDecoder)                 //Downstream
    pipe.addLast("httpEncoder", new HttpResponseEncoder)                //Upstream
    pipe.addLast("aggregator", new HttpObjectAggregator(1048576))       //Downstream
    pipe.addLast("jsonDecoder", JsonObjectDecoder)                      //Downstream
    pipe.addLast("jsonEncoder", JsonObjectEncoder)                      //Upstream
    pipe.addLast("packetJsonDecoder", PacketJsonDecoder)                //Downstream
    pipe.addLast("packetJsonEncoder", PacketJsonEncoder)                //Upstream
    pipe.addLast("chunkedWriter", new ChunkedWriteHandler())            //Upstream
    pipe.addLast("webserverHandler", webserverHandler)                  //Downstream
    pipe.addLast("idleStateHandler", new IdleStateHandler(10, 30, 60))  //Upstream & Downstream
    pipe.addLast("websocketHandler", websocketHandler)                  //Downstream
    pipe.addLast("packetHandler", PacketHandler)                        //Downstream
  }
}
