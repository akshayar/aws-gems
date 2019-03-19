/**
 * 
 */
package com.sap.trigger.landing.s3.conf;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import com.amazonaws.services.datapipeline.DataPipeline;
import com.amazonaws.services.datapipeline.DataPipelineClientBuilder;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author aksrawat
 *
 */
@Configuration
@ComponentScan(basePackages = { "com.sap.trigger.landing.s3", "com.sap.feed.config.reader" })
public class BeanConfiguration {
	
	@Bean
	public DataPipeline createDataPipelineClient(){
		return  DataPipelineClientBuilder.defaultClient();
	}
	
	@Bean
	public ObjectMapper createObjectMapper() {
		return new ObjectMapper();
	}

}
