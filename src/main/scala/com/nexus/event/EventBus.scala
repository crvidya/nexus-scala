package com.nexus.event

import java.util.concurrent.ConcurrentHashMap
import scala.collection.mutable.ArrayBuffer
import java.util.concurrent.atomic.AtomicInteger

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
  //private final val busID =
}
