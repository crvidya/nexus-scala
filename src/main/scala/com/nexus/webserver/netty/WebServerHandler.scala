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

package com.nexus.webserver.netty

import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http._
import io.netty.handler.codec.http.websocketx.WebSocketFrame
import io.netty.buffer.Unpooled
import io.netty.util.CharsetUtil
import com.nexus.webserver.annotation.Authenticated
import com.nexus.webserver.handlers.WebServerHandlerWebsocket
import io.netty.handler.ssl.NotSslRecordException
import com.nexus.webserver.{WebServerHandlerFactory, TWebServerHandler}

/**
 * TODO: Edit description
 *
 * @author jk-5
 */
class WebServerHandler extends SimpleChannelInboundHandler[AnyRef] {
  private var handler: TWebServerHandler = _
  private var request: HttpRequest = _
  override def channelReadComplete(ctx: ChannelHandlerContext) = ctx.flush() //FIXME!
  override def channelRead0(ctx: ChannelHandlerContext, msg: AnyRef) = {
    if(msg.isInstanceOf[FullHttpRequest]) this.handleHttpRequest(ctx, msg.asInstanceOf[FullHttpRequest])
    else if(msg.isInstanceOf[WebSocketFrame]) this.handleWebSocketFrame(ctx, msg.asInstanceOf[WebSocketFrame])
  }
  def handleHttpRequest(ctx: ChannelHandlerContext, req: FullHttpRequest) =
    if(!req.getDecoderResult.isSuccess) this.sendHttpResponse(ctx, req, new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.BAD_REQUEST))
    else{
      println(req.headers().get(HttpHeaders.Names.AUTHORIZATION))
      if(req.headers().contains(HttpHeaders.Names.AUTHORIZATION)){
        val data = req.headers().get(HttpHeaders.Names.AUTHORIZATION)
        println(data)
      }

      this.handler = WebServerHandlerFactory.handleRequest(ctx, req)
      if(this.handler != null){
        if(this.handler.getClass.isAnnotationPresent(classOf[Authenticated])){
          //TODO: do authentication stuff
        }
        this.handler.handleRequest(ctx, req)
      }else{
        val res = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.NOT_FOUND) //TODO: json response
        val buf = Unpooled.copiedBuffer(res.getStatus.toString, CharsetUtil.UTF_8)
        res.content().writeBytes(buf)
        buf.release()
        HttpHeaders.setContentLength(res, res.content().readableBytes())
        val f = ctx.channel().write(res)
        if(!HttpHeaders.isKeepAlive(req) || res.getStatus.code() != 200) f.addListener(ChannelFutureListener.CLOSE)
      }
    }
  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable) {
    if(cause.isInstanceOf[NotSslRecordException]){
      //this.sendRedirect(ctx, )
      //TODO: redirect to SSL
    }
  }

  private def handleWebSocketFrame(ctx: ChannelHandlerContext, frame: WebSocketFrame) =
    if(this.handler.isInstanceOf[WebServerHandlerWebsocket]) this.handler.asInstanceOf[WebServerHandlerWebsocket].handleWebSocketFrame(ctx, frame)

  private def sendHttpResponse(ctx: ChannelHandlerContext, req: FullHttpRequest, res: FullHttpResponse){
    if(res.getStatus.code() != 200){
      val buf = Unpooled.copiedBuffer(res.getStatus.toString, CharsetUtil.UTF_8) //TODO: json response
      res.content().writeBytes(buf)
      buf.release()
      HttpHeaders.setContentLength(res, res.content().readableBytes())
    }
    val f = ctx.channel().write(res)
    if(!HttpHeaders.isKeepAlive(req) || res.getStatus.code() != 200) f.addListener(ChannelFutureListener.CLOSE)
  }
  private def sendRedirect(ctx: ChannelHandlerContext, destination: String){
    val response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.FOUND)
    response.headers().set(HttpHeaders.Names.LOCATION, destination)
    ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE)
  }
}
