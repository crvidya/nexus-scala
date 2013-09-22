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
import com.nexus.data.json.JsonObject.{HashIndexTable, Member}
import org.mockito.{Matchers, Mockito}
import com.nexus.TestUtils

/**
 * No description given
 *
 * @author jk-5
 */
class JsonObject_Test {
  private var obj: JsonObject = _

  @Before def setup(){
    obj = new JsonObject
  }

  @Test def readFromReader(){
    Assert.assertEquals(new JsonObject(), JsonObject.readFrom(new StringReader("{}")))
    Assert.assertEquals(new JsonObject().add("a", 23), JsonObject.readFrom(new StringReader("{\"a\": 23}")))
  }

  @Test def readFromString(){
    Assert.assertEquals(new JsonObject(), JsonObject.readFrom("{}"))
    Assert.assertEquals(new JsonObject().add("a", 23), JsonObject.readFrom("{\"a\": 23}"))
  }

  @Test def isEmptyIsTrueAfterCreation() = Assert.assertTrue(this.obj.isEmpty)

  @Test def isEmptyIsFalseAfterAdd(){
    this.obj.add("a", true)
    Assert.assertFalse(obj.isEmpty)
  }

  @Test def sizeIsZeroAfterCreation() = Assert.assertEquals(0, this.obj.size)

  @Test def sizeIsOneAfterAdd(){
    this.obj.add("a", true)
    Assert.assertEquals(1, this.obj.size)
  }

  @Test def namesAreEmptyAfterCreation() = Assert.assertTrue(this.obj.getNames.isEmpty)

  @Test def namesContainsNameAfterAdd(){
    obj.add("foo", true)
    val names = obj.getNames
    Assert.assertEquals(1, names.size)
    Assert.assertEquals("foo", names(0))
  }

  @Test def namesReflectsChanges(){
    val names = obj.getNames
    obj.add("foo", true)
    Assert.assertEquals(1, names.size)
    Assert.assertEquals("foo", names(0))
  }

  /*@Test(expected=classOf[UnsupportedOperationException])
  def namesPreventsModification(){
    val names = obj.getNames
    names += "foo"
  }*/

  @Test def iteratorIsEmptyAfterCreation() = Assert.assertFalse(this.obj.iterator.hasNext)

  @Test def iteratorHasNextAfterAdd(){
    obj.add("a", true)
    val iterator = obj.iterator
    Assert.assertTrue(iterator.hasNext)
  }

  @Test def iteratorNextReturnsActualValue(){
    obj.add("a", true)
    val iterator = obj.iterator
    Assert.assertEquals(new Member("a", JsonValue.TRUE), iterator.next())
  }

  @Test def iteratorNextProgressesToNextValue(){
    obj.add("a", true)
    obj.add("b", false)
    val iterator = obj.iterator
    iterator.next()
    Assert.assertTrue(iterator.hasNext)
    Assert.assertEquals(new Member("b", JsonValue.FALSE), iterator.next())
  }

  @Test(expected = classOf[NoSuchElementException])
  def iteratorNextFailsAtEnd(){
    val iterator = obj.iterator
    iterator.next()
  }

  /*@Test(expected=classOf[ConcurrentModificationException])
  def iteratorDetectsConcurrentModification(){
    val iterator = obj.iterator
    obj.add("foo", true)
    iterator.next()
  }*/

  @Test def getFailsWithNullName() {
    TestUtils.assertException(new Runnable() {
      def run() = obj.get(null)
    }, classOf[NullPointerException], "Name is null")
  }

  @Test def getReturnsNullForNonExistingMember(){
    Assert.assertNull(obj.get("foo"))
  }

  @Test def getReturnsValueForName(){
    obj.add("foo", true)
    Assert.assertEquals(JsonValue.TRUE, obj.get("foo"))
  }

  @Test def getReturnsLastValueForName(){
    obj.add("foo", true).add("foo", false)
    Assert.assertEquals(JsonValue.FALSE, obj.get("foo"))
  }

  @Test def addFailsWithNullName() {
    TestUtils.assertException(new Runnable() {
      def run() = obj.add(null, 23)
    }, classOf[NullPointerException], "Name is null")
  }

  @Test def addInt(){
    obj.add("a", 23)
    Assert.assertEquals("{\"a\":23}", obj.stringify)
  }
  @Test def addIntEnablesChaining(){
    Assert.assertSame(obj, obj.add("a", 23))
  }

