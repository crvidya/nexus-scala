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

import scala.collection.immutable.HashMap
import com.nexus.network.packet._

/**
 * No description given
 *
 * @author jk-5
 */
object PacketManager {
  private final val packets = HashMap[Int, Class[_ <: Packet]](
    0x00 -> classOf[PacketKeepAlive],
    0x01 -> classOf[PacketAuthenticate],
    0x02 -> classOf[PacketAuthenticationSuccess],
    0x03 -> classOf[PacketCloseConnection]
  )

  def getPacketID(cl: Class[_ <: Packet]): Int = this.packets.find(p => p._2 == cl).get._1
  def getPacketFromID(id: Int): Packet = this.packets.get(id).getOrElse(null).newInstance()
}
