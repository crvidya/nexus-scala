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
object PacketJsonEncoder extends MessageToMessageEncoder[Packet] {

  override def encode(ctx: ChannelHandlerContext, packet: Packet, out: util.List[AnyRef]){
    val data = new JsonObject
    packet.write(data)
    val packetData = new JsonObject
    packetData.add("id", packet.getPacketID)
    if(packet.hasData) packetData.add("data", data)
    out.add(packetData)
  }
}
