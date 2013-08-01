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

import java.util.{List => JList}
import java.util.jar.JarFile
import com.google.common.base.Preconditions
import com.google.common.collect.Lists
import com.google.common.base.Splitter

class PluginCandidate(private final val file:JarFile) {

	def getContainerFile = this.file
	def explore:JList[PluginContainer] = {
		val manifest = this.file.getManifest()
		val plugins = manifest.getEntries().get("NexusPlugin").get("NexusPlugin"); //FIXME: untested
		if(plugins == null || plugins == ""){
			return Lists.newArrayListWithCapacity(0)
		}else return PluginContainerBuilder.getContainers(plugins.toString()) //XXX
	}
}
