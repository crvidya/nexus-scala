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

import io.netty.handler.codec.http.websocketx._
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.ssl.NotSslRecordException
import io.netty.handler.timeout.ReadTimeoutException
import com.nexus.network.packet.PacketCloseConnection
import com.nexus.network.NetworkRegistry

/**
 * No description given
 *
 * @author jk-5
 */
class WebSocketHandler extends SimpleChannelInboundHandler[WebSocketFrame] {

  private var readTimeoutHandler: CancelableReadTimeoutHandler = _
  private var handshaker: WebSocketServerHandshaker = _

  override def channelRead0(ctx: ChannelHandlerContext, msg: WebSocketFrame){
    msg match {
      case t: TextWebSocketFrame => //TODO: This is invalid data! Handle it!
      case f: CloseWebSocketFrame => NetworkRegistry.getHandler(ctx).get.closeConnection("Client requested disconnect")
      case f: PingWebSocketFrame => ctx.channel().write(new PongWebSocketFrame(f.content().retain()))
      case f => throw new UnsupportedOperationException("%s frame types not supported".format(msg.getClass.getName))
    }
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) = cause match{
    case e: NotSslRecordException => //TODO: Redirect to SSL
    case e: ReadTimeoutException => ctx.writeAndFlush(new PacketCloseConnection("Not logged in for 10 seconds"))
  }

  def setReadTimeoutHandler(handler: CancelableReadTimeoutHandler) = this.readTimeoutHandler = handler
  def setHandshaker(h: WebSocketServerHandshaker) = this.handshaker = h
  def getHandshaker = this.handshaker
}
