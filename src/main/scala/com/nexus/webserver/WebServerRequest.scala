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

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{QueryStringDecoder, FullHttpRequest}
import java.net.InetSocketAddress
import com.nexus.util.Utils
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerRequest(private final val ctx: ChannelHandlerContext, private final val request: FullHttpRequest) {

  private final val queryStringDecoder = new QueryStringDecoder(this.request.getUri)
  private final val postParameters = new HttpPostRequestDecoder(this.request)
  private final val params = this.queryStringDecoder.parameters()

  def getAddress = this.ctx.channel().remoteAddress().asInstanceOf[InetSocketAddress].getAddress
  def getHttpVersion = this.request.getProtocolVersion
  def getMethod = this.request.getMethod
  def getPath = Utils.sanitizeURI(this.request.getUri)

  def isHeaderPresent(key:String) = this.request.headers().contains(key)
  def isParameterPresent(key:String) = this.params.containsKey(key)

  def getHeader(key:String): Option[String] = this.request.headers().get(key) match{
    case s: String => Some(s)
    case _ => None
  }
  def getParameter(key:String): Option[String] = if(this.params.get(key) == null || this.params.get(key).size() == 0) None else Some(this.params.get(key).get(0))
  def getPostData = this.postParameters

  def getContext = this.ctx
  def getHttpRequest = this.request

  //TODO: enable me!
  //def getUserFromParameter(key:String) = User.fromID(this.getParameter(key).toInt)
}
