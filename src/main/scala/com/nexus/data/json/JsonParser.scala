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

import java.io.Reader

object JsonParser {
  private def isWhiteSpace(ch: Int) = ch == ' ' || ch == '\t' || ch == '\n' || ch == '\r'
  private def isDigit(ch: Int) = ch >= '0' && ch <= '9'
  private def isHexDigit(ch: Int) = ch >= '0' && ch <= '9' || ch >= 'a' && ch <= 'f' || ch >= 'A' && ch <= 'F'
}

class JsonParser(private final val reader: Reader) {

  private final val recorder = new StringBuilder
  private var current: Int = 0
  private var line: Int = 0
  private var offset: Int = 0
  private var lineOffset: Int = 0

  private[json] def parse: JsonValue = {
    start()
    skipWhiteSpace()
    val result: JsonValue = readValue
    skipWhiteSpace()
    if(!endOfText) throw this.error("Unexpected character")
    result
  }

  private def start(){
    line = 1
    offset = -1
    lineOffset = 0
    read()
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
    recorder.setLength(0)
    while (current != '"') {
      if (current == '\\') {
        readEscape()
      }
      else if (current < 0x20) {
        throw expected("valid string character")
      }
      else {
        recorder.append(current.asInstanceOf[Char])
        read()
      }
    }
    read()
    new JsonString(recorder.toString())
  }

  private def readEscape(){
    read()
    current match {
      case '"' => recorder.append(current.asInstanceOf[Char])
      case '/' => recorder.append(current.asInstanceOf[Char])
      case '\\' => recorder.append(current.asInstanceOf[Char])
      case 'b' => recorder.append('\b')
      case 'f' => recorder.append('\f')
      case 'n' => recorder.append('\n')
      case 'r' => recorder.append('\r')
      case 't' => recorder.append('\t')
      case 'u' => {
          val hexChars: Array[Char] = new Array[Char](4)
          for(i <- 0 until 4) {
            read()
            if (!JsonParser.isHexDigit(current)) {
              throw expected("hexadecimal digit")
            }
            hexChars(i) = current.asInstanceOf[Char]
          }
          recorder.append(Integer.parseInt(String.valueOf(hexChars), 16).asInstanceOf[Char])
        }
      case _ => throw expected("valid escape sequence")
    }
    read()
  }

  private def readNumber: JsonValue = {
    recorder.setLength(0)
    readAndAppendChar('-')
    val firstDigit: Int = current
    if (!readAndAppendDigit) {
      throw expected("digit")
    }
    if (firstDigit != '0') {
      while (readAndAppendDigit) {
      }
    }
    readFraction
    readExponent
    new JsonNumber(recorder.toString())
  }

  private def readFraction: Boolean = {
    if (!readAndAppendChar('.')) {
      return false
    }
    if (!readAndAppendDigit) {
      throw expected("digit")
    }
    while (readAndAppendDigit) {
    }
    true
  }

  private def readExponent: Boolean = {
    if (!readAndAppendChar('e') && !readAndAppendChar('E')) {
      return false
    }
    if (!readAndAppendChar('+')) {
      readAndAppendChar('-')
    }
    if (!readAndAppendDigit) {
      throw expected("digit")
    }
    while (readAndAppendDigit) {
    }
    true
  }

  private def readName: String = {
    if (current != '"') {
      throw expected("name")
    }
    readString
    recorder.toString()
  }

  private def readAndAppendChar(ch: Char): Boolean = {
    if (current != ch) {
      return false
    }
    recorder.append(ch)
    read()
    true
  }

  private def readChar(ch: Char): Boolean = {
    if (current != ch) {
      return false
    }
    read()
    true
  }

  private def readAndAppendDigit: Boolean = {
    if (!JsonParser.isDigit(current)) return false
    recorder.append(current.asInstanceOf[Char])
    read()
    true
  }

  private def skipWhiteSpace() = while (JsonParser.isWhiteSpace(current) && !endOfText) read()

  private def read(){
    if (endOfText) throw error("Unexpected end of input")
    offset += 1
    if (current == '\n') {
      line += 1
      lineOffset = offset
    }
    current = reader.read
  }

  private def endOfText = current == -1

  private def expected(expected: String): ParseException = {
    if (endOfText) {
      return error("Unexpected end of input")
    }
    error("Expected " + expected)
  }

  private def error(message: String) = new ParseException(message, offset, line, offset - lineOffset)
}