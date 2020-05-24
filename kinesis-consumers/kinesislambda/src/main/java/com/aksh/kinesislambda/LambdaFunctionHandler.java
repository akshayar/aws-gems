package com.aksh.kinesislambda;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import com.aksh.kinesislambda.handler.Handler;
import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent;
import com.amazonaws.services.lambda.runtime.events.KinesisEvent.KinesisEventRecord;

public class LambdaFunctionHandler implements RequestHandler<KinesisEvent, Integer> {
	Handler handler;
	ApplicationContext applicationContext;
	public LambdaFunctionHandler() {
		applicationContext=new AnnotationConfigApplicationContext(BeanConfig.class);
		handler=applicationContext.getBean(Handler.class);
	}

    @Override
    public Integer handleRequest(KinesisEvent event, Context context) {
        context.getLogger().log("Input: " + event);

        for (KinesisEventRecord record : event.getRecords()) {
            handler.handle(record);
        }

        return event.getRecords().size();
    }
}
