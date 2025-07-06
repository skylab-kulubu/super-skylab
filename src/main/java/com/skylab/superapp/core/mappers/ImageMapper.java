package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.Image;
import com.skylab.superapp.entities.DTOs.Image.GetImageDto;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImageMapper {

    public GetImageDto toDto(Image image) {
        return GetImageDto.builder()
                .id(image.getId())
                .name(image.getName())
                .type(image.getType())
                .url(image.getUrl())
                .category(image.getCategory())
                .eventTypeName(image.getEvent() != null ?
                        image.getEvent().getType().getName() : null)
                .build();
    }

    public List<GetImageDto> toDtoList(List<Image> images) {
        return images.stream()
                .map(this::toDto)
                .toList();
    }
}