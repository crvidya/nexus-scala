package com.nexus.network

import javax.net.ssl.SSLContext
import java.security.KeyStore
import javax.net.ssl.KeyManagerFactory
import java.io.ByteArrayInputStream
import io.netty.handler.codec.base64.Base64
import java.io.FileInputStream
import java.security.Security
import com.nexus.traits.TLoader

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
				case e:Exception => 
			}
		}catch{
			case e:Exception => 
		}
		if(this.valid) println("Found valid SSL certificate")
		else println("No valid SSL certificate was found")
    }
    
	def getContext = this.serverContext;
	def isValid = this.valid;
}
