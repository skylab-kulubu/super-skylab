package com.skylab.superapp.entities.DTOs.Announcement;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.skylab.superapp.entities.Announcement;
import com.skylab.superapp.entities.DTOs.Photo.GetPhotoDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetAnnouncementDto {
    private int id;

    private String title;

    private String description;

    private String tenant;

    private String content;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm")
    private Date date;

    @JsonProperty("isActive")
    private boolean isActive;

    private String type;

    private List<GetPhotoDto> photos;

    private String author;

    private String formUrl;

    public GetAnnouncementDto(Announcement announcement) {
        this.id = announcement.getId();
        this.title = announcement.getTitle();
        this.description = announcement.getDescription();
        this.tenant = announcement.getTenant();
        this.content = announcement.getContent();
        this.date = announcement.getDate();
        this.isActive = announcement.isActive();
        this.type = announcement.getType();
        this.photos = GetPhotoDto.buildListGetPhotoDto(announcement.getPhotos());
        this.author = announcement.getUser().getUsername();
        this.formUrl = announcement.getFormUrl();
    }

    public static List<GetAnnouncementDto> buildListGetAnnouncementDto(List<Announcement> announcements) {
        return announcements.stream()
                .map(GetAnnouncementDto::new)
                .toList();
    }

}
