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

import org.junit.{Test, Assert}
import org.hamcrest.core.StringStartsWith
import java.io.{IOException, StringReader, Reader}
import com.nexus.TestUtils

/**
 * No description given
 *
 * @author jk-5
 */
class JsonParser_Test {

  @Test def parseRejectsEmptyString(){
    this.assertParseException(0, "Unexpected end of input", "")
  }

  @Test def parseRejectsEmptyReader(){
    val exception = TestUtils.assertException(new Runnable(){
      def run() = parse(new StringReader(""))
    }, classOf[ParseException])
    Assert.assertEquals(0, exception.getOffset)
    Assert.assertThat(exception.getMessage, StringStartsWith.startsWith("Unexpected end of input at "))
  }

  @Test def parseAcceptsArrays(){
    Assert.assertEquals(new JsonArray, parse("[]"))
  }

  @Test def parseAcceptsObjects(){
    Assert.assertEquals(new JsonObject, parse("{}"))
  }

  @Test def parseAcceptsStrings(){
    Assert.assertEquals(new JsonString(""), parse("\"\""))
  }

  @Test def parseAcceptsLitherals(){
    Assert.assertEquals(JsonValue.NULL, parse("null"))
  }

  @Test def parseStripsPadding(){
    Assert.assertEquals(new JsonArray, parse(" [  ]  "))
  }

  @Test def parseIgnoresAllWhitespace(){
    Assert.assertEquals(new JsonArray, parse("\t\r\n [\t\r\n ]\t\r\n "))
  }

  @Test def parseFailsWithUnterminatedString(){
    this.assertParseException(5, "Unexpected end of input", "[\"foo")
  }

  @Test def parseHandlesLineBreaksAndColumnsCorrectly(){
    this.assertParseException(0, 1, 0, "!")
    this.assertParseException(2, 2, 0, "[\n!")
    this.assertParseException(3, 2, 0, "[\r\n!")
    this.assertParseException(6, 3, 1, "[ \n \n !")
    this.assertParseException(7, 2, 3, "[ \r\n \r !")
  }

  @Test def arraysEmpty(){
    Assert.assertEquals("[]", parse("[]").stringify)
  }

  @Test def arraysSingleValue(){
    Assert.assertEquals("[23]", parse("[23]").stringify)
  }

  @Test def arraysMultipleValues(){
    Assert.assertEquals("[23,42]", parse("[23,42]").stringify)
  }

  @Test def arraysWithWhitespace(){
    Assert.assertEquals("[23,42]", parse("[ 23 , 42 ]").stringify)
  }

  @Test def arraysNested(){
    Assert.assertEquals("[[23],42]", parse("[[23],42]").stringify)
  }

  @Test def arraysIllegalSyntax(){
    this.assertParseException(1, "Expected value", "[,]")
    this.assertParseException(4, "Expected ',' or ']'", "[23 42]")
    this.assertParseException(4, "Expected value", "[23,]")
  }

  @Test def arraysIncomplete(){
    this.assertParseException(1, "Unexpected end of input", "[")
    this.assertParseException(2, "Unexpected end of input", "[ ")
    this.assertParseException(3, "Unexpected end of input", "[23")
    this.assertParseException(4, "Unexpected end of input", "[23 ")
    this.assertParseException(4, "Unexpected end of input", "[23,")
    this.assertParseException(5, "Unexpected end of input", "[23, ")
  }

  @Test def objectsEmpty(){
    Assert.assertEquals("{}", parse("{}").stringify)
  }

  @Test def objectsSingleValue(){
    Assert.assertEquals("{\"foo\":23}", parse("{\"foo\":23}").stringify)
  }

  @Test def objectsMultipleValues(){
    Assert.assertEquals("{\"foo\":23,\"bar\":42}", parse("{\"foo\":23,\"bar\":42}").stringify)
  }

  @Test def objectsWithWhitespace(){
    Assert.assertEquals("{\"foo\":23,\"bar\":42}", parse(" { \"foo\" : 23 , \"bar\" : 42 } ").stringify)
  }

