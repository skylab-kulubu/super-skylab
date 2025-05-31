package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.AnnouncementService;
import com.skylab.superapp.entities.DTOs.Announcement.CreateAnnouncementDto;
import com.skylab.superapp.entities.DTOs.Announcement.GetAnnouncementDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/announcements")
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @PostMapping("/addAnnouncement")
    public ResponseEntity<?> addAnnouncement(@RequestBody CreateAnnouncementDto createAnnouncementDto) {
        var result = announcementService.addAnnouncement(createAnnouncementDto);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/deleteAnnouncement")
    public ResponseEntity<?> deleteAnnouncement(@RequestParam int id) {
        var result = announcementService.deleteAnnouncement(id);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/updateAnnouncement")
    public ResponseEntity<?> updateAnnouncement(@RequestBody GetAnnouncementDto getAnnouncementDto) {
        var result = announcementService.updateAnnouncement(getAnnouncementDto);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getAllByTenant")
    public ResponseEntity<?> getAllAnnouncementsByTenant(@RequestParam String tenant) {
        var result = announcementService.getAllAnnouncementsByTenant(tenant);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/addImagesToAnnouncement")
    public ResponseEntity<?> addImagesToAnnouncement(@RequestParam int id, @RequestBody List<Integer> imageIds) {
        var result = announcementService.addImagesToAnnouncement(id, imageIds);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }
}