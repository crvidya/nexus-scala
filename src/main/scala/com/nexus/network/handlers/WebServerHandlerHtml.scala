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