  @Test def objectsNested(){
    Assert.assertEquals("{\"foo\":{\"bar\":42}}", parse("{\"foo\":{\"bar\":42}}").stringify)
  }

  @Test def objectsIllegalSyntax(){
    this.assertParseException(1, "Expected name", "{,}")
    this.assertParseException(1, "Expected name", "{:}")
    this.assertParseException(1, "Expected name", "{23}")
    this.assertParseException(4, "Expected ':'", "{\"a\"}")
    this.assertParseException(5, "Expected ':'", "{\"a\" \"b\"}")
    this.assertParseException(5, "Expected value", "{\"a\":}")
    this.assertParseException(8, "Expected name", "{\"a\":23,}")
    this.assertParseException(8, "Expected name", "{\"a\":23,42")
  }

  @Test def objectsIncomplete(){
    this.assertParseException(1, "Unexpected end of input", "{")
    this.assertParseException(2, "Unexpected end of input", "{ ")
    this.assertParseException(2, "Unexpected end of input", "{\"")
    this.assertParseException(4, "Unexpected end of input", "{\"a\"")
    this.assertParseException(5, "Unexpected end of input", "{\"a\" ")
    this.assertParseException(5, "Unexpected end of input", "{\"a\":")
    this.assertParseException(6, "Unexpected end of input", "{\"a\": ")
    this.assertParseException(7, "Unexpected end of input", "{\"a\":23")
    this.assertParseException(8, "Unexpected end of input", "{\"a\":23 ")
    this.assertParseException(8, "Unexpected end of input", "{\"a\":23,")
    this.assertParseException(9, "Unexpected end of input", "{\"a\":23, ")
  }

  @Test def stringsEmptyStringIsAccepted(){
    Assert.assertEquals("", parse("\"\"").asString)
  }

  @Test def stringsAsciiCharactersAreAccepted(){
    Assert.assertEquals(" ", parse("\" \"").asString)
    Assert.assertEquals("a", parse("\"a\"").asString)
    Assert.assertEquals("foo", parse("\"foo\"").asString)
    Assert.assertEquals("A2-D2", parse("\"A2-D2\"").asString)
    Assert.assertEquals("\u007f", parse("\"\u007f\"").asString)
  }

  @Test def stringsNonAsciiCharactersAreAccepted(){
    Assert.assertEquals("Русский", parse("\"Русский\"").asString)
    Assert.assertEquals("العربية", parse("\"العربية\"").asString)
    Assert.assertEquals("日本語", parse("\"日本語\"").asString)
  }

  @Test def stringsControlCharactersAreRejected(){
    this.assertParseException(3, "Expected valid string character", "\"--\n--\"")
    this.assertParseException(3, "Expected valid string character", "\"--\r\n--\"")
    this.assertParseException(3, "Expected valid string character", "\"--\t--\"")
    this.assertParseException(3, "Expected valid string character", "\"--\u0000--\"")
    this.assertParseException(3, "Expected valid string character", "\"--\u001f--\"")
  }

  @Test def stringsValidEscapesAreAccepted(){
    Assert.assertEquals(" \" ", parse("\" \\\" \"").asString)
    Assert.assertEquals(" \\ ", parse("\" \\\\ \"").asString)
    Assert.assertEquals(" / ", parse("\" \\/ \"").asString)
    Assert.assertEquals(" \u0008 ", parse("\" \\b \"").asString)
    Assert.assertEquals(" \u000c ", parse("\" \\f \"").asString)
    Assert.assertEquals(" \r ", parse("\" \\r \"").asString)
    Assert.assertEquals(" \n ", parse("\" \\n \"").asString)
    Assert.assertEquals(" \t ", parse("\" \\t \"").asString)
  }

  @Test def stringsEscapeAtStart(){
    Assert.assertEquals("\\x", parse("\"\\\\x\"").asString)
  }

