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

import com.nexus.data.couchdb.CouchDB
import com.nexus.data.json.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
object UserDatabase {

  def getUser(username: String): Option[User] = {
    val data = CouchDB.getViewData("users", "byUsername").get()
    val json = JsonObject.readFrom(data.getResponseBody).get("rows").asArray
    val userData = json.getValues.map(_.asObject).find(_.get("key").asString == username).map(_.get("value").asObject)
    if(userData.isDefined){
      val u = new User(username)
      u.readDB(userData.get)
      Some(u)
    } else None
  }
}
