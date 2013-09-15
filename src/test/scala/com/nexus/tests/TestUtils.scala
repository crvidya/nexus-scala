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

package com.nexus.tests

import org.junit.Assert

/**
 * No description given
 *
 * @author jk-5
 */
object TestUtils {

  def assertException[T <: Exception](r: Runnable, typ: Class[T]): T = {
    val exception = this.catchException(r)
    Assert.assertNotNull("An exception should be thrown", exception)
    Assert.assertNotNull("Expected exception should not be null", typ.getName)
    Assert.assertSame("Exception thrown should be equal to the expected exception", exception.getClass, typ)
    exception.asInstanceOf[T]
  }

  def assertException[T <: Exception](r: Runnable, typ: Class[T], msg: String): T = {
    val exception = this.assertException(r, typ)
    Assert.assertEquals("Exception message should be equal", "message", exception.getMessage)
    exception
  }

  private def catchException(r: Runnable): Exception = try{
    r.run()
    null
  }catch {
    case e: Exception => return e
  }
}
