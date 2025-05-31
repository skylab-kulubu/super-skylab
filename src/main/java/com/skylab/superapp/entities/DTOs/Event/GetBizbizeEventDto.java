package com.skylab.superapp.entities.DTOs.Event;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skylab.superapp.entities.DTOs.Photo.GetPhotoDto;
import com.skylab.superapp.entities.Event;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetBizbizeEventDto {
    private int id;

    private String title;

    private String guestName;

    private String linkedin;

    private String description;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private Date date;

    @JsonProperty("isActive")
    private boolean isActive;

    private List<GetPhotoDto> photos;

    private String type;

    private String formUrl;


    public GetBizbizeEventDto(Event event) {
        this.id = event.getId();
        this.title = event.getTitle();
        this.guestName = event.getGuestName();
        this.linkedin = event.getLinkedin();
        this.description = event.getDescription();
        this.date = event.getDate();
        this.isActive = event.isActive();
        this.photos = GetPhotoDto.buildListGetPhotoDto(event.getImages());
        this.type = event.getType();
        this.formUrl = event.getFormUrl();
    }

    public static List<GetBizbizeEventDto> buildListGetBizbizeEventDto(List<Event> events) {
        return events.stream()
                .map(GetBizbizeEventDto::new)
                .toList();
    }



}
