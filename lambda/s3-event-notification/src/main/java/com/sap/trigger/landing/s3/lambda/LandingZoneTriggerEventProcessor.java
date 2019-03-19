/**
 * 
 */
package com.sap.trigger.landing.s3.lambda;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author aksrawat
 *
 */
@Component
public class LandingZoneTriggerEventProcessor {
	
	private static final Logger log=LoggerFactory.getLogger(LandingZoneTriggerEventProcessor.class);
	@Autowired
	private ObjectMapper mapper;
	
	public LandingZoneTriggerEventProcessor() {
		super();
	}
	
	/**
	 * Used for testing.
	 * @param mapper
	 * @param sqs
	 * @param feedConfigReader
	 * @param queueName
	 */
	LandingZoneTriggerEventProcessor(ObjectMapper mapper, String queueName) {
		super();
		this.mapper = mapper;
	}



	public String handleRequest(S3Event event) throws Exception {
		String fileKey=event.getRecords().get(0).getS3().getObject().getKey();
		log.info("Received event:{}" , fileKey);
		try {
			if(!fileKey.contains("exception")) {
				return "Processed";
			}else {
				throw new Exception();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.error(String.format("Error in processing evene object %s ", event));
			throw e;
		}
	}

}
