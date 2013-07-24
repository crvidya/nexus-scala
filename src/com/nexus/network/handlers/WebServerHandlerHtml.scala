package com.nexus.network.handlers

import java.util.{Map => JMap}
import com.google.common.collect.Maps
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.channel.SimpleChannelInboundHandler
import io.netty.handler.codec.http.DefaultFullHttpResponse
import io.netty.handler.codec.http.HttpContent
import io.netty.handler.codec.http.HttpHeaders
import io.netty.handler.codec.http.HttpHeaders.Names.CONNECTION
import io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH
import io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_TYPE
import io.netty.handler.codec.http.HttpHeaders.is100ContinueExpected
import io.netty.handler.codec.http.HttpRequest
import io.netty.handler.codec.http.HttpResponseStatus
import io.netty.handler.codec.http.HttpResponseStatus.CONTINUE
import io.netty.handler.codec.http.HttpVersion
import io.netty.handler.codec.http.LastHttpContent
import io.netty.util.CharsetUtil
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.io.File
import com.nexus.util.Utils
import scala.collection.JavaConversions._
import com.nexus.network.NetworkChannelHandler

object WebServerHandlerHtml extends NetworkChannelHandler {

	private final val buffer = new StringBuffer
	private var request:HttpRequest = _
	
	override def channelRead(ctx:ChannelHandlerContext, msg:Any){
		if(msg.isInstanceOf[HttpRequest]){
			this.request = msg.asInstanceOf[HttpRequest]
			
			if(is100ContinueExpected(this.request)) ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, CONTINUE))
			
			this.buffer.setLength(0)
			this.buffer.append("WEBSERVER\r\n")
			this.buffer.append("=========\r\n")
			this.buffer.append(this.request.getProtocolVersion())
		}
		if(msg.isInstanceOf[HttpContent]){
			val httpContent = msg.asInstanceOf[HttpContent]
			val content = httpContent.content()
			if(msg.isInstanceOf[LastHttpContent]){
				val trailer = msg.asInstanceOf[LastHttpContent]
				val keepAlive = this.request.headers().contains(CONNECTION) && this.request.headers().get(CONNECTION).equalsIgnoreCase("keep-alive")
				val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, if(trailer.getDecoderResult().isSuccess()) HttpResponseStatus.OK else HttpResponseStatus.BAD_REQUEST, Unpooled.copiedBuffer(this.buffer.toString(), CharsetUtil.UTF_8))
				response.headers().set(CONTENT_TYPE, "text/plain; charset=UTF-8")
				if(keepAlive){
					response.headers().set(CONTENT_LENGTH, response.content().readableBytes())
					response.headers().set(CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
				}
				ctx.write(response)
			}
		}
	}
}
