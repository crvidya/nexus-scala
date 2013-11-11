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

package com.nexus.concurrent.tasks

import java.util.concurrent.Callable
import com.lambdaworks.crypto.SCryptUtil

/**
 * No description given
 *
 * @author jk-5
 */
class CreateSCryptHashTask(private final val input: String, private final val cpuCost: Int = 16384, private final val memoryCost: Int = 8, private final val parallel: Int = 1) extends Callable[String] {
  def call(): String = SCryptUtil.scrypt(this.input, this.cpuCost, this.memoryCost, this.parallel)
}
class CheckSCryptHashTask(private final val input: String, private final val hash: String) extends Callable[Boolean] {
  def call(): Boolean = SCryptUtil.check(this.input, this.hash)
}
