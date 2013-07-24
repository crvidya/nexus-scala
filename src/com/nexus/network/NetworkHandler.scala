package com.nexus.network

import java.util.{Map => JMap}

import scala.collection.JavaConversions.asScalaSet

import com.google.common.collect.Maps
import com.nexus.util.Utils

import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.ssl.NotSslRecordException

object NetworkHandler {
	private final val handlers:JMap[String,NetworkChannelHandler] = Maps.newLinkedHashMap();
	def addHandler(path:String,handler:NetworkChannelHandler) = this.handlers.put(path, handler);
}

class NetworkHandler extends SimpleChannelInboundHandler[Object] {
	
	private var handler:NetworkChannelHandler = _
	
	override def channelReadComplete(ctx:ChannelHandlerContext) = ctx.flush()
	override protected def channelRead0(ctx:ChannelHandlerContext, msg:Object){
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
		if(this.handler != null) this.handler.channelRead(ctx, msg)
	}
	
	override def exceptionCaught(ctx:ChannelHandlerContext, e:Throwable){
		if(e.isInstanceOf[NotSslRecordException]){
			//ctx.write(msg)
		}else{
			e.printStackTrace();
		}
	}
}
