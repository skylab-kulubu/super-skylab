package com.skylab.superapp.entities.DTOs.Event;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
@Setter
public class PatchEventRequest {
    private String name;
    private String description;
    private String location;
    private String formUrl;
    private String prizeInfo;
    private String linkedin;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String ownerTeam;
    private UUID seasonId;
    private Boolean isRanked;
    private Boolean active;
}
