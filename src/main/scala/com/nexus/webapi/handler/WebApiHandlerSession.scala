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

package com.nexus.webapi.handler

import com.nexus.webapi.TWebApiHandler
import com.nexus.webserver.WebServerRequest
import com.nexus.data.json.JsonObject
import io.netty.handler.codec.http.HttpMethod
import io.netty.handler.codec.http.multipart.InterfaceHttpData
import io.netty.handler.codec.http.multipart.Attribute
import com.nexus.authentication.{SessionManager, UserDatabase}
import com.nexus.errorhandling.JsonError

/**
 * No description given
 *
 * @author jk-5
 */
class WebApiHandlerSession extends TWebApiHandler {
  private final val RESPONSE_OBJECT = new JsonObject().set("auth", "ok")

  def handle(request: WebServerRequest): JsonObject = {
    if(request.getMethod == HttpMethod.POST){
      val usernameField = request.getPostData.getBodyHttpData("username")
      val passwordField = request.getPostData.getBodyHttpData("password")
      val tfaKeyField = request.getPostData.getBodyHttpData("tfaKey")
      var tfaProvided = false
      var tfaKey: Option[Long] = None
      if(usernameField == null) JsonError.LOGIN_USERNAME_UNDEFINED.throwException()
      if(passwordField == null) JsonError.LOGIN_PASSWORD_UNDEFINED.throwException()
      if(tfaKeyField != null && tfaKeyField.getHttpDataType == InterfaceHttpData.HttpDataType.Attribute){
        tfaProvided = true
        val value = tfaKeyField.asInstanceOf[Attribute].getValue
        var parsed: Long = 0L
        try{
          parsed = value.toLong
        }catch{
          case e: NumberFormatException => JsonError.LOGIN_TFAKEY_NONUMBER.throwException()
        }
        tfaKey = Some(parsed)
      }
      var username: Option[String] = None
      var password: Option[String] = None
      if(usernameField.getHttpDataType == InterfaceHttpData.HttpDataType.Attribute){
        username = Some(usernameField.asInstanceOf[Attribute].getValue)
      }
      if(passwordField.getHttpDataType == InterfaceHttpData.HttpDataType.Attribute){
        password = Some(passwordField.asInstanceOf[Attribute].getValue)
      }
      if(username.isEmpty) JsonError.LOGIN_USERNAME_UNDEFINED.throwException()
      if(password.isEmpty) JsonError.LOGIN_PASSWORD_UNDEFINED.throwException()

      val user = UserDatabase.getUser(username.get)
      if(user.isEmpty) JsonError.LOGIN_USERNAME_WRONG.throwException()
      if(!SessionManager.checkPassword(user.get, password.get)) JsonError.LOGIN_PASSWORD_WRONG.throwException()
      if(user.get.getTfaData.isEnabled){
        if(!tfaProvided) JsonError.LOGIN_TFAKEY_UNDEFINED.throwException()
        val valid = user.get.getTfaData.getProtocol.checkKey(user.get, tfaKey.get)
        if(!valid) JsonError.LOGIN_TFAKEY_WRONG.throwException()
      }
      val session = SessionManager.getSession(user.get, password.get)
      if(session.isEmpty) JsonError.LOGIN_PASSWORD_WRONG.throwException()
      return this.RESPONSE_OBJECT.set("session", session.get.toJson)
    }
    JsonError.UNHANDLED_METHOD.throwException()
    null
  }
}
