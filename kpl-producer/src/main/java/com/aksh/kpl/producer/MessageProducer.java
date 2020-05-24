/**
 * 
 */
package com.aksh.kpl.producer;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClientBuilder;
import com.amazonaws.services.kinesis.model.CreateStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamRequest;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.ListStreamsRequest;
import com.amazonaws.services.kinesis.model.ListStreamsResult;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.PutRecordResult;
import com.amazonaws.services.kinesis.model.ResourceNotFoundException;
import com.amazonaws.services.kinesis.model.StreamDescription;

import lombok.extern.slf4j.Slf4j;

/**
 * @author aksha
 *
 */
@Component
@Slf4j
public class MessageProducer {
	@Value("${streamName:aksh-first}")
	public String streamName;

	@Value("${shardCount:1}")
	private int shardCount;

	@Value("${region:us-east-1}")
	private String region;

	/*
	 * Before running the code: Fill in your AWS access credentials in the provided
	 * credentials file template, and be sure to move the file to the default
	 * location (~/.aws/credentials) where the sample code will load the credentials
	 * from. https://console.aws.amazon.com/iam/home?#security_credential
	 *
	 * WARNING: To avoid accidental leakage of your credentials, DO NOT keep the
	 * credentials file in your source directory.
	 */

	private static AmazonKinesis kinesis;
	
	ExecutorService executorService=Executors.newCachedThreadPool();
	
	private boolean shutdown=false;

	@PostConstruct
	private void init() throws Exception {
		initCredentials();

		// Describe the stream and check if it exists.
		initStreams();

		listAllStreams();
		executorService.execute(()->{
			try {
				pushMessages();
			} catch (Exception e) {
				log.error("Error",e);
			}	
		});
		
		
	}
	@PreDestroy
	void shutdown() {
		log.info("Shutdown");
		executorService.shutdown();
		shutdown=true;
	}

	private void initCredentials() throws Exception {
		/*
		 * The ProfileCredentialsProvider will return your [default] credential profile
		 * by reading from the credentials file located at (~/.aws/credentials).
		 */
		ProfileCredentialsProvider credentialsProvider = new ProfileCredentialsProvider();
		try {
			credentialsProvider.getCredentials();
		} catch (Exception e) {
			throw new AmazonClientException("Cannot load the credentials from the credential profiles file. "
					+ "Please make sure that your credentials file is at the correct "
					+ "location (~/.aws/credentials), and is in valid format.", e);
		}

		kinesis = AmazonKinesisClientBuilder.standard().withCredentials(credentialsProvider).withRegion(region).build();
	}

	public void pushMessages() throws Exception {

		log.info("Putting records in stream : {} until this application is stopped.", streamName);
		System.out.println("Press CTRL-C to stop.");
		// Write records to the stream until this program is aborted.
		while (!shutdown) {
			long createTime = System.currentTimeMillis();
			PutRecordRequest putRecordRequest = new PutRecordRequest();
			putRecordRequest.setStreamName(streamName);
			putRecordRequest.setData(ByteBuffer.wrap(String.format("testData-%d", createTime).getBytes()));
			putRecordRequest.setPartitionKey(String.format("partitionKey-%d", createTime));
			PutRecordResult putRecordResult = kinesis.putRecord(putRecordRequest);
			log.info("Successfully put record, partition key : {}, ShardID : {}, SequenceNumber : {}",
					putRecordRequest.getPartitionKey(), putRecordResult.getShardId(),
					putRecordResult.getSequenceNumber());
			wait(2000);
		}
	}

	private void listAllStreams() {
		// List all of my streams.
		ListStreamsRequest listStreamsRequest = new ListStreamsRequest();
		listStreamsRequest.setLimit(10);
		ListStreamsResult listStreamsResult = kinesis.listStreams(listStreamsRequest);
		List<String> streamNames = listStreamsResult.getStreamNames();
		while (listStreamsResult.isHasMoreStreams()) {
			if (streamNames.size() > 0) {
				listStreamsRequest.setExclusiveStartStreamName(streamNames.get(streamNames.size() - 1));
			}

			listStreamsResult = kinesis.listStreams(listStreamsRequest);
			streamNames.addAll(listStreamsResult.getStreamNames());
		}
		// Print all of my streams.
		log.info("List of my streams:{}", streamNames);
	}

	private void initStreams() throws InterruptedException {

		DescribeStreamRequest describeStreamRequest = new DescribeStreamRequest().withStreamName(streamName);
		try {
			StreamDescription streamDescription = kinesis.describeStream(describeStreamRequest).getStreamDescription();
			log.info("Stream {} has a status of {}", streamName, streamDescription.getStreamStatus());

			if ("DELETING".equals(streamDescription.getStreamStatus())) {
				log.info("Stream is being deleted. This sample will now exit.");
				System.exit(0);
			}

			// Wait for the stream to become active if it is not yet ACTIVE.
			if (!"ACTIVE".equals(streamDescription.getStreamStatus())) {
				waitForStreamToBecomeAvailable(streamName);
			}
		} catch (ResourceNotFoundException ex) {
			log.info("Stream {} does not exist. Creating it now.", streamName);

			// Create a stream. The number of shards determines the provisioned throughput.
			CreateStreamRequest createStreamRequest = new CreateStreamRequest();
			createStreamRequest.setStreamName(streamName);
			createStreamRequest.setShardCount(shardCount);
			kinesis.createStream(createStreamRequest);
			// The stream is now being created. Wait for it to become active.
			waitForStreamToBecomeAvailable(streamName);
		}
	}

	private static void waitForStreamToBecomeAvailable(String myStreamName) throws InterruptedException {
		log.info("Waiting for {} to become ACTIVE...", myStreamName);

		long startTime = System.currentTimeMillis();
		long endTime = startTime + TimeUnit.MINUTES.toMillis(10);
		while (System.currentTimeMillis() < endTime) {
			Thread.sleep(TimeUnit.SECONDS.toMillis(20));

			try {
				DescribeStreamRequest describeStreamRequest = new DescribeStreamRequest();
				describeStreamRequest.setStreamName(myStreamName);
				// ask for no more than 10 shards at a time -- this is an optional parameter
				describeStreamRequest.setLimit(10);
				DescribeStreamResult describeStreamResponse = kinesis.describeStream(describeStreamRequest);

				String streamStatus = describeStreamResponse.getStreamDescription().getStreamStatus();
				log.info("current state: {}", streamStatus);
				if ("ACTIVE".equals(streamStatus)) {
					return;
				}
			} catch (ResourceNotFoundException ex) {
				// ResourceNotFound means the stream doesn't exist yet,
				// so ignore this error and just keep polling.
			} catch (AmazonServiceException ase) {
				throw ase;
			}
		}

		throw new RuntimeException(String.format("Stream %s never became active", myStreamName));
	}

	private static void wait(int sleep) {
		try {
			Thread.sleep(sleep);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
