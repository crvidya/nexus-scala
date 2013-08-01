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

import java.util.{Map => JMap}

import scala.collection.JavaConversions.asScalaSet

import com.google.common.collect.Maps
import com.nexus.util.Utils

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.ssl.NotSslRecordException
import com.nexus.logging.NexusLog

object NetworkHandler {
	private final val handlers:JMap[String,NetworkChannelHandler] = Maps.newLinkedHashMap();
	def addHandler(path:String,handler:NetworkChannelHandler) = this.handlers.put(path, handler);
}

class NetworkHandler extends SimpleChannelInboundHandler[Object] {
	
	private var handler:NetworkChannelHandler = _
	
	override def channelReadComplete(ctx:ChannelHandlerContext){
    //ctx.flush()
  }
	override protected def channelRead0(ctx:ChannelHandlerContext, msg:Object){
    NexusLog.info("Read from the channel")
		if(msg.isInstanceOf[HttpRequest]){
			val request = msg.asInstanceOf[HttpRequest]
			request.setUri(Utils.sanitizeURI(request.getUri()))
			val uri = request.getUri()
			this.handler = null
			for(h <- NetworkHandler.handlers.entrySet()){
				if(uri.trim().startsWith(h.getKey().trim())){
					this.handler = h.getValue()
				}
			}
		}
    NexusLog.info("matched handler " + this.handler)
		if(this.handler != null) this.handler.channelRead(ctx, msg)
	}
	
	override def exceptionCaught(ctx:ChannelHandlerContext, e:Throwable){
    e.printStackTrace()
		if(e.isInstanceOf[NotSslRecordException]){
			//ctx.write(msg)
		}else{
			e.printStackTrace();
		}
	}
}
