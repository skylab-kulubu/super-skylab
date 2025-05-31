package com.skylab.superapp.entities.DTOs.Event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skylab.superapp.entities.DTOs.Image.GetImageDto;
import com.skylab.superapp.entities.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetEventDto {
    private int id;

    private String title;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private Date date;

    @JsonProperty("isActive")
    private boolean isActive;

    private List<GetImageDto> images;

    private String type;

    private String formUrl;




    public GetEventDto(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.description = event.getDescription();
        this.date = event.getDate();
        this.isActive = event.isActive();
        this.images = GetImageDto.buildListGetImageDto(event.getImages());
        this.type = event.getType().getName();
        this.formUrl = event.getFormUrl();
    }

    public static List<GetEventDto> buildListGetEventDto(List<Event> events) {
        return events.stream()
                .map(GetEventDto::new)
                .toList();
    }
}
