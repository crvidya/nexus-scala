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

package com.nexus.authentication

import com.nexus.data.couchdb.{DatabaseType, TCouchDBSerializable}
import com.nexus.data.json.JsonObject
import com.nexus.authentication.tfa.{TFAProtocolManager, TFAStorage}

/**
 * No description given
 *
 * @author jk-5
 */
@DatabaseType("user")
case class User(private var username: String) extends TCouchDBSerializable {

  private var passwordHash: String = _
  private var tfa: TFAStorage = _

  def writeToJsonForDB(data: JsonObject){
    data.add("tfa", this.tfa.writeToJson())
    data.add("username", this.username)
    data.add("passwordHash", this.passwordHash)
  }

  def readFromJsonForDB(data: JsonObject){
    this.tfa = TFAProtocolManager.read(data.get("tfa").asObject)
    this.username = data.get("username").asString
    this.passwordHash = data.get("passwordHash").asString
  }

  @inline def getUsername = this.username
  @inline def getPasswordHash = this.passwordHash
  @inline def getTfaData = this.tfa
}
