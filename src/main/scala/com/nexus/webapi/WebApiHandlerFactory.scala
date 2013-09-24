package com.nexus.webapi

import com.nexus.webserver.{WebServerRequest, WebServerResponse}
import com.nexus.util.Utils
import java.util.regex.Pattern
import scala.collection.mutable.ListBuffer

/**
 * No description given
 *
 * @author jk-5
 */
object WebApiHandlerFactory {

  def handleRequest(request: WebServerRequest, response: WebServerResponse): TWebApiHandler = {
    try{
      var handler: TWebApiHandler = null
      val args = ListBuffer[String]()
      var breakIterator = false
      for(e: (String, TWebApiHandler) <- WebApiHandlerRegistry.getHandlers if !breakIterator){
        var path = Utils.sanitizeURI(request.getPath)
        val regex = Pattern.compile(e._1)
        val matcher = regex.matcher(path)
        if(matcher.find()){
          var i = 1
          var breakLoop = false
          while(!breakLoop) try{
            val res = path.replaceAll(e._1, "$" + i)
            if(res.equals("$" + i)) breakLoop = true
            args += res
            i += 1
          }catch{
            case e:IndexOutOfBoundsException => breakLoop = true
          }
          path = matcher.group()
          handler = e._2
          breakIterator = true
        }
      }
      return handler
    }
    null
  }
}
