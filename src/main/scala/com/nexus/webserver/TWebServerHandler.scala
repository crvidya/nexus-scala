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

package com.nexus.webserver

import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext}
import io.netty.handler.codec.http._
import java.util._
import java.text.SimpleDateFormat
import com.nexus.util.Utils
import java.io.File
import com.nexus.webserver.netty.WebServerHandler
import io.netty.buffer.Unpooled
import com.nexus.data.json.JsonObject
import io.netty.util.CharsetUtil
import com.nexus.Version
import com.nexus.time.NexusTime
import com.nexus.errorhandling.JsonErrorException

/**
 * No description given
 *
 * @author jk-5
 */
trait TWebServerHandler {

  private var nettyHandler: WebServerHandler = _

  def setNettyHandler(handler: WebServerHandler) = this.nettyHandler = handler
  def getNettyHandler = this.nettyHandler

  def handleRequest(ctx: ChannelHandlerContext, req: FullHttpRequest){
    val request = new WebServerRequest(ctx, req)
    val response = new WebServerResponse(request)
    try{
      this.handle(request, response)
    }catch{
      case e: JsonErrorException => {
        response.sendHeaders(e.error.getStatus)
        response.sendData(e.error.toErrorJson)
        response.close()
      }
    }
  }
  def handle(request: WebServerRequest, response: WebServerResponse){
    response.sendHeaders(HttpResponseStatus.NOT_FOUND)
    response.sendError("This handler is not implemented")
    response.close()
  }
}
