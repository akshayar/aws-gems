package com.aksh.kcl.enh.consumer;

/*
 *  Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 *  Licensed under the Amazon Software License (the "License").
 *  You may not use this file except in compliance with the License.
 *  A copy of the License is located at
 *
 *  http://aws.amazon.com/asl/
 *
 *  or in the "license" file accompanying this file. This file is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 *  express or implied. See the License for the specific language governing
 *  permissions and limitations under the License. 
 */

/*
 * Copyright 2019 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.aksh.kcl.enh.consumer.processor.RecordProcessorFactory;

import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.kinesis.common.ConfigsBuilder;
import software.amazon.kinesis.coordinator.Scheduler;

@Component
public class SampleSingle {

	private static final Logger log = LoggerFactory.getLogger(SampleSingle.class);

	@Value("${streamName:aksh-first}")
	public String streamName;

	@Value("${applicationName:SampleKinesisApplication}")
	private String applicationName;

	@Autowired
	private KinesisAsyncClient kinesisClient;
	
	@Autowired
	private RecordProcessorFactory recordProcessorFactory;
	
	@Autowired
	private DynamoDbAsyncClient dynamoClient;
	
	@Autowired
	private CloudWatchAsyncClient cloudWatchClient;

	@PostConstruct
	public void init() {
		run();
	}

	private void run() {

		ConfigsBuilder configsBuilder = new ConfigsBuilder(streamName, streamName, kinesisClient, dynamoClient,
				cloudWatchClient, UUID.randomUUID().toString(), recordProcessorFactory);

		Scheduler scheduler = new Scheduler(configsBuilder.checkpointConfig(), configsBuilder.coordinatorConfig(),
				configsBuilder.leaseManagementConfig(), configsBuilder.lifecycleConfig(),
				configsBuilder.metricsConfig(), configsBuilder.processorConfig(), configsBuilder.retrievalConfig());

		Thread schedulerThread = new Thread(scheduler);
		schedulerThread.setDaemon(true);
		schedulerThread.start();

		log.info("Press enter to shutdown");
		BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
		try {
			reader.readLine();
		} catch (IOException ioex) {
			log.error("Caught exception while waiting for confirm. Shutting down.", ioex);
		}

		shutDown( scheduler);
	}

	private void shutDown(Scheduler scheduler) {
		log.info("Cancelling producer, and shutting down executor.");

		Future<Boolean> gracefulShutdownFuture = scheduler.startGracefulShutdown();
		log.info("Waiting up to 20 seconds for shutdown to complete.");
		try {
			gracefulShutdownFuture.get(20, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			log.info("Interrupted while waiting for graceful shutdown. Continuing.");
		} catch (ExecutionException e) {
			log.error("Exception while executing graceful shutdown.", e);
		} catch (TimeoutException e) {
			log.error("Timeout while waiting for shutdown. Scheduler may not have exited.");
		}
		log.info("Completed, shutting down now.");
	}

}