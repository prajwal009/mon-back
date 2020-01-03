package com.api.apimonitoring;

public class Response {
	
	public String responseSize ="";
	public String responseTime ="";
	public String responseText ="";
	public String timeStamp ="";
	public int responseCode =0;
	
	
	
	
	public int getResponseCode() {
		return responseCode;
	}
	public void setResponseCode(int responseCode) {
		this.responseCode = responseCode;
	}
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getResponseSize() {
		return responseSize;
	}
	public void setResponseSize(String responseSize) {
		this.responseSize = responseSize;
	}
	public String getResponseTime() {
		return responseTime;
	}
	public void setResponseTime(String responseTime) {
		this.responseTime = responseTime;
	}
	public String getResponseText() {
		return responseText;
	}
	public void setResponseText(String responseText) {
		this.responseText = responseText;
	}
	@Override
	public String toString() {
		return "Response [responseSize=" + responseSize + ", responseTime=" + responseTime + ", responseText="
				+ responseText + ", timeStamp=" + timeStamp + ", responseCode=" + responseCode + "]";
	}
	public Response(String responseSize, String responseTime, String responseText, String timeStamp, int responseCode) {
		super();
		this.responseSize = responseSize;
		this.responseTime = responseTime;
		this.responseText = responseText;
		this.timeStamp = timeStamp;
		this.responseCode = responseCode;
	}
	public Response() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
	
	
	
	
	
	
	
}
