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

package com.nexus.data.config

import java.io.BufferedReader
import java.io.File
import java.io.FileInputStream
import java.io.InputStreamReader
import java.io.PrintStream
import java.nio.charset.Charset
import java.util
import scala.collection.JavaConversions._

class Configuration(var propertyFile: File, var saveOnChange: Boolean = true, var encoding: String = Charset.defaultCharset().name()){

  var propertyMap: util.HashMap[String, String] = new util.HashMap[String, String]
  private var loading: Boolean = false

  def load(){
    clear()
    loading = true
    try {
      val reader: BufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(propertyFile), encoding))
      var reading = true
      while (reading) {
        val read: String = reader.readLine
        if (read == null) reading = false
        else{
          val equalIndex: Int = read.indexOf('=')
          if (equalIndex == -1) reading = false
          else setProperty(read.substring(0, equalIndex), read.substring(equalIndex + 1))
        }
      }
      reader.close()
    }
    catch{
      case e: Exception => throw new RuntimeException(e)
    }
    loading = false
  }

  def save(){
    try {
      val writer: PrintStream = new PrintStream(propertyFile)
      for (entry <- propertyMap.entrySet) {
        writer.println(entry.getKey + "=" + entry.getValue)
      }
      writer.close()
    }
    catch {
      case e: Exception => {
        throw new RuntimeException(e)
      }
    }
  }

  def clear() = propertyMap.clear()
  def hasProperty(key: String) = propertyMap.containsKey(key)
  def removeProperty(key: String) = if (propertyMap.remove(key) != null && saveOnChange && !loading) save()

  def setProperty(key: String, value: Int): Unit = this.setProperty(key, value.toString)
  def setProperty(key: String, value: Boolean): Unit = this.setProperty(key, value.toString)
  def setProperty(key: String, value: String) {
    propertyMap.put(key, value)
    if (saveOnChange && !loading) save()
  }
  def getProperty(property: String, defaultvalue: Int): Int = {
    try getProperty(property, Integer.toString(defaultvalue)).toInt
    catch {
      case nfe: NumberFormatException => return defaultvalue
    }
  }

  def getProperty(property: String, defaultvalue: Boolean): Boolean = {
    try getProperty(property, defaultvalue.toString).toBoolean
    catch {
      case nfe: NumberFormatException => return defaultvalue
    }
  }

  def getProperty(property: String, defaultvalue: String): String = {
    val value: String = propertyMap.get(property)
    if (value == null) {
      setProperty(property, defaultvalue)
      return defaultvalue
    }
    value
  }
  def getProperty(property: String) = propertyMap.get(property)
}