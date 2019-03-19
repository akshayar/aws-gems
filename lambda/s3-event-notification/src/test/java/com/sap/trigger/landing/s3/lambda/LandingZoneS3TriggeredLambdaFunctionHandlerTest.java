package com.sap.trigger.landing.s3.lambda;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.services.datapipeline.DataPipelineClient;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
@RunWith(MockitoJUnitRunner.class)
public class LandingZoneS3TriggeredLambdaFunctionHandlerTest {

	private final String CONTENT_TYPE = "image/jpeg";
	private S3Event event;

	@Mock
	private DataPipelineClient dpClient;
	


	@Before
	public void setUp() throws IOException {
		event = TestUtils.parse("/s3-event.put.json", S3Event.class);

		// TODO: customize your mock logic for s3 client
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(CONTENT_TYPE);
		

	}


	private Context createContext() {
		TestContext ctx = new TestContext();

		// TODO: customize your context here if needed.
		ctx.setFunctionName("Your Function Name");

		return ctx;
	}

	@Test
	public void testLambdaFunctionHandler() {
		LandingZoneTriggerEventProcessor processor=new LandingZoneTriggerEventProcessor(new ObjectMapper(), "queueName");
		LandingZoneS3TriggeredLambdaFunctionHandler handler = new LandingZoneS3TriggeredLambdaFunctionHandler(processor);
		Context ctx = createContext();
		String output = handler.handleRequest(event, ctx);
		Assert.assertNotNull(output);
	}

}
