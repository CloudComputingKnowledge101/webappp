package com.example.demo.services;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import software.amazon.awssdk.auth.credentials.InstanceProfileCredentialsProvider;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

@Service
public class S3FileTransferService {

	private final String lexicon = "ABCDEFGHIJKLMNOPQRSTUVWXYZ12345674890";
	private final Random random = new Random();
	private final Set<String> identifiers = new HashSet<String>();

	private S3Client s3client;

	@Value("${aws.s3.bucket.name}")
	private String s3BucketName;

	@Value("${aws.s3.bucket.file.url}")
	private String endpointUrl;

	@Value("${aws.access.key}")
	private String accessKeyId;

	@Value("${aws.secret.key}")
	private String accessKeySecret;

	@Value("${aws.s3.bucket.region}")
	private String s3RegionName;

	@PostConstruct
	private void initializeAmazon() {
		
		/*
		 * AwsCredentialsProvider provider = new
		 * EC2ContainerCredentialsProviderWrapper(); StaticCredentialsProvider
		 * .create(AwsBasicCredentials.create(accessKeyId, accessKeySecret));
		 */
				
		InstanceProfileCredentialsProvider provider = InstanceProfileCredentialsProvider.builder().asyncCredentialUpdateEnabled(true).build();
				
		this.s3client = S3Client.builder().credentialsProvider(provider).region(Region.US_WEST_2).build();
		//this.s3BucketName = randomBucketNameGeneration(random);
		createBucket();
		
		System.out.println("S3 CLIENT: " + this.s3client.toString());
	}

	private File convertMultiPartFileToFile(final MultipartFile multipartFile) {
		final File file = new File(multipartFile.getOriginalFilename());
		try (final FileOutputStream outputStream = new FileOutputStream(file)) {
			outputStream.write(multipartFile.getBytes());
		} catch (IOException e) {

		}
		return file;
	}

	public InputStream findByName(String fileName) {

		GetObjectRequest objectRequest = GetObjectRequest.builder().key(fileName).bucket(s3BucketName).build();

		ResponseBytes<GetObjectResponse> objectBytes = s3client.getObjectAsBytes(objectRequest);

		return objectBytes.asInputStream();
	}

	public ArrayList<String> save(final MultipartFile multipartFile) {
		try {
			final File file = convertMultiPartFileToFile(multipartFile);
			final String fileName = LocalDateTime.now() + "_" + file.getName();

			System.out.println("BUCKET NAME: " + s3BucketName + " FILENAME: " + fileName + " file: " + file.exists());

			PutObjectRequest putObjectRequest = PutObjectRequest.builder().bucket(s3BucketName).key(fileName).build();
			final String fileUrl = endpointUrl + "/" + s3BucketName + "/" + fileName;
			s3client.putObject(putObjectRequest, RequestBody.fromFile(file));

			ArrayList<String> fileNameandURL = new ArrayList<String>();
			fileNameandURL.add(fileName);
			fileNameandURL.add(fileUrl);

			System.out.println("Image data: " + fileNameandURL.toString());

			return fileNameandURL;
			// Files.delete(file.toPath()); // Remove the file locally created in the
			// project folder
		} catch (AwsServiceException e) {

			System.err.println(e.getMessage());
		}

		return null;
	}
	
	public void deleteObject(String bucketName, String imageName) {
		
		s3client.deleteObject(DeleteObjectRequest.builder().bucket(bucketName).key(imageName).build());
	}

	private void createBucket(){
		
		if(this.s3client.listBuckets().buckets().contains(s3BucketName)) {
			
			return;
		}
		
		CreateBucketRequest request = CreateBucketRequest.builder().bucket(s3BucketName).build();
		this.s3client.createBucket(request);
	}

	/*private String randomBucketNameGeneration(Random random) {

		StringBuilder builder = new StringBuilder();
		while (builder.toString().length() == 0) {
			int length = random.nextInt(5) + 5;
			for (int i = 0; i < length; i++) {
				builder.append(lexicon.charAt(random.nextInt(lexicon.length())));
			}
			if (identifiers.contains(builder.toString())) {
				builder = new StringBuilder();
			}
		}
		return builder.toString().toLowerCase();
	}*/
}
