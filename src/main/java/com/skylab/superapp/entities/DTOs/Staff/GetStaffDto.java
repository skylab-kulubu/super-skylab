package com.skylab.superapp.entities.DTOs.Staff;

import com.skylab.superapp.entities.DTOs.Photo.GetPhotoDto;
import com.skylab.superapp.entities.Staff;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class GetStaffDto {

    private int id;

    private String firstName;

    private String lastName;

    private String linkedin;

    private String department;

    private GetPhotoDto photo;

    public GetStaffDto(Staff staff) {
        this.id = staff.getId();
        this.firstName = staff.getFirstName();
        this.lastName = staff.getLastName();
        this.linkedin = staff.getLinkedin();
        this.department = staff.getDepartment();
        this.photo = new GetPhotoDto(staff.getPhoto());
    }

    public static List<GetStaffDto> buildListGetStaffDto(List<Staff> staffList) {
        return staffList.stream()
                .map(GetStaffDto::new)
                .toList();
    }
}
