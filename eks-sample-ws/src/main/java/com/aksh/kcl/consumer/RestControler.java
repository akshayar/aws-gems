package com.aksh.kcl.consumer;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.securitytoken.AWSSecurityTokenService;
import com.amazonaws.services.securitytoken.model.GetCallerIdentityRequest;
import lombok.extern.java.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@Log
public class RestControler {
	@Autowired
	private AmazonS3 s3;
	@Autowired
	private AWSSecurityTokenService sts;
	@Value("${bucket.name}")
	private String bucketName;
	@RequestMapping("/")
	public String ping() {
		String stStr="";
		String s3List="";
		String error="";
		try{
			stStr=sts.getCallerIdentity(new GetCallerIdentityRequest()).getArn();
			s3List=s3.listBuckets().stream().map(s->s.getName()).collect(Collectors.toList())+"";
		}catch (Exception e){
			e.printStackTrace();
			error=e.getMessage();
		}
		Map<String,String> map=new HashMap<>();
		map.put("sts",stStr);
		map.put("s3",s3List);
		map.put("error",error);
		map.put("date",new Date()+"");
		return map+"";
	}

	@RequestMapping(method = RequestMethod.PUT, path = "s3")
	private Map<String,Object> createFile() {
		log.info("Creating file ");
		String fileName=System.currentTimeMillis()+"file.txt";
		PutObjectResult result=  s3.putObject(bucketName,fileName,"File Created at "+System.currentTimeMillis());
		return Collections.singletonMap(fileName,result);
	}

	
	@RequestMapping(method = RequestMethod.GET, path = "s3/{fileName}")
	private String get(@PathVariable String fileName) throws Exception{
		InputStream stream=s3.getObject(bucketName,fileName).getObjectContent();
		return StreamUtils.copyToString(stream, Charset.defaultCharset());
	}

}
