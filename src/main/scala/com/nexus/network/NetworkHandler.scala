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
