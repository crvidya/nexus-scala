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

import org.junit.{Assert, Test, Before}
import java.io.StringReader
import org.mockito.{Matchers, Mockito}

/**
 * No description given
 *
 * @author jk-5
 */
class JsonArray_Test {
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

  @Test(expected=classOf[IndexOutOfBoundsException]) def getFailsWithInvalidIndex(): Unit = {array.get(0)}

  @Test def addInt(){
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

  @Test(expected = classOf[NullPointerException])
  def addJsonFailsWithNull() {
    array.add(null.asInstanceOf[JsonValue])
  }

  @Test def addJsonNestedArray(){
    val inner = new JsonArray
    inner.add(23)
    array.add(inner)
    Assert.assertEquals("[[23]]", array.stringify)
  }

  @Test def addJsonNestedArrayModifiedAfterAdd(){
    val inner = new JsonArray
    array.add(inner)
    inner.add(23)
    Assert.assertEquals("[[23]]", array.stringify)
  }

  @Test def addJsonNestedObject(){
    val inner = new JsonObject
    inner.add("a", 23)
    array.add(inner)
    Assert.assertEquals("[{\"a\":23}]", array.stringify)
  }

  @Test def addJsonNestedObjectModifiedAfterAdd(){
    val inner = new JsonObject
    array.add(inner)
    inner.add("a", 23)
    Assert.assertEquals("[{\"a\":23}]", array.stringify)
  }

  @Test def setInt(){
    array.add(false)
    array.set(0, 23)
    Assert.assertEquals("[23]", array.stringify)
  }
  @Test def setIntEnablesChaining(){
    array.add(false)
    Assert.assertSame(array, array.set(0, 23))
  }

  @Test def setLong(){
    array.add(false)
    array.set(0, 23l)
    Assert.assertEquals("[23]", array.stringify)
  }
  @Test def setLongEnablesChaining(){
    array.add(false)
    Assert.assertSame(array, array.set(0, 23l))
  }

  @Test def setFloat(){
    array.add(false)
    array.set(0, 3.14f)
    Assert.assertEquals("[3.14]", array.stringify)
  }
  @Test def setFloatEnablesChaining(){
    array.add(false)
    Assert.assertSame(array, array.set(0, 3.14f))
  }

  @Test def setDouble(){
    array.add(false)
    array.set(0, 3.14d)
    Assert.assertEquals("[3.14]", array.stringify)
  }
  @Test def setDoubleEnablesChaining(){
    array.add(false)
    Assert.assertSame(array, array.set(0, 3.14d))
  }

  @Test def setBoolean(){
    array.add(false)
    array.set(0, true)
    Assert.assertEquals("[true]", array.stringify)
  }
  @Test def setBooleanEnablesChaining(){
    array.add(false)
    Assert.assertSame(array, array.set(0, true))
  }

  @Test def setString(){
    array.add(false)
    array.set(0, "foo")
    Assert.assertEquals("[\"foo\"]", array.stringify)
  }
  @Test def setStringEnablesChaining(){
    array.add(false)
    Assert.assertSame(array, array.set(0, "\"foo\""))
  }

  @Test def setJsonNull(){
    array.add(false)
    array.set(0, JsonValue.NULL)
    Assert.assertEquals("[null]", array.stringify)
  }

  @Test def setJsonArray(){
    array.add(false)
    array.set(0, new JsonArray)
    Assert.assertEquals("[[]]", array.stringify)
  }

  @Test def setJsonObject(){
    array.add(false)
    array.set(0, new JsonObject)
    Assert.assertEquals("[{}]", array.stringify)
  }

  @Test(expected = classOf[NullPointerException])
  def setJsonFailsWithNull(){
    array.add(false)
    array.set(0, null.asInstanceOf[JsonValue])
  }

  @Test(expected = classOf[IndexOutOfBoundsException])
  def setJsonFailsWithInvalidIndex(){
    array.set(0, JsonValue.NULL)
  }

  @Test def setJsonEnablesChaining(){
    array.add(false)
    Assert.assertSame(array, array.set(0, JsonValue.NULL))
  }

  @Test def setJsonReplacesDifferentArrayElements(){
    array.add(3).add(6).add(9)
    array.set(1, 4).set(2, 5)
    Assert.assertEquals("[3,4,5]", array.stringify)
  }

  @Test def writeDelegatesToJsonWriter(){
    val writer = Mockito.mock(classOf[JsonWriter])
    array.write(writer)
    Mockito.verify(writer).writeArray(Matchers.same(array))
  }

  @Test def isArray() = Assert.assertTrue(array.isArray)
  @Test def asArray() = Assert.assertSame(array, array.asArray)
  @Test def equalsIsTrueForSameInstance() = Assert.assertTrue(array.equals(array))

  @Test def equalsIsTrueForEqualArrays(){
    Assert.assertTrue(array().equals(array()))
    Assert.assertTrue(array("foo", "bar").equals(array("foo", "bar")))
  }

  @Test def equalsIsFalseForDifferentArrays(){
    Assert.assertFalse(array("foo", "bar").equals(array("foo", "bar", "baz")))
    Assert.assertFalse(array("foo", "bar").equals(array("bar", "foo")))
  }

  @Test def equalsIsFalseForNull() = Assert.assertFalse(array.equals(null))
  @Test def equalsIsFalseForSubclass() = Assert.assertFalse(array.equals(new JsonArray{}))

  @Test def hashCodeEqualsForEqualArrays(){
    Assert.assertTrue(array().hashCode == array().hashCode)
    Assert.assertTrue(array("foo", "bar").hashCode == array("foo", "bar").hashCode)
  }

  @Test def hashCodeDiffersForDifferentArrays(){
    Assert.assertFalse(array("foo", "bar").hashCode == array("foo", "bar", "baz").hashCode)
    Assert.assertFalse(array("foo", "bar").hashCode == array("bar", "foo").hashCode)
  }

  private def array(vals: String*): JsonArray = {
    val array = new JsonArray
    for(value <- vals) array.add(value)
    array
  }
}
