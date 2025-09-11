package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.AnnouncementService;
import com.skylab.superapp.core.constants.AnnouncementMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.Announcement.AnnouncementDto;
import com.skylab.superapp.entities.DTOs.Announcement.CreateAnnouncementRequest;
import com.skylab.superapp.entities.DTOs.Announcement.UpdateAnnouncementRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @PostMapping("/addAnnouncement")
    public ResponseEntity<DataResult<AnnouncementDto>> addAnnouncement(@RequestBody CreateAnnouncementRequest createAnnouncementRequest) {
        var announcement = announcementService.addAnnouncement(createAnnouncementRequest);


       return ResponseEntity.status(HttpStatus.CREATED)
               .body(new SuccessDataResult<>(announcement, AnnouncementMessages.ANNOUNCEMENT_ADD_SUCCESS,
                       HttpStatus.CREATED));
    }

    @DeleteMapping("/deleteAnnouncement/{id}")
    public ResponseEntity<Result> deleteAnnouncement(@PathVariable UUID id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(AnnouncementMessages.ANNOUNCEMENT_DELETE_SUCCESS,
                        HttpStatus.OK));
    }

    @PutMapping("/updateAnnouncement/{id}")
    public ResponseEntity<DataResult<AnnouncementDto>> updateAnnouncement(@PathVariable UUID id,
                                                     @RequestBody UpdateAnnouncementRequest updateAnnouncementRequest) {
        var announcement = announcementService.updateAnnouncement(id, updateAnnouncementRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(announcement, AnnouncementMessages.ANNOUNCEMENT_UPDATE_SUCCESS,
                        HttpStatus.OK));
    }

    @GetMapping("/getAnnouncementById/{id}")
    public ResponseEntity<DataResult<AnnouncementDto>> getAnnouncementById(@PathVariable UUID id,
                                                                           @RequestParam(defaultValue = "false") boolean includeUser,
                                                                           @RequestParam(defaultValue = "false") boolean includeEventType,
                                                                           @RequestParam(defaultValue = "false") boolean includeImages) {
        var announcement = announcementService.getAnnouncementById(id, includeUser, includeEventType, includeImages);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(announcement, AnnouncementMessages.ANNOUNCEMENT_GET_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/getAllAnnouncements")
    public ResponseEntity<DataResult<List<AnnouncementDto>>> getAllAnnouncements(@RequestParam(defaultValue = "false") boolean includeUser,
                                                                                 @RequestParam(defaultValue = "false") boolean includeEventType,
                                                                                 @RequestParam(defaultValue = "false") boolean includeImages) {
        var announcements = announcementService.getAllAnnouncements(includeUser, includeEventType, includeImages);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult(announcements, AnnouncementMessages.ANNOUNCEMENT_GET_ALL_SUCCESS,
                        HttpStatus.OK));
    }

    @GetMapping("/getAllByEventTypeId/{eventTypeId}")
    public ResponseEntity<DataResult<List<AnnouncementDto>>> getAllAnnouncementsByEventTypeId(@PathVariable UUID eventTypeId,
                                                                                              @RequestParam(defaultValue = "false") boolean includeUser,
                                                                                              @RequestParam(defaultValue = "false") boolean includeEventType,
                                                                                              @RequestParam(defaultValue = "false") boolean includeImages) {
        var result = announcementService.getAllAnnouncementsByEventTypeId(eventTypeId, includeUser, includeEventType, includeImages);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult(result, AnnouncementMessages.ANNOUNCEMENT_GET_SUCCESS,
                        HttpStatus.OK));
    }

    @PostMapping("/addImagesToAnnouncement")
    public ResponseEntity<Result> addImagesToAnnouncement(@RequestParam UUID id, @RequestBody List<UUID> imageIds) {
        announcementService.addImagesToAnnouncement(id, imageIds);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(AnnouncementMessages.ANNOUNCEMENT_ADD_IMAGES_SUCCESS, HttpStatus.OK));
    }
}