package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.media.response.MediaUploadResponseDto;
import com.skylab.superapp.entities.Media;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MediaMapper {

    @Mapping(target = "name", source = "fileName")
    @Mapping(target = "type", source = "fileType")
    @Mapping(target = "size", source = "fileSize")
    @Mapping(target = "url", expression = "java(\"https://cdn.yildizskylab.com/\" + savedMedia.getFileUrl())")
    MediaUploadResponseDto toMediaUploadResponseDto(Media savedMedia);

    default String mapMediaToUrl(Media media) {
        if (media == null || media.getFileUrl() == null) return null;
        return "https://cdn.yildizskylab.com/" + media.getFileUrl();
    }
}