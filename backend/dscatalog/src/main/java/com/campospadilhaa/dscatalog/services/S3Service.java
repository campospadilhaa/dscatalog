package com.campospadilhaa.dscatalog.services;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FilenameUtils;
import org.joda.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;

@Service
public class S3Service {

	private static Logger LOG = LoggerFactory.getLogger(S3Service.class);

	@Autowired
	private AmazonS3 s3client;

	@Value("${s3.bucket}")
	private String bucketName;

	/* código default substituído pela implementação abaixo
	public void uploadFile(String localFilePath) {
		try {
			File file = new File(localFilePath);
	
			LOG.info("Upload start");
			s3client.putObject(new PutObjectRequest(bucketName, "test.jpg", file));
			LOG.info("Upload end");
		}
		catch (AmazonServiceException e) {
			LOG.info("AmazonServiceException: " + e.getErrorMessage());
			LOG.info("Status code: " + e.getErrorCode());
		}
		catch (AmazonClientException e) {
			LOG.info("AmazonClientException: " + e.getMessage());
		}
	}*/

	public URL uploadFile(MultipartFile file) {
		try {
			String originalName = file.getOriginalFilename();
			String extension = FilenameUtils.getExtension(originalName);

			String fileNameS3 = Instant.now().toDate().getTime() + "." + extension;

			InputStream inputStream = file.getInputStream();

			String contentType = file.getContentType();

			URL url = uploadFile(inputStream, fileNameS3, contentType);

			return url;
		}
		catch (IOException e) {
			throw new IllegalArgumentException(e.getMessage());
		}
	}

	private URL uploadFile(InputStream inputStream, String fileNameS3, String contentType) {

		ObjectMetadata meta = new ObjectMetadata();
		meta.setContentType(contentType);

		LOG.info("Upload start");
		s3client.putObject(bucketName, fileNameS3, inputStream, meta);
		LOG.info("Upload end");

		URL url = s3client.getUrl(bucketName, fileNameS3);

		return url;
	}
}