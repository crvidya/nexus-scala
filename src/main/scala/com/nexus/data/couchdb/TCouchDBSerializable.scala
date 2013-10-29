package com.nexus.data.couchdb

import com.nexus.data.json.JsonObject

/**
 * No description given
 *
 * @author jk-5
 */
trait TCouchDBSerializable {

  private var _databaseType: String = this.getClass.getSimpleName
  private var _databaseId: UID = null
  private var _databaseRevision: String = null
  private var _existsInDatabase: Boolean = false

  protected final def setDatabaseType(typ: String) = this._databaseType = typ

  private final def writeDB(data: JsonObject){
    if(this._existsInDatabase) data.add("_id", this._databaseId.toString)
    if(this._existsInDatabase) data.add("_rev", this._databaseRevision)
    data.add("type", this._databaseType)
    this.writeToJsonForDB(data)
  }
  private final def readDB(data: JsonObject){
    if(this._databaseId == null) this._databaseId = new UID(data.get("_id").asString)
    this._databaseRevision = data.get("_rev").asString
    this.readFromJsonForDB(data)
  }

  protected def writeToJsonForDB(data: JsonObject)
  protected def readFromJsonForDB(data: JsonObject)

  def saveToDatabase(){
    if(this._databaseId == null) this._databaseId = CouchDB.newID
    val data = new JsonObject
    this.writeDB(data)
    val response = CouchDB.updateDocument(this._databaseId, data).get()
    val resData = JsonObject.readFrom(response.getResponseBody)
    if(resData.get("ok").asBoolean) this._databaseRevision = resData.get("rev").asString
    this._existsInDatabase = true
  }

  def refreshFromDatabase(){
    val response = CouchDB.getDocument(this._databaseId).get()
    val resData = JsonObject.readFrom(response.getResponseBody)
    this.readDB(resData)
    this._existsInDatabase = true
  }

  final def getID = this._databaseId
  private [couchdb] final def setID(id: UID){
    if(this._databaseId != null) throw new IllegalStateException("The database id is already set!")
    this._databaseId = id
  }
}
