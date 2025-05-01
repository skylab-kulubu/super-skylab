package com.skylab.superapp.business.abstracts;

import com.skylab.superapp.core.results.DataResult;
import com.skylab.superapp.core.results.Result;
import com.skylab.superapp.entities.DTOs.Staff.CreateStaffDto;
import com.skylab.superapp.entities.DTOs.Staff.GetStaffDto;
import com.skylab.superapp.entities.Staff;

import java.util.List;

public interface StaffService {

    Result addStaff(CreateStaffDto createStaffDto);

    Result deleteStaff(int id);

    Result updateStaff(GetStaffDto getStaffDto);

    DataResult<List<GetStaffDto>> getAllStaff();

    DataResult<List<GetStaffDto>> getAllStaffByTenant(String tenant);

    DataResult<Staff> getStaffEntityById(int id);

}
