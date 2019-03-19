package com.sap.trigger.landing.s3.lambda;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.S3Event;
import com.amazonaws.services.s3.model.ObjectMetadata;

/**
 * A simple test harness for locally invoking your Lambda function handler.
 */
@RunWith(MockitoJUnitRunner.class)
public class LandingZoneS3TriggeredLambdaFunctionHandlerIT {

	private final String CONTENT_TYPE = "image/jpeg";
	private S3Event event;
	
	@BeforeClass
	public static void beforeClass() {
		System.setProperty("queueName","parque-work-list");
		//-DqueueName=parque-work-list
//		System.getenv().put("queueName","parque-work-list");
	}

	@Before
	public void setUp() throws IOException {
		event = TestUtils.parse("/s3-event.put.json", S3Event.class);
		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(CONTENT_TYPE);
	}

	private Context createContext() {
		TestContext ctx = new TestContext();
		ctx.setFunctionName("Your Function Name");
		return ctx;
	}

	@Test
	public void testLambdaFunctionHandler() {
		LandingZoneS3TriggeredLambdaFunctionHandler handler = new LandingZoneS3TriggeredLambdaFunctionHandler();

		Context ctx = createContext();
		String output = handler.handleRequest(event, ctx);
		Assert.assertNotNull(output);
	}

}
