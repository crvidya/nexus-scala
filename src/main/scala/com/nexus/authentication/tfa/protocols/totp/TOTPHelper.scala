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

package com.nexus.authentication.tfa.protocols.totp

import com.nexus.data.codec.Base32
import java.util
import java.util.Random
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import com.nexus.time.NexusTime

/**
 * No description given
 *
 * @author jk-5
 */
object TOTPHelper {

  def generateSecretKey: String = {
    val buffer: Array[Byte] = new Array[Byte](secretSize + numOfScratchCodes * scratchCodeSize)
    new Random().nextBytes(buffer)
    val secretKey: Array[Byte] = util.Arrays.copyOf(buffer, secretSize)
    val encodedKey: String = Base32.encode(secretKey)
    encodedKey
  }

  def getQRBarcodeURL(user: String, host: String, secret: String): String =
    "https://www.google.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=otpauth://totp/%s@%s%%3Fsecret%%3D%s".format(user, host, secret)

  def checkCode(secret: String, code: Long): Boolean = new TOTPHelper().check_code(secret, code, NexusTime.getCurrentTime)

  private def verify_code(key: Array[Byte], t: Long): Int = {
    val data: Array[Byte] = new Array[Byte](8)
    var value: Long = t
    var i: Int = 8
    while ({i -= 1;i} > -1) {
      data(i) = value.asInstanceOf[Byte]
      value >>>= 8
    }
    val signKey: SecretKeySpec = new SecretKeySpec(key, "HmacSHA1")
    val mac: Mac = Mac.getInstance("HmacSHA1")
    mac.init(signKey)
    val hash: Array[Byte] = mac.doFinal(data)
    val offset: Int = hash(20 - 1) & 0xF
    var truncatedHash: Long = 0
    i = 0
    while (i < 4) {
      truncatedHash <<= 8
      truncatedHash |= (hash(offset + i) & 0xFF)
      i += 1
    }
    truncatedHash &= 0x7FFFFFFF
    truncatedHash %= 1000000
    truncatedHash.asInstanceOf[Int]
  }

  private[tfa] final val secretSize: Int = 10
  private[tfa] final val numOfScratchCodes: Int = 5
  private[tfa] final val scratchCodeSize: Int = 8
}

class TOTPHelper {
  /**
   * set the windows size. This is an integer value representing the number of 30 second windows we allow
   * The bigger the window, the more tolerant of clock skew we are.
   *
   * @param s window size - must be >=1 and <=17.  Other values are ignored
   */
  def setWindowSize(s: Int) {
    if (s >= 1 && s <= 17) window_size = s
  }

  def check_code(secret: String, code: Long, timeMsec: Long): Boolean = {
    val decodedKey: Array[Byte] = Base32.decode(secret)
    val t: Long = (timeMsec / 1000L) / 30L
    var i: Int = -window_size
    while (i <= window_size) {
      var hash: Long = 0L
      try {
        hash = TOTPHelper.verify_code(decodedKey, t + i)
      }
      catch {
        case e: Exception => {
          e.printStackTrace()
          throw new RuntimeException(e.getMessage)
        }
      }
      if (hash == code) {
        return true
      }
      i += 1
    }
    false
  }

  private[tfa] var window_size: Int = 3
}