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

import com.nexus.network.packet.Packet
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import com.nexus.concurrent.WorkerPool
import com.nexus.network.handlers.{NetworkHandler, DummyNetworkHandler}

/**
 * No description given
 *
 * @author jk-5
 */
class PacketHandler extends SimpleChannelInboundHandler[Packet] {

  def channelRead0(ctx: ChannelHandlerContext, packet: Packet){
    var handler = NetworkRegistry.getHandler(ctx)
    if(!handler.isDefined) handler = Some(new DummyNetworkHandler(ctx))
    WorkerPool.execute(new ProcessPacketTask(packet, handler.get))
  }

  class ProcessPacketTask(packet: Packet, handler: NetworkHandler) extends Runnable{
    def run(){
      packet.processPacket(handler)
    }
  }
}
