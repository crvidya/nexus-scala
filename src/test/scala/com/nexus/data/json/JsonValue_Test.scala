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

import org.junit.{Assert, Test}
import java.io.{StringWriter, StringReader}
import org.mockito.Mockito
import com.nexus.TestUtils

/**
 * No description given
 *
 * @author jk-5
 */
class JsonValue_Test {

  @Test def valueOfInt(){
    Assert.assertEquals("0", JsonValue.valueOf(0).toString)
    Assert.assertEquals("23", JsonValue.valueOf(23).toString)
    Assert.assertEquals("-1", JsonValue.valueOf(-1).toString)
    Assert.assertEquals("2147483647", JsonValue.valueOf(Int.MaxValue).toString)
    Assert.assertEquals("-2147483648", JsonValue.valueOf(Int.MinValue).toString)
  }

  @Test def valueOfLong(){
    Assert.assertEquals("0", JsonValue.valueOf(0l).toString)
    Assert.assertEquals("9223372036854775807", JsonValue.valueOf(Long.MaxValue).toString)
    Assert.assertEquals("-9223372036854775808", JsonValue.valueOf(Long.MinValue).toString)
  }

  @Test def valueOfFloat(){
    Assert.assertEquals("23.5", JsonValue.valueOf(23.5f).toString)
    Assert.assertEquals("-3.1416", JsonValue.valueOf(-3.1416f).toString)
    Assert.assertEquals("1.23E-6", JsonValue.valueOf(0.00000123f).toString)
    Assert.assertEquals("-1.23E7", JsonValue.valueOf(-12300000f).toString)
  }

  @Test def valueOfFloatCutsOffPointZero(){
    Assert.assertEquals("0", JsonValue.valueOf(0f).toString)
    Assert.assertEquals("-1", JsonValue.valueOf(-1f).toString)
    Assert.assertEquals("10", JsonValue.valueOf(10f).toString)
  }

  @Test def valueOfFloatFailsWithInfinity(){
    TestUtils.assertException(new Runnable(){
      def run() = JsonValue.valueOf(Float.PositiveInfinity)
    }, classOf[IllegalArgumentException], "Infinite and NaN values not permitted in JSON")
  }

  @Test def valueOfFloatFailsWithNaN(){
    TestUtils.assertException(new Runnable(){
      def run() = JsonValue.valueOf(Float.NaN)
    }, classOf[IllegalArgumentException], "Infinite and NaN values not permitted in JSON")
  }

  @Test def valueOfDouble(){
    Assert.assertEquals("23.5", JsonValue.valueOf(23.5d).toString)
    Assert.assertEquals("-3.1416", JsonValue.valueOf(-3.1416d).toString)
    Assert.assertEquals("1.23E-6", JsonValue.valueOf(0.00000123d).toString)
    Assert.assertEquals("-1.23E7", JsonValue.valueOf(-12300000d).toString)
  }

  @Test def valueOfDoubleCutsOffPointZero(){
    Assert.assertEquals("0", JsonValue.valueOf(0d).toString)
    Assert.assertEquals("-1", JsonValue.valueOf(-1d).toString)
    Assert.assertEquals("10", JsonValue.valueOf(10d).toString)
  }

  @Test def valueOfDoubleFailsWithInfinity(){
    TestUtils.assertException(new Runnable(){
      def run() = JsonValue.valueOf(Double.PositiveInfinity)
    }, classOf[IllegalArgumentException], "Infinite and NaN values not permitted in JSON")
  }

  @Test def valueOfDoubleFailsWithNaN(){
    TestUtils.assertException(new Runnable(){
      def run() = JsonValue.valueOf(Double.NaN)
    }, classOf[IllegalArgumentException], "Infinite and NaN values not permitted in JSON")
  }

  @Test def valueOfBoolean(){
    Assert.assertSame(JsonValue.TRUE, JsonValue.valueOf(true))
    Assert.assertSame(JsonValue.FALSE, JsonValue.valueOf(false))
  }

  @Test def valueOfString(){
    Assert.assertEquals("", JsonValue.valueOf("").asString)
    Assert.assertEquals("Hello", JsonValue.valueOf("Hello").asString)
    Assert.assertEquals("\"Hello\"", JsonValue.valueOf("\"Hello\"").asString)
  }

