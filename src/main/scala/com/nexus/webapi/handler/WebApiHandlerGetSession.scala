package com.nexus.webapi.handler

import com.nexus.webapi.TWebApiHandler
import com.nexus.webserver.WebServerRequest
import com.nexus.data.json.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
class WebApiHandlerGetSession extends TWebApiHandler {

  def handle(request: WebServerRequest): JsonObject = {
    new JsonObject().add("secret", "12345").add("username", request.getParameter("username").getOrElse("")).add("password", request.getParameter("password").getOrElse(""))
  }
}
