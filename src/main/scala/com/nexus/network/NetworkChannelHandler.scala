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

import io.netty.util.internal.TypeParameterMatcher
import io.netty.util.ReferenceCountUtil
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http._
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import io.netty.channel.ChannelFutureListener
import com.nexus.util.Utils
import java.io.File
import java.util.regex.Pattern
import java.text.SimpleDateFormat
import java.util._

abstract class NetworkChannelHandler {
	private var req:WebServerRequest = _
	private var res:WebServerResponse = _
	private var request:HttpRequest = _

  private final val ALLOWED_FILE_NAME = Pattern.compile("[A-Za-z0-9][-_A-Za-z0-9\\.]")
  private final val HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz"
  private final val HTTP_DATE_GMT_TIMEZONE = "GMT"
  private final val HTTP_CACHE_SECONDS = 60

	def channelRead(ctx:ChannelHandlerContext, msg:Any){
		if(msg.isInstanceOf[HttpRequest]){
			this.request = msg.asInstanceOf[HttpRequest]
			if(HttpHeaders.is100ContinueExpected(this.request)) ctx.write(new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE))
			this.req = new WebServerRequest(ctx, this.request)
		}
		if(msg.isInstanceOf[HttpContent]){
			
		}
	}
	def handleRequest(request:WebServerRequest,response:WebServerResponse){
		
	}
	def sendError(status:HttpResponseStatus, ctx:ChannelHandlerContext){
		val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, Unpooled.copiedBuffer(status.toString() + "\r\n", CharsetUtil.UTF_8))
		response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/plain; charset=UTF-8")
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
	}
	def sendRedirect(location:String, ctx:ChannelHandlerContext){
		val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND)
		response.headers().set(HttpHeaders.Names.LOCATION, location)
		ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
	}
	def sendFileList(dir:File, ctx:ChannelHandlerContext){
	  val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
    response.headers().set(HttpHeaders.Names.CONTENT_TYPE, "text/html; charset=UTF-8")
    val buf = new StringBuffer()
    val dirPath = dir.getPath
    buf.append("<!DOCTYPE html>\r\n")
    buf.append("<html><head><title>")
    buf.append("Nexus - %s".format(dirPath))
    buf.append("</title></head><body>\r\n")
    buf.append("<h3>Contents of %s</h3>".format(dirPath))
    buf.append("<ul><li><a href=\"../\">..</a></li>")
    for(f <- dir.listFiles()){
      if (!(f.isHidden || f.canRead)){
        val name = f.getName
        if(ALLOWED_FILE_NAME.matcher(name).matches()){
          buf.append("<li><a href=\"%s\">%s</a></li>".format(name, name))
        }
      }
    }
    buf.append("</ul></body></html>")
    val buffer = Unpooled.copiedBuffer(buf, CharsetUtil.UTF_8)
    response.content().writeBytes(buffer)
    buffer.release()
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
	}
  def setDateAndCacheHeaders(fileToCache:File, response:HttpResponse){
    val formatter = new SimpleDateFormat(this.HTTP_DATE_FORMAT, Locale.US)
    formatter.setTimeZone(TimeZone.getTimeZone(this.HTTP_DATE_GMT_TIMEZONE))
    val time = new GregorianCalendar() //TODO NexusTime for this!!!
    response.headers().set(HttpHeaders.Names.DATE, formatter.format(time.getTime)) //TODO NexusTime for this!!!

    time.add(Calendar.SECOND, this.HTTP_CACHE_SECONDS)
    response.headers().set(HttpHeaders.Names.EXPIRES, formatter.format(time.getTime)) //TODO NexusTime for this!!!
    response.headers().set(HttpHeaders.Names.CACHE_CONTROL, "private, max-age=" + this.HTTP_CACHE_SECONDS)
    response.headers().set(HttpHeaders.Names.LAST_MODIFIED, formatter.format(new Date(fileToCache.lastModified())))
  }
	def sanitizeURI(uri:String):String = {
    val u = Utils.sanitizeURI(uri)
    if(u.startsWith("/")) return u.substring(1)
    else return u
  }
}
