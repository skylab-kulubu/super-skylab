package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.FileService;
import com.skylab.superapp.core.config.r2.FolderType;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.exceptions.ValidationException;
import com.skylab.superapp.core.utilities.storage.R2StorageService;
import com.skylab.superapp.dataAccess.FileDao;
import com.skylab.superapp.entities.File;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Service
public class FileManager implements FileService {

    private final FileDao fileDao;

    private final Logger logger = LoggerFactory.getLogger(FileManager.class);

    private final R2StorageService r2StorageService;

    public FileManager(FileDao fileDao, R2StorageService r2StorageService) {
        this.fileDao = fileDao;
        this.r2StorageService = r2StorageService;
    }


    @Override
    public File uploadFile(MultipartFile file) {
        logger.info("Uploading file: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            throw new ValidationException("File cannot be empty");
        }

        if (file.getSize() > 20 * 1024 * 1024) {
            throw new ValidationException("File size exceeds 20MB limit");
        }

        try {
            byte[] fileBytes = sanitizeFile(file);

            String fileKey = r2StorageService.uploadFile(
                    fileBytes,
                    file.getOriginalFilename(),
                    file.getContentType(),
                    FolderType.FILE
            );

            File newFile = new File();
            newFile.setFileName(file.getOriginalFilename());
            newFile.setFileType(file.getContentType());
            newFile.setFileUrl(fileKey);
            newFile.setFileSize((long) fileBytes.length);

            return fileDao.save(newFile);

        } catch (Exception e) {
            logger.error("File upload failed: {}", e.getMessage());
            throw new RuntimeException("File upload failed");
        }
    }

    private byte[] sanitizeFile(MultipartFile file) throws IOException {
        String extension = getFileExtension(file.getOriginalFilename())
                .toLowerCase().substring(1);

        return switch (extension) {
            case "pdf" -> removePdfMetadata(file);
            default -> file.getBytes();
        };
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            throw new IllegalArgumentException("File has no extension: " + fileName);
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    @Override
    public void deleteFile(UUID fileId) {

        logger.info("Deleting file with ID: {}", fileId);
        File file = fileDao.findById(fileId)
                .orElseThrow(() -> new ResourceNotFoundException("File not found with ID: " + fileId));

        r2StorageService.deleteFile(file.getFileUrl());
        fileDao.delete(file);

    }

    private byte[] removePdfMetadata(MultipartFile file) throws IOException {
        try (PDDocument document = Loader.loadPDF(file.getBytes())) {

            PDDocumentInformation info = new PDDocumentInformation();
            document.setDocumentInformation(info);
            document.getDocumentCatalog().setMetadata(null);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);

            return outputStream.toByteArray();
        }
    }
}
