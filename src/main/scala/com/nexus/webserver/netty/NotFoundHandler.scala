package com.nexus.webserver.netty

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.{HttpResponseStatus, FullHttpRequest}
import com.nexus.webserver.WebServerUtils

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
object NotFoundHandler extends SimpleChannelInboundHandler[FullHttpRequest] {

  def channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    println("NOT FOUND " + msg.getUri)
    WebServerUtils.sendError(ctx, HttpResponseStatus.NOT_FOUND)
  }
}
