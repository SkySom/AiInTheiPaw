package io.sommers.zio.twitch
package util

import com.sun.org.slf4j.internal.LoggerFactory
import zio.{IO, ZIO}

import java.nio.charset.StandardCharsets
import java.security.{InvalidKeyException, MessageDigest, NoSuchAlgorithmException}
import java.time.Instant
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec

/*
 * Based off of https://github.com/twitch4j/twitch4j/blob/master/eventsub-common/src/main/java/com/github/twitch4j/eventsub/util/EventSubVerifier.java
 * License: https://github.com/twitch4j/twitch4j/blob/master/LICENSE
 */
private class TwitchSignatureVerifier {

}

object TwitchSignatureVerifier {
  private val JAVA_HMAC_ALGORITHM = "HmacSHA256"
  private val SIGNATURE_HASH_PREFIX = "sha256="
  private val HASH_LENGTH = 256 / 4
  private val HMAC_FUNCTION = ThreadLocal.withInitial(() => {
    try Mac.getInstance(JAVA_HMAC_ALGORITHM)
    catch {
      case _: NoSuchAlgorithmException =>
        null
    }
  })

  def verifySignature(secret: SecretKeySpec, messageId: String, messageTimestamp: Instant, requestBody: Array[Byte], expectedSignature: String): IO[Throwable, Boolean] = {
    if (secret == null || expectedSignature == null || messageId == null || messageTimestamp == null || requestBody == null) {
      return ZIO.fail(new IllegalArgumentException("Found Null arguments"))
    }
    if (expectedSignature.length - SIGNATURE_HASH_PREFIX.length != HASH_LENGTH || !expectedSignature.regionMatches(true, 0, SIGNATURE_HASH_PREFIX, 0, SIGNATURE_HASH_PREFIX.length)) {
      return ZIO.fail(new IllegalArgumentException("Could not verify unknown eventsub signature hash scheme; {}".formatted(expectedSignature)))
    }
    val mac = HMAC_FUNCTION.get
    if (mac == null) {
      return ZIO.fail(new IllegalStateException("Unable to get HMAC"))
    }
    try mac.init(secret)
    catch {
      case e: InvalidKeyException => return ZIO.fail(e)
    }
    val id = messageId.getBytes(StandardCharsets.UTF_8)
    val timestamp = messageTimestamp.toString.getBytes(StandardCharsets.UTF_8)
    val message = new Array[Byte](id.length + timestamp.length + requestBody.length)
    System.arraycopy(id, 0, message, 0, id.length)
    System.arraycopy(timestamp, 0, message, id.length, timestamp.length)
    System.arraycopy(requestBody, 0, message, id.length + timestamp.length, requestBody.length)
    val computedHmac = mac.doFinal(message)
    mac.reset() // Clean-up

    val expectedHmac = hexStringToByteArray(expectedSignature.substring(SIGNATURE_HASH_PREFIX.length))
    ZIO.succeed(MessageDigest.isEqual(computedHmac, expectedHmac)) // constant-time comparison
  }

  /**
   * @see #verifySignature(SecretKeySpec, String, String, byte[], String)
   */
  def verifySignature(secret: Array[Byte], messageId: String, messageTimestamp: Instant, requestBody: Array[Byte], expectedSignature: String): IO[Throwable, Boolean] =
    verifySignature(new SecretKeySpec(secret, JAVA_HMAC_ALGORITHM), messageId, messageTimestamp, requestBody, expectedSignature)

  /**
   * @see #verifySignature(SecretKeySpec, String, String, byte[], String)
   */
  def verifySignature(secret: String, messageId: String, messageTimestamp: Instant, requestBody: Array[Byte], expectedSignature: String): IO[Throwable, Boolean] =
    verifySignature(secret.getBytes(StandardCharsets.UTF_8), messageId, messageTimestamp, requestBody, expectedSignature)

  private def hexStringToByteArray(s: String): Array[Byte] = {
    val len = s.length
    val data = new Array[Byte](len / 2)
    var i = 0
    while (i < len) {
      data(i / 2) = ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16)).toByte

      i += 2
    }
    data
  }
}
