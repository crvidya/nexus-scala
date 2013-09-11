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
import com.google.common.collect.Lists
import scala.collection.JavaConversions._

/**
 * No description given
 *
 * @author jk-5
 */
object ListenerList {
  private val allLists: util.List[ListenerList] = Lists.newArrayList()
  private var maxSize = 0
  def resize(max: Int){
    if(max <= this.maxSize) return
    this.allLists.foreach(_.resizeLists(max))
    this.maxSize = max
  }
  def clearBusID(id:Int) = this.allLists.foreach(_.lists(id).dispose())
  def unregisterAll(id:Int, listener:IEventListener) = this.allLists.foreach(_.unregister(id, listener))
}

class ListenerList(private final val parent: ListenerList = null) {

  private var lists: Array[ListenerListInst] = new Array[ListenerListInst](0)

  ListenerList.allLists.add(this)
  this.resizeLists(ListenerList.maxSize)

  def resizeLists(max: Int){
    if(this.parent != null) this.parent.resizeLists(max)
    if(this.lists.length >= max) return
    val newList = new Array[ListenerListInst](max)
    var x = 0
    while(x < this.lists.length){
      newList(x) = this.lists(x)
      x += 1
    }
    while(x < max){
      if(this.parent != null) newList(x) = new ListenerListInst(this.parent.getInstance(x))
      else newList(x) = new ListenerListInst()
      x += 1
    }
    this.lists = newList
  }

  protected def getInstance(id: Int) = this.lists(id)
  def getListeners(id: Int) = this.lists(id).getListeners
  def register(id: Int, priority: EventPriority, listener: IEventListener) = this.lists(id).register(priority, listener)
  def unregister(id:Int, listener:IEventListener) = this.lists(id).unregister(listener)
}
