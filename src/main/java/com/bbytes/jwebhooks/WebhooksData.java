package com.bbytes.jwebhooks;

import java.io.Serializable;
import java.util.Map;

/**
 * Web hook  class that holds data / payload
 * 
 * @author Thanneer
 *
 */
public class WebhooksData implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6209200465420120108L;
	
	String event;
	
	String timeStamp;
	
	String id;
	
	Map<String,Object> payLoad;

	/**
	 * @return the event
	 */
	public String getEvent() {
		return event;
	}

	/**
	 * @param event the event to set
	 */
	public void setEvent(String event) {
		this.event = event;
	}

	/**
	 * @return the timeStamp
	 */
	public String getTimeStamp() {
		return timeStamp;
	}

	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the payLoad
	 */
	public Map<String, Object> getPayLoad() {
		return payLoad;
	}

	/**
	 * @param payLoad the payLoad to set
	 */
	public void setPayLoad(Map<String, Object> payLoad) {
		this.payLoad = payLoad;
	}

}
