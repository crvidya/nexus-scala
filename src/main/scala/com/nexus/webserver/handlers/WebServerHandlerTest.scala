package com.nexus.webserver.handlers

import io.netty.channel.{ChannelHandlerContext, SimpleChannelInboundHandler}
import io.netty.handler.codec.http.{HttpResponseStatus, FullHttpRequest}
import com.nexus.webserver.netty.RoutedHandler
import com.nexus.webserver.WebServerUtils

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerHandlerTest extends SimpleChannelInboundHandler[FullHttpRequest] with RoutedHandler {
  def channelRead0(ctx: ChannelHandlerContext, msg: FullHttpRequest){
    WebServerUtils.sendError(ctx, HttpResponseStatus.OK)
  }
}
