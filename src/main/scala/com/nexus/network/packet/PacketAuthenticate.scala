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

package com.nexus.network.packet

import com.nexus.data.json.JsonObject
import io.netty.channel.ChannelHandlerContext
import com.nexus.network.NetworkRegistry
import com.nexus.network.handlers.{DummyNetworkHandler, NetworkHandler}

/**
 * No description given
 *
 * @author jk-5
 */
class PacketAuthenticate extends Packet {

  private var data: String = _

  def write(data: JsonObject){

  }
  def read(data: JsonObject){
    this.data = data.get("secret").asString
  }
  def processPacket(handler: NetworkHandler){
    //TODO: Authenticate it!
    if(handler.needsAuthentication){
      val handlerClass = NetworkRegistry.getHandlerClass(this.getDecoder)
      if(handlerClass.isEmpty) return //TODO: Handle this!
      val newHandler = handlerClass.get.getConstructor(classOf[ChannelHandlerContext]).newInstance(handler.getChannelContext)
      NetworkRegistry.upgradeHandler(handler, newHandler)
      newHandler.handlerRegistered()
    }
  }
}
