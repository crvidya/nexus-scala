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

package com.nexus.logging

import java.util.logging.StreamHandler
import java.io.OutputStream
import java.util.logging.LogRecord

class ConsoleStreamHandler(private final val out:OutputStream) extends StreamHandler {
	this.setOutputStream(this.out)
	override def close = this.synchronized{this.flush()}
	override def publish(record:LogRecord) = this.synchronized{
		super.publish(record)
		this.flush()
	}
}