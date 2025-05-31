package com.skylab.superapp.entities.DTOs.Image;

import com.skylab.superapp.entities.Image;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetImageDto {

    private int id;
    private String imageUrl;
    private String tenant;

    public GetImageDto(Image image) {
        this.id = image.getId();
        this.imageUrl = image.getUrl();
        this.tenant = image.getEvent() != null ? image.getEvent().getType().getName() : null;
    }

    public static List<GetImageDto> buildListGetImageDto(List<Image> images) {
        return images.stream()
                .map(GetImageDto::new)
                .toList();
    }
}