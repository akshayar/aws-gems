package com.sap.trigger.landing.s3.lambda;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.sap.trigger.landing.s3.conf.BeanConfiguration;

public class LandingZoneS3TriggeredLambdaFunctionHandler implements RequestHandler<S3Event, String> {
	
	private LandingZoneTriggerEventProcessor processor;

	private Class<?>[] beanConfig = { BeanConfiguration.class };

	@SuppressWarnings("resource")
	private void initalizeApplicationContext(Class<?>[] beanConfig) {
		ApplicationContext applicationContext=new AnnotationConfigApplicationContext(beanConfig);
		processor=applicationContext.getBean(LandingZoneTriggerEventProcessor.class);
	}

	public LandingZoneS3TriggeredLambdaFunctionHandler() {
		initalizeApplicationContext(beanConfig);
	}

	// Test purpose only.
	LandingZoneS3TriggeredLambdaFunctionHandler(LandingZoneTriggerEventProcessor testProcessor) {
		this.processor=testProcessor;
	}

	@Override
	public String handleRequest(S3Event event, Context context) {
		context.getLogger().log("Received event: " + event);
		try {
			return processor.handleRequest(event);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}