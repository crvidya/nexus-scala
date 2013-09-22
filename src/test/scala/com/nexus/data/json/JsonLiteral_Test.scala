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
import java.io.StringWriter

/**
 * No description given
 *
 * @author jk-5
 */
class JsonLiteral_Test {

  private var stringWriter: StringWriter = _
  private var jsonWriter: JsonWriter = _

  @Before
  def setup(){
    stringWriter = new StringWriter
    jsonWriter = new JsonWriter(stringWriter)
  }

  @Test def writeNull(){
    JsonValue.NULL.write(jsonWriter)
    Assert.assertEquals("null", stringWriter.toString)
  }

  @Test def writeTrue(){
    JsonValue.TRUE.write(jsonWriter)
    Assert.assertEquals("true", stringWriter.toString)
  }

  @Test def writeFalse(){
    JsonValue.FALSE.write(jsonWriter)
    Assert.assertEquals("false", stringWriter.toString)
  }

  @Test def toStringNull() = Assert.assertEquals("null", JsonValue.NULL.toString)
  @Test def toStringTrue() = Assert.assertEquals("true", JsonValue.TRUE.toString)
  @Test def toStringFalse() = Assert.assertEquals("false", JsonValue.FALSE.toString)

  @Test def asBoolean(){
    Assert.assertTrue(JsonValue.TRUE.asBoolean)
    Assert.assertFalse(JsonValue.FALSE.asBoolean)
  }

  @Test(expected = classOf[UnsupportedOperationException])
  def asBooleanFailsIfNoBoolean(){
    new JsonLiteral("foo").asBoolean
  }

  @Test def isNull(){
    Assert.assertTrue(JsonValue.NULL.isNull)
    Assert.assertFalse(JsonValue.TRUE.isNull)
    Assert.assertFalse(JsonValue.FALSE.isNull)
  }

  @Test def isBoolean(){
    Assert.assertFalse(JsonValue.NULL.isBoolean)
    Assert.assertTrue(JsonValue.TRUE.isBoolean)
    Assert.assertTrue(JsonValue.FALSE.isBoolean)
  }

  @Test def isTrue(){
    Assert.assertFalse(JsonValue.NULL.isTrue)
    Assert.assertTrue(JsonValue.TRUE.isTrue)
    Assert.assertFalse(JsonValue.FALSE.isTrue)
  }

  @Test def isFalse(){
    Assert.assertFalse(JsonValue.NULL.isFalse)
    Assert.assertFalse(JsonValue.TRUE.isFalse)
    Assert.assertTrue(JsonValue.FALSE.isFalse)
  }

  @Test def equalsIsTrueForSameInstance(){
    val lit = new JsonLiteral("foo")
    Assert.assertTrue(lit.equals(lit))
  }

  @Test def equalsIsTrueForEqualObjects(){
    Assert.assertTrue(new JsonLiteral("foo").equals(new JsonLiteral("foo")))
  }

  @Test def equalsIsFalseForDiffrentObjects(){
    Assert.assertFalse(new JsonLiteral("foo").equals(new JsonLiteral("bar")))
  }

  @Test def equalsIsFalseForNull(){
    Assert.assertFalse(new JsonLiteral("foo").equals(null))
  }

  @Test def equalsIsFalseForSubclass(){
    Assert.assertFalse(new JsonLiteral("foo").equals(new JsonLiteral("foo"){}))
  }

  @Test def hashCodeEqualsForEqualObjects(){
    Assert.assertTrue(new JsonLiteral("foo").hashCode == new JsonLiteral("foo").hashCode)
  }

  @Test def hashCodeDiffersForDifferingObjects(){
    Assert.assertFalse(new JsonLiteral("foo").hashCode == new JsonLiteral("bar").hashCode)
  }
}
