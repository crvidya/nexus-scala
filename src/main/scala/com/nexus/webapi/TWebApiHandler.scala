package com.nexus.webapi

import com.nexus.data.json.JsonObject
import com.nexus.webserver.WebServerRequest

/**
 * No description given
 *
 * @author jk-5
 */
trait TWebApiHandler {

  def handle(request: WebServerRequest): JsonObject
  override def toString: String = this.getClass.getSimpleName
}
