package com.nexus.util

import scala.collection.mutable
import io.netty.channel.ChannelHandler

/**
 * No description given
 *
 * @author jk-5
 */
class MultiplexingURLResolver {
  private final val handlers = mutable.HashMap[String, Class[_ <: ChannelHandler]]()

  def addURLPattern(pattern: String, handler: Class[_ <: ChannelHandler]) = this.handlers.put(pattern, handler)
  def getValueForURL(url: String): Option[URLData] = {
    var u = url
    if(u.endsWith("/")) u = u.substring(0, u.length - 1)

    var keyopt = this.getKeyForExactMatch(u)
    if(keyopt.isDefined) return Some(new URLData(u, u, mutable.HashMap[String, String](), this.handlers.get(u).get))
    getValueForUrlWithParameters(u)
  }

  private def getKeyForExactMatch(url: String): Option[String] = this.handlers.keySet.find(_.equals(url))
  private def getValueForUrlWithParameters(url: String): Option[URLData] = {
    this.handlers.keySet.filter(h => h.contains("{") && h.contains("}")).foreach(pattern => {
      var doesMatch = false
      val properties = mutable.HashMap[String, String]()
      val urlParts = url.split("/")
      val currUrlParts = pattern.split("/")
      if(urlParts.length == currUrlParts.length){
        for(i <- 0 until currUrlParts.length){
          val part = currUrlParts(i)
          if(part.startsWith("{") && part.endsWith("}")){
            properties.put(part.substring(1, part.length - 1), urlParts(i))
          }else doesMatch = false

          if(doesMatch) return Some(new URLData(pattern, url, properties, this.handlers.get(pattern).get))
        }
      }
    })
    None
  }
}
