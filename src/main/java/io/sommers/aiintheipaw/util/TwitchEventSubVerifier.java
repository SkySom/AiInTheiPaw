package io.sommers.aiintheipaw.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

/*
 * Based off of https://github.com/twitch4j/twitch4j/blob/master/eventsub-common/src/main/java/com/github/twitch4j/eventsub/util/EventSubVerifier.java
 * License: https://github.com/twitch4j/twitch4j/blob/master/LICENSE
 */
public class TwitchEventSubVerifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(TwitchEventSubVerifier.class);

    private static final String JAVA_HMAC_ALGORITHM = "HmacSHA256";
    private static final String SIGNATURE_HASH_PREFIX = "sha256=";
    private static final int HASH_LENGTH = 256 / 4;

    private static final ThreadLocal<Mac> HMAC_FUNCTION = ThreadLocal.withInitial(() -> {
        try {
            return Mac.getInstance(JAVA_HMAC_ALGORITHM);
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    });

    public static boolean verifySignature(SecretKeySpec secret, String messageId, String messageTimestamp, byte[] requestBody, String expectedSignature) {
        if (secret == null || expectedSignature == null || messageId == null || messageTimestamp == null || requestBody == null) {
            LOGGER.warn("Could not verify eventsub signature with null argument");
            return false;
        }

        if (expectedSignature.length() - SIGNATURE_HASH_PREFIX.length() != HASH_LENGTH ||
                !expectedSignature.regionMatches(true, 0, SIGNATURE_HASH_PREFIX, 0, SIGNATURE_HASH_PREFIX.length())) {
            LOGGER.debug("Could not verify unknown eventsub signature hash scheme; {}", expectedSignature);
            return false;
        }

        final Mac mac = HMAC_FUNCTION.get();
        if (mac == null) {
            LOGGER.error("Unable to prepare hash function for eventsub signature verification!");
            return false;
        }

        try {
            mac.init(secret);
        } catch (InvalidKeyException e) {
            LOGGER.error("Unable to initialize secret for eventsub signature verification!", e);
            return false;
        }

        final byte[] id = messageId.getBytes(StandardCharsets.UTF_8);
        final byte[] timestamp = messageTimestamp.getBytes(StandardCharsets.UTF_8);
        final byte[] message = new byte[id.length + timestamp.length + requestBody.length];
        System.arraycopy(id, 0, message, 0, id.length);
        System.arraycopy(timestamp, 0, message, id.length, timestamp.length);
        System.arraycopy(requestBody, 0, message, id.length + timestamp.length, requestBody.length);
        final byte[] computedHmac = mac.doFinal(message);
        mac.reset(); // Clean-up
        final byte[] expectedHmac = hexStringToByteArray(expectedSignature.substring(SIGNATURE_HASH_PREFIX.length()));
        return MessageDigest.isEqual(computedHmac, expectedHmac); // constant-time comparison
    }

    /**
     * @see #verifySignature(SecretKeySpec, String, String, byte[], String)
     */
    public static boolean verifySignature(byte[] secret, String messageId, String messageTimestamp, byte[] requestBody, String expectedSignature) {
        return verifySignature(new SecretKeySpec(secret, JAVA_HMAC_ALGORITHM), messageId, messageTimestamp, requestBody, expectedSignature);
    }

    /**
     * @see #verifySignature(SecretKeySpec, String, String, byte[], String)
     */
    public static boolean verifySignature(String secret, String messageId, String messageTimestamp, byte[] requestBody, String expectedSignature) {
        return verifySignature(secret.getBytes(StandardCharsets.UTF_8), messageId, messageTimestamp, requestBody, expectedSignature);
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
