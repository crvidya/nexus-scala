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

package com.nexus.network.handlers

import com.nexus.network.packet.PacketCloseConnection
import io.netty.handler.codec.http.websocketx.{CloseWebSocketFrame, WebSocketServerHandshaker}
import io.netty.channel.{ChannelFuture, ChannelHandlerContext}
import com.nexus.webserver.netty.WebSocketHandler

/**
 * No description given
 *
 * @author jk-5
 */
class NetworkHandlerWebsocket(_ctx: ChannelHandlerContext) extends NetworkHandler(_ctx) {

  private val handshaker: WebSocketServerHandshaker = this.getChannelContext.channel().pipeline().get(classOf[WebSocketHandler]).getHandshaker

  override def closeConnection(reason: String): ChannelFuture = {
    this.sendPacket(new PacketCloseConnection(reason))
    this.handshaker.close(this.getChannelContext.channel(), new CloseWebSocketFrame(1000, reason))
  }
}
