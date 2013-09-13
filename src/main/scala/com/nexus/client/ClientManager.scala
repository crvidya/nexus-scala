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

package com.nexus.client

import scala.collection.immutable.HashMap
import com.nexus.client.web.NexusClientWebapp
import scala.collection.mutable
import com.nexus.authentication.AuthSession

/**
 * No description given
 *
 * @author jk-5
 */
object ClientManager {

  private final val clients = mutable.HashSet[NexusClient]()
  private final val clientTypes = HashMap[String, Class[_ <: NexusClient]](
    "webapp" -> classOf[NexusClientWebapp]
  )

  def registerClient(typ: String, session: AuthSession): NexusClient = {
    val clientClass = this.clientTypes.get(typ).getOrElse(null)
    if(clientClass == null) throw new IllegalArgumentException("Client " + typ + " was not found")
    val client = clientClass.getConstructor(classOf[AuthSession]).newInstance(session)
    if(!this.registerClient(client)) throw new RuntimeException("Something went wrong while registering the client")
    client
  }
  def registerClient(client: NexusClient):Boolean = this.clients.add(client)
}
