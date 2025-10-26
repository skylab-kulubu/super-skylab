package com.skylab.superapp.core.utilities.storage;

import com.skylab.superapp.core.config.r2.FolderType;
import com.skylab.superapp.core.properties.R2Properties;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.util.UUID;

@Service
public class R2StorageService {


    private final S3Client s3Client;
    private final R2Properties r2Properties;


    public R2StorageService(S3Client s3Client, R2Properties r2Properties) {
        this.s3Client = s3Client;
        this.r2Properties = r2Properties;
    }


    public String uploadFile(byte[] fileBytes, String originalFileName, String contentType, FolderType folderType){

        String key;
        if (folderType == FolderType.IMAGE) {
            key = "images/" + UUID.randomUUID().toString();
        }else if (folderType == FolderType.FILE){
            key = "files/" + UUID.randomUUID().toString();
        } else {
            throw new IllegalArgumentException("Invalid folder type: " + folderType);
        }


        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(r2Properties.getBucketName())
                .key(key)
                .contentType(contentType)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(fileBytes));


        return key;
    }

    public void deleteFile(String key){
        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(r2Properties.getBucketName())
                .key(key)
                .build();
        s3Client.deleteObject(deleteObjectRequest);
    }



}
