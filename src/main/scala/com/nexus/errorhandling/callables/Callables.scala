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

package com.nexus.errorhandling.callables

import java.util.concurrent.Callable
import com.nexus.Version
import java.lang.management.ManagementFactory
import scala.collection.JavaConversions

/**
 * No description given
 *
 * @author jk-5
 */
object CallableVersion extends Callable[String] {def call = Version.version}
object CallableBuild extends Callable[Int] {def call = Version.build}
object CallableOSInfo extends Callable[String] {def call = System.getProperty("os.name") + " (" + System.getProperty("os.arch") + ") version " + System.getProperty("os.version")}
object CallableJavaInfo extends Callable[String] {def call = System.getProperty("java.version") + ", " + System.getProperty("java.vendor")}
object CallableJavaVMInfo extends Callable[String] {def call = System.getProperty("java.vm.name") + " (" + System.getProperty("java.vm.info") + "), " + System.getProperty("java.vm.vendor")}
object CallableCurrentThread extends Callable[String] {def call = Thread.currentThread().getName}
object CallableMemory extends Callable[String] {def call: String = {
  val runtime = Runtime.getRuntime
  val max = runtime.maxMemory()
  val total = runtime.totalMemory()
  val free = runtime.freeMemory()
  val maxMB = max / 1024L / 1024L
  val totalMB = total / 1024L / 1024L
  val freeMB = free / 1024L / 1024L
  free + " bytes (" + freeMB + " MB) / " + total + " bytes (" + totalMB + " MB) up to " + max + " bytes (" + maxMB + " MB)"
}}
object CallableJVMFlags extends Callable[String] {def call: String = {
  val args = JavaConversions.asScalaBuffer(ManagementFactory.getRuntimeMXBean.getInputArguments)
  "%d total, %s".format(args.length, args.mkString(" "))
}}