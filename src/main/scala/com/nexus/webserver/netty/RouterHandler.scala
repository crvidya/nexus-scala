package com.nexus.webserver.netty

import io.netty.channel.ChannelHandler.Sharable
import io.netty.channel._
import io.netty.handler.codec.http.FullHttpRequest
import com.nexus.util.{URLData, MultiplexingURLResolver}
import scala.collection.JavaConversions._
import java.util.Map.Entry
import io.netty.handler.codec.MessageToMessageDecoder
import java.util

/**
 * No description given
 *
 * @author jk-5
 */
@Sharable
class RouterHandler(private val resolver: MultiplexingURLResolver, private val handlerName: String) extends MessageToMessageDecoder[FullHttpRequest] {

  def decode(ctx: ChannelHandlerContext, msg: FullHttpRequest, out: util.List[AnyRef]){
    println(msg.getUri)
    val url = msg.getUri
    val urlData = this.resolver.getValueForURL(url)
    if(urlData.isEmpty) ctx.pipeline().replace(this.handlerName, this.handlerName, NotFoundHandler)
    else{
      println("found")
      val handler = urlData.get.getHandler.newInstance()
      handler match{
        case h: RoutedHandler => {
          println(h.getClass.getName)
          h.setURLData(urlData.get)
          h.setRouterHandler(this)
        }
      }
      ctx.pipeline().replace(this.handlerName, this.handlerName, handler)
      println("replaced, is now " + handler.getClass.getName)
    }
    //ctx.fireChannelRead(msg)
    out.add(msg)
  }
}

trait RoutedHandler extends ChannelHandler {
  private var _urlData: URLData = _
  private var _routerHandler: RouterHandler = _

  def setRouterHandler(handler: RouterHandler) = this._routerHandler = handler
  def getRouterHandler = this._routerHandler
  def setURLData(urlData: URLData) = this._urlData = urlData
  def getURLData = this._urlData
}
