package com.nexus.network.packet

import com.nexus.data.json.JsonObject
import com.nexus.network.PacketManager
import io.netty.channel.ChannelHandlerContext

/**
 * No description given
 *
 * @author jk-5
 */
abstract class Packet {
  private var decoder: String = ""
  def processPacket(ctx: ChannelHandlerContext)
  def write(data: JsonObject)
  def read(data: JsonObject)
  def hasData = true
  final def getPacketID = PacketManager.getPacketID(this.getClass)
  final def setDecoder(decoder: String) = this.decoder = decoder
  final def getDecoder = this.decoder
}
