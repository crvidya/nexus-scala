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
class JsonWriter_Test {

  private var output: StringWriter = _
  private var writer: JsonWriter = _

  @Before
  def setup(){
    output = new StringWriter
    writer = new JsonWriter(output)
  }

  @Test def writePassesThrough(){
    writer.write("foo")
    Assert.assertEquals("foo", output.toString)
  }

  @Test def writeStringEmpty(){
    writer.writeString("")
    Assert.assertEquals("\"\"", output.toString)
  }

  @Test def writeStringEscapesBackslashes(){
    writer.writeString("foo\\bar")
    Assert.assertEquals("\"foo\\\\bar\"", output.toString)
  }

  @Test def escapesQuotes(){
    writer.writeString("a\"b")
    Assert.assertEquals("\"a\\\"b\"", output.toString)
  }

  @Test def escapesEscapedQuotes(){
    writer.writeString("foo\\\"bar")
    Assert.assertEquals("\"foo\\\\\\\"bar\"", output.toString)
  }

  @Test def escapesNewLine(){
    writer.writeString("foo\nbar")
    Assert.assertEquals("\"foo\\nbar\"", output.toString)
  }

  @Test def escapesWindowsNewLine(){
    writer.writeString("foo\r\nbar")
    Assert.assertEquals("\"foo\\r\\nbar\"", output.toString)
  }

  @Test def escapesTabs(){
    writer.writeString("foo\tbar")
    Assert.assertEquals("\"foo\\tbar\"", output.toString)
  }

  @Test def escapesSpecialCharacters(){
    writer.writeString("foo\u2028bar\u2029")
    Assert.assertEquals("\"foo\\u2028bar\\u2029\"", output.toString)
  }

  @Test def escapesZeroCharacter(){
    writer.writeString(string('f', 'o', 'o', 0.toChar, 'b', 'a', 'r'))
    Assert.assertEquals("\"foo\\u0000bar\"", output.toString)
  }

  @Test def escapesEscapeCharacter(){
    writer.writeString(string('f', 'o', 'o', 27.toChar, 'b', 'a', 'r'))
    Assert.assertEquals("\"foo\\u001bbar\"", output.toString)
  }

  @Test def escapesControlCharacters(){
    writer.writeString(string(1.toChar, 8.toChar, 15.toChar, 16.toChar, 31.toChar))
    Assert.assertEquals("\"\\u0001\\u0008\\u000f\\u0010\\u001f\"", output.toString)
  }

  @Test def escapesFirstChar(){
    writer.writeString(string('\\', 'x'))
    Assert.assertEquals("\"\\\\x\"", output.toString)
  }

  @Test def writeObjectParts(){
    writer.writeBeginObject()
    writer.writeNameValueSeparator()
    writer.writeObjectValueSeparator()
    writer.writeEndObject()
    Assert.assertEquals("{:,}", output.toString)
  }

  @Test def writeObjectEmpty(){
    writer.writeObject(new JsonObject)
    Assert.assertEquals("{}", output.toString)
  }

  @Test def writeObjectWithSingleValue(){
    writer.writeObject(new JsonObject().add("a", 23))
    Assert.assertEquals("{\"a\":23}", output.toString)
  }

  @Test def writeObjectWithMultipleValues(){
    writer.writeObject(new JsonObject().add("a", 23).add("b", 3.14f).add("c", "foo").add("d", true).add("e", null.asInstanceOf[String]))
    Assert.assertEquals("{\"a\":23,\"b\":3.14,\"c\":\"foo\",\"d\":true,\"e\":null}", output.toString)
  }

  @Test def writeArrayParts(){
    writer.writeBeginArray()
    writer.writeArrayValueSeparator()
    writer.writeEndArray()
    Assert.assertEquals("[,]", output.toString)
  }

  @Test def writeArrayEmpty(){
    writer.writeArray(new JsonArray)
    Assert.assertEquals("[]", output.toString)
  }

  @Test def writeArrayWithSingleValue(){
    writer.writeArray(new JsonArray().add(23))
    Assert.assertEquals("[23]", output.toString)
  }

  @Test def writeArrayWithMultipleValues(){
    writer.writeArray(new JsonArray().add(23).add("foo").add(false))
    Assert.assertEquals("[23,\"foo\",false]", output.toString)
  }

  def string(chars: Char *): String = String.valueOf(chars.toArray)
}
