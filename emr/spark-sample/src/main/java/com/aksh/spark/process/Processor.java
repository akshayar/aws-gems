package com.aksh.spark.process;

import javax.annotation.PostConstruct;

import org.apache.log4j.Logger;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Processor {
	private static final Logger logger = Logger.getLogger(Processor.class);

	@Value("${s3.bucketName}")
	private String s3BucketName;

	@Value("${s3.filePath}")
	private String s3FilePath;

	@Value("${s3.protocol}")
	private String s3Protocol;

	@Autowired
	private JavaSparkContext javaSparkContext;

	@PostConstruct
	public void init() {
		logger.info(this.s3Protocol + this.s3BucketName + "/" + this.s3FilePath);
	}

	public void process(String... args) {

		/*
		 * Calling the function which configures the Hadoop configuration of the Spark
		 * context, sets the AWS credentials, and reads the file.
		 */
		readS3File();

	}

	/**
	 * Function to set Spark's Hadoop configuration, AWS credentials, and read a
	 * text file from Amazon S3. The function then counts the number of lines in the
	 * files, and prints it to the console.
	 */
	private void readS3File() {

		/*
		 * Creating the S3 path of the file using the configuration parameters.
		 */
		String s3FilePath = "s3a://crds-poc/crds/gb/merged/account-dcs/";// this.s3Protocol + this.s3BucketName + "/" +
																			// this.s3FilePath;

		/*
		 * Creating an RDD from the S3 file. The "lines" variable will have String RDDs,
		 * which are a list of all the lines in the file - each line in the file becomes
		 * a list item.
		 */
		JavaRDD<String> lines = this.javaSparkContext.textFile(s3FilePath);

		/*
		 * Calling the "count()" action on the "lines" RDD to get the number of lines in
		 * the file.
		 */
		long lineCount = lines.count();

		/*
		 * Printing the value to console.
		 */
		logger.info("==========================================");
		logger.info("Line count: " + lineCount);
		logger.info("==========================================");
	}
}
