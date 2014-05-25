jwebhooks
=========

Java library to create and consume webhooks in a secure way. HMAC is used to sign the request and the consumer has a method to verify the request. 


  **Usage :**

Producer

      WebhooksProducer producer = WebhooksProducer.getInstance();
      producer.setTimeOut(1000);
      
      final WebhooksData data = new WebhooksData();
      data.setEvent("test event");
      data.setTimeStamp("");
      data.setId("123");
      String statusCode = producer.sendMessage(data,"http://localhost:4567/test/jwebhooks","secretKey");	
      
      ---or---
      // to send string message use  
      String statusCode = producer.sendMessage("test data","http://localhost:4567/test/jwebhooks","secretKey");

Consumer

    WebhooksConsumer consumer = new WebhooksConsumer("secretKey");
    boolean valid = consumer.verifyRequest(request.raw()));
    					


To Do :

1. Add date to header and check if the request is not older than 30 secs
2. Add nonce to header to avoid replay attack
