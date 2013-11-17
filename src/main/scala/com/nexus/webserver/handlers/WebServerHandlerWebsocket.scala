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

package com.nexus.webserver.handlers

import com.nexus.webserver.{SslContextProvider, TWebServerHandler}
import io.netty.channel.{SimpleChannelInboundHandler, ChannelHandlerContext}
import io.netty.handler.codec.http.{HttpHeaders, FullHttpRequest}
import io.netty.handler.codec.http.websocketx._
import com.nexus.webserver.netty.{WebSocketHandler, RoutedHandler}

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerHandlerWebsocket extends SimpleChannelInboundHandler[FullHttpRequest] with RoutedHandler {

  def channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest) {
    println(this.getURLData.getURL)
    val factory = new WebSocketServerHandshakerFactory("%s://".format(if(SslContextProvider.isValid) "wss" else "ws") + msg.headers().get(HttpHeaders.Names.HOST) + "/websocket", null, false)
    val handshaker = factory.newHandshaker(msg)
    if(handshaker == null) WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel())
    else{
      handshaker.handshake(ctx.channel(), msg)
      ctx.pipeline().get("websocketHandler").asInstanceOf[WebSocketHandler].setHandshaker(handshaker)
    }
  }
}
