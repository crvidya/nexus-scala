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

class JsonString(private final val string: String) extends JsonValue {
  if(this.string == null) throw new NullPointerException("String can\'t be null")

  private [json] def write(writer: JsonWriter) = writer.writeString(this.string)
  override def isString = true
  override def asString = this.string
  override def hashCode = this.string.hashCode
  override def equals(obj: Any) =
    if(obj == null) false
    else if(this.getClass.ne(obj.getClass)) false
    else this.string == obj.asInstanceOf[JsonString].asString
}