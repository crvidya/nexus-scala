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

package com.nexus.plugin

import java.util.{List => JList}
import java.util.jar.JarFile
import com.google.common.collect.Lists

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
