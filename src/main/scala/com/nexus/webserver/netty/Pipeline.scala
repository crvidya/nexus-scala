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
import com.nexus.network.codec.{PacketWebSocketDecoder, PacketWebSocketEncoder}
import com.nexus.network.PacketHandler
import io.netty.handler.stream.ChunkedWriteHandler
import io.netty.handler.timeout.IdleStateHandler
import com.nexus.util.MultiplexingURLResolver
import com.nexus.webserver.handlers.{WebServerHandlerTest, WebServerHandlerWebsocket}
import io.netty.handler.logging.{LogLevel, LoggingHandler}

/**
 * TODO: Enter description
 * 
 * @author jk-5
 */
object Pipeline extends ChannelInitializer[SocketChannel] {

  private val webserverMultiplexer = new MultiplexingURLResolver
  this.webserverMultiplexer.addURLPattern("/websocket", classOf[WebServerHandlerWebsocket])
  this.webserverMultiplexer.addURLPattern("/test", classOf[WebServerHandlerTest])

  override def initChannel(channel: SocketChannel){
    val pipe = channel.pipeline()

    if(SslContextProvider.isValid){
      val engine = SslContextProvider.getContext.createSSLEngine()
      engine.setUseClientMode(false)
      pipe.addLast("ssl", new SslHandler(engine))                       //Upstream & Downstream
    }

    val websocketHandler = new WebSocketHandler
    //val webserverHandler = new WebServerHandler
    //webserverHandler.setWebSocketHandler(websocketHandler)

    pipe.addLast("httpDecoder", new HttpRequestDecoder)                 //Downstream
    pipe.addLast("httpEncoder", new HttpResponseEncoder)                //Upstream
    //pipe.addLast("gzip", new HttpContentCompressor(6));                 //Upstream
    pipe.addLast("httpHeaderAppender", HttpHeaderAppender);             //Upstream
    pipe.addLast("aggregator", new HttpObjectAggregator(1048576))       //Downstream
    pipe.addLast("chunkedWriter", new ChunkedWriteHandler())            //Upstream
    //pipe.addLast("webserverHandler", webserverHandler)                  //Downstream
    pipe.addLast("webserverRouter", new RouterHandler(this.webserverMultiplexer, "routedHandler"))               //Downstream
    pipe.addLast("packetWetSocketDecoder", PacketWebSocketDecoder)      //Downstream
    pipe.addLast("packetWetSocketEncoder", PacketWebSocketEncoder)      //Upstream
    pipe.addLast("routedHandler", NotFoundHandler)                      //Downstream
    pipe.addLast("idleStateHandler", new IdleStateHandler(10, 30, 60))  //Upstream & Downstream
    pipe.addLast("websocketHandler", websocketHandler)                  //Downstream
    pipe.addLast("packetHandler", PacketHandler)                        //Downstream
    pipe.addLast("notFoundHandler", NotFoundHandler)                    //Downstream
  }
}
