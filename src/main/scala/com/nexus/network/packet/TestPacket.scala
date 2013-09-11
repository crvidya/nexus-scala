package com.nexus.network.packet

import com.nexus.data.json.JsonObject
import com.nexus.concurrent.WorkerPool
import java.util.concurrent.FutureTask

/**
 * No description given
 *
 * @author jk-5
 */
case class TestPacket(content: String) extends Packet {

  override def write(data: JsonObject){

  }
  override def read(data: JsonObject){

  }
  override def processPacket(){

  }
}
