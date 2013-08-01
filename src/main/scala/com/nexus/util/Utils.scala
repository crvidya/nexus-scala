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

package com.nexus.util

import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.io.File
import javax.activation.MimetypesFileTypeMap

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

  def getMimeType(f:File):String = new MimetypesFileTypeMap().getContentType(f)
}
