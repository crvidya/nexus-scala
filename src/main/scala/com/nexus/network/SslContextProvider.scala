/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 Jeffrey Kog (jk-5), Martijn Reening (martijnreening)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.nexus.network

import javax.net.ssl.SSLContext
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import java.io.ByteArrayInputStream
import io.netty.handler.codec.base64.Base64
import java.io.FileInputStream
import java.security.Security
import com.nexus.traits.TLoader
import com.nexus.logging.NexusLog

object SslContextProvider extends TLoader {

	private final val PROTOCOL = "TLS";
    private var serverContext:SSLContext = _;
    private var valid = false;

    def load{
    	try{
			var algorithm = Security.getProperty("ssl.KeyManagerFactory.algorithm");
			if(algorithm == null) algorithm = "SunX509";
			try{
				val keyStoreFilePath = System.getProperty("keystore.file.path");
				val keyStoreFilePassword = System.getProperty("keystore.file.password");
	
				val ks = KeyStore.getInstance("JKS");
				val fin = new FileInputStream(keyStoreFilePath);
				ks.load(fin, keyStoreFilePassword.toCharArray());
	
				// Set up key manager factory to use our key store
				// Assume key password is the same as the key store file
				// password
				val kmf = KeyManagerFactory.getInstance(algorithm);
				kmf.init(ks, keyStoreFilePassword.toCharArray());
	
				// Initialise the SSLContext to work with our key managers.
				serverContext = SSLContext.getInstance(PROTOCOL);
				serverContext.init(kmf.getKeyManagers(), null, null);
				this.valid = true;
			}catch{
				case e:Exception => {}
			}
		}catch{
			case e:Exception => {}
		}
		if(this.valid) NexusLog.info("Valid SSL certificate was found")
		else NexusLog.info("No valid SSL certificate was found")
  }
    
	def getContext = this.serverContext;
	def isValid = this.valid;
}
