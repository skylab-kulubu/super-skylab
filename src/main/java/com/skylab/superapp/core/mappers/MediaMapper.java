package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.media.response.MediaUploadResponseDto;
import com.skylab.superapp.entities.Media;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MediaMapper {

    public String toString(Media media) {
        if (media == null) {
            return null;
        }
        return "https://cdn.yildizskylab.com/" + media.getFileUrl();
    }

    public List<String> toStringList(List<Media> mediaList) {
        if (mediaList == null) {
            return List.of();
        }
        return mediaList.stream()
                .map(this::toString)
                .toList();
    }

    public MediaUploadResponseDto toMediaUploadResponseDto(Media savedMedia) {
        if (savedMedia == null) {
            return null;
        }
        MediaUploadResponseDto dto = new MediaUploadResponseDto();

        dto.setId(savedMedia.getId());
        dto.setName(savedMedia.getFileName());
        dto.setType(savedMedia.getFileType());
        dto.setUrl("https://cdn.yildizskylab.com/" + savedMedia.getFileUrl());
        dto.setSize(savedMedia.getFileSize());

        return dto;
    }


}
