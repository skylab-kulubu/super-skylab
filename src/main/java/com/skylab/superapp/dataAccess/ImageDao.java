package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface ImageDao extends JpaRepository<Image, Integer> {

    @Transactional
    Optional<Image> findByUrl(String url);
}
