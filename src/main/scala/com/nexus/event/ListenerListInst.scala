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

package com.nexus.event

import java.util
import scala.collection.JavaConversions._

/**
 * TODO: Edit description
 *
 * @author jk-5
 */
class ListenerListInst(private var parent: ListenerListInst = null) {
  val count = EventPriority.values.length
  private var rebuild = true
  private var listeners: Array[IEventListener] = _
  private final val priorities = new util.ArrayList[util.ArrayList[IEventListener]](this.count)

  for(x <- 0 until this.count) this.priorities.add(new util.ArrayList[IEventListener])

  protected def shouldRebuild: Boolean = this.rebuild || (this.parent != null && this.parent.shouldRebuild)
  def dispose(){
    this.priorities.foreach(_.clear())
    this.priorities.clear()
    this.parent = null
    this.listeners = null
  }
  def getListeners(priority: EventPriority): util.ArrayList[IEventListener] = {
    val ret = new util.ArrayList[IEventListener](this.priorities.get(priority.ordinal()))
    if(this.parent != null) ret.addAll(this.parent.getListeners(priority))
    ret
  }
  def getListeners: Array[IEventListener] = {
    if(this.shouldRebuild) this.buildCache
    this.listeners
  }
  private def buildCache(){
    if(this.parent != null && this.parent.shouldRebuild) this.parent.buildCache()
    val ret = new util.ArrayList[IEventListener]()
    EventPriority.values().foreach(value => ret.addAll(this.getListeners(value)))
    this.listeners = ret.toArray(new Array[IEventListener](ret.size()))
    this.rebuild = false
  }
  def register(priority: EventPriority, listener: IEventListener){
    this.priorities.get(priority.ordinal()).add(listener)
    this.rebuild = true
  }
  def unregister(listener: IEventListener) = for(list <- this.priorities) if(list.remove(listener)) this.rebuild = true
}
