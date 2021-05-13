package com.aksh.test.s3;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

public class Main {
	/** Run with following java arguments - 
	 * 		<bucket-name> <s3-key> <src-path>
	 * Example   
	 *   	abdemo-logs test2/file.pdf src/main/resources/file.pdf
	 * @param args
	 * @throws Exception
	 */
	public static void main(String args[]) throws Exception {
		
		String bucketName=args[0];
		String s3Key=args[1];
		String sourceFilePath=args[2];
		
		URL url=generatePresignedURL(bucketName,s3Key);
		
		System.out.println("Presigned URL:"+url.toURI());


		upoadToUrl(url,sourceFilePath);
	}

	private static void upoadToUrl(URL url, final String sourceFilePath) throws URISyntaxException, IOException, ClientProtocolException {
		// Creating CloseableHttpClient object
		CloseableHttpClient httpclient = HttpClients.createDefault();

		// Creating the MultipartEntityBuilder
		MultipartEntityBuilder entitybuilder = MultipartEntityBuilder.create();

		// Setting the mode
		entitybuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);


		// Adding a file
		entitybuilder.addBinaryBody("image", new File(sourceFilePath));

		// Building a single entity using the parts
		HttpEntity mutiPartHttpEntity = entitybuilder.build();

		// Building the RequestBuilder request object
		RequestBuilder reqbuilder = RequestBuilder.put(url.toURI());

		// Set the entity object to the RequestBuilder
		reqbuilder.setEntity(mutiPartHttpEntity);

		// Building the request
		HttpUriRequest multipartRequest = reqbuilder.build();

		// Executing the request
		HttpResponse httpresponse = httpclient.execute(multipartRequest);

		// Printing the status and the contents of the response
		System.out.println(EntityUtils.toString(httpresponse.getEntity()));
		System.out.println(httpresponse.getStatusLine());
	}

	private static URL generatePresignedURL(String bucketName,String objectKey) {
		AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
                .withCredentials(new ProfileCredentialsProvider())
                .withRegion("ap-south-1")
                .build();

        // Set the pre-signed URL to expire after one hour.
        java.util.Date expiration = new java.util.Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60;
        expiration.setTime(expTimeMillis);

        // Generate the pre-signed URL.
        System.out.println("Generating pre-signed URL.");
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, objectKey)
                .withMethod(HttpMethod.PUT)
                .withExpiration(expiration);
        return  s3Client.generatePresignedUrl(generatePresignedUrlRequest);
	}

}