  @Test def stringsEscapeAtEnd(){
    Assert.assertEquals("x\\", parse("\"x\\\\\"").asString)
  }

  @Test def stringsIllegalEscapesAreRejected(){
    this.assertParseException(2, "Expected valid escape sequence", "\"\\a\"")
    this.assertParseException(2, "Expected valid escape sequence", "\"\\x\"")
    this.assertParseException(2, "Expected valid escape sequence", "\"\\000\"")
  }

  @Test def stringsValidUnicodeEscapesAreAccepted(){
    Assert.assertEquals("\u0021", parse("\"\\u0021\"").asString)
    Assert.assertEquals("\u4711", parse("\"\\u4711\"").asString)
    Assert.assertEquals("\uffff", parse("\"\\uffff\"").asString)
    Assert.assertEquals("\uabcdx", parse("\"\\uabcdx\"").asString)
  }

  @Test def stringsIllegalUnicodeEscapesAreRejected(){
    this.assertParseException(3, "Expected hexadecimal digit", "\"\\u \"")
    this.assertParseException(3, "Expected hexadecimal digit", "\"\\ux\"")
    this.assertParseException(5, "Expected hexadecimal digit", "\"\\u20 \"")
    this.assertParseException(6, "Expected hexadecimal digit", "\"\\u000x\"")
  }

  @Test def stringsIncompleteStringsAreRejected(){
    this.assertParseException(1, "Unexpected end of input", "\"")
    this.assertParseException(4, "Unexpected end of input", "\"foo")
    this.assertParseException(5, "Unexpected end of input", "\"foo\\")
    this.assertParseException(6, "Unexpected end of input", "\"foo\\n")
    this.assertParseException(6, "Unexpected end of input", "\"foo\\u")
    this.assertParseException(7, "Unexpected end of input", "\"foo\\u0")
    this.assertParseException(9, "Unexpected end of input", "\"foo\\u000")
    this.assertParseException(10, "Unexpected end of input", "\"foo\\u0000")
  }

  @Test def numbersInteger(){
    Assert.assertEquals(new JsonNumber("0"), parse("0"))
    Assert.assertEquals(new JsonNumber("-0"), parse("-0"))
    Assert.assertEquals(new JsonNumber("1"), parse("1"))
    Assert.assertEquals(new JsonNumber("-1"), parse("-1"))
    Assert.assertEquals(new JsonNumber("23"), parse("23"))
    Assert.assertEquals(new JsonNumber("-23"), parse("-23"))
    Assert.assertEquals(new JsonNumber("1234567890"), parse("1234567890"))
    Assert.assertEquals(new JsonNumber("123456789012345678901234567890"), parse("123456789012345678901234567890"))
  }

  @Test def numbersMinusZero(){
    val value = parse("-0")

    Assert.assertEquals(0, value.asInt)
    Assert.assertEquals(0l, value.asLong)
    Assert.assertEquals(0f, value.asFloat, 0)
    Assert.assertEquals(0d, value.asDouble, 0)
  }

  @Test def numbersDecimal(){
    Assert.assertEquals(new JsonNumber("0.23"), parse("0.23"))
    Assert.assertEquals(new JsonNumber("-0.23"), parse("-0.23"))
    Assert.assertEquals(new JsonNumber("1234567890.12345678901234567890"), parse("1234567890.12345678901234567890"))
  }

  @Test def numbersWithExponent(){
    Assert.assertEquals(new JsonNumber("0.1e9"), parse("0.1e9"))
    Assert.assertEquals(new JsonNumber("0.1E9"), parse("0.1E9"))
    Assert.assertEquals(new JsonNumber("-0.23e9"), parse("-0.23e9"))
    Assert.assertEquals(new JsonNumber("0.23e9"), parse("0.23e9"))
    Assert.assertEquals(new JsonNumber("0.23e+9"), parse("0.23e+9"))
    Assert.assertEquals(new JsonNumber("0.23e-9"), parse("0.23e-9"))
  }

