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

package com.nexus.data.json

import org.junit.{Before, Assert, Test}

/**
 * No description given
 *
 * @author jk-5
 */
class ParseException_Test {

  var ex: ParseException = _

  @Before def setup(){
    ex = new ParseException("Foo", 17, 23, 42)
  }

  @Test def position(){
    Assert.assertEquals(17, ex.getOffset)
    Assert.assertEquals(23, ex.getLine)
    Assert.assertEquals(42, ex.getColumn)
  }

  @Test def message(){
    Assert.assertEquals("Foo at 23:42", ex.getMessage)
  }
}
