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

package com.nexus.network.handlers

import java.io.{FileNotFoundException, RandomAccessFile, File}

import com.nexus.network.{SslContextProvider, NetworkChannelHandler}

import io.netty.channel._
import io.netty.handler.codec.http._
import com.nexus.util.Utils
import io.netty.handler.stream.ChunkedFile
import com.nexus.logging.NexusLog

object WebServerHandlerHtml extends NetworkChannelHandler {

	private final val buffer = new StringBuffer
  private final val htdocsLocation = if(System.getProperty("nexus.webserver.htdocslocation", "htdocs/").endsWith("/")) System.getProperty("nexus.webserver.htdocslocation", "htdocs/") else System.getProperty("nexus.webserver.htdocslocation", "htdocs/") + "/"
	
	override def channelRead(ctx:ChannelHandlerContext, msg:Any){
		if(!msg.isInstanceOf[HttpRequest]) return
		val request = msg.asInstanceOf[HttpRequest]
		if(!request.getDecoderResult.isSuccess()){
			this.sendError(HttpResponseStatus.BAD_REQUEST, ctx)
			return
		}
		if(request.getMethod() != HttpMethod.GET){
			this.sendError(HttpResponseStatus.METHOD_NOT_ALLOWED, ctx)
			return
		}
		val uri = request.getUri()
		val path = htdocsLocation + this.sanitizeURI(uri)
		if(path == null){
			this.sendError(HttpResponseStatus.FORBIDDEN, ctx)
			return
		}
		val file = new File(path)
    NexusLog.info(file.getAbsolutePath)
		if(file.isHidden() || !file.exists()){
			this.sendError(HttpResponseStatus.NOT_FOUND, ctx)
			return
	  }
		if(file.isDirectory()){
			if(uri.endsWith("/")){
				this.sendFileList(file, ctx)
			}else{
				this.sendRedirect(uri + '/', ctx)
			}
      return
		}
    if (!file.isFile){
      this.sendError(HttpResponseStatus.FORBIDDEN, ctx)
      return
    }
    var raf:RandomAccessFile = null
    try{
      raf = new RandomAccessFile(file, "r")
    }catch{
      case e:FileNotFoundException => {
        this.sendError(HttpResponseStatus.NOT_FOUND, ctx)
        return
      }
    }
    NexusLog.info("1")
    val fileLength = raf.length()
    val response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)
    response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, fileLength)
    response.headers().set(HttpHeaders.Names.CONTENT_TYPE, Utils.getMimeType(file))
    this.setDateAndCacheHeaders(file, response)
    if (HttpHeaders.isKeepAlive(request)) response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
    ctx.write(response)
    NexusLog.info("2")

    var sendFuture:ChannelFuture = null
    if (SslContextProvider.isValid){
      sendFuture = ctx.write(new DefaultFileRegion(raf.getChannel, 0, fileLength), ctx.newProgressivePromise())
    }else{
      sendFuture = ctx.write(new ChunkedFile(raf, 0, fileLength, 8192), ctx.newProgressivePromise())
    }
    sendFuture.addListener(new ChannelProgressiveFutureListener {
      override def operationProgressed(future: ChannelProgressiveFuture, progress: Long, total: Long) {
        if (total < 0){
          println("Transfer progress " + progress)
        }else{
          println("Transfer progress" + progress + "/" + total)
        }
      }

      override def operationComplete(future: ChannelProgressiveFuture) {
        println("Complete")
      }
    })
    val lastFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)
    if (!HttpHeaders.isKeepAlive(request)) lastFuture.addListener(ChannelFutureListener.CLOSE)
	}
}
