package com.nexus.data.couchdb

import com.nexus.traits.TLoader
import com.nexus.Nexus

/**
 * TODO: Edit description
 *
 * @author jk-5
 */
object CouchDB extends TLoader{
  private var serverHostname: String = _
  private var serverPort: Int = _
  private var databaseName: String = _

  def load(){
    this.serverHostname = Nexus.getConfig.getTag("database").getTag("hostname").setComment("The IP/Address for the couchdb server").getValue
    this.serverPort = Nexus.getConfig.getTag("database").getTag("port").setComment("The port for the couchdb server").getIntValue(5984)
    this.databaseName = Nexus.getConfig.getTag("database").getTag("name").setComment("The name of the couchdb database that Nexus will use").getValue("Nexus")
  }
}
