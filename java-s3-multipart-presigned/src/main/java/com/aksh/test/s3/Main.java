package com.aksh.test.s3;

import java.io.File;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.amazonaws.HttpMethod;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;

public class Main {
	public static void main(String args[]) throws Exception {
		
		URL url=generatePresignedURL("abdemo-logs","test/slony.pdf");
		
		System.out.println("Presigned URL:"+url.toURI());


		// Creating CloseableHttpClient object
		CloseableHttpClient httpclient = HttpClients.createDefault();

		// Creating a file object
		File file = new File("sample.txt");

		// Creating the FileBody object
		FileBody filebody = new FileBody(file, ContentType.DEFAULT_BINARY);

		// Creating the MultipartEntityBuilder
		MultipartEntityBuilder entitybuilder = MultipartEntityBuilder.create();

		// Setting the mode
		entitybuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);

		// Adding text
		entitybuilder.addTextBody("sample_text", "This is the text part of our file");

		// Adding a file
		entitybuilder.addBinaryBody("image", new File("src/main/resources/slony.pdf"));

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
