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
import com.nexus.network.handlers.NetworkHandler

/**
 * No description given
 *
 * @author jk-5
 */
object NetworkRegistry {

  private final val decoderToHandlerClass = mutable.HashMap[String, Class[_ <: NetworkHandler]](

  )
  private final val ctxToHandlerMap = mutable.HashMap[ChannelHandlerContext, NetworkHandler]()

  def getHandlerClass(decoder: String) = this.decoderToHandlerClass.get(decoder)
  def addHandler(ctx: ChannelHandlerContext, handler: NetworkHandler) = this.ctxToHandlerMap.put(ctx, handler)
  def getHandler(ctx: ChannelHandlerContext): Option[NetworkHandler] = this.ctxToHandlerMap.get(ctx)
}
