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
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{HttpHeaders, FullHttpRequest}
import io.netty.handler.codec.http.websocketx._

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerHandlerWebsocket(private final val websocketPath: String) extends TWebServerHandler {

  override def handleRequest(ctx: ChannelHandlerContext, req: FullHttpRequest) {
    val factory = new WebSocketServerHandshakerFactory("%s://".format(if(SslContextProvider.isValid) "wss" else "ws") + req.headers().get(HttpHeaders.Names.HOST + this.websocketPath), null, false)
    val handshaker = factory.newHandshaker(req)
    if(handshaker == null) WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(ctx.channel())
    else{
      handshaker.handshake(ctx.channel(), req)
      this.getNettyHandler.getWebSocketHandler.setHandshaker(handshaker)
    }
  }
}
