package com.aksh.kcl.enh.consumer.processor;

import org.slf4j.MDC;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;
import software.amazon.kinesis.exceptions.InvalidStateException;
import software.amazon.kinesis.exceptions.ShutdownException;
import software.amazon.kinesis.lifecycle.events.InitializationInput;
import software.amazon.kinesis.lifecycle.events.LeaseLostInput;
import software.amazon.kinesis.lifecycle.events.ProcessRecordsInput;
import software.amazon.kinesis.lifecycle.events.ShardEndedInput;
import software.amazon.kinesis.lifecycle.events.ShutdownRequestedInput;
import software.amazon.kinesis.processor.ShardRecordProcessor;

/**
 * Processes records and checkpoints progress.
 */
@Component
@Scope(scopeName = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Slf4j
public class SampleRecordProcessor implements ShardRecordProcessor {

	private static final String SHARD_ID_MDC_KEY = "ShardId";

	private String shardId;

	public void initialize(InitializationInput initializationInput) {
		shardId = initializationInput.shardId();
		MDC.put(SHARD_ID_MDC_KEY, shardId);
		try {
			log.info("Initializing @ Sequence: {}", initializationInput.extendedSequenceNumber());
		} finally {
			MDC.remove(SHARD_ID_MDC_KEY);
		}
	}

	public void processRecords(ProcessRecordsInput processRecordsInput) {
		MDC.put(SHARD_ID_MDC_KEY, shardId);
		try {
			log.info("Processing {} record(s)", processRecordsInput.records().size());
			processRecordsInput.records().forEach(
					r -> log.info("Processing record pk: {} -- Seq: {}", r.partitionKey(), r.sequenceNumber()));
		} catch (Throwable t) {
			log.error("Caught throwable while processing records. Aborting.");
			Runtime.getRuntime().halt(1);
		} finally {
			MDC.remove(SHARD_ID_MDC_KEY);
		}
	}

	public void leaseLost(LeaseLostInput leaseLostInput) {
		MDC.put(SHARD_ID_MDC_KEY, shardId);
		try {
			log.info("Lost lease, so terminating.");
		} finally {
			MDC.remove(SHARD_ID_MDC_KEY);
		}
	}

	public void shardEnded(ShardEndedInput shardEndedInput) {
		MDC.put(SHARD_ID_MDC_KEY, shardId);
		try {
			log.info("Reached shard end checkpointing.");
			shardEndedInput.checkpointer().checkpoint();
		} catch (ShutdownException | InvalidStateException e) {
			log.error("Exception while checkpointing at shard end. Giving up.", e);
		} finally {
			MDC.remove(SHARD_ID_MDC_KEY);
		}
	}

	public void shutdownRequested(ShutdownRequestedInput shutdownRequestedInput) {
		MDC.put(SHARD_ID_MDC_KEY, shardId);
		try {
			log.info("Scheduler is shutting down, checkpointing.");
			shutdownRequestedInput.checkpointer().checkpoint();
		} catch (ShutdownException | InvalidStateException e) {
			log.error("Exception while checkpointing at requested shutdown. Giving up.", e);
		} finally {
			MDC.remove(SHARD_ID_MDC_KEY);
		}
	}
}