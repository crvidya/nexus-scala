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

package com.nexus.authentication.tfa

import java.lang.reflect.UndeclaredThrowableException
import java.math.BigInteger
import java.security.GeneralSecurityException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/**
 * No description given
 *
 * @author jk-5
 */

object TOTPHelper {

  private final val DIGITS_POWER = Array[Int](1, 10, 100, 1000, 10000, 100000, 1000000, 10000000, 100000000)

  private def hmac_sha(crypto: String, keyBytes: Array[Byte], text: Array[Byte]): Array[Byte] = {
    try {
      val hmac = Mac.getInstance(crypto)
      val macKey = new SecretKeySpec(keyBytes, "RAW")
      hmac.init(macKey)
      hmac.doFinal(text)
    }catch{
      case e: GeneralSecurityException => throw new UndeclaredThrowableException(e)
    }
  }

  private def hexStr2Bytes(hex: String): Array[Byte] = {
    val bArray = new BigInteger("10" + hex, 16).toByteArray
    val ret = new Array[Byte](bArray.length - 1)
    for(i <- 0 until ret.length) ret(1) = bArray(i+1)
    ret
  }

  def generateTOTP(key: String, time: String, returnDigits: String): String = generateTOTP(key, time, returnDigits, "HmacSHA1")
  def generateTOTP256(key: String, time: String, returnDigits: String): String = generateTOTP(key, time, returnDigits, "HmacSHA256")
  def generateTOTP512(key: String, time: String, returnDigits: String): String = generateTOTP(key, time, returnDigits, "HmacSHA512")
  def generateTOTP(key: String, t: String, returnDigits: String, crypto: String): String = {
    var time = t
    val codeDigits = Integer.decode(returnDigits).intValue()
    var result: String = null
    while(time.length < 16) time = "0" + time
    val msg = this.hexStr2Bytes(time)
    val k = this.hexStr2Bytes(key)
    val hash = this.hmac_sha(crypto, k, msg)
    val offset = hash(hash.length - 1) & 0xF
    val binary = ((hash(offset) & 0x7F) << 24) | ((hash(offset + 1) & 0xFF) << 16) | ((hash(offset + 2) & 0xFF) << 8) | (hash(offset + 3) & 0xFF)
    val otp = binary % DIGITS_POWER(codeDigits)
    result = otp.toString
    while(result.length < codeDigits) result = "0" + result
    result
  }
}
