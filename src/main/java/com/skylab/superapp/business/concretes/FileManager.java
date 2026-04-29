package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.FileService;
import com.skylab.superapp.core.config.r2.FolderType;
import com.skylab.superapp.core.constants.FileMessages;
import com.skylab.superapp.core.exceptions.BusinessException;
import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.exceptions.ValidationException;
import com.skylab.superapp.core.utilities.storage.R2StorageService;
import com.skylab.superapp.dataAccess.FileDao;
import com.skylab.superapp.entities.File;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileManager implements FileService {

    private final FileDao fileDao;
    private final R2StorageService r2StorageService;

    @Override
    public File uploadFile(MultipartFile file) {
        log.info("Initiating file upload. FileName: {}, FileSize: {}", file.getOriginalFilename(), file.getSize());

        if (file.isEmpty()) {
            log.warn("File upload failed: File is empty. FileName: {}", file.getOriginalFilename());
            throw new ValidationException(FileMessages.FILE_IS_EMPTY);
        }

        if (file.getSize() > 20 * 1024 * 1024) {
            log.warn("File upload failed: File size exceeds 20MB limit. FileName: {}, FileSize: {}", file.getOriginalFilename(), file.getSize());
            throw new ValidationException(FileMessages.FILE_SIZE_EXCEEDED);
        }

        try {
            log.debug("Processing file sanitization. FileName: {}", file.getOriginalFilename());
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

            File savedFile = fileDao.save(newFile);

            log.info("File uploaded and saved successfully. FileId: {}, FileKey: {}", savedFile.getId(), fileKey);
            return savedFile;

        } catch (Exception e) {
            log.error("File upload failed: Unexpected error during processing. FileName: {}, ErrorMessage: {}", file.getOriginalFilename(), e.getMessage(), e);
            throw new BusinessException(FileMessages.FILE_UPLOAD_FAILED + ": " + e.getMessage());
        }
    }

    private byte[] sanitizeFile(MultipartFile file) throws IOException {
        String extension = getFileExtension(file.getOriginalFilename()).toLowerCase().substring(1);

        log.debug("Determining sanitization strategy. Extension: {}", extension);

        return switch (extension) {
            case "pdf" -> removePdfMetadata(file);
            default -> file.getBytes();
        };
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            log.warn("File extension validation failed: No extension found. FileName: {}", fileName);
            throw new ValidationException(FileMessages.FILE_HAS_NO_EXTENSION);
        }
        return fileName.substring(fileName.lastIndexOf("."));
    }

    @Override
    public void deleteFile(UUID fileId) {
        log.info("Initiating file deletion. FileId: {}", fileId);

        File file = fileDao.findById(fileId)
                .orElseThrow(() -> {
                    log.error("File deletion failed: Resource not found. FileId: {}", fileId);
                    return new ResourceNotFoundException("File not found with ID: " + fileId);
                });

        r2StorageService.deleteFile(file.getFileUrl());
        fileDao.delete(file);

        log.info("File deleted successfully. FileId: {}", fileId);
    }

    private byte[] removePdfMetadata(MultipartFile file) throws IOException {
        log.debug("Stripping metadata from PDF document. FileName: {}", file.getOriginalFilename());

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