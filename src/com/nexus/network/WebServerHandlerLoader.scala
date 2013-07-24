package com.nexus.network

import com.nexus.traits.TLoader
import com.nexus.network.handlers.WebServerHandlerHtml

object WebServerHandlerLoader extends TLoader {

	override def load{
		NetworkHandler.addHandler("/test/", WebServerHandlerHtml);
	}
}