  @Test def valueOfStringToleratesNull(){
    Assert.assertSame(JsonValue.NULL, JsonValue.valueOf(null))
  }

  @Test def readFromString(){
    Assert.assertEquals(new JsonArray, JsonValue.readFrom("[]"))
    Assert.assertEquals(new JsonObject, JsonValue.readFrom("{}"))
    Assert.assertEquals(JsonValue.valueOf("foo"), JsonValue.readFrom("\"foo\""))
    Assert.assertEquals(JsonValue.valueOf(23), JsonValue.readFrom("23"))
    Assert.assertSame(JsonValue.NULL, JsonValue.readFrom("null"))
  }

  @Test def readFromReader(){
    Assert.assertEquals(new JsonArray, JsonValue.readFrom(new StringReader("[]")))
    Assert.assertEquals(new JsonObject, JsonValue.readFrom(new StringReader("{}")))
    Assert.assertEquals(JsonValue.valueOf("foo"), JsonValue.readFrom(new StringReader("\"foo\"")))
    Assert.assertEquals(JsonValue.valueOf(23), JsonValue.readFrom(new StringReader("23")))
    Assert.assertSame(JsonValue.NULL, JsonValue.readFrom(new StringReader("null")))
  }

  @Test def readFromReaderDoesNotCloseReader(){
    val reader = Mockito.spy(new StringReader("{}"))
    JsonValue.readFrom(reader)
    Mockito.verify(reader, Mockito.never()).close()
  }

  @Test def writeTo(){
    val value: JsonValue = new JsonObject
    val writer = new StringWriter
    value.writeTo(writer)
    Assert.assertEquals("{}", writer.toString)
  }

  @Test def writeToDoesNotCloseWriter(){
    val value: JsonValue = new JsonObject
    val writer = Mockito.spy(new StringWriter)
    value.writeTo(writer)
    Mockito.verify(writer, Mockito.never()).close()
  }

  @Test def asObjectFailsOnIncompatibleType(){
    TestUtils.assertException(new Runnable(){
      def run() = JsonValue.NULL.asObject
    }, classOf[UnsupportedOperationException], "Not an object: null")
  }

  @Test def asArrayFailsOnIncompatibleType(){
    TestUtils.assertException(new Runnable(){
      def run() = JsonValue.NULL.asArray
    }, classOf[UnsupportedOperationException], "Not an array: null")
  }

  @Test def asStringFailsOnIncompatibleType(){
    TestUtils.assertException(new Runnable(){
      def run() = JsonValue.NULL.asString
    }, classOf[UnsupportedOperationException], "Not a string: null")
  }

  @Test def asIntFailsOnIncompatibleType(){
    TestUtils.assertException(new Runnable(){
      def run() = JsonValue.NULL.asInt
    }, classOf[UnsupportedOperationException], "Not a number: null")
  }

  @Test def asLongFailsOnIncompatibleType(){
    TestUtils.assertException(new Runnable(){
      def run() = JsonValue.NULL.asLong
    }, classOf[UnsupportedOperationException], "Not a number: null")
  }

  @Test def asFloatFailsOnIncompatibleType(){
    TestUtils.assertException(new Runnable(){
      def run() = JsonValue.NULL.asFloat
    }, classOf[UnsupportedOperationException], "Not a number: null")
  }

  @Test def asDoubleFailsOnIncompatibleType(){
    TestUtils.assertException(new Runnable(){
      def run() = JsonValue.NULL.asDouble
    }, classOf[UnsupportedOperationException], "Not a number: null")
  }

  @Test def asBooleanFailsOnIncompatibleType(){
    TestUtils.assertException(new Runnable(){
      def run() = JsonValue.NULL.asBoolean
    }, classOf[UnsupportedOperationException], "Not a boolean: null")
  }

  @Test def asXXXReturnsFalseForIncompatibleType(){
    val value = new JsonValue {
      def write(writer: JsonWriter) {}
    }
    Assert.assertFalse(value.isArray)
    Assert.assertFalse(value.isObject)
    Assert.assertFalse(value.isString)
    Assert.assertFalse(value.isNumber)
    Assert.assertFalse(value.isBoolean)
    Assert.assertFalse(value.isNull)
    Assert.assertFalse(value.isTrue)
    Assert.assertFalse(value.isFalse)
  }
}
