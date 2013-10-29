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
    this.ssl = Nexus.getConfig.getTag("database").useBraces.setComment("Database options").getTag("ssl").setComment("Should we use SSL?").getBooleanValue(false)
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
}