  @Test def addLong(){
    obj.add("a", 23l)
    Assert.assertEquals("{\"a\":23}", obj.stringify)
  }
  @Test def addLongEnablesChaining(){
    Assert.assertSame(obj, obj.add("a", 23l))
  }

  @Test def addFloat(){
    obj.add("a", 3.14f)
    Assert.assertEquals("{\"a\":3.14}", obj.stringify)
  }
  @Test def addFloatEnablesChaining(){
    Assert.assertSame(obj, obj.add("a", 3.14f))
  }

  @Test def addDouble(){
    obj.add("a", 3.14d)
    Assert.assertEquals("{\"a\":3.14}", obj.stringify)
  }
  @Test def addDoubleEnablesChaining(){
    Assert.assertSame(obj, obj.add("a", 3.14d))
  }

  @Test def addBoolean(){
    obj.add("a", true)
    Assert.assertEquals("{\"a\":true}", obj.stringify)
  }
  @Test def addBooleanEnablesChaining(){
    Assert.assertSame(obj, obj.add("a", true))
  }

  @Test def addString(){
    obj.add("a", "bar")
    Assert.assertEquals("{\"a\":\"bar\"}", obj.stringify)
  }
  @Test def addStringToleratesNull(){
    obj.add("a", null.asInstanceOf[String])
    Assert.assertEquals("{\"a\":null}", obj.stringify)
  }
  @Test def addStringEnablesChaining(){
    Assert.assertSame(obj, obj.add("a", "foo"))
  }

  @Test def addJsonNull(){
    obj.add("a", JsonValue.NULL)
    Assert.assertEquals(obj.stringify, "{\"a\":null}")
  }

  @Test def addJsonArray(){
    obj.add("a", new JsonArray)
    Assert.assertEquals(obj.stringify, "{\"a\":[]}")
  }

  @Test def addJsonObject(){
    obj.add("a", new JsonObject)
    Assert.assertEquals(obj.stringify, "{\"a\":{}}")
  }

  @Test def addJsonEnablesChaining(){
    Assert.assertSame(obj, obj.add("a", JsonValue.NULL))
  }

  @Test def addJsonFailsWithNull() {
    TestUtils.assertException(new Runnable() {
      def run() = obj.add("a", null.asInstanceOf[JsonValue])
    }, classOf[NullPointerException], "Value is null")
  }

  @Test def addJsonNestedArray(){
    val array = new JsonArray
    array.add(23)
    obj.add("a", array)
    Assert.assertEquals("{\"a\":[23]}", obj.stringify)
  }

  @Test def addJsonNestedArrayModifiedAfterAdd(){
    val array = new JsonArray
    obj.add("a", array)
    array.add(23)
    Assert.assertEquals("{\"a\":[23]}", obj.stringify)
  }

  @Test def addJsonNestedObject(){
    val o = new JsonObject
    o.add("a", 23)
    obj.add("a", o)
    Assert.assertEquals("{\"a\":{\"a\":23}}", obj.stringify)
  }

  @Test def addJsonNestedObjectModifiedAfterAdd(){
    val o = new JsonObject
    obj.add("a", o)
    o.add("a", 23)
    Assert.assertEquals("{\"a\":{\"a\":23}}", obj.stringify)
  }

  @Test def setInt(){
    obj.set("a", 23)
    Assert.assertEquals("{\"a\":23}", obj.stringify)
  }
  @Test def setIntEnablesChaining(){
    Assert.assertSame(obj, obj.set("a", 23))
  }

  @Test def setLong(){
    obj.set("a", 23l)
    Assert.assertEquals("{\"a\":23}", obj.stringify)
  }
  @Test def setLongEnablesChaining(){
    Assert.assertSame(obj, obj.set("a", 23l))
  }

  @Test def setFloat(){
    obj.set("a", 3.14f)
    Assert.assertEquals("{\"a\":3.14}", obj.stringify)
  }
  @Test def setFloatEnablesChaining(){
    Assert.assertSame(obj, obj.set("a", 3.14f))
  }

  @Test def setDouble(){
    obj.set("a", 3.14d)
    Assert.assertEquals("{\"a\":3.14}", obj.stringify)
  }
  @Test def setDoubleEnablesChaining(){
    Assert.assertSame(obj, obj.set("a", 3.14d))
  }

  @Test def setBoolean(){
    obj.set("a", true)
    Assert.assertEquals("{\"a\":true}", obj.stringify)
  }
  @Test def setBooleanEnablesChaining(){
    Assert.assertSame(obj, obj.set("a", true))
  }

