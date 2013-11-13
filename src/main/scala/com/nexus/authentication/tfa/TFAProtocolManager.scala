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

import scala.collection.immutable
import com.nexus.authentication.tfa.protocols.totp.TOTPProtocol
import com.nexus.data.json.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
object TFAProtocolManager {
  private val protocols = immutable.List[TFAProtocol]{TOTPProtocol}

  def getProtocol(name: String) = this.protocols.find(_.getName == name)

  def read(json: JsonObject): TFAStorage = {
    val ret = new TFAStorage()
    val protocol = this.getProtocol(json.get("protocol").asString)
    if(protocol.isEmpty) ret.setEnabled(enabled = false)
    else{
      ret.setProtocol(protocol.get)
      ret.readFromJson(json)
    }
    ret
  }
}
