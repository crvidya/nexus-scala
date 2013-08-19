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

package com.nexus.webserver

import javax.net.ssl.SSLContext
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import java.io.FileInputStream
import java.security.Security
import com.nexus.traits.TLoader
import com.nexus.logging.NexusLog

/**
 * TODO: Enter description
 * 
 * @author jk-5
 */
object SslContextProvider extends TLoader {

  private final val PROTOCOL = "TLS"
  private var serverContext: SSLContext = _
  private var valid = false

  override def load {
    try{
      var algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm")
      if(algorithm == null) algorithm = "SunX509"
      try{
        val keyStoreFilePath = System.getProperty("keystore.file.path")
        val keyStoreFilePassword = System.getProperty("keystore.file.password")
        val ks = KeyStore.getInstance("JKS")
        val fin = new FileInputStream(keyStoreFilePath)
        ks.load(fin, keyStoreFilePassword.toCharArray)
        val kmf = KeyManagerFactory.getInstance(algorithm)
        kmf.init(ks, keyStoreFilePassword.toCharArray)
        serverContext = SSLContext.getInstance(PROTOCOL)
        serverContext.init(kmf.getKeyManagers, null, null)
        this.valid = true
      }catch{case e: Exception =>}
    }catch{case e: Exception =>}
    if(this.valid) NexusLog.info("Valid SSL certificate was found")
    else NexusLog.info("No valid SSL certificate was found")
  }

  def getContext = this.serverContext
  def isValid = this.valid
}
