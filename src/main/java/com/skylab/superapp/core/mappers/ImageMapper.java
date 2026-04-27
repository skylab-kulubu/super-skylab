package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.Image.response.UploadImageResponseDto;
import com.skylab.superapp.entities.Image;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ImageMapper {

    @Mapping(target = "name", source = "fileName")
    @Mapping(target = "type", source = "fileType")
    @Mapping(target = "size", source = "fileSize")
    @Mapping(target = "url", expression = "java(\"https://cdn.yildizskylab.com/\" + savedImage.getFileUrl())")
    UploadImageResponseDto toUploadImageResponseDto(Image savedImage);

    default String mapImageToUrl(Image image) {
        if (image == null || image.getFileUrl() == null) return null;
        return "https://cdn.yildizskylab.com/" + image.getFileUrl();
    }
}