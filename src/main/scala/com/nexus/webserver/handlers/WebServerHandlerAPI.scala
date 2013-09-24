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

import com.nexus.webserver.{WebServerResponse, WebServerRequest, TWebServerHandler}
import com.nexus.webapi.{TWebApiHandler, WebApiHandlerFactory}
import com.nexus.data.json.JsonObject
import io.netty.handler.codec.http.HttpResponseStatus
import com.nexus.errorhandling.{ReportedException, ErrorHandler, ErrorReportCategory, ErrorReport}

/**
 * No description given
 *
 * @author jk-5
 */
class WebServerHandlerAPI extends TWebServerHandler {

  override def handle(request: WebServerRequest, response: WebServerResponse){
    var handler: TWebApiHandler = null
    try{
      handler = WebApiHandlerFactory.handleRequest(request, response)
      val data = handler.handle(request)
      val ret = new JsonObject().addError("none").add("data", data)
      response.sendHeaders(HttpResponseStatus.OK)
      response.sendData(ret)
      response.close()
    }catch{
      case e: Exception => {
        response.sendHeaders(HttpResponseStatus.INTERNAL_SERVER_ERROR)
        response.sendError("Error while processing request")
        response.close()
        val report = new ErrorReport("Error while processing WebApi request", e)
        val category = new ErrorReportCategory(report, "Client being processed")
        report.addCategory(category)
        category.addSection("WebApi function", handler)
        category.addSection("Hostname", request.getAddress.getHostName)
        category.addSection("Address", request.getAddress.getHostAddress)
        ErrorHandler.unexpectedException(new ReportedException(report))
      }
    }
  }
}
