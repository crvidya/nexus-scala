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

import java.text.DecimalFormat
import java.util
import com.nexus.Nexus
import java.util.concurrent.TimeUnit
import com.nexus.logging.NexusLog
import java.util.logging.Level
import com.google.common.base.Stopwatch
import com.google.common.collect.Lists
import scala.collection.JavaConversions._

/**
 * TODO: Edit description
 *
 * @author jk-5
 */
object Timer extends Thread {
  private final val ticksPerSecond = 10
  private final val secondLength = 1000
  private final val tickDelay:Float = this.secondLength / this.ticksPerSecond
  private var ticksSinceLastWarning = 0L
  private final val decimalFormat = new DecimalFormat()

  this.decimalFormat.setMaximumFractionDigits(3)
  this.decimalFormat.setMinimumFractionDigits(3)

  private var running = false

  private final val scheduledTicks: util.List[IScheduledTickHandler] = Lists.newArrayList()

  Nexus.getEventBus.register(this)

  this.setName("Timer")
  this.setDaemon(true)

  def startTimer(){
    this.running = true
    super.start()
  }

  def stopTimer() = this.running = false

  //@EventListener def onServerShutdown(event:ServerCloseEvent) = this.Stop

  def getTicksPerSecond:Int = this.ticksPerSecond
  def getTickTime:Float = this.tickDelay

  override def run(){
    while(this.running){
      try{
        this.Tick()
        Thread.sleep(this.tickDelay.toLong)
      }catch{case e: Exception => {}}
    }
  }

  private def Tick(){
    this.rescheduleTicks()
    this.ticksSinceLastWarning += 1
    val stopwatch = new Stopwatch()
    stopwatch.start()
    this.scheduledTicks.foreach(_.onTick())
    stopwatch.stop()
    val ellapsed = stopwatch.elapsed(TimeUnit.MICROSECONDS).floatValue / 1000F
    if(ellapsed >= this.tickDelay){
      if(this.ticksSinceLastWarning > 5 * this.getTicksPerSecond){
        NexusLog.log("Timer", Level.WARNING, "Something blocked the timer! It took longer to tick than the ideal maxumum length of " + this.decimalFormat.format(this.getTickTime) + "ms (it took " + this.decimalFormat.format(ellapsed) + "ms). The ideal speed of " + this.getTicksPerSecond + "tps can not be reached!")
        this.ticksSinceLastWarning = 0
      }
    }
  }

  private def rescheduleTicks() = TickHandler.updateTickQueue(this.scheduledTicks)
}
