package com.skylab.superapp.core.utilities.storage;

import com.skylab.superapp.core.config.r2.FolderType;
import com.skylab.superapp.core.properties.R2Properties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class R2StorageService {

    private final S3Client s3Client;
    private final R2Properties r2Properties;

    public String uploadFile(byte[] fileBytes, String originalFileName, String contentType, FolderType folderType) {
        log.info("Initiating file upload to R2 storage. OriginalFileName: {}, FolderType: {}", originalFileName, folderType);

        String key;
        if (folderType == FolderType.IMAGE) {
            key = "images/" + UUID.randomUUID();
        } else if (folderType == FolderType.FILE) {
            key = "files/" + UUID.randomUUID();
        } else {
            log.error("R2 file upload failed: Invalid folder type provided. FolderType: {}", folderType);
            throw new IllegalArgumentException("Invalid folder type: " + folderType);
        }

        try {
            log.debug("Uploading object to R2 bucket. BucketName: {}, Key: {}", r2Properties.getBucketName(), key);

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket(r2Properties.getBucketName())
                    .key(key)
                    .contentType(contentType)
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));

            log.info("File uploaded to R2 successfully. Key: {}", key);
            return key;

        } catch (Exception e) {
            log.error("R2 file upload failed. Key: {}, ErrorMessage: {}", key, e.getMessage(), e);
            throw e;
        }
    }

    public void deleteFile(String key) {
        log.info("Initiating file deletion from R2 storage. Key: {}", key);

        try {
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(r2Properties.getBucketName())
                    .key(key)
                    .build();

            s3Client.deleteObject(deleteObjectRequest);

            log.info("File deleted from R2 successfully. Key: {}", key);

        } catch (Exception e) {
            log.error("R2 file deletion failed. Key: {}, ErrorMessage: {}", key, e.getMessage(), e);
            throw e;
        }
    }
}