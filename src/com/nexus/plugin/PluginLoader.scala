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
