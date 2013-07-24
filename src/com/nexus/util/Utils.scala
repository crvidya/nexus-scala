package com.nexus.util

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.io.File

object Utils {

	def sanitizeURI(u:String):String = {
		var uri = u;
        try{
            uri = URLDecoder.decode(uri, "UTF-8");
        }catch{
        	case e:UnsupportedEncodingException => try{
                uri = URLDecoder.decode(uri, "ISO-8859-1");
            }catch{
            	case e:UnsupportedEncodingException => throw new Error();
            }
        }
        uri = uri.replace('/', File.separatorChar);
        
        if (uri.contains(File.separator + '.') ||
            uri.contains('.' + File.separator) ||
            uri.startsWith(".") || uri.endsWith(".")) {
            return null;
        }
        
        return uri;
	}
}
