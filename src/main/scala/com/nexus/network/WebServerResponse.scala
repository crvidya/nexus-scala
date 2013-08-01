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
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpResponseStatus
import com.nexus.Version
import io.netty.handler.codec.http.DefaultHttpResponse
import io.netty.handler.codec.http.HttpVersion
import io.netty.handler.codec.http.HttpResponse
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpMethod
import com.nexus.data.json.JsonObject

class WebServerResponse(private final val request:WebServerRequest) {

	private var httpResponse:HttpResponse = _
	private final val ctx = request.getContext
	private final val httpRequest = request.getHttpRequest
	private var HeadersReadyToSend = false
	private var HeadersSent = false;
	private var ResponseClosed = false;
	
	def setHeader(name:String,value:AnyRef) = this.httpResponse.headers().set(name,value)
	
	def SendHeaders(status:HttpResponseStatus){
		this.httpResponse = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status)
		this.setHeader(HttpHeaders.Names.SERVER, "nexus-scala/" + Version.version)
		this.HeadersReadyToSend = true
	}
	private def SendHeadersToSession{
		try{
			this.ctx.write(this.httpResponse)
			this.HeadersSent = true
		}catch{case e:Exception =>}
	}
	def ForceSendHeaders{
		if(!this.HeadersReadyToSend) throw new IllegalStateException("WebServerStatus and headers are not set!")
		this.SendHeadersToSession
	}
	def ForceSendHeaders(status:HttpResponseStatus){
		if(!this.HeadersReadyToSend) this.SendHeaders(status)
		this.SendHeadersToSession
	}
	def SendData(p:TWebServerResponse){
		if(!this.HeadersReadyToSend) throw new IllegalStateException("WebServerStatus and headers are not set!")
		
		val data = p.getResponseData
		val length = data.length()
		if(length == 0){
			this.setHeader("Content-Length", "0")
			this.ForceSendHeaders(HttpResponseStatus.NO_CONTENT)
			this.close
			return
		}

		//this.setHeader(HttpHeaders.Names.CONTENT_LENGTH, length.toString)
		this.setHeader(HttpHeaders.Names.CONTENT_TYPE, p.getMimeType)
		
		if(!this.HeadersSent) this.ForceSendHeaders
		if(this.httpRequest.getMethod() == HttpMethod.HEAD) return
		this.ctx.write(data)
	}
	def SendError(s:String){
		val p = new JsonObject
		p.addError("none")
		this.SendData(p)
	}
	/*def SendEventResult(e:Event){
		val p = new JSONPacket()
		p.addEventResult(e)
		this.SendData(p)
	}*/
	def close{
		this.ctx.close()
		this.ResponseClosed = true
	}
	def isOpen:Boolean = !this.ResponseClosed
}
