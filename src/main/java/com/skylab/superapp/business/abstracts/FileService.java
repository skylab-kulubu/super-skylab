package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.entities.File;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileService {

    File uploadFile(MultipartFile file);

    void deleteFile(UUID fileId);

}
