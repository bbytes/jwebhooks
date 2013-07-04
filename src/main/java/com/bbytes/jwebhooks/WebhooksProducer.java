/**
 * 
 */
package com.bbytes.jwebhooks;

import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.AutoRetryHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.PoolingClientConnectionManager;
import org.apache.http.message.BasicHeader;
import org.apache.http.params.CoreConnectionPNames;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;

/**
 * 
 * Webhook producer singleton class to send any message to WebHook URL .The
 * class performs the hmac signature on the content and is added as header that
 * can be used by consumer for verifying the request. All the messages or
 * request content should be json format.
 * 
 * @author thanneer
 * 
 */
public class WebhooksProducer {

	private static final Logger log = Logger.getLogger(WebhooksProducer.class);

	private final static String DATE_FORMAT = "EEE, d MMM yyyy HH:mm:ss z";

	protected final static int DEFAULT_TIMEOUT = 3000;

	protected int timeOut = DEFAULT_TIMEOUT;

	/**
	 * Maximum number of allowed retries if the server responds with a HTTP code
	 * in our retry code list. Default value is 3.
	 */
	protected int maxRetries = 3;

	/**
	 * Retry interval between subsequent requests, in milliseconds. Default
	 * value is 1 hr.
	 */
	protected int retryInterval = 3600;

	private HttpClient httpClient;

	private PoolingClientConnectionManager cm;

	private static WebhooksProducer instance = new WebhooksProducer();

	private WebhooksProducer() {
		// Create an HttpClient with the ThreadSafeClientConnManager.
		// This connection manager if more than one thread will
		// be using the HttpClient.
		cm = new PoolingClientConnectionManager();
		cm.setMaxTotal(2);

		// max 3 tries after timeout
		httpClient = new AutoRetryHttpClient(new DefaultHttpClient(cm), new WebhooksServiceUnavailableRetryStrategy(
				maxRetries, retryInterval));
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeOut);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeOut);
		httpClient.getParams().setParameter(ClientPNames.CONN_MANAGER_TIMEOUT, 0L);

	}

	public static WebhooksProducer getInstance() {
		return instance;
	}

	/**
	 * <p>
	 * Send message using httpClient.
	 * </p>
	 * 
	 * @param message
	 *            to be sent in request body
	 * @param postURL
	 *            the url to which the request is posted - webhook url
	 * @param secretKey
	 *            the key used to sign the content using hmac algo
	 * @return status code
	 * @throws Exception
	 */
	public int sendMessage(String message, String postURL, String secretKey) throws Exception {

		int statusCode = 0;
		HttpPost postRequest = new HttpPost(postURL);

		try {
			StringEntity input = new StringEntity(message);
			input.setContentType("application/json");
			postRequest.setEntity(input);
			// apply hmac signature , date and content md5 in header
			postRequest.setHeaders(getHeaderList(message, postURL, secretKey));

			HttpResponse httpResponse;

			httpResponse = httpClient.execute(postRequest);
			statusCode = httpResponse.getStatusLine().getStatusCode();
			EntityUtils.consume(httpResponse.getEntity());
			if (statusCode != HttpStatus.SC_OK) {
				log.error("Error Server URL " + postRequest.getURI().getPath() + " return status code " + statusCode);
				throw new HttpResponseException(statusCode, "Status code not 200 but" + statusCode);
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new Exception("Error in webhook producer", e);
		} finally {
			postRequest.releaseConnection();
		}
		return statusCode;
	}

	/**
	 * <p>
	 * Send message using httpClient.
	 * </p>
	 * 
	 * @param data
	 *            to be sent in request body
	 * @param postURL
	 *            the url to which the request is posted - webhook url
	 * @param secretKey
	 *            the key used to sign the content using hmac algo
	 * @return status code
	 * @throws Exception
	 */
	public int sendMessage(WebhooksData data, String postURL, String secretKey) throws Exception {
		if (data == null)
			throw new IllegalArgumentException("Webhook Data cannot be null");

		try {
			ObjectMapper mapper = new ObjectMapper();
			String payLoad = mapper.writeValueAsString(data);
			return sendMessage(payLoad, postURL, secretKey);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new Exception("Error in webhook producer", e);
		}
	}

	/**
	 * The method the computes the hmac signature and adds three headers - hmac
	 * signature , date and content md5 (request body md5)
	 * 
	 * @param content
	 * @param postUrl
	 * @param SECRET_KEY
	 * @return
	 * @throws SignatureException
	 * @throws NoSuchAlgorithmException
	 */
	private Header[] getHeaderList(String content, String postUrl, String SECRET_KEY) throws SignatureException,
			NoSuchAlgorithmException {
		String simpleFormatDate = new SimpleDateFormat(DATE_FORMAT).format(new Date());
		String contentMd5 = WebhookSignature.calculateMD5(content);
		String toSign = contentMd5 + "\n" + simpleFormatDate + "\n" + postUrl;
		String hmac = WebhookSignature.calculateRFC2104HMAC(SECRET_KEY, toSign);
		BasicHeader hmacHeader = new BasicHeader(WebhooksConstants.HMAC_SIGNATURE_HEADER, hmac);
		BasicHeader dateHeader = new BasicHeader(WebhooksConstants.DATE_HEADER, simpleFormatDate);
		BasicHeader contentMD5Header = new BasicHeader(WebhooksConstants.CONTENT_MD5_HEADER, contentMd5);
		Header[] headerArray = new Header[3];
		headerArray[0] = hmacHeader;
		headerArray[1] = dateHeader;
		headerArray[2] = contentMD5Header;

		return headerArray;
	}

	/**
	 * @return the timeOut
	 */
	public int getTimeOut() {
		return timeOut;
	}

	/**
	 * @param timeOut
	 *            the timeOut to set
	 */
	public void setTimeOut(int timeOut) {
		this.timeOut = timeOut;
		httpClient.getParams().setParameter(CoreConnectionPNames.CONNECTION_TIMEOUT, timeOut);
		httpClient.getParams().setParameter(CoreConnectionPNames.SO_TIMEOUT, timeOut);
	}

	/**
	 * @return the maxRetries
	 */
	public int getMaxRetries() {
		return maxRetries;
	}

	/**
	 * @param maxRetries
	 *            the maxRetries to set
	 */
	public void setMaxRetries(int maxRetries) {
		this.maxRetries = maxRetries;
	}

	/**
	 * @return the retryInterval
	 */
	public int getRetryInterval() {
		return retryInterval;
	}

	/**
	 * @param retryInterval
	 *            the retryInterval to set
	 */
	public void setRetryInterval(int retryInterval) {
		this.retryInterval = retryInterval;
	}

	public void close() {
		if (cm != null)
			cm.shutdown();

		instance = null;
	}
}
