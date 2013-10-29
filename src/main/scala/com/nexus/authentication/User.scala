package com.nexus.authentication

import com.nexus.data.couchdb.TCouchDBSerializable
import com.nexus.data.json.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
case class User(private var username: String) extends TCouchDBSerializable {

  this.setDatabaseType("user")

  def writeToJsonForDB(data: JsonObject){
    data.add("username", this.username)
  }

  def readFromJsonForDB(data: JsonObject){
    this.username = data.get("username").asString
  }

  def getUsername = this.username
}
