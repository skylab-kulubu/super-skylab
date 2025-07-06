package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Competition;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CompetitionDao extends JpaRepository<Competition, Integer> {
    List<Competition> findAllByActive(boolean active);
}
