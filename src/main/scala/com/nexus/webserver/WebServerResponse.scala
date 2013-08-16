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

import io.netty.handler.codec.http._
import com.nexus.Version
import com.nexus.data.json.JsonObject
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import io.netty.channel.ChannelHandlerContext

/**
 * TODO: Edit description
 *
 * @author jk-5
 */
class WebServerResponse(private final val request: WebServerRequest = null) {

  private var ctx = request.getContext
  private final val httpRequest = request.getHttpRequest
  private final val httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NO_CONTENT)
  private var headersReadyToSend = false
  private var headersSent = false
  private var responseClosed = false

  def this(c: ChannelHandlerContext){
    this
    this.ctx = c
  }

  def setHeader(name: String, value: AnyRef) = this.httpResponse.headers().set(name, value)
  def sendHeaders(status: HttpResponseStatus){
    this.httpResponse.setStatus(status)
    this.httpResponse.setProtocolVersion(request.getHttpVersion)
    this.setHeader("server", "nexus-scala/%s.%d".format(Version.version, Version.build))
    this.headersReadyToSend = true
  }
  private def sendHeadersToSession = try {
    this.ctx.write(this.httpResponse)
    this.headersSent = true
  }
  def forceSendHeaders{
    if(!this.headersReadyToSend) throw new IllegalStateException("WebServerStatus and headers are not set!")
    this.sendHeadersToSession
  }
  def forceSendHeaders(status:HttpResponseStatus){
    if(!this.headersReadyToSend) this.sendHeaders(status)
    this.sendHeadersToSession
  }
  def sendData(p:TWebServerResponse){
    if(!this.headersReadyToSend) throw new IllegalStateException("WebServerStatus and headers are not set!")

    val data = p.getResponseData
    val length = data.length()
    if(length == 0){
      this.setHeader(HttpHeaders.Names.CONTENT_LENGTH, "0")
      this.forceSendHeaders(HttpResponseStatus.NO_CONTENT)
      this.close
      return
    }

    this.setHeader(HttpHeaders.Names.CONTENT_TYPE, "%s; charset=UTF-8".format(p.getMimeType))

    if(!this.headersSent) this.forceSendHeaders
    if(this.httpRequest != null && this.httpRequest.getMethod() == HttpMethod.HEAD) return
    this.ctx.write(Unpooled.copiedBuffer(data, CharsetUtil.UTF_8))
  }
  def sendError(s:String){
    val p = new JsonObject
    p.addError("none")
    this.sendData(p)
  }
  /*def SendEventResult(e:Event){
    val p = new JSONPacket()
    p.addEventResult(e)
    this.sendData(p)
  }*/
  def close{
    this.ctx.flush().close()
    this.responseClosed = true
  }
  def isOpen:Boolean = !this.responseClosed
}
