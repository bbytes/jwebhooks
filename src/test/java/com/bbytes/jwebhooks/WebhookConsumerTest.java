/**
 * 
 */
package com.bbytes.jwebhooks;

import static spark.Spark.post;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * 
 * @author Thanneer
 * 
 */
public class WebhookConsumerTest {

	private WebhooksProducer producer;

	private int index = 1;

	private int statusCode = 0;

	@Before
	public void setup() {
		post(new Route("/test/jwebhooks") {
			@Override
			public Object handle(Request request, Response response) {
				System.out.println("came into request handler");
				response.status(200);
				WebhooksConsumer consumer = new WebhooksConsumer("test123");
				try {
					Assert.assertTrue(consumer.verifyRequest(request.raw()));
				} catch (Exception e) {
					e.printStackTrace();
				}
				return "success";
			}
		});

		try {
			Thread.sleep(500);
		} catch (Exception e) {
		}
	}

	@Test
	public void testSendRequest() throws Exception {
		producer = WebhooksProducer.getInstance();
		producer.setTimeOut(1000);
		final WebhooksData data = new WebhooksData();
		data.setEvent("test event");
		data.setTimeStamp("");
		data.setId("123");
		for (int i = 0; i < 2; i++) {
			 Thread t = new Thread(new Runnable() {
			 public void run() {
			try {
				statusCode = producer.sendMessage("test" + index++,
						"http://localhost:4567/test/jwebhooks", "test123");
				System.out.println("status code " + statusCode);
			} catch (Exception e) {
				e.printStackTrace();
			}
				}
			});
			t.start();
			t.join();

			 Assert.assertEquals(200, statusCode);
		}
//		Thread.currentThread().sleep(20000);

	}
}
