package com.nexus.network.packet

import com.nexus.data.json.JsonObject
import com.nexus.network.PacketManager

/**
 * TODO: Edit description
 *
 * @author jk-5
 */
abstract class Packet {
  def processPacket()
  def write(data: JsonObject)
  def read(data: JsonObject)
  def getPacketID = PacketManager.getPacketID(this.getClass)
}
