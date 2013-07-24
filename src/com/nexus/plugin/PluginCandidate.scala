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
