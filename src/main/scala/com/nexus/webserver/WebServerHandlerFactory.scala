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

import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http._
import java.util
import com.google.common.collect.Lists
import com.nexus.util.Utils
import java.util.regex.Pattern
import scala.collection.JavaConversions._

/**
 * TODO: Edit description
 *
 * @author jk-5
 */
object WebServerHandlerFactory {
  def handleRequest(ctx: ChannelHandlerContext, req: FullHttpRequest): TWebServerHandler = {
    try{
      var handler:TWebServerHandler = null
      val args: util.List[String] = Lists.newArrayList()
      var breakIterator = false
      for(e: util.Map.Entry[String, TWebServerHandler] <- WebServerHandlerRegistry.getHandlers.entrySet() if !breakIterator){
        var path = Utils.sanitizeURI(req.getUri)
        val regex = Pattern.compile(e.getKey)
        val matcher = regex.matcher(path)
        if(matcher.find()){
          var i = 1
          var breakLoop = false
          while(!breakLoop) try{
            val res = path.replaceAll(e.getKey, "$" + i)
            if(res.equals("$" + i)) breakLoop = true
            args.add(res)
            i += 1
          }catch{
            case e:IndexOutOfBoundsException => breakLoop = true
          }
          path = matcher.group()
          handler = e.getValue
          breakIterator = true
        }
      }
      return handler
    }
    null
  }
}
