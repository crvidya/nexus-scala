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

package com.nexus.time.synchronisation

import java.util.Date
import com.nexus.traits.TLoader
import com.nexus.concurrent.WorkerPool
import java.util.concurrent.TimeUnit
import com.nexus.logging.NexusLog

/**
 * No description given
 *
 * @author jk-5
 */
object TimeSynchronisationHandler extends TLoader {

  private var synchroniser: TimeSynchroniser = _
  private var failedAttempts = 0

  def synchronise(){
    println("Synchronising")
    try{
      this.synchroniser.synchronise()
      this.failedAttempts = 0
    }catch{
      case e: Exception => {
        this.failedAttempts += 1
      }
    }
    if(this.failedAttempts >= 10){
      this.synchroniser = new TimeSynchroniserInternal
      this.failedAttempts = 0
      NexusLog.warning("The time synchroniser was changed to %s because the previous one failed!".format(this.synchroniser.getClass.getSimpleName))
    }
  }

  def load(){
    WorkerPool.scheduleWithInterval(new Runnable(){def run() = synchronise()}, 0, 5, TimeUnit.MINUTES)
  }

  private[time] def getCurrentDate: Date = this.synchroniser.getCurrentDate
  private[time] def getCurrentTime: Long = this.synchroniser.getCurrentTime
}
