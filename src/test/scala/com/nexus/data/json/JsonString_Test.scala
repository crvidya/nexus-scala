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

import java.io.StringWriter
import org.junit.{Assert, Test, Before}

/**
 * No description given
 *
 * @author jk-5
 */
class JsonString_Test {

  var stringWriter: StringWriter = _
  var jsonWriter: JsonWriter = _

  @Before def setup(){
    stringWriter = new StringWriter
    jsonWriter = new JsonWriter(stringWriter)
  }

  @Test(expected = classOf[NullPointerException])
  def constructorFailsWithNull(){
    new JsonString(null)
  }

  @Test def write(){
    new JsonString("foo").write(jsonWriter)
    Assert.assertEquals("\"foo\"", stringWriter.toString())
  }

  @Test def writeEscapesStrings(){
    new JsonString("foo\\bar").write(jsonWriter)
    Assert.assertEquals("\"foo\\\\bar\"", stringWriter.toString())
  }

  @Test def isString() = Assert.assertTrue(new JsonString("foo").isString)
  @Test def asString() = Assert.assertEquals("foo", new JsonString("foo").asString)

  @Test def equalsIsTrueForSameInstance(){
    val string = new JsonString("foo")
    Assert.assertTrue(string.equals(string))
  }

  @Test def equalsIsTrueForEqualStrings(){
    Assert.assertTrue(new JsonString("foo").equals(new JsonString("foo")))
  }

  @Test def equalsIsFalseForDifferentStrings(){
    Assert.assertFalse(new JsonString("").equals(new JsonString("foo")))
    Assert.assertFalse(new JsonString("foo").equals(new JsonString("bar")))
  }

  @Test def equalsIsFalseForNull() {
    Assert.assertFalse(new JsonString("foo").equals(null))
  }

  @Test def equalsIsFalseForSubclass() {
    Assert.assertFalse(new JsonString("foo").equals(new JsonString("foo"){}))
  }

  @Test def hashCodeEqualsForEqualStrings() {
    Assert.assertTrue(new JsonString("foo").hashCode() == new JsonString("foo").hashCode())
  }

  @Test def hashCodeDiffersForDifferentStrings() {
    Assert.assertFalse(new JsonString("").hashCode() == new JsonString("foo").hashCode())
    Assert.assertFalse(new JsonString("foo").hashCode() == new JsonString("bar").hashCode())
  }
}
