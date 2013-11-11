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

import com.nexus.data.couchdb.{DatabaseType, UID, TCouchDBSerializable}
import com.nexus.data.json.JsonObject
import java.util.Date
import com.nexus.time.NexusTime

/**
 * No description given
 *
 * @author jk-5
 */
@DatabaseType("session")
class AuthSession(private var userID: UID) extends TCouchDBSerializable {

  private var created = NexusTime.getCurrentDate

  protected def writeToJsonForDB(data: JsonObject){
    data.add("userID", this.userID.toString)
    data.add("created", this.created.getTime)
  }
  protected def readFromJsonForDB(data: JsonObject){
    this.userID = new UID(data.get("userID").asString)
    this.created = new Date(data.get("created").asLong)
  }

  def toJson = new JsonObject().add("id", this.getID.toString).add("created", this.created.getTime)
}