  @Test def numbersWithInvalidFormat(){
    this.assertParseException(0, "Expected value", "+1")
    this.assertParseException(0, "Expected value", ".1")
    this.assertParseException(1, "Unexpected character", "02")
    this.assertParseException(2, "Unexpected character", "-02")
    this.assertParseException(1, "Expected digit", "-x")
    this.assertParseException(2, "Expected digit", "1.x")
    this.assertParseException(2, "Expected digit", "1ex")
    this.assertParseException(3, "Unexpected character", "1e1x")
  }

  @Test def numbersIncomplete(){
    this.assertParseException(1, "Unexpected end of input", "-")
    this.assertParseException(2, "Unexpected end of input", "1.")
    this.assertParseException(4, "Unexpected end of input", "1.0e")
    this.assertParseException(5, "Unexpected end of input", "1.0e-")
  }

  @Test def nullComplete(){
    Assert.assertEquals(JsonValue.NULL, parse("null"))
  }

  @Test def nullIncomplete(){
    this.assertParseException(1, "Unexpected end of input", "n")
    this.assertParseException(2, "Unexpected end of input", "nu")
    this.assertParseException(3, "Unexpected end of input", "nul")
  }

  @Test def nullWithIllegalCharacter(){
    this.assertParseException(1, "Expected 'u'", "nx")
    this.assertParseException(2, "Expected 'l'", "nux")
    this.assertParseException(3, "Expected 'l'", "nulx")
    this.assertParseException(4, "Unexpected character", "nullx")
  }

  @Test def trueComplete(){
    Assert.assertEquals(JsonValue.TRUE, parse("true"))
  }

  @Test def trueIncomplete(){
    this.assertParseException(1, "Unexpected end of input", "t")
    this.assertParseException(2, "Unexpected end of input", "tr")
    this.assertParseException(3, "Unexpected end of input", "tru")
  }

  @Test def trueWithIllegalCharacter(){
    this.assertParseException(1, "Expected 'r'", "tx")
    this.assertParseException(2, "Expected 'u'", "trx")
    this.assertParseException(3, "Expected 'e'", "trux")
    this.assertParseException(4, "Unexpected character", "truex")
  }

  @Test def falseComplete(){
    Assert.assertEquals(JsonValue.FALSE, parse("false"))
  }

  @Test def falseIncomplete(){
    this.assertParseException(1, "Unexpected end of input", "f")
    this.assertParseException(2, "Unexpected end of input", "fa")
    this.assertParseException(3, "Unexpected end of input", "fal")
    this.assertParseException(4, "Unexpected end of input", "fals")
  }

  @Test def falseWithIllegalCharacter(){
    this.assertParseException(1, "Expected 'a'", "fx")
    this.assertParseException(2, "Expected 'l'", "fax")
    this.assertParseException(3, "Expected 's'", "falx")
    this.assertParseException(4, "Expected 'e'", "falsx")
    this.assertParseException(5, "Unexpected character", "falsex")
  }

  def assertParseException(offset: Int, message: String, json: String){
    val exception = TestUtils.assertException(new Runnable(){
      def run() = parse(json)
    }, classOf[ParseException])
    Assert.assertEquals(offset, exception.getOffset)
    Assert.assertThat(exception.getMessage, StringStartsWith.startsWith(message + " at "))
  }

  def assertParseException(offset: Int, line: Int, column: Int, json: String){
    val exception = TestUtils.assertException(new Runnable(){
      def run() = parse(json)
    }, classOf[ParseException])
    Assert.assertEquals("Offset equals", offset, exception.getOffset)
    Assert.assertEquals("Line equals", line, exception.getLine)
    Assert.assertEquals("Column equals", column, exception.getColumn)
  }

  def parse(json: String): JsonValue = try{
    new JsonParser(json).parse
  }catch{
    case e: IOException => throw new RuntimeException(e)
  }

  def parse(reader: Reader): JsonValue = try{
    new JsonParser(reader).parse
  }catch{
    case e: IOException => throw new RuntimeException(e)
  }
}
