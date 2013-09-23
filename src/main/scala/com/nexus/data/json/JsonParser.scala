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

import java.io.{StringReader, Reader}
import com.nexus.data.BufferedTextReader

object JsonParser {
  private def isWhiteSpace(ch: Int) = ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r'
  private def isDigit(ch: Int) = ch >= '0' && ch <= '9'
  private def isHexDigit(ch: Int) = ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F'
  private def isEndOfText(ch: Int) = ch == -1
}

class JsonParser(private final val _reader: Reader) {

  def this(json: String) = this(new StringReader(json))

  private final val reader = new BufferedTextReader(this._reader)
  private var current: Int = 0

  private[json] def parse: JsonValue = {
    read()
    skipWhiteSpace()
    val result: JsonValue = readValue
    skipWhiteSpace()
    if(!JsonParser.isEndOfText(this.current)) throw this.error("Unexpected character")
    result
  }

  private def readValue: JsonValue =
    current match {
      case 'n' => readNull
      case 't' => readTrue
      case 'f' => readFalse
      case '"' => readString
      case '[' => readArray
      case '{' => readObject
      case '-'|'0'|'1'|'2'|'3'|'4'|'5'|'6'|'7'|'8'|'9' => readNumber
      case _ => throw expected("value")
    }

  private def readArray: JsonArray = {
    read()
    val array: JsonArray = new JsonArray
    skipWhiteSpace()
    if (readChar(']')) {
      return array
    }
    do {
      skipWhiteSpace()
      array.add(readValue)
      skipWhiteSpace()
    } while (readChar(','))
    if (!readChar(']')) {
      throw expected("',' or ']'")
    }
    array
  }

  private def readObject: JsonObject = {
    read()
    val `object`: JsonObject = new JsonObject
    skipWhiteSpace()
    if (readChar('}')) {
      return `object`
    }
    do {
      skipWhiteSpace()
      val name: String = readName
      skipWhiteSpace()
      if (!readChar(':')) {
        throw expected("':'")
      }
      skipWhiteSpace()
      `object`.add(name, readValue)
      skipWhiteSpace()
    } while (readChar(','))
    if (!readChar('}')) {
      throw expected("',' or '}'")
    }
    `object`
  }

  private def readNull: JsonValue = {
    read()
    readRequiredChar('u')
    readRequiredChar('l')
    readRequiredChar('l')
    JsonValue.NULL
  }

  private def readTrue: JsonValue = {
    read()
    readRequiredChar('r')
    readRequiredChar('u')
    readRequiredChar('e')
    JsonValue.TRUE
  }

  private def readFalse: JsonValue = {
    read()
    readRequiredChar('a')
    readRequiredChar('l')
    readRequiredChar('s')
    readRequiredChar('e')
    JsonValue.FALSE
  }

  private def readRequiredChar(ch: Char) = if (!readChar(ch)) throw expected("'" + ch + "'")
  private def readString: JsonValue = {
    read()
    var buffer: StringBuilder = null
    this.reader.startCapture()
    while (current != '"') { //IntelliJ complains about current not being updated. It is! Ignore that!
      if (current == '\\') {
        if(buffer == null) buffer = new StringBuilder()
        buffer.append(reader.endCapture)
        this.readEscape(buffer)
        this.reader.startCapture()
      }else if (current < 0x20) {
        throw expected("valid string character")
      }else read()
    }
    var capture = this.reader.endCapture
    if(buffer != null){
      buffer.append(capture)
      capture = buffer.toString()
      buffer.setLength(0)
    }
    read()
    new JsonString(capture)
  }

  private def readEscape(buffer: StringBuilder){
    read()
    current match {
      case '"' => buffer.append(current.asInstanceOf[Char])
      case '/' => buffer.append(current.asInstanceOf[Char])
      case '\\' => buffer.append(current.asInstanceOf[Char])
      case 'b' => buffer.append('\b')
      case 'f' => buffer.append('\f')
      case 'n' => buffer.append('\n')
      case 'r' => buffer.append('\r')
      case 't' => buffer.append('\t')
      case 'u' => {
          val hexChars: Array[Char] = new Array[Char](4)
          for(i <- 0 until 4) {
            read()
            if (!JsonParser.isHexDigit(current)) {
              throw expected("hexadecimal digit")
            }
            hexChars(i) = current.asInstanceOf[Char]
          }
          buffer.append(Integer.parseInt(String.valueOf(hexChars), 16).asInstanceOf[Char])
        }
      case _ => throw expected("valid escape sequence")
    }
    read()
  }

  private def readNumber: JsonValue = {
    this.reader.startCapture()
    this.readChar('-')
    val firstDigit: Int = current
    if (!readDigit) {
      throw expected("digit")
    }
    if (firstDigit != '0') {
      while (readDigit) {
      }
    }
    readFraction
    readExponent
    new JsonNumber(this.reader.endCapture)
  }

  private def readFraction: Boolean = {
    if (!readChar('.')) {
      return false
    }
    if (!readDigit) {
      throw expected("digit")
    }
    while (readDigit) {
    }
    true
  }

  private def readExponent: Boolean = {
    if (!readChar('e') && !readChar('E')) {
      return false
    }
    if (!readChar('+')) {
      readChar('-')
    }
    if (!readDigit) {
      throw expected("digit")
    }
    while (readDigit) {
    }
    true
  }

  private def readName: String = {
    if (current != '"') {
      throw expected("name")
    }
    readString.asString
  }

  private def readChar(ch: Char): Boolean = {
    if (current != ch) {
      return false
    }
    read()
    true
  }

  private def readDigit: Boolean = {
    if (!JsonParser.isDigit(current)) return false
    read()
    true
  }

  private def skipWhiteSpace() = while (JsonParser.isWhiteSpace(current) && !JsonParser.isEndOfText(this.current)) read()

  private def read(){
    if (JsonParser.isEndOfText(this.current)) throw error("Unexpected end of input")
    current = reader.read()
  }

  private def expected(expected: String): ParseException = {
    if (JsonParser.isEndOfText(this.current)) {
      return error("Unexpected end of input")
    }
    error("Expected " + expected)
  }

  private def error(message: String): ParseException = {
    val offset = if(JsonParser.isEndOfText(this.current)) reader.getIndex else reader.getIndex - 1
    new ParseException(message, offset, reader.getLine, offset - reader.getColumn - 1)
  }
}