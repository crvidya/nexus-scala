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

import java.util.concurrent.ConcurrentHashMap
import scala.collection.mutable.ArrayBuffer
import java.util.concurrent.atomic.AtomicInteger
import com.google.common.reflect.TypeToken
import java.lang.reflect.{Method, Modifier}
import com.nexus.logging.NexusLog
import scala.collection.JavaConversions._

/**
 * No description given
 *
 * @author jk-5
 */
object EventBus {
  private final val maxID = new AtomicInteger()
}

class EventBus {
  private final val listeners = new ConcurrentHashMap[Any, ArrayBuffer[IEventListener]]()
  private final val busID = EventBus.maxID.getAndIncrement
  ListenerList.resize(this.busID + 1)

  def register(target: Any){
    if(this.listeners.containsKey(target)) return
    val supers = TypeToken.of(target.getClass).getTypes.rawTypes()
    for(method <- target.getClass.getMethods){
      var registered = false
      for(cls <- supers.iterator()){
        if(!registered){
          try{
            val real = cls.getDeclaredMethod(method.getName, method.getParameterTypes: _*)
            if(real.isAnnotationPresent(classOf[EventHandler])){
              val parameterTypes = method.getParameterTypes
              if(parameterTypes.length != 1){
                throw new IllegalArgumentException("Method " + method + " has @EventHandler annotation, but provides " + parameterTypes.length + " arguments.  Event handler methods must require a single argument.")
              }
              val eventType = parameterTypes(0)

              if(!classOf[Event].isAssignableFrom(eventType)){
                throw new IllegalArgumentException("Method " + method + " has @EventHandler annotation, but provides an argument that is not an Event (" + eventType + ")")
              }
              register(eventType, target, method)
              registered = true
            }
          }catch{
            case e: NoSuchMethodException =>
          }
        }
      }
    }
  }

  private def register(eventType: Class[_], target: Any, method: Method){
    if(Modifier.isAbstract(eventType.getModifiers)){
      NexusLog.warning("Tried to register an event listener for an abstract event!")
      NexusLog.warning("This is bad! The event listener will not be registered.")
      NexusLog.warning("Remove the abstract modifier from the event %s to fix this", eventType.getName)
    }
  }
}
