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

package com.nexus.network

import io.netty.handler.codec.ByteToMessageDecoder
import io.netty.channel.ChannelHandlerContext
import io.netty.buffer.ByteBuf
import java.util
import io.netty.handler.ssl.SslHandler
import io.netty.handler.codec.compression.{ZlibWrapper, ZlibCodecFactory}
import io.netty.handler.codec.http.{HttpContentCompressor, HttpRequestDecoder, HttpResponseEncoder}

/**
 * This class indentifies the protocols of requests and tries to redirect it to the proper handlers
 * 
 * @author jk-5
 */
class PortUnificationHandler(detectSsl: Boolean, detectGzip: Boolean) extends ByteToMessageDecoder {
	override protected def decode(ctx: ChannelHandlerContext, in: ByteBuf, out: util.List[AnyRef]) {
		if(in.readableBytes() < 5) return
		if(this.isSsl(in)) this.enableSsl(ctx)
		else{
			val byte1 = in.getUnsignedByte(in.readerIndex())
			val byte2 = in.getUnsignedByte(in.readerIndex() + 1)
			if(this.isGzip(byte1, byte2)) this.enableGzip(ctx)
			else if(this.isHttp(byte1, byte2)) this.switchToHttp(ctx)
			else{
				//Unknown protocol; discard everything and close the connection
				in.clear()
				ctx.close()
			}
		}
	}
	private def isSsl(buf: ByteBuf):Boolean =  if(this.detectSsl) SslHandler.isEncrypted(buf) else false
	private def isGzip(byte1: Int, byte2: Int):Boolean = if(this.detectGzip) (byte1 == 31 && byte2 == 139) else false
	private def isHttp(byte1: Int, byte2: Int):Boolean =
		byte1 == 'G' && byte2 == 'E' || //GET
		byte1 == 'P' && byte2 == 'O' || //POST
		byte1 == 'P' && byte2 == 'U' || //PUT
		byte1 == 'H' && byte2 == 'E' || //HEAD
		byte1 == 'O' && byte2 == 'P' || //OPTIONS
		byte1 == 'P' && byte2 == 'A' || //PATCH
		byte1 == 'D' && byte2 == 'E' || //DELETE
		byte1 == 'T' && byte2 == 'R' || //TRACE
		byte1 == 'C' && byte2 == 'O'    //CONNECT
	private def enableSsl(ctx: ChannelHandlerContext) {
		val pipe = ctx.pipeline()
		val engine = SslContextProvider.getContext.createSSLEngine()
		engine.setUseClientMode(false)

		pipe.addLast("ssl", new SslHandler(engine))
		pipe.addLast("unificationA", new PortUnificationHandler(false, this.detectGzip))
		pipe.remove(this)
	}
	private def enableGzip(ctx: ChannelHandlerContext) {
		val pipe = ctx.pipeline()
		pipe.addLast("gzipdeflater", ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP))
		pipe.addLast("gzipinflater", ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP))
		pipe.addLast("unificationB", new PortUnificationHandler(this.detectSsl, false))
		pipe.remove(this)
	}
	private def switchToHttp(ctx: ChannelHandlerContext) {
		val pipe = ctx.pipeline()
		pipe.addLast("decoder", new HttpRequestDecoder)
		pipe.addLast("encoder", new HttpResponseEncoder)
		pipe.addLast("deflater", new HttpContentCompressor)
		pipe.addLast("handler", new NetworkHandler)
	}
}
