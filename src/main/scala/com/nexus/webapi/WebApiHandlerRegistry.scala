package com.nexus.webapi

import scala.collection.mutable
import com.nexus.webapi.handler.WebApiHandlerGetSession

/**
 * No description given
 *
 * @author jk-5
 */
object WebApiHandlerRegistry {

  private final val handlers = mutable.LinkedHashMap[String, TWebApiHandler](
    "/getSession/" -> new WebApiHandlerGetSession
  )
  def getHandlers = this.handlers
}
