/**
 * 
 */
package com.aksh.kinesislambda.handler;

import org.springframework.stereotype.Component;

import com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord;

import lombok.extern.slf4j.Slf4j;

/**
 * @author aksrawat
 *
 */
@Component
@Slf4j
public class Handler {
	
	public void handle(KinesisEventRecord record) {
		String payload = new String(record.getKinesis().getData().array());
        log.info("Payload: " + payload);
	}
	

}
