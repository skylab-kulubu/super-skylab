package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.PhotoService;
import com.skylab.superapp.business.abstracts.StaffService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.business.constants.StaffMessages;
import com.skylab.superapp.core.results.*;
import com.skylab.superapp.dataAccess.StaffDao;
import com.skylab.superapp.entities.DTOs.Staff.CreateStaffDto;
import com.skylab.superapp.entities.DTOs.Staff.GetStaffDto;
import com.skylab.superapp.entities.Staff;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StaffManager implements StaffService {


    private StaffDao staffDao;
    private PhotoService photoService;
    private UserService userService;

    public StaffManager(StaffDao staffDao, @Lazy PhotoService photoService, @Lazy UserService userService) {
        this.staffDao = staffDao;
        this.photoService = photoService;
        this.userService = userService;
    }

    @Override
    public DataResult<Integer> addStaff(CreateStaffDto createStaffDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getName();

        if (createStaffDto.getTenant() == null || createStaffDto.getTenant().isEmpty()) {
            return new ErrorDataResult<>(StaffMessages.TenantCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        var tenantCheck = userService.tenantCheck(createStaffDto.getTenant(), username);
        if(!tenantCheck){
            return new ErrorDataResult<>(StaffMessages.UserNotAuthorized, HttpStatus.UNAUTHORIZED);
        }

        var photo = photoService.getPhotoEntityById(createStaffDto.getPhotoId());
        if (!photo.isSuccess()) {
            return new ErrorDataResult<>(photo.getMessage(), photo.getHttpStatus());
        }

        Staff staff = Staff.builder()
                .tenant(createStaffDto.getTenant())
                .department(createStaffDto.getDepartment())
                .firstName(createStaffDto.getFirstName())
                .lastName(createStaffDto.getLastName())
                .linkedin(createStaffDto.getLinkedin())
                .tenant(createStaffDto.getTenant())
                .photo(photo.getData())
                .build();

        staffDao.save(staff);
        return new SuccessDataResult<>(staff.getId(),StaffMessages.StaffAddedSuccess, HttpStatus.CREATED);
    }

    @Override
    public Result deleteStaff(int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getName();

        var staff = staffDao.findById(id);
        if (staff == null) {
            return new ErrorResult(StaffMessages.StaffNotFound, HttpStatus.NOT_FOUND);
        }

        var tenantCheck = userService.tenantCheck(staff.getTenant(), username);
        if(!tenantCheck){
            return new ErrorResult(StaffMessages.UserNotAuthorized, HttpStatus.UNAUTHORIZED);
        }

        staffDao.delete(staff);
        return new SuccessResult(StaffMessages.StaffDeletedSuccess, HttpStatus.OK);
    }

    @Override
    public Result updateStaff(GetStaffDto getStaffDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getName();

        var staff = staffDao.findById(getStaffDto.getId());
        if (staff == null) {
            return new ErrorResult(StaffMessages.StaffNotFound, HttpStatus.NOT_FOUND);
        }

        var tenantCheck = userService.tenantCheck(staff.getTenant(), username);
        if(!tenantCheck){
            return new ErrorResult(StaffMessages.UserNotAuthorized, HttpStatus.UNAUTHORIZED);
        }

        staff.setFirstName(getStaffDto.getFirstName() == null ? staff.getFirstName() : getStaffDto.getFirstName());
        staff.setLastName(getStaffDto.getLastName() == null ? staff.getLastName() : getStaffDto.getLastName());
        staff.setDepartment(getStaffDto.getDepartment() == null ? staff.getDepartment() : getStaffDto.getDepartment());
        staff.setLinkedin(getStaffDto.getLinkedin() == null ? staff.getLinkedin() : getStaffDto.getLinkedin());

        staffDao.save(staff);
        return new SuccessResult(StaffMessages.StaffUpdatedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetStaffDto>> getAllStaff() {
        var result = staffDao.findAll();
        if (result.isEmpty()) {
            return new ErrorDataResult<>(StaffMessages.StaffNotFound, HttpStatus.NOT_FOUND);
        }

        var returnStaff = GetStaffDto.buildListGetStaffDto(result);
        return new SuccessDataResult<>(returnStaff, StaffMessages.StaffListedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<GetStaffDto>> getAllStaffByTenant(String tenant) {
        var result = staffDao.findAllByTenant(tenant);
        if (result.isEmpty()) {
            return new ErrorDataResult<>(StaffMessages.StaffNotFound, HttpStatus.NOT_FOUND);
        }

        var returnStaff = GetStaffDto.buildListGetStaffDto(result);
        return new SuccessDataResult<>(returnStaff, StaffMessages.StaffListedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<Staff> getStaffEntityById(int id) {
        var result = staffDao.findById(id);
        if (result == null) {
            return new ErrorDataResult<>(StaffMessages.StaffNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<>(result, StaffMessages.StaffListedSuccess, HttpStatus.OK);
    }
}
