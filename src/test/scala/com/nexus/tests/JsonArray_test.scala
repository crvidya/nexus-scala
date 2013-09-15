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

import com.nexus.data.json.{JsonObject, JsonValue, JsonArray}
import org.junit.{Assert, Test, Before}
import java.io.StringReader

/**
 * No description given
 *
 * @author jk-5
 */
class JsonArray_test {
  private var array: JsonArray = _

  @Before def setup(){
    array = new JsonArray
  }

  @Test def readFromReader(){
    Assert.assertEquals(new JsonArray(), JsonArray.readFrom(new StringReader("[]")))
    Assert.assertEquals(new JsonArray().add("a").add(23), JsonArray.readFrom(new StringReader("[\"a\", 23]")))
  }

  @Test def readFromString(){
    Assert.assertEquals(new JsonArray(), JsonArray.readFrom("[]"))
    Assert.assertEquals(new JsonArray().add("a").add(23), JsonArray.readFrom("[\"a\", 23]"))
  }

  @Test def isEmptyIsTrueAfterCreation() = Assert.assertTrue(this.array.isEmpty)

  @Test def isEmptyIsFalseAfterAdd(){
    this.array.add(true)
    Assert.assertFalse(array.isEmpty)
  }

  @Test def sizeIsZeroAfterCreation() = Assert.assertEquals(0, this.array.size)

  @Test def sizeIsOneAfterAdd(){
    this.array.add(true)
    Assert.assertEquals(1, this.array.size)
  }

  @Test def iteratorIsEmptyAfterCreation() = Assert.assertFalse(this.array.iterator.hasNext)

  @Test def iteratorHasNextAfterAdd(){
    array.add(true)
    val iterator = array.iterator
    Assert.assertTrue(iterator.hasNext)
    Assert.assertEquals(JsonValue.TRUE, iterator.next())
    Assert.assertFalse(iterator.hasNext)
  }

  /*@Test(expected=classOf[UnsupportedOperationException])
  def iteratorDoesNotAllowModification(){
    array.add(23)
    val iterator = array.iterator
    iterator.next()
    iterator.remove()
  }*/

  /*@Test(expected=classOf[ConcurrentModificationException])
  def iteratorDetectsConcurrentModification(){
    val iterator = array.iterator
    array.add(23)
    iterator.next()
  }*/

  @Test def valuesIsEmptyAfterCreation() = Assert.assertTrue(array.getValues.isEmpty)

  @Test
  def valuesContainsValueAfterAdd(){
    array.add(true)
    Assert.assertEquals(1, array.getValues.size)
    Assert.assertEquals(JsonValue.TRUE, array.getValues(0))
  }

  @Test
  def valuesReflectsChanges(){
    val values = array.getValues
    array.add(true)
    Assert.assertEquals(array.getValues, values)
  }

  @Test
  def getReturnsValue(){
    array.add(23)
    val value = array.get(0)
    Assert.assertEquals(JsonValue.valueOf(23), value)
  }

  @Test(expected=classOf[IndexOutOfBoundsException]) def getFailsWithInvalidIndex() = array.get(0)

  @Test
  def addInt(){
    array.add(23)
    Assert.assertEquals("[23]", array.stringify)
  }
  @Test def addIntEnablesChaining() = Assert.assertSame(array, array.add(23))

  @Test def addFloat(){
    array.add(3.14f)
    Assert.assertEquals("[3.14]", array.stringify)
  }
  @Test def addFloatEnablesChaining() = Assert.assertSame(array, array.add(3.14f))

  @Test def addDouble(){
    array.add(3.14d)
    Assert.assertEquals("[3.14]", array.stringify)
  }
  @Test def addDoubleEnablesChaining() = Assert.assertSame(array, array.add(3.14d))

  @Test def addBoolean(){
    array.add(true)
    Assert.assertEquals("[true]", array.stringify)
  }
  @Test def addBooleanEnablesChaining() = Assert.assertSame(array, array.add(true))

  @Test def addString(){
    array.add("foo")
    Assert.assertEquals("[\"foo\"]", array.stringify)
  }
  @Test def addStringEnablesChaining() = Assert.assertSame(array, array.add("foo"))

  @Test def addStringToleratesNull(){
    array.add(null.asInstanceOf[String])
    Assert.assertEquals("[null]", array.stringify)
  }

  @Test def addJsonNull(){
    array.add(JsonValue.NULL)
    Assert.assertEquals("[null]", array.stringify)
  }

  @Test def addJsonArray(){
    array.add(new JsonArray)
    Assert.assertEquals("[[]]", array.stringify)
  }

  @Test def addJsonObject(){
    array.add(new JsonObject)
    Assert.assertEquals("[{}]", array.stringify)
  }

  @Test def addJsonEnablesChaining() = Assert.assertSame(array, array.add("foo"))
}
