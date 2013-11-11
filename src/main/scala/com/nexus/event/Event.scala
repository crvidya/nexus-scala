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

import java.lang.annotation.Annotation

/**
 * No description given
 *
 * @author jk-5
 */
object Event {
  private final val listeners = new ListenerList
}
class Event {
  private var result = EventResult.DEFAULT
  private var eventCanceled = false
  private final val eventCancelable = this.HasAnnotation(classOf[Cancelable])
  private final val eventHasResult = this.HasAnnotation(classOf[HasResult])

  this.setup()

  private def HasAnnotation(ann: Class[_ <: Annotation]):Boolean = {
    var cls: Class[_] = this.getClass
    while(cls != classOf[Event]){
      if(cls.isAnnotationPresent(ann)) return true
      cls = cls.getSuperclass
    }
    false
  }

  @inline def isCancelable = this.eventCancelable
  @inline def isCanceled = this.eventCanceled
  @inline def getListenerList = Event.listeners
  @inline def hasResult = this.eventHasResult
  @inline def getResult = this.result

  def setCanceled(c:Boolean){
    if(!this.isCancelable) throw new IllegalStateException("Attempted to cancel an uncancelable event")
    this.eventCanceled = c
  }
  def setResult(v:EventResult){
    if(!this.hasResult) throw new IllegalStateException("Attempted to set the result of an event that does not have an result")
    this.result = v
  }
  protected def setup(){}
}
