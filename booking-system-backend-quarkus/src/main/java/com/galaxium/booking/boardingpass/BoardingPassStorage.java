package com.galaxium.booking.boardingpass;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;

import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;

import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.jboss.logging.Logger;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;


@ApplicationScoped
public class BoardingPassStorage {

    @Inject
    MinioClient minioClient;

    @Inject
    Logger logger;

    @ConfigProperty(name = "bucket.boarding-pass", defaultValue = "boarding-passes")
    String bucketName;

    public byte[] getBoardingPass(String objectName) {
        try (GetObjectResponse response = minioClient.getObject(
            GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build())) {
            return response.readAllBytes();
        } catch (Exception e) {
            logger.error(e);
            throw new IllegalStateException(e);
        }
    }

    public void storeBoardingPass(String objectName, byte[] boardingPass) {
        initializeBucketIfNotPresent();
        storeBoardingPassToBucket(objectName, boardingPass);
    }

    private void storeBoardingPassToBucket(String objectName, byte[] boardingPass) {
        try (ByteArrayInputStream bais = new ByteArrayInputStream(boardingPass)) {

            // 4. Upload the stream using PutObjectArgs
            minioClient.putObject(
                PutObjectArgs.builder()
                    .bucket(bucketName)
                    .object(objectName)
                    // Pass the stream, total size, and part size (-1 lets MinIO handle part sizes)
                    .stream(bais, boardingPass.length, -1)
                    .contentType("application/pdf")
                    .build()
            );

            logger.infof("Successfully uploaded %s to bucket %s", objectName, bucketName);
        } catch (Exception e) {
            logger.error(e);
            throw new IllegalStateException(e);
        }
    }

    private void initializeBucketIfNotPresent() {
        try {
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
                logger.infof("Bucket '%s' created successfully.", bucketName);
            }
        } catch (Exception e) {
            logger.error(e);
            throw new IllegalStateException(e);
        }
    }

}
