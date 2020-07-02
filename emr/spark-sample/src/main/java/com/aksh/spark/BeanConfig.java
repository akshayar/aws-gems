package com.aksh.spark;

import java.util.Arrays;
import java.util.Properties;
import java.util.stream.StreamSupport;

import org.apache.log4j.Logger;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.EnumerablePropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;

/**
 * @author aksrawat
 *
 */
@Configuration
@ComponentScan(basePackages = { "com.aksh.spark" })
public class BeanConfig {
	
	private static final Logger logger = Logger.getLogger(BeanConfig.class);
	
	//"local[1]"
	@Value("${local.mode:false}")
	private Boolean isLocal;
	private JavaSparkContext javaSparkContext;

	@Autowired
	Environment env;
	
	private Properties getSparkProperties() {
		Properties props = new Properties();
		MutablePropertySources propSrcs = ((AbstractEnvironment) env).getPropertySources();
		StreamSupport.stream(propSrcs.spliterator(), false).filter(ps -> ps instanceof EnumerablePropertySource)
				.map(ps -> ((EnumerablePropertySource) ps).getPropertyNames()).flatMap(Arrays::<String>stream)
				.filter(propName -> propName.startsWith("sparkConf.")).peek(logger::info).forEach(propName -> props
						.setProperty(propName.replaceFirst("sparkConf.", ""), env.getProperty(propName)));
		return props;
	}


	@Bean
	public JavaSparkContext createJavaSparkContext() {

		/*
		 * Create a SparkConf object and set the app name and the master. These two
		 * values could come from either the properties file, or the command line, or
		 * could be hard-coded, like I've done here. But the "host" parameter, if not
		 * local, has to be a valid Spark Master URL.
		 */
		SparkConf conf = new SparkConf().setAppName("SparkS3FileRead");
		if(isLocal) {
			conf=conf.setMaster("local[1]");
		}

		/*
		 * Using the SparkConf object created above, we're going to create a new
		 * JavaSparkContext object and return the same.
		 */
		javaSparkContext= new JavaSparkContext(conf);
		
		/*
		 * Getting the Hadoop configuration object from the JavaSparkContext object, and
		 * over-riding the default values from the configuration values.
		 */
		org.apache.hadoop.conf.Configuration hadoopConfig = javaSparkContext.hadoopConfiguration();
		Properties props = getSparkProperties();

		props.forEach((key, value) -> {
			hadoopConfig.set(key + "", value + "");
		});
		
		return javaSparkContext;
	}
	
}
