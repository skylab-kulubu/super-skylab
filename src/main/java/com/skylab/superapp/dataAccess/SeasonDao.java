package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.EventType;
import com.skylab.superapp.entities.Season;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeasonDao extends JpaRepository<Season, Integer> {
    Optional<Season> findById(int id);

    Optional<Season> findByName(String name);

    boolean existsByName(String name);

    List<Season> findAllByActive(boolean isActive);
}
