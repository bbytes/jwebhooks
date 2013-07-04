package com.bbytes.jwebhooks;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ServiceUnavailableRetryStrategy;
import org.apache.http.protocol.HttpContext;

/**
 * The class that decides the strategy when the client is not able to reach the
 * server to send request , how to retry and ho many times are decided by this
 * class
 * 
 * @author Thanneer
 * 
 */
public class WebhooksServiceUnavailableRetryStrategy implements ServiceUnavailableRetryStrategy {

	/**
	 * Maximum number of allowed retries if the server responds with a HTTP code
	 * in our retry code list. Default value is 3.
	 */
	private final int maxRetries;

	/**
	 * Retry interval between subsequent requests, in milliseconds. Default
	 * value is 3 second.
	 */
	private final long retryInterval;

	
	public WebhooksServiceUnavailableRetryStrategy(int maxRetries, int retryInterval) {
		super();
		if (maxRetries < 1) {
			throw new IllegalArgumentException("MaxRetries must be greater than 1");
		}
		if (retryInterval < 1) {
			throw new IllegalArgumentException("Retry interval must be greater than 1");
		}
		this.maxRetries = maxRetries;
		this.retryInterval = retryInterval;
	}

	/**
	 * Defaults to 3 seconds and 3 retries
	 */
	public WebhooksServiceUnavailableRetryStrategy() {
		this(3, 3000);
	}

	public boolean retryRequest(HttpResponse response, int executionCount, HttpContext context) {
		return executionCount <= maxRetries && response.getStatusLine().getStatusCode() != HttpStatus.SC_OK;
	}

	public long getRetryInterval() {
		return retryInterval;
	}

}
