package com.skylab.superapp.entities.DTOs.Photo;

import com.skylab.superapp.entities.Photo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetPhotoDto {

    private int id;

    private String photoUrl;

    private String tenant;

    public GetPhotoDto(Photo photo) {
        this.id = photo.getId();
        this.photoUrl = photo.getPhotoUrl();
        this.tenant = photo.getTenant();
    }

    public static List<GetPhotoDto> buildListGetPhotoDto(List<Photo> photos) {
        return photos.stream()
                .map(GetPhotoDto::new)
                .toList();
    }

}
