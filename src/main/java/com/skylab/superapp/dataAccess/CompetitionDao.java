package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CompetitionDao extends JpaRepository<Competition, UUID> {
    List<Competition> findAllByActive(boolean active);
}
