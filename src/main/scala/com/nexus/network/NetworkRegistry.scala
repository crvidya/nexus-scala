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

import scala.collection.mutable
import io.netty.channel.ChannelHandlerContext
import com.nexus.network.handlers.{DummyNetworkHandler, NetworkHandlerWebsocket, NetworkHandler}

/**
 * No description given
 *
 * @author jk-5
 */
object NetworkRegistry {

  private final val decoderToHandlerClass = mutable.HashMap[String, Class[_ <: NetworkHandler]](
    "websocket" -> classOf[NetworkHandlerWebsocket]
  )
  private final val ctxToHandlerMap = mutable.HashMap[ChannelHandlerContext, NetworkHandler]()

  @inline def getHandlerClass(decoder: String) = this.decoderToHandlerClass.get(decoder)
  @inline def addHandler(handler: NetworkHandler) = this.ctxToHandlerMap.put(handler.getChannelContext, handler)
  @inline def getHandler(ctx: ChannelHandlerContext): Option[NetworkHandler] = this.ctxToHandlerMap.get(ctx)
  @inline def getOrCreateHandler(ctx: ChannelHandlerContext): NetworkHandler = this.ctxToHandlerMap.get(ctx).getOrElse(new DummyNetworkHandler(ctx))
  def upgradeHandler(handler: NetworkHandler, newHandler: NetworkHandler){
    this.ctxToHandlerMap.remove(this.ctxToHandlerMap.find(_._2 == handler).get._1)
    this.addHandler(newHandler)
  }
}
