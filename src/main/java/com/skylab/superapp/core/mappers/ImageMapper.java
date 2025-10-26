package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.Image.response.UploadImageResponseDto;
import com.skylab.superapp.entities.Image;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ImageMapper {

    public String toString(Image image){
        if (image == null) {
            return null;
        }
        return "https://cdn.yildizskylab.com/" + image.getFileUrl();
    }

    public List<String> toStringList(List<Image> images){
        return images.stream()
                .map(this::toString)
                .toList();
    }


    public UploadImageResponseDto toUploadImageResponseDto(Image savedImage) {
        if (savedImage == null) {
            return null;
        }
        UploadImageResponseDto dto = new UploadImageResponseDto();
        dto.setId(savedImage.getId());
        dto.setFileName(savedImage.getFileName());
        dto.setFileType(savedImage.getFileType());
        dto.setFileUrl("https://cdn.yildizskylab.com/"+savedImage.getFileUrl());
        dto.setFileSize(savedImage.getFileSize());
        return dto;
    }
}