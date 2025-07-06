package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.AnnouncementService;
import com.skylab.superapp.core.constants.AnnouncementMessages;
import com.skylab.superapp.core.mappers.AnnouncementMapper;
import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.core.results.SuccessDataResult;
import com.skylab.superapp.core.results.SuccessResult;
import com.skylab.superapp.entities.DTOs.Announcement.CreateAnnouncementDto;
import com.skylab.superapp.entities.DTOs.Announcement.GetAnnouncementDto;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    private final AnnouncementMapper announcementMapper;

    public AnnouncementController(AnnouncementService announcementService, AnnouncementMapper announcementMapper) {
        this.announcementService = announcementService;
        this.announcementMapper = announcementMapper;
    }

    @PostMapping("/addAnnouncement")
    public ResponseEntity<Result> addAnnouncement(@RequestBody CreateAnnouncementDto createAnnouncementDto, HttpServletRequest request) {
       announcementService.addAnnouncement(createAnnouncementDto);
       return ResponseEntity.status(HttpStatus.CREATED)
               .body(new SuccessResult(AnnouncementMessages.ANNOUNCEMENT_ADD_SUCCESS, HttpStatus.CREATED,  request.getRequestURI()));
    }

    @DeleteMapping("/deleteAnnouncement/{id}")
    public ResponseEntity<Result> deleteAnnouncement(@PathVariable int id, HttpServletRequest request) {
        announcementService.deleteAnnouncement(id);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(AnnouncementMessages.ANNOUNCEMENT_DELETE_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @PutMapping("/updateAnnouncement/{id}")
    public ResponseEntity<Result> updateAnnouncement(@PathVariable int id, @RequestBody GetAnnouncementDto getAnnouncementDto, HttpServletRequest request) {
        getAnnouncementDto.setId(id);
        announcementService.updateAnnouncement(getAnnouncementDto);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(AnnouncementMessages.ANNOUNCEMENT_UPDATE_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/getAnnouncementById/{id}")
    public ResponseEntity<DataResult<GetAnnouncementDto>> getAnnouncementById(@PathVariable int id, HttpServletRequest request) {
        var announcement = announcementService.getAnnouncementById(id);
        var dto = announcementMapper.toDto(announcement);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult<>(dto, AnnouncementMessages.ANNOUNCEMENT_GET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/getAllAnnouncements")
    public ResponseEntity<DataResult<List<GetAnnouncementDto>>> getAllAnnouncements(HttpServletRequest request) {
        var announcements = announcementService.getAllAnnouncements();
        var dtos = announcementMapper.toDtoList(announcements);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult(dtos, AnnouncementMessages.ANNOUNCEMENT_GET_ALL_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/getAllByEventTypeName")
    public ResponseEntity<DataResult<List<GetAnnouncementDto>>> getAllAnnouncementsByEventTypeName(@RequestParam String eventTypeName, HttpServletRequest request) {
        var result = announcementService.getAllAnnouncementsByEventTypeName(eventTypeName);
        var dtos = announcementMapper.toDtoList(result);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult(dtos, AnnouncementMessages.ANNOUNCEMENT_GET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/getAllByEventTypeId")
    public ResponseEntity<DataResult<List<GetAnnouncementDto>>> getAllAnnouncementsByEventTypeId(@RequestParam int eventTypeId, HttpServletRequest request) {
        var result = announcementService.getAllAnnouncementsByEventTypeId(eventTypeId);
        var dtos = announcementMapper.toDtoList(result);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessDataResult(dtos, AnnouncementMessages.ANNOUNCEMENT_GET_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }

    @PostMapping("/addImagesToAnnouncement")
    public ResponseEntity<Result> addImagesToAnnouncement(@RequestParam int id, @RequestBody List<Integer> imageIds, HttpServletRequest request) {
        announcementService.addImagesToAnnouncement(id, imageIds);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new SuccessResult(AnnouncementMessages.ANNOUNCEMENT_ADD_IMAGES_SUCCESS, HttpStatus.OK, request.getRequestURI()));
    }
}