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

import scala.collection.mutable.ArrayBuffer

/**
 * No description given
 *
 * @author jk-5
 */

class ListenerListInst(private var parent: Option[ListenerListInst] = None) {

  private var rebuild = true
  private var listeners: Array[IEventListener] = null
  private var priorities = ArrayBuffer[ArrayBuffer[IEventListener]]()

  for(x <- 0 until EventPriority.values.size) this.priorities += ArrayBuffer[IEventListener]()

  @inline def preformParent(u: (ListenerListInst) => Unit) = this.parent.foreach(u)

  def dispose(){
    this.priorities.foreach(_.clear())
    this.priorities.clear()
    this.parent = None
    this.listeners = null
  }

  def getListeners(priority: EventPriority): ArrayBuffer[IEventListener] = {
    val buffer = ArrayBuffer[IEventListener]()
    if(this.parent.isDefined) this.parent.get.getListeners(priority).copyToBuffer(buffer)
    buffer
  }

  def getListeners: Array[IEventListener] = {
    if (this.shouldRebuild) this.buildCache()
    listeners
  }

  @inline protected def shouldRebuild: Boolean = this.rebuild || parent.exists(_.shouldRebuild)

  private def buildCache(){
    if(this.parent.exists(_.shouldRebuild)) this.preformParent(_.buildCache())
    val ret = ArrayBuffer[IEventListener]()
    EventPriority.values.foreach(v => ret ++= this.getListeners(v))  // ++= -> addAll
    this.listeners = ret.toArray
    this.rebuild = false
  }

  def register(priority: EventPriority, listener: IEventListener) {
    this.priorities(priority.ordinal()) += listener
    this.rebuild = true
  }

  def unregister(listener: IEventListener) {
    this.priorities.filter(list => (list -= listener) != null).foreach(u => this.rebuild = true)
  }
}
