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

import com.nexus.network.packet.{PacketCloseConnection, PacketAuthenticationSuccess, Packet}
import io.netty.channel.{ChannelFuture, ChannelHandlerContext}
import io.netty.handler.timeout.{IdleStateEvent, IdleState}

/**
 * No description given
 *
 * @author jk-5
 */
abstract class NetworkHandler(private final val ctx: ChannelHandlerContext) {

  def sendPacket(packet: Packet) = this.ctx.writeAndFlush(packet)
  def closeConnection(reason: String): ChannelFuture = {
    this.sendPacket(new PacketCloseConnection(reason))
    this.ctx.close()
  }
  def closeConnection(): ChannelFuture = this.closeConnection("No reason given")
  private def onHandlerRegistered() = {}

  final def needsAuthentication = this.isInstanceOf[DummyNetworkHandler]

  final def handlerRegistered(){
    this.onHandlerRegistered()
    this.sendPacket(new PacketAuthenticationSuccess)
  }

  def onPipelineEvent(event: AnyRef){
    event match {
      case e: IdleStateEvent => e.state() match {
        case IdleState.READER_IDLE => if(this.needsAuthentication){
          ctx.writeAndFlush(new PacketCloseConnection("Not logged in for 10 seconds"))
          ctx.close()
        }
        case IdleState.WRITER_IDLE => //TODO: Try ping
        case IdleState.ALL_IDLE => //TODO: Not sure what to do here? Just close it?
      }
    }
  }

  final def getChannelContext = this.ctx
}

class DummyNetworkHandler(_ctx: ChannelHandlerContext) extends NetworkHandler(_ctx)