  @Test def setString(){
    obj.set("a", "foo")
    Assert.assertEquals("{\"a\":\"foo\"}", obj.stringify)
  }
  @Test def setStringEnablesChaining(){
    Assert.assertSame(obj, obj.set("a", "foo"))
  }

  @Test def setJsonNull(){
    obj.set("a", JsonValue.NULL)
    Assert.assertEquals("{\"a\":null}", obj.stringify)
  }

  @Test def setJsonArray(){
    obj.set("a", new JsonArray)
    Assert.assertEquals("{\"a\":[]}", obj.stringify)
  }

  @Test def setJsonObject(){
    obj.set("a", new JsonObject)
    Assert.assertEquals("{\"a\":{}}", obj.stringify)
  }

  @Test def setJsonEnablesChaining(){
    Assert.assertSame(obj, obj.set("a", JsonValue.NULL))
  }

  @Test def setAddsElementIfMissing(){
    obj.set("a", JsonValue.TRUE)
    Assert.assertEquals("{\"a\":true}", obj.stringify)
  }

  @Test def setModifiesElementIfExisting(){
    obj.add("a", JsonValue.TRUE)
    obj.set("a", JsonValue.FALSE)
    Assert.assertEquals("{\"a\":false}", obj.stringify)
  }

  @Test def setModifiesLastElementIfMultipleExisting(){
    obj.add("a", 1)
    obj.add("a", 2)
    obj.set("a", 3)
    Assert.assertEquals("{\"a\":1,\"a\":3}", obj.stringify)
  }

  @Test def removeFailsWithNullName() {
    TestUtils.assertException(new Runnable() {
      def run() = obj.remove(null)
    }, classOf[NullPointerException], "Name is null")
  }

  @Test def removeRemovesMatchingMember(){
    obj.add("a", 1)
    obj.remove("a")
    Assert.assertEquals("{}", obj.stringify)
  }

  @Test def removeRemovesOnlyMatchingMember(){
    obj.add("a", 1)
    obj.add("b", 2)
    obj.add("c", true)
    obj.remove("b")
    Assert.assertEquals("{\"a\":1,\"c\":true}", obj.stringify)
  }

  @Test def removeRemovesOnlyLastMatchingMember(){
    obj.add("a", 1)
    obj.add("a", 2)
    obj.remove("a")
    Assert.assertEquals("{\"a\":1}", obj.stringify)
  }

  @Test def removeRemovesOnlyLastMatchingMemberAfterRemove(){
    obj.add("a", 1)
    obj.remove("a")
    obj.add("a", 2)
    obj.add("a", 3)
    obj.remove("a")
    Assert.assertEquals("{\"a\":2}", obj.stringify)
  }

  @Test def removeDoesNotModifyObjectWithoutMatchingMember(){
    obj.add("a", 1)
    obj.remove("b")
    Assert.assertEquals("{\"a\":1}", obj.stringify)
  }

  @Test def writeDelegatesToJsonWriter(){
    val writer = Mockito.mock(classOf[JsonWriter])
    obj.write(writer)
    Mockito.verify(writer).writeObject(Matchers.same(obj))
  }

  @Test def isObject() = Assert.assertTrue(obj.isObject)
  @Test def asArray() = Assert.assertSame(obj, obj.asObject)
  @Test def equalsIsTrueForSameInstance() = Assert.assertTrue(obj.equals(obj))

  @Test def equalsIsTrueForEqualObjects(){
    Assert.assertTrue(obj().equals(obj()))
    Assert.assertTrue(obj("foo", "bar", "1", "2").equals(obj("foo", "bar", "1", "2")))
  }

  @Test def equalsIsFalseForDifferentObjects(){
    Assert.assertFalse(obj("foo", "bar").equals(obj("foo", "bar", "baz", "ba")))
    Assert.assertFalse(obj("foo", "bar").equals(obj("bar", "foo")))
  }

  @Test def equalsIsFalseForNull() = Assert.assertFalse(obj.equals(null))
  @Test def equalsIsFalseForSubclass() = Assert.assertFalse(obj.equals(new JsonObject{}))

  @Test def hashCodeEqualsForEqualArrays(){
    Assert.assertTrue(obj().hashCode == obj().hashCode)
    Assert.assertTrue(obj("foo", "bar").hashCode == obj("foo", "bar").hashCode)
  }

  @Test def hashCodeDiffersForDifferentArrays(){
    Assert.assertFalse(obj().hashCode == obj("foo", "bar").hashCode)
    Assert.assertFalse(obj("foo", "bar").hashCode == obj("foo", "bar", "baz", "1").hashCode)
    Assert.assertFalse(obj("foo", "bar").hashCode == obj("bar", "foo").hashCode)
  }

