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

import com.google.common.collect.Maps
import java.util.{Map => JMap, List => JList}
import com.nexus.traits.TLoader
import com.nexus.webserver.handlers.{WebServerHandlerTest, WebServerHandlerWebsocket, WebServerHandlerHtml}
import com.nexus.logging.NexusLog

/**
 * No description given
 *
 * @author jk-5
 */
object WebServerHandlerRegistry extends TLoader {
  private final val handlers: JMap[String, TWebServerHandler] = Maps.newLinkedHashMap()
  def getHandlers = this.handlers
  override def load{
    this.handlers.put("/websocket/", new WebServerHandlerWebsocket("/websocket"))
    this.handlers.put("/test/(.*)", new WebServerHandlerTest)
    this.handlers.put("/(.*)", new WebServerHandlerHtml)
    NexusLog.info("Registered %d webserver handler%s".format(this.handlers.size(), if(this.handlers.size() != 1) "s"))
  }
}
