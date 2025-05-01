package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Staff;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StaffDao extends JpaRepository<Staff, Integer> {
    List<Staff> findAllByTenant(String tenant);

    Staff findById(int id);

}
