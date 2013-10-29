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
object ListenerList {

  private val allLists = ArrayBuffer[ListenerList]()
  private var maxSize = 0

  def resize(max: Int) {
    if (max <= maxSize) return
    this.allLists.foreach(_.resizeLists(max))
    maxSize = max
  }
  @inline def clearBusID(id: Int) = this.allLists.foreach(_.lists(id).dispose())
  @inline def unregiterAll(id: Int, listener: IEventListener) = this.allLists.foreach(_.unregister(id, listener))
}

class ListenerList(private val parent: Option[ListenerList] = None) {

  private var lists = new Array[ListenerListInst](0)

  ListenerList.allLists += this
  this.resizeLists(ListenerList.maxSize)

  def resizeLists(max: Int) {
    this.preformParent(_.resizeLists(max))
    if (this.lists.length >= max) return

    val newList = new Array[ListenerListInst](max)
    var x = 0
    while(x < this.lists.length){
      newList(x) = this.lists(x)
      x += 1
    }
    while (x < max) {
      if(this.parent.isDefined) newList(x) = new ListenerListInst(Some(this.parent.get.getInstance(x)))
      else newList(x) = new ListenerListInst
      x += 1
    }
    this.lists = newList
  }

  @inline protected def getInstance(id: Int) = this.lists(id)
  @inline def getListeners(id: Int): Array[IEventListener] = this.lists(id).getListeners
  @inline def register(id: Int, priority: EventPriority, listener: IEventListener) = this.lists(id).register(priority, listener)
  @inline def unregister(id: Int, listener: IEventListener) = this.lists(id).unregister(listener)
  @inline def preformParent(u: (ListenerList) => Unit) = this.parent.foreach(u)
}