  @Test def indexOfReturnsNoIndexIfEmpty(){
    Assert.assertEquals(-1, obj.indexOf("foo"))
  }

  @Test def indexOfReturnsIndexOfMember(){
    obj.add("foo", "a")
    Assert.assertEquals(0, obj.indexOf("foo"))
  }

  @Test def indexOfReturnsIndexOfLastMemberAfterRemove(){
    obj.add("foo", "a")
    obj.add("foo", "b")
    obj.remove("foo")
    Assert.assertEquals(0, obj.indexOf("foo"))
  }

  @Test def indexOfReturnsIndexOfLastMemberForBigObject(){
    obj.add("foo", "a")
    for(i <- 0 to 255) obj.add("x-" + i, 0) //Fill it with 255 elements. HashIndexTable does not help us for indexes > 256
    obj.add("foo", "b")
    Assert.assertEquals(257, obj.indexOf("foo"))
  }

  @Test def hashIndexTableCopyConstructor(){
    val original = new HashIndexTable
    original.add("foo", 23)
    val copy = new HashIndexTable(original)
    Assert.assertEquals(23, copy.get("foo"))
  }

  @Test def hashIndexTableAdd(){
    val table = new HashIndexTable
    table.add("name-0", 0)
    table.add("name-1", 1)
    table.add("name-fe", 0xfe)
    table.add("name-ff", 0xff)

    Assert.assertEquals(0, table.get("name-0"))
    Assert.assertEquals(1, table.get("name-1"))
    Assert.assertEquals(0xfe, table.get("name-fe"))
    Assert.assertEquals(-1, table.get("name-ff"))
  }

  @Test def hashIndexTableAddOverwritesPreviousValue(){
    val table = new HashIndexTable
    table.add("name", 0)
    table.add("name", 1)
    Assert.assertEquals(1, table.get("name"))
  }

  @Test def hashIndexTableAddClearsPreviousValueIfIndexExceeds0xff(){
    val table = new HashIndexTable
    table.add("name", 20)
    table.add("name", 300)
    Assert.assertEquals(-1, table.get("name"))
  }

  @Test def hashIndexTableRemove(){
    val table = new HashIndexTable
    table.add("name", 20)
    table.remove("name")
    Assert.assertEquals(-1, table.get("name"))
  }

  @Test def memberReturnsNameAndValue(){
    val member = new Member("a", JsonValue.TRUE)
    Assert.assertEquals("a", member.getName)
    Assert.assertEquals(JsonValue.TRUE, member.getValue)
  }

  @Test def memberEqualsIsTrueForSameInstance(){
    val member = new Member("a", JsonValue.TRUE)
    Assert.assertTrue(member.equals(member))
  }

  @Test def memberEqualsIsTrueForEqualObjects(){
    Assert.assertTrue(new Member("a", JsonValue.TRUE).equals(new Member("a", JsonValue.TRUE)))
  }

  @Test def memberEqualsIsFalseForDifferingObjects(){
    Assert.assertFalse(new Member("a", JsonValue.TRUE).equals(new Member("b", JsonValue.TRUE)))
    Assert.assertFalse(new Member("a", JsonValue.TRUE).equals(new Member("a", JsonValue.FALSE)))
  }

  @Test def memberEqualsIsFalseForNull(){
    Assert.assertFalse(new Member("a", JsonValue.TRUE).equals(null))
  }

  @Test def memberEqualsIsFalseForSubclass(){
    Assert.assertFalse(new Member("a", JsonValue.TRUE).equals(new Member("a", JsonValue.TRUE){}))
  }

  @Test def memberHashCodeEqualsForEqualObjects(){
    Assert.assertTrue(new Member("a", JsonValue.TRUE).hashCode == new Member("a", JsonValue.TRUE).hashCode)
  }

  @Test def memberHashCodeDiffersForDifferingObjects(){
    Assert.assertFalse(new Member("a", JsonValue.TRUE).hashCode == new Member("b", JsonValue.TRUE).hashCode)
    Assert.assertFalse(new Member("a", JsonValue.TRUE).hashCode == new Member("a", JsonValue.FALSE).hashCode)
  }

  def obj(namesAndVals: String *): JsonObject = {
    val ret = new JsonObject
    var last: String = null
    for(i <- namesAndVals){
      if(last == null) last = i
      else{
        ret.add(last, i)
        last = null
      }
    }
    ret
  }
}
