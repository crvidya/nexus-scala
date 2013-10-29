/*package com.nexus.event

import scala.collection.mutable.ArrayBuffer
import com.nexus.event.EventPriority.EventPriority

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

  @inline protected def getInstance(id: Int): ListenerListInst = this.lists(id)
  @inline def getListeners(id: Int): Array[IEventListener] = this.lists(id).getListeners
  @inline def register(id: Int, priority: EventPriority, listener: IEventListener) = this.lists(id).register(priority, listener)
  @inline def unregister(id: Int, listener: IEventListener) = this.lists(id).unregister(listener)
  @inline def preformParent(u: (ListenerList) => Unit) = this.parent.foreach(u)


  private class ListenerListInst(private var parent: Option[ListenerListInst] = None) {

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
      this.priorities(priority.id) += listener
      this.rebuild = true
    }

    def unregister(listener: IEventListener) {
      this.priorities.filter(list => (list -= listener) != null).foreach(u => this.rebuild = true)
    }
  }
}                           */