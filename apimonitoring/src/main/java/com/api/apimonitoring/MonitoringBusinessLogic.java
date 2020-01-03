package com.api.apimonitoring;




import static java.util.concurrent.TimeUnit.SECONDS;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;

import kong.unirest.HttpResponse;
import kong.unirest.JsonNode;
import kong.unirest.Unirest;

@EnableAsync
@EnableScheduling
@Service
public class MonitoringBusinessLogic {
	
	Monitors monitors;
	
	@Autowired
	GmailSenderApplication mailSender;
	@Autowired
	IMonitoringRepository monitoringRepository;   
	int oldCode=0;
	    
	private ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
	
	public void start(boolean bool, String id) {
		
//		String url = monitoringRepository.findUrl(id);
//		String methodType = monitoringRepository.findMethodType(id);
//		Long time = monitoringRepository.findTime(id);
//		String jsonBody = monitoringRepository.findJsonBody(id);
//		String headerName = monitoringRepository.findHeaderName(id);
//		String headerValue = monitoringRepository.findHeaderValue(id);
		
		
		Monitors monitorOne = monitoringRepository.findOneMonitor(id);
		String url =monitorOne.getUrl();
		String methodType = monitorOne.getMethodType();
		Long time = monitorOne.getTime();
		Object jsonBody = monitorOne.getJsonBody();
		
		List<Headers> headers = monitorOne.getHeaders();
		
		//System.out.println(responseSend);
		
		Runnable runne = new Runnable() {

			@SuppressWarnings({ "deprecation" })
			@Override
			public void run() {
				
				System.out.println("started run");
				
					//int stopped = monitoringRepository.findIsExecuting(id);
					boolean stopped = monitorOne.isExecuting();
					System.out.println(stopped + " stopped value");
					
					Monitors m1 = monitoringRepository.findOneMonitor(id);
					boolean stopp = m1.isExecuting();
					
					if(stopp == true) {					
						try {
							System.out.println("entered");
							//monitoringRepository.updateIsExecuting(id, false);
							
							
							//int statusCode = sendHttpUnirest(url,methodType,headers,jsonBody);
//							Response response = sendHttpUnirest(url,methodType,headers,jsonBody);
//							
//							System.out.println(response);
//							
//							monitorOne.setResponse(response);
//							
//							int statusCode = response.getResponseCode();
							
							List<Object> response = sendHttpUnirest(url,methodType,headers,jsonBody);
							int statusCode = (int) response.get(0);
							
							System.out.println(statusCode+"  "+id );
							String timeStamp = (String) response.get(1);
							monitorOne.setTimeStamp(timeStamp);
							try {

								String responseSize = (String) response.get(2);
								System.out.println(responseSize + " size");
								monitorOne.setResponseSize(responseSize);
							}catch(Exception e) {}
							//monitoringRepository.updateStatusCode(id, statusCode);
							monitorOne.setStatusCode(statusCode);
							monitoringRepository.save(monitorOne);
							
							boolean send=checkCode(statusCode);
							
							
							if(send==true)
							{
								System.out.println("Email sending" +id);
								mailSender.sendmail(id);
								System.out.println("Email sent");
							}
							
							
							//float successCount=monitoringRepository.findSuccessCount(id);
							float successCount = monitorOne.getSuccessCount();
							
							//float totalRuns = monitoringRepository.findTotalRuns(id);
							float totalRuns = monitorOne.getTotalRuns();
							if(statusCode==200) {
								successCount++;
								monitorOne.setSuccessCount(successCount);
							}
							totalRuns++;
							monitorOne.setTotalRuns(totalRuns);
							float apdex = calculateApdex(successCount,totalRuns);
							monitorOne.setApdex(apdex);
							//monitoringRepository.updateApdex(id, successCount, totalRuns, apdex);
							System.out.println(monitorOne.isExecuting());
							monitoringRepository.save(monitorOne);
							
						} catch (Exception e) {
							// TODO Auto-generated catch block
							System.out.println("cought");
							e.printStackTrace();
						}
						
						
						
					}
				
					else {
						System.out.println("stoppping");
						Thread.currentThread().stop();
					}
					
			}
			
		};
		// monitoringRepository.updateIsExecuting(id, true);
		 //monitorOne.setExecuting(true);
		 //monitoringRepository.save(monitorOne);
		 ScheduledFuture<?> runneHandle = 
	    			scheduler.scheduleAtFixedRate(runne, time,time, SECONDS);
		 
		 
			 scheduler.schedule(new Runnable() {
				    @Override
		    		public void run() {
				    
				   
				   	if(bool == true) {
				    		runneHandle.toString();
			    	}
			    	else {
			    		runneHandle.cancel(true);
				    		System.out.println("cancelled");
				 
				    	}
		    		}
		    	}, time, SECONDS); 
		 
		 
			
	}
	

	    
	    //........................unirest start...........................
	
	
		//Response response;
	 
