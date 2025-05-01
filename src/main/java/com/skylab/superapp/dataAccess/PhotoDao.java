package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhotoDao extends JpaRepository<Photo, Integer> {
    Photo findById(int id);

    List<Photo> findAllByTenant(String tenant);
}
