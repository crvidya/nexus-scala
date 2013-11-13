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

package com.nexus.authentication.tfa

import com.nexus.data.json.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
class TFAStorage(private var protocol: TFAProtocol = null){

  private var enabled = false
  private var secret: String = _

  def readFromJson(json: JsonObject){
    this.enabled = json.get("enabled").asBoolean
    this.secret = json.get("secret").asString
  }
  def writeToJson(): JsonObject = new JsonObject().add("enabled", this.enabled).add("protocol", this.protocol.getName).add("secret", this.secret)

  @inline def getProtocol = this.protocol
  @inline def getSecret = this.secret
  @inline def isEnabled = this.enabled

  @inline def setEnabled(enabled: Boolean) = this.enabled = enabled
  @inline def setProtocol(protocol: TFAProtocol) = this.protocol = protocol
}
