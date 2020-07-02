package com.aksh.spark;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.aksh.spark.process.Processor;

@SpringBootApplication(exclude= {org.springframework.boot.autoconfigure.gson.GsonAutoConfiguration.class})
public class Main  implements CommandLineRunner{
	private static final Logger logger = Logger.getLogger(Main.class);
	
	@Autowired
	Processor processor;
	
	
	public static void main(String[] args) {
		SpringApplication.run(Main.class);
	}
	
	
	@Override
	public void run(String... args) throws Exception {
		processor.process(args);
	}

}
