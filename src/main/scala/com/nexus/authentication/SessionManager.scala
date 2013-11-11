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

package com.nexus.authentication

import com.nexus.concurrent.WorkerPool
import com.nexus.concurrent.tasks.CheckSCryptHashTask

/**
 * No description given
 *
 * @author jk-5
 */
object SessionManager {

  def getSession(user: User, password: String): Option[AuthSession] = {
    val future = WorkerPool.submit(new CheckSCryptHashTask(password, user.getPasswordHash))
    if(future.get()){
      val session = new AuthSession(user.getID)
      session.saveToDatabase()
      return Some(session)
    }
    None
  }
}
