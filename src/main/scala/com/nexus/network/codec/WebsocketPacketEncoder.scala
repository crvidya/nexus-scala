package com.nexus.network.codec

import io.netty.handler.codec.MessageToMessageEncoder
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import java.util
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame
import com.nexus.network.packet.Packet

/**
 * Encodes an WebsocketPacket into an TextWebSocketFrame
 *
 * @author jk-5
 */
@Sharable
class WebsocketPacketEncoder extends MessageToMessageEncoder[Packet] {

  override def encode(ctx: ChannelHandlerContext, packet: Packet, out: util.List[AnyRef]){
    out.add(new TextWebSocketFrame())
  }
}
