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
