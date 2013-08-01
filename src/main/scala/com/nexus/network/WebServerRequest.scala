/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Jeffrey Kog (jk-5), Martijn Reening (martijnreening)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
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
