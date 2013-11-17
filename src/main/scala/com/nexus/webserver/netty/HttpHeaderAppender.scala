package com.nexus.webserver.netty

import io.netty.handler.codec.MessageToMessageEncoder
import io.netty.handler.codec.http.HttpResponse
import io.netty.channel.ChannelHandlerContext
import java.util
import com.nexus.webserver.WebServerUtils

/**
 * No description given
 *
 * @author jk-5
 */
object HttpHeaderAppender extends MessageToMessageEncoder[HttpResponse] {
  def encode(ctx: ChannelHandlerContext, msg: HttpResponse, out: util.List[AnyRef]) = {
    WebServerUtils.setDefaultHeaders(msg)
    out.add(msg)
  }
}