	    public List<Object> sendHttpUnirest(String url,String methodType , List<Headers> headersList , Object jsonBody ) throws Exception
	    {
	        int responseCode;
	        String timeStamp;
	        String size;
	        List<Object> responseList = new ArrayList<>();
	        
	        Unirest.config().reset();
        	Unirest.config().enableCookieManagement(false);
        	
        	Map<String, String> headers = new TreeMap<String, String>();
        	
        	if(headersList!=null) {
        		for(Headers header : headersList) {
            		
        			//System.out.println(header.getHeaderKey() + "  key");
        			headers.put(header.getHeaderKey(), header.getHeaderValue());
        			//System.out.println(header.getHeaderValue() + "   value");
        		}
        	}
        	
        	//Monitors monitor;
        	
	        switch(methodType)
	        {
	            case "GET":
	            	
	                HttpResponse<JsonNode> getResponse=Unirest.
	                get(url).headers(headers)
                   .asJson();
	                    //statuscod=getResponse.getStatus();
	                     responseCode = getResponse.getStatus();
	                    System.out.println(responseCode + " status code");
	                    responseList.add(responseCode);
	               
	                     timeStamp = getResponse.getHeaders().get("Date").get(0);
	                    System.out.println(timeStamp);
	             
	                    responseList.add(timeStamp);
	                    try {
	                    	 size = getResponse.getHeaders().get("content-Length").get(0);
		                    
		                    	//this.monitor.setResponseSize(size.toString());
		                    	responseList.add(size);
			                    System.out.println(size);
		                    
	                    }catch(Exception e) {
	                    	
	                    }
	                   
	                    break;
	            case "POST":
	                HttpResponse<String> postResponse=Unirest.post(url).headers(headers)
	                .body(jsonBody)
	                .asString();
	                 responseCode = postResponse.getStatus();
                    System.out.println(responseCode + " status code");
                    responseList.add(responseCode);
                    timeStamp = postResponse.getHeaders().get("Date").get(0);
                    responseList.add(timeStamp);
                    try {
                    	 size = postResponse.getHeaders().get("content-Length").get(0);
	                    	responseList.add(size);
                    }catch(Exception e) {}
	                break;
	            case "PUT":
	                HttpResponse<String> putResponse=Unirest.put(url).headers(headers)
	                .body(jsonBody)
	                .asString();
	                responseCode = putResponse.getStatus();
                    System.out.println(responseCode + " status code");
                    responseList.add(responseCode);
                    timeStamp = putResponse.getHeaders().get("Date").get(0);
                    responseList.add(timeStamp);
                    try {
                    	 size = putResponse.getHeaders().get("content-Length").get(0);
	                    	responseList.add(size);
                    }catch(Exception e) {}
	                break;
	            case "DELETE":
	                HttpResponse<String> deleteResponse=Unirest.delete(url).headers(headers)
	                .asString();
	                responseCode = deleteResponse.getStatus();
                    System.out.println(responseCode + " status code");
                    responseList.add(responseCode);
                    timeStamp = deleteResponse.getHeaders().get("Date").get(0);
                    responseList.add(timeStamp);
                    try {
                    	 size = deleteResponse.getHeaders().get("content-Length").get(0);
	                    	responseList.add(size);
                    }catch(Exception e) {}
	                break;   
	        }
	       
			//Response response = null;
			return responseList;
	       
	    }
	    
	    
	    
	    //......................unirest end...........................
	      
	    
	   // .............calculating apdex...................
	 public float calculateApdex(float successCount,float totalRuns) {
		 if(totalRuns!=0) {
			 return successCount/totalRuns;
		 }
		 return 0;
	 }
	 
	//.......................................................
	 
	 
	 
	 public boolean checkCode(int response)
		{
			
			if(response==oldCode)
			{
				return false;
			}
			oldCode=response;
			return true;
			
		}
	

}