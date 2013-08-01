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

import io.netty.channel.ChannelHandlerContext
import java.net.InetSocketAddress
import io.netty.handler.codec.http.HttpRequest
import com.nexus.util.Utils
import io.netty.handler.codec.http.QueryStringDecoder

class WebServerRequest(private final val ctx:ChannelHandlerContext, private final val request:HttpRequest) {

	private final val queryStringDecoder = new QueryStringDecoder(request.getUri());
    private final val params = queryStringDecoder.parameters();
	
	def getAddress = this.ctx.channel().remoteAddress().asInstanceOf[InetSocketAddress].getAddress()
	def getHttpVersion = this.request.getProtocolVersion()
	def getMethod = this.request.getMethod()
	def getPath = Utils.sanitizeURI(this.request.getUri())
	
	def isHeaderPresent(key:String) = this.request.headers().contains(key)
	def isParameterPresent(key:String) = this.params.containsKey(key)
	
	def getHeader(key:String) = this.request.headers().get(key)
	def getParameter(key:String) = this.params.get(key).get(0)
	
	def getHeaderOrEmptyString(key:String) = if(this.isParameterPresent(key)) this.getParameter(key) else ""
	def getParameterOrEmptyString(key:String) = if(this.isHeaderPresent(key)) this.getHeader(key) else ""
	
	def getContext = this.ctx
	def getHttpRequest = this.request
		
	//TODO: enable me!
	//def getUserFromParameter(key:String) = User.fromID(this.getParameter(key).toInt)
}
