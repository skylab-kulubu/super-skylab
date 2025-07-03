package com.skylab.superapp.entities.DTOs.Image;

import com.skylab.superapp.entities.Image;
import com.skylab.superapp.entities.ImageCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GetImageDto {
    private int id;
    private String name;
    private String type;
    private String url;
    private ImageCategory imageCategory;
    private String eventTypeName;
}

