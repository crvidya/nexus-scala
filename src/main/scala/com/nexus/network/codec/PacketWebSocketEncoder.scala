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

package com.nexus.network.codec

import io.netty.handler.codec.MessageToMessageEncoder
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import java.util
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import com.nexus.network.packet.Packet
import com.nexus.data.json.JsonObject

/**
 * Encodes an Packet into an JsonObject
 *
 * @author jk-5
 */
@Sharable
object PacketWebSocketEncoder extends MessageToMessageEncoder[Packet] {

  override def encode(ctx: ChannelHandlerContext, packet: Packet, out: util.List[AnyRef]){
    val data = new JsonObject
    packet.write(data)
    val packetData = new JsonObject
    packetData.add("id", packet.getPacketID)
    if(packet.hasData) packetData.add("data", data)
    out.add(new TextWebSocketFrame(packetData.stringify))
  }
}
