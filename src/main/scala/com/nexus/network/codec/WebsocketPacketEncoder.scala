package com.nexus.network.codec

import io.netty.handler.codec.MessageToMessageEncoder
import com.nexus.network.websocket.packet.WebsocketPacket
import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.ChannelHandlerContext
import java.util
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame

/**
 * Encodes an WebsocketPacket into an TextWebSocketFrame
 *
 * @author jk-5
 */
@Sharable
class WebsocketPacketEncoder extends MessageToMessageEncoder[WebsocketPacket] {

  override def encode(ctx: ChannelHandlerContext, packet: WebsocketPacket, out: util.List[AnyRef]){
    out.add(new TextWebSocketFrame())
  }
}
