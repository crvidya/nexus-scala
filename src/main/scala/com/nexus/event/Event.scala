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
import com.nexus.event.EventResult._

/**
 * TODO: Edit description
 *
 * @author jk-5
 */
object Event {
  private val listeners = new ListenerList
}

class Event {
  private var result: EventResult = EventResult.DEFAULT
  private var eventCanceled:Boolean = false
  private final val eventCancelable:Boolean = this.hasAnnotation(classOf[Cancelable])
  private final val eventHasResult:Boolean = this.hasAnnotation(classOf[HasResult])

  this.setup()

  private def hasAnnotation(ann:Class[_ <: Annotation]):Boolean = {
    var cls:Class[_] = this.getClass
    while(cls != classOf[Event]){
      if(cls.isAnnotationPresent(ann)) return true
      cls = cls.getSuperclass
    }
    false
  }

  def isCancelable:Boolean = this.eventCancelable
  def isCanceled:Boolean = this.eventCanceled
  def setCanceled(c:Boolean){
    if(!this.isCancelable) throw new IllegalArgumentException("Attempted to cancel an uncancelable event")
    this.eventCanceled = c
  }
  def HasResult = this.eventHasResult
  def getResult = this.result
  def setResult(v: EventResult){
    if(!this.HasResult) throw new IllegalArgumentException("Attempted to set the result of an event that does not have an result")
    this.result = v
  }
  protected def setup(){}
  def getListenerList:ListenerList = Event.listeners
}
