package com.aksh.kcl.consumer;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.AWSSecurityTokenServiceClientBuilder;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

/**
 * Sample Amazon Kinesis Application.
 */
@SpringBootApplication
@Log
public class KclApplicationSample {

	@Value("${region:ap-south-1}")
	private String region;


	public static void main(String[] args) {
		if(Optional.ofNullable(args).map(Arrays::asList).orElse(Collections.emptyList()).contains("--container-run")){
			System.setProperty("container-run","true");
		}
		SpringApplication.run(KclApplicationSample.class, args);
	}

	@Bean
	public AmazonS3 s3() {
		return AmazonS3ClientBuilder.standard().withRegion(region).build();
	}
	@Bean
	public AWSSecurityTokenService sts() {
		return AWSSecurityTokenServiceClientBuilder.standard().withRegion(region).build();
	}

	@Bean
	public CommandLineRunner commandLineRunner(ApplicationContext ctx) {
		return args -> {

			/*
			 * System.out.println("Let's inspect the beans provided by Spring Boot:");
			 * 
			 * String[] beanNames = ctx.getBeanDefinitionNames(); Arrays.sort(beanNames);
			 * for (String beanName : beanNames) { System.out.println(beanName); }
			 */

		};
	}

}
