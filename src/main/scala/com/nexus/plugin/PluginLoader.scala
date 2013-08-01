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

package com.nexus.plugin

import scala.util.Properties
import java.io.{File => JFile, FilenameFilter}
import java.util.{List => JList}
import com.google.common.collect.Lists
import java.util.jar.JarFile

object PluginLoader {

	private final val PLUGIN_DIR = new JFile(Properties.propOrElse("nexus.plugin.dir", "plugins"))
	private final val PluginCandidates:JList[PluginCandidate] = Lists.newArrayList();
	
	def loadPlugins{
		for(p <- PLUGIN_DIR.listFiles(PluginFilter)){
			this.PluginCandidates.add(new PluginCandidate(new JarFile(p)));
		}
	}
	
	object PluginFilter extends FilenameFilter{
		def accept(file:JFile, name:String):Boolean = file.getName().endsWith(".jar")
	}
}
