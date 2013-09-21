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

package com.nexus.concurrent

import com.nexus.Nexus
import java.util.concurrent._
import java.util.concurrent.atomic.AtomicInteger
import com.nexus.traits.TLoader
import java.lang.Thread.UncaughtExceptionHandler
import com.nexus.errorhandling.{ReportedException, ErrorReport, ErrorHandler}

/**
 * No description given
 *
 * @author jk-5
 */
object WorkerPool extends TLoader {
  private var pool: ScheduledExecutorService = _
  def execute(r: Runnable) = this.pool.execute(r)
  def submit[T](c: Callable[T]): Future[T] = this.pool.submit(c)
  def schedule(r: Runnable, delay: Long, unit: TimeUnit) = this.pool.schedule(r, delay, unit)
  def schedule[T](c: Callable[T], delay: Long, unit: TimeUnit) = this.pool.schedule(c, delay, unit)
  def scheduleWithInterval(r: Runnable, initialDelay: Long, interval: Long, unit: TimeUnit) = this.pool.scheduleAtFixedRate(r, initialDelay, interval, unit)
  def load() = this.pool = Executors.newScheduledThreadPool(Nexus.getConfig.getTag("numberOfWorkers").setComment("The number of worker threads that will be used for all kinds of async tasks").getIntValue(4), WorkerThreadFactory)
}

object WorkerThreadFactory extends ThreadFactory {
  private final val workerNumber = new AtomicInteger(1)
  def newThread(r: Runnable): Thread = {
    val thr = new Thread(r, "Nexus-Worker-" + workerNumber.getAndIncrement)
    thr.setUncaughtExceptionHandler(NexusUncaughtExceptionHandler)
    thr
  }
}

object NexusUncaughtExceptionHandler extends UncaughtExceptionHandler {
  def uncaughtException(thread: Thread, t: Throwable) = t match{
    case report: ReportedException => ErrorHandler.unexpectedException(report)
    case ex => {
      val report = new ErrorReport("Uncaught exception in worker thread", t)
      ErrorHandler.unexpectedException(new ReportedException(report))
    }
  }
}
