/**
 * 
 */
package com.bbytes.jwebhooks;

import java.security.SignatureException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.Header;
import org.apache.http.HttpRequest;

/**
 * 
 * Webhook consumer to consume http request from post and check hmac
 * signature.The http request can be of any form like servlet request or apache
 * http client request
 * 
 * @author Thanneer
 * 
 */
public class WebhooksConsumer {

	private String secretKey = null;

	public WebhooksConsumer(String secretKey) {
		this.secretKey = secretKey;
	}

	/**
	 * The method verifies the request by checking the http request header hmac
	 * signature and the signature created with the secret key provided, if it
	 * matches then valid request else the request was tampered or originated
	 * from a unauthorized source.The secret key is the once that is passed in
	 * constructor
	 * 
	 * @param request
	 * @return
	 * @throws Exception
	 */
	public boolean verifyRequest(HttpRequest request) throws Exception {
		if (request == null) {
			throw new IllegalArgumentException("Request cannot nbe null");
		}
		Map<String, String> headersAsMap = new HashMap<String, String>();
		for (Header header : request.getAllHeaders()) {
			headersAsMap.put(header.getName(), header.getValue());
		}
		return verifyHeaderMap(headersAsMap, request.getRequestLine().getUri(), secretKey);

	}

	/**
	 * The method verifies the request by checking the servlet request header
	 * hmac signature and the signature created with the secret key provided, if
	 * it matches then valid request else the request was tampered or originated
	 * from a unauthorized source.The secret key is the once that is passed in
	 * constructor
	 * 
	 * @param servletRequest
	 * @return
	 * @throws Exception
	 */
	public boolean verifyRequest(HttpServletRequest servletRequest) throws Exception {
		if (servletRequest == null) {
			throw new IllegalArgumentException("Request cannot nbe null");
		}
		Map<String, String> headersAsMap = new HashMap<String, String>();
		Enumeration<String> headerNames = servletRequest.getHeaderNames();
		while (headerNames.hasMoreElements()) {
			String headerName = (String) headerNames.nextElement();
			headersAsMap.put(headerName, servletRequest.getHeader(headerName));
		}
		return verifyHeaderMap(headersAsMap, servletRequest.getRequestURL().toString(), secretKey);

	}

	/**
	 * The method verifies the header hmac signature and the signature created
	 * with the secret key provided, if it matches then valid request else the
	 * request was tampered or originated from a unauthorized source.
	 * 
	 * @param headersAsMap
	 * @param postUrl
	 * @param SECRET_KEY
	 * @return
	 * @throws SignatureException
	 */
	public boolean verifyHeaderMap(Map<String, String> headersAsMap, String postUrl, String SECRET_KEY)
			throws SignatureException {
		if (headersAsMap == null)
			throw new IllegalArgumentException("Header in webhook request is null");

		String hmacSignatureInHeader = headersAsMap.get(WebhooksConstants.HMAC_SIGNATURE_HEADER);
		String simpleFormatDate = headersAsMap.get(WebhooksConstants.DATE_HEADER);
		String contentMd5 = headersAsMap.get(WebhooksConstants.CONTENT_MD5_HEADER);
		String toSign = contentMd5 + "\n" + simpleFormatDate + "\n" + postUrl;
		String hmacToBeVerified = WebhookSignature.calculateRFC2104HMAC(SECRET_KEY, toSign);
		if (hmacToBeVerified.equals(hmacSignatureInHeader))
			return true;

		return false;

	}
}
