package com.skylab.superapp.entities.DTOs.Image;

import com.skylab.superapp.entities.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseImageDto {

    private int id;

    private String name;

    private String type;

    private String url;

    private User createdBy;
}
