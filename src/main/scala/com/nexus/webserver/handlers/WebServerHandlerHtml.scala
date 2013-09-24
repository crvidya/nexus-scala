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

package com.nexus.webserver.handlers

import com.nexus.webserver.{SslContextProvider, TWebServerHandler}
import io.netty.channel._
import io.netty.handler.codec.http._
import com.nexus.util.Utils
import java.io.{FileNotFoundException, RandomAccessFile, File}
import java.util.Locale
import java.text.SimpleDateFormat
import io.netty.handler.stream.ChunkedFile

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerHandlerHtml extends TWebServerHandler {
  private final val useSendFile = SslContextProvider.isValid
  private final val htdocs = System.getProperty("nexus.webserver.htdocslocation", "htdocs")
  private final val htdocsLocation = if(htdocs.endsWith("/")) htdocs.substring(0,htdocs.length -1) else htdocs

  override def handleRequest(ctx: ChannelHandlerContext, req: FullHttpRequest){
    if(req.getMethod != HttpMethod.GET){
      this.sendError(ctx, HttpResponseStatus.BAD_REQUEST)
      return
    }
    val uri = req.getUri.split("\\?", 2)(0)
    val path = htdocsLocation + Utils.sanitizeURI(uri)
    if(path == null){
      this.sendError(ctx, HttpResponseStatus.FORBIDDEN)
      return
    }
    var file = new File(path)
    if(file.isDirectory){
      val index = new File(file, "index.html")
      if(index.exists() && index.isFile) file = index
    }
    if(file.isHidden || !file.exists){
      this.sendError(ctx, HttpResponseStatus.NOT_FOUND)
      return
    }
    if(file.isDirectory){
      if(uri.endsWith("/")) {
        //this.sendFileList(ctx, file) //TODO: file list?
      }else this.sendRedirect(ctx, uri + "/")
      return
    }
    if(!file.isFile){
      this.sendError(ctx, HttpResponseStatus.FORBIDDEN)
      return
    }
    val ifModifiedSince = req.headers().get(HttpHeaders.Names.IF_MODIFIED_SINCE)
    if(ifModifiedSince != null && !ifModifiedSince.isEmpty){
      val dateFormatter = new SimpleDateFormat(this.HTTP_DATE_FORMAT, Locale.US)
      val ifModifiedSinceDate = dateFormatter.parse(ifModifiedSince)
      val ifModifiedSinceDateSeconds = ifModifiedSinceDate.getTime / 1000
      val fileLastModifiedSeconds = file.lastModified() / 1000
      if(ifModifiedSinceDateSeconds == fileLastModifiedSeconds){
        this.sendNotModified(ctx)
        return
      }
    }
    var raf:RandomAccessFile = null
    try{
      raf = new RandomAccessFile(file, "r")
    }catch{
      case e: FileNotFoundException => {
        this.sendError(ctx, HttpResponseStatus.NOT_FOUND)
        return
      }
    }
    val fileLength = raf.length()
    val response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK)

    this.setContentLength(response, fileLength)
    this.setContentType(response, file)
    this.setDateAndCacheHeaders(response, file)
    this.setDefaultHeaders(response)
    if(HttpHeaders.isKeepAlive(req)) response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE)
    ctx.write(response)

    var sendFileFuture: ChannelFuture = null
    if(this.useSendFile) sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel, 0, fileLength), ctx.newProgressivePromise())
    else sendFileFuture = ctx.write(new ChunkedFile(raf, 0, fileLength, 8192), ctx.newProgressivePromise())

    val lastContentFuture = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT)
    if(!HttpHeaders.isKeepAlive(req)) lastContentFuture.addListener(ChannelFutureListener.CLOSE)
  }
}
