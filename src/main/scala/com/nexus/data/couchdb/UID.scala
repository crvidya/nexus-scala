package com.nexus.data.couchdb

import java.util.UUID

/**
 * No description given
 *
 * @author jk-5
 */
object UID {
  def randomUID: UID = new UID(UUID.randomUUID().toString.replaceAll("-", ""))
}
class UID(private final val uid: String) {
  override def toString = this.uid
}
