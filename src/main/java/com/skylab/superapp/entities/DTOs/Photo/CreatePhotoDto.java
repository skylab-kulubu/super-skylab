package com.skylab.superapp.entities.DTOs.Photo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CreatePhotoDto {

    private String photoUrl;

    private String tenant;

}

