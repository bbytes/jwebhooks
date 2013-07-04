package com.bbytes.jwebhooks;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Utility method that is used to create hmac signature for the web hook post
 * request.
 * 
 * @author Thanneer
 * 
 */
public class WebhookSignature {

	private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

	/**
	 * Computes RFC 2104-compliant HMAC signature. * @param data The data to be
	 * signed.
	 * 
	 * @param key
	 *            The signing key.
	 * @return The Base64-encoded RFC 2104-compliant HMAC signature.
	 * @throws java.security.SignatureException
	 *             when signature generation fails
	 */
	public static String calculateRFC2104HMAC(String data, String key) throws java.security.SignatureException {
		String result;
		try {

			// get an hmac_sha1 key from the raw key bytes
			SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);

			// get an hmac_sha1 Mac instance and initialize with the signing key
			Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
			mac.init(signingKey);

			// compute the hmac on input data bytes
			byte[] rawHmac = mac.doFinal(data.getBytes());

			// base64-encode the hmac
			result = encodeBase64(rawHmac);

		} catch (Exception e) {
			throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
		}
		return result;
	}

	/**
	 * Calculate the MD5 of the given content.
	 * 
	 * @param contentToEncode
	 * @return
	 * @throws NoSuchAlgorithmException
	 */
	public static String calculateMD5(String contentToEncode) throws NoSuchAlgorithmException {
		MessageDigest digest = MessageDigest.getInstance("MD5");
		digest.update(contentToEncode.getBytes());
		String result = new String(Base64.encodeBase64(digest.digest()));
		return result;
	}

	/**
	 * Performs base64-encoding of input bytes.
	 * 
	 * @param rawData
	 *            * Array of bytes to be encoded.
	 * @return * The base64 encoded string representation of rawData.
	 */
	public static String encodeBase64(byte[] rawData) {
		return Base64.encodeBase64String(rawData);

	}
}