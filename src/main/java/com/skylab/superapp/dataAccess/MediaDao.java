package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MediaDao extends JpaRepository<Media, UUID> {

    List<Media> findByAttachedFalseAndCreatedAtBefore(LocalDateTime threshold);


}
