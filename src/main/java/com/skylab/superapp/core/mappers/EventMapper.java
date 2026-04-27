package com.skylab.superapp.core.mappers;

import com.skylab.superapp.entities.Event;
import com.skylab.superapp.entities.Image;
import com.skylab.superapp.entities.DTOs.Event.EventDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EventMapper {

    @Mapping(source = "ranked", target = "ranked")
    @Mapping(source = "coverImage", target = "coverImageUrl", qualifiedByName = "mapImageToUrl")
    @Mapping(source = "images", target = "imageUrls", qualifiedByName = "mapImagesToUrls")
    EventDto eventToEventDto(Event event);

    @Named("mapImageToUrl")
    default String mapImageToUrl(Image image) {
        if (image == null) return null;
        return image.getFileUrl();
    }

    @Named("mapImagesToUrls")
    default List<String> mapImagesToUrls(List<Image> images) {
        if (images == null) return null;
        return images.stream().map(Image::getFileUrl).collect(Collectors.toList());
    }
}