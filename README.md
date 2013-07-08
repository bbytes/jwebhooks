jwebhooks
=========

Java library to create and consume webhooks in a secure way.

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
    consumer.verifyRequest(request.raw()));
    					


