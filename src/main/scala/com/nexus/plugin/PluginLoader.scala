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
