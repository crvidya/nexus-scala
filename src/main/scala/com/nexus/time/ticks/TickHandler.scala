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

package com.nexus.time.ticks

import java.util
import java.util.PriorityQueue
import com.google.common.collect.Queues
import java.util.concurrent.atomic.AtomicLong
import scala.collection.JavaConversions._

/**
 * No description given
 *
 * @author jk-5
 */
object TickHandler {
  private val tickHandlers: PriorityQueue[TickQueueElement] = Queues.newPriorityQueue()
  private val tickCounter = new AtomicLong()

  def getTimerSpeed:Int = Timer.getTicksPerSecond

  def registerScheduledTickHandler(handler:IScheduledTickHandler) = this.tickHandlers.add(new TickQueueElement(handler, this.tickCounter.get()))
  def registerTickHandler(handler:ITickHandler) = this.registerScheduledTickHandler(new SingleIntervalHandler(handler))

  def unregisterScheduledTickHandler(handler:IScheduledTickHandler){
    this.tickHandlers.synchronized{
      for(e <- this.tickHandlers.filter(_.getTicker == handler)){
        this.tickHandlers.remove(e)
      }
    }
  }
  def unregisterTickHandler(handler:ITickHandler){
    this.tickHandlers.synchronized{
      for(e <- this.tickHandlers.filter(_.getTicker.isInstanceOf[SingleIntervalHandler])){
        val sih = e.getTicker.asInstanceOf[SingleIntervalHandler]
        if(sih.getHandler == handler) this.tickHandlers.remove(e)
      }
    }
  }

  def updateTickQueue(ticks:util.List[IScheduledTickHandler]){
    ticks.synchronized{
      ticks.clear()
      val tick = this.tickCounter.incrementAndGet()
      var break = false
      while(!break){
        if(this.tickHandlers.size == 0 || !this.tickHandlers.peek.scheduledNow(tick)) break = true
        else{
          val element = this.tickHandlers.poll()
          element.update(tick)
          this.tickHandlers.offer(element)
          ticks.add(element.getTicker)
        }
      }
    }
  }
}
