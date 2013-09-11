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

import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext}
import io.netty.handler.codec.http._
import java.util._
import java.text.SimpleDateFormat
import com.nexus.util.Utils
import java.io.File
import com.nexus.webserver.netty.WebServerHandler

/**
 * No description given
 *
 * @author jk-5
 */
trait TWebServerHandler {

  private var nettyHandler: WebServerHandler = _
  protected final val HTTP_DATE_FORMAT = "EEE, dd MMM yyyy HH:mm:ss zzz"
  protected final val HTTP_DATE_GMT_TIMEZONE = "GMT"
  protected final val HTTP_CACHE_SECONDS = 60

  def setNettyHandler(handler: WebServerHandler) = this.nettyHandler = handler
  def getNettyHandler = this.nettyHandler

  def handleRequest(ctx: ChannelHandlerContext, req: FullHttpRequest){
    val request = new WebServerRequest(ctx, req)
    val response = new WebServerResponse(request)
    this.handle(request, response)
  }
  def handle(request: WebServerRequest, response: WebServerResponse){
    response.sendHeaders(HttpResponseStatus.NOT_FOUND)
    response.sendError("This handler is not implemented")
    response.close()
  }

  protected def sendError(ctx: ChannelHandlerContext, status: HttpResponseStatus){
    val res = new WebServerResponse(ctx)
    res.sendHeaders(status)
    res.sendError(status.toString)
    res.close()
  }
  protected def sendRedirect(ctx: ChannelHandlerContext, destination: String){
    val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND)
    response.headers().set(HttpHeaders.Names.LOCATION, destination)
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
  }
  protected def sendNotModified(ctx: ChannelHandlerContext){
    val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_MODIFIED)
    this.setDateHeader(response)
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
  }
  protected def setDateHeader(response: FullHttpResponse){
    val formatter = new SimpleDateFormat(HTTP_DATE_FORMAT, Locale.US)
    formatter.setTimeZone(TimeZone.getTimeZone(HTTP_DATE_GMT_TIMEZONE)) //TODO: nexus time!
    val time = new GregorianCalendar() //TODO: nexus time!
    response.headers().set(HttpHeaders.Names.DATE, formatter.format(time.getTime))
  }
  protected def setContentLength(response: HttpResponse, fileLength: Long) = HttpHeaders.setContentLength(response, fileLength)
  protected def setContentType(response: HttpResponse, file: File) = response.headers().set(HttpHeaders.Names.CONTENT_TYPE, Utils.getMimeType(file))
  protected def setDateAndCacheHeaders(response: HttpResponse, file: File){
    val formatter = new SimpleDateFormat(this.HTTP_DATE_FORMAT, Locale.US)
    formatter.setTimeZone(TimeZone.getTimeZone(this.HTTP_DATE_GMT_TIMEZONE))
    val time = new GregorianCalendar() //TODO: nexus time!
    response.headers().set(HttpHeaders.Names.DATE, formatter.format(time.getTime))

    time.add(Calendar.SECOND, this.HTTP_CACHE_SECONDS)
    response.headers().set(HttpHeaders.Names.EXPIRES, formatter.format(time.getTime))
    response.headers().set(HttpHeaders.Names.CACHE_CONTROL, "private, max-age=" + this.HTTP_CACHE_SECONDS)
    response.headers().set(HttpHeaders.Names.LAST_MODIFIED, formatter.format(new Date(file.lastModified())))
  }
}
