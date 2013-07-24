package com.nexus.network

import io.netty.util.internal.TypeParameterMatcher
import io.netty.util.ReferenceCountUtil
import io.netty.channel.ChannelHandlerContext

abstract class NetworkChannelHandler {
	def channelRead(ctx:ChannelHandlerContext, msg:Any){
		
	}
	/*def handleRequest(request:WebServerRequest,response:WebServerResponse){
		response.
	}*/
}
