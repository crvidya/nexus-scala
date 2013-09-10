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

import io.netty.handler.codec.MessageToMessageDecoder
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import io.netty.channel.ChannelHandlerContext
import java.util
import com.nexus.data.json.JsonObject
import com.nexus.network.PacketManager

/**
 * Decodes an TextWebSocketFrame into an WebsocketPacket
 *
 * @author jk-5
 */
class WebsocketPacketDecoder extends MessageToMessageDecoder[TextWebSocketFrame] {

  override def decode(ctx: ChannelHandlerContext, msg: TextWebSocketFrame, out: util.List[AnyRef]){
    val jsonData = JsonObject.readFrom(msg.text())
    if(jsonData == null || jsonData.get("id") == null) return
    val packet = PacketManager.getPacketFromID(jsonData.get("id").asInt)
    if(packet == null) return
    if(jsonData.get("data") != null){
      packet.read(jsonData.get("data").asObject)
    }
  }
}
