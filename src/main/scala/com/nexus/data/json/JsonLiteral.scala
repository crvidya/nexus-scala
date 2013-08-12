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

class JsonLiteral(private final val value: String) extends JsonValue {

  private [json] def write(writer: JsonWriter) = writer.write(value)
  override def toString = this.value
  override def asBoolean = if(this.isBoolean) this.isTrue else super.asBoolean
  override def isNull = this == JsonValue.NULL
  override def isBoolean = this == JsonValue.TRUE || this == JsonValue.FALSE
  override def isTrue = this == JsonValue.TRUE
  override def isFalse = this == JsonValue.FALSE
  override def hashCode = this.value.hashCode
  override def equals(obj: Any) =
    if(obj == null) false
    else if(this.getClass ne obj.getClass) false
    else this.value == obj.asInstanceOf[JsonLiteral].toString
}