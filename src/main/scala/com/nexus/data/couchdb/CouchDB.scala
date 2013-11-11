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

package com.nexus.data.couchdb

import com.nexus.traits.TLoader
import com.nexus.Nexus
import com.nexus.data.json.JsonObject
import com.ning.http.client.{ListenableFuture, RequestBuilder, AsyncHttpClient, Response}
import io.netty.handler.codec.http.HttpHeaders

/**
 * No description given
 *
 * @author jk-5
 */
object CouchDB extends TLoader{
  private var serverHostname: String = _
  private var serverPort: Int = _
  private var databaseName: String = _
  private var ssl: Boolean = false

  def newID = UID.randomUID

  def load(){
    this.serverHostname = Nexus.getConfig.getTag("database").useBraces.setComment("Database options").getTag("hostname").setComment("The IP/Address for the couchdb server").getValue("localhost")
    this.serverPort = Nexus.getConfig.getTag("database").useBraces.setComment("Database options").getTag("port").setComment("The port for the couchdb server").getIntValue(5984)
    this.databaseName = Nexus.getConfig.getTag("database").useBraces.setComment("Database options").getTag("name").setComment("The name of the couchdb database that Nexus will use").getValue("nexus")
    this.ssl = Nexus.getConfig.getTag("database").useBraces.setComment("Database options").getTag("ssl").setComment("Should we use SSL?").getBooleanValue(default = false)
  }

  def getObjectFromID[T <: TCouchDBSerializable](id: UID, obj: T): T = {
    obj.setID(id)
    obj.refreshFromDatabase()
    obj
  }

  def updateDocument(id: UID, data: JsonObject): ListenableFuture[Response] = {
    assert(id != null)
    assert(data != null)
    val builder = new RequestBuilder("PUT")
    builder.setUrl((if(this.ssl) "https://" else "http://") + this.serverHostname + ":" + this.serverPort + "/" + this.databaseName + "/" + id.toString)
    builder.setHeader(HttpHeaders.Names.CONTENT_TYPE, "application/json")
    builder.setBody(data.stringify)
    val request = builder.build()
    val client = new AsyncHttpClient()
    client.executeRequest(request)
  }

  def getDocument(id: UID): ListenableFuture[Response] = {
    val builder = new RequestBuilder("GET")
    builder.setUrl((if(this.ssl) "https://" else "http://") + this.serverHostname + ":" + this.serverPort + "/" + this.databaseName + "/" + id.toString)
    val request = builder.build()
    val client = new AsyncHttpClient()
    client.executeRequest(request)
  }

  def getViewData(viewGroup: String, viewName: String): ListenableFuture[Response] = {
    val builder = new RequestBuilder("GET")
    builder.setUrl((if(this.ssl) "https://" else "http://") + this.serverHostname + ":" + this.serverPort + "/" + this.databaseName + "/_design/" + viewGroup + "/_view/" + viewName)
    val request = builder.build()
    val client = new AsyncHttpClient()
    client.executeRequest(request)
  }
}
