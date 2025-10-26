package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.AnnouncementService;
import com.skylab.superapp.core.constants.AnnouncementMessages;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.Announcement.AnnouncementDto;
import com.skylab.superapp.entities.DTOs.Announcement.CreateAnnouncementRequestDto;
import com.skylab.superapp.entities.DTOs.Announcement.UpdateAnnouncementRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @PostMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<DataResult<AnnouncementDto>> addAnnouncement(
            @RequestPart("data") @Valid CreateAnnouncementRequestDto createAnnouncementRequest,
            @RequestPart(value = "coverImage", required = false) MultipartFile coverImage) {
        var announcement = announcementService.addAnnouncement(createAnnouncementRequest, coverImage);

       return ResponseEntity.status(HttpStatus.CREATED)
               .body(new SuccessDataResult<>(announcement, AnnouncementMessages.ANNOUNCEMENT_ADD_SUCCESS,
                       HttpStatus.CREATED));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteAnnouncement(@PathVariable UUID id) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(AnnouncementMessages.ANNOUNCEMENT_DELETE_SUCCESS,
                        HttpStatus.OK));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<DataResult<AnnouncementDto>> updateAnnouncement(@PathVariable UUID id,
                                                     @RequestBody UpdateAnnouncementRequest updateAnnouncementRequest) {
        var announcement = announcementService.updateAnnouncement(id, updateAnnouncementRequest);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(announcement, AnnouncementMessages.ANNOUNCEMENT_UPDATE_SUCCESS,
                        HttpStatus.OK));
    }

    @GetMapping("/{id}")
    public ResponseEntity<DataResult<AnnouncementDto>> getAnnouncementById(@PathVariable UUID id,
                                                                           @RequestParam(defaultValue = "false") boolean includeEventType) {
        var announcement = announcementService.getAnnouncementById(id, includeEventType);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(announcement, AnnouncementMessages.ANNOUNCEMENT_GET_SUCCESS, HttpStatus.OK));
    }

    @GetMapping("/")
    public ResponseEntity<DataResult<List<AnnouncementDto>>> getAllAnnouncements(@RequestParam(defaultValue = "false") boolean includeUser,
                                                                                 @RequestParam(defaultValue = "false") boolean includeEventType,
                                                                                 @RequestParam(defaultValue = "false") boolean includeImages) {
        var announcements = announcementService.getAllAnnouncements(includeUser, includeEventType, includeImages);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult(announcements, AnnouncementMessages.ANNOUNCEMENT_GET_ALL_SUCCESS,
                        HttpStatus.OK));
    }

    @GetMapping("/event-type/{eventTypeId}")
    public ResponseEntity<DataResult<List<AnnouncementDto>>> getAllAnnouncementsByEventTypeId(@PathVariable UUID eventTypeId,
                                                                                              @RequestParam(defaultValue = "false") boolean includeUser,
                                                                                              @RequestParam(defaultValue = "false") boolean includeEventType,
                                                                                              @RequestParam(defaultValue = "false") boolean includeImages) {
        var result = announcementService.getAllAnnouncementsByEventTypeId(eventTypeId, includeUser, includeEventType, includeImages);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult(result, AnnouncementMessages.ANNOUNCEMENT_GET_SUCCESS,
                        HttpStatus.OK));
    }
}