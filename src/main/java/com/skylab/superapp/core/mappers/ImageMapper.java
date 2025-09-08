package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.DTOs.Image.ImageDto;
import com.skylab.superapp.entities.Image;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ImageMapper {

    public ImageDto toDto(Image image) {

        if (image == null) {
            return null;
        }

        return ImageDto.builder()
                .id(image.getId())
                .name(image.getName())
                .type(image.getType())
                .url(image.getUrl())
                .category(image.getCategory())
                .eventTypeName(image.getEvent() != null ?
                        image.getEvent().getType().getName() : null)
                .build();
    }

    public List<ImageDto> toDtoList(List<Image> images) {
        return images.stream()
                .map(this::toDto)
                .toList();
    }
}