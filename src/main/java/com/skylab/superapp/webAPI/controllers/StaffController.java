package com.skylab.superapp.webAPI.controllers;

import com.skylab.superapp.business.abstracts.StaffService;
import com.skylab.superapp.entities.DTOs.Staff.CreateStaffDto;
import com.skylab.superapp.entities.DTOs.Staff.GetStaffDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/staff")
public class StaffController {

    private final StaffService staffService;

    public StaffController(StaffService staffService) {
        this.staffService = staffService;
    }

    @PostMapping("/addStaff")
    public ResponseEntity<?> addStaff(@RequestBody CreateStaffDto createStaffDto) {
        var result = staffService.addStaff(createStaffDto);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/deleteStaff")
    public ResponseEntity<?> deleteStaff(@RequestParam int id) {
        var result = staffService.deleteStaff(id);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @PostMapping("/updateStaff")
    public ResponseEntity<?> updateStaff(@RequestBody GetStaffDto getStaffDto) {
        var result = staffService.updateStaff(getStaffDto);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getAllByTenant")
    public ResponseEntity<?> getAllStaffByTenant(@RequestParam String tenant) {
        var result = staffService.getAllStaffByTenant(tenant);
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }

    @GetMapping("/getAll")
    public ResponseEntity<?> getAllStaff() {
        var result = staffService.getAllStaff();
        return ResponseEntity.status(result.getHttpStatus()).body(result);
    }
}
