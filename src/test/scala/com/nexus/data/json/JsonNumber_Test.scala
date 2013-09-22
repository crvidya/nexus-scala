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
import com.nexus.data.json.{JsonNumber, JsonWriter}
import org.junit.{Assert, Test, Before}

/**
 * No description given
 *
 * @author jk-5
 */
class JsonNumber_Test {

  var output: StringWriter = _
  var writer: JsonWriter = _

  @Before def setup() {
    output = new StringWriter()
    writer = new JsonWriter(output)
  }

  @Test(expected = classOf[NullPointerException])
  def constructorFailsWithNull() {
    new JsonNumber(null)
  }

  @Test def write(){
    new JsonNumber("23").write(writer)
    Assert.assertEquals("23", output.toString)
  }

  @Test def toStringReturnsInputString(){
    Assert.assertEquals("foo", new JsonNumber("foo").toString())
  }

  @Test def isNumber() {
    Assert.assertTrue(new JsonNumber("23").isNumber)
  }

  @Test def asInt() {
    Assert.assertEquals(23, new JsonNumber("23").asInt)
  }

  @Test(expected = classOf[NumberFormatException])
  def asIntFailsWithExceedingValues() {
    new JsonNumber("10000000000").asInt
  }

  @Test(expected = classOf[NumberFormatException])
  def asIntFailsWithExponent() {
    new JsonNumber("1e5").asInt
  }

  @Test(expected = classOf[NumberFormatException])
  def asIntFailsWithFractional() {
    new JsonNumber("23.5").asInt
  }

  @Test def asLong() {
    Assert.assertEquals(23l, new JsonNumber("23").asLong)
  }

  @Test(expected = classOf[NumberFormatException])
  def asLongFailsWithExceedingValues() {
    new JsonNumber("10000000000000000000").asLong
  }

  @Test(expected = classOf[NumberFormatException])
  def asLongFailsWithExponent() {
    new JsonNumber("1e5").asLong
  }

  @Test(expected = classOf[NumberFormatException])
  def asLongFailsWithFractional() {
    new JsonNumber("23.5").asLong
  }

  @Test def asFloat() {
    Assert.assertEquals(23.05f, new JsonNumber("23.05").asFloat, 0)
  }

  @Test def asFloatReturnsInfinityForExceedingValues() {
    Assert.assertEquals(Float.PositiveInfinity, new JsonNumber("1e50").asFloat, 0)
    Assert.assertEquals(Float.NegativeInfinity, new JsonNumber("-1e50").asFloat, 0)
  }

  @Test def asDouble() {
    val result = new JsonNumber("23.05").asDouble
    Assert.assertEquals(23.05, result, 0)
  }

  @Test def asDoubleReturnsInfinityForExceedingValues() {
    Assert.assertEquals(Double.PositiveInfinity, new JsonNumber("1e500").asDouble, 0)
    Assert.assertEquals(Double.NegativeInfinity, new JsonNumber("-1e500").asDouble, 0)
  }

  @Test def equalsRrueForSameInstance() {
    val number = new JsonNumber("23")
    Assert.assertTrue(number.equals(number))
  }

  @Test def equalsTrueForEqualNumberStrings() {
    Assert.assertTrue(new JsonNumber("23").equals(new JsonNumber("23")))
  }

  @Test def equalsFalseForDifferentNumberStrings() {
    Assert.assertFalse(new JsonNumber("23").equals(new JsonNumber("42")))
    Assert.assertFalse(new JsonNumber("1e+5").equals(new JsonNumber("1e5")))
  }

  @Test def equalsFalseForNull() {
    Assert.assertFalse(new JsonNumber("23").equals(null))
  }

  @Test def equalsFalseForSubclass() {
    Assert.assertFalse(new JsonNumber("23").equals(new JsonNumber("23"){}))
  }

  @Test def hashCodeEqualsForEqualStrings() {
    Assert.assertTrue(new JsonNumber("23").hashCode() == new JsonNumber("23").hashCode())
  }

  @Test def hashCodeDiffersForDifferentStrings() {
    Assert.assertFalse(new JsonNumber("23").hashCode() == new JsonNumber("42").hashCode())
  }
}
