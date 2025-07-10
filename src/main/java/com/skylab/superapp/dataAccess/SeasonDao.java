package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Season;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SeasonDao extends JpaRepository<Season, UUID> {
    Optional<Season> findById(UUID id);

    Optional<Season> findByName(String name);

    boolean existsByName(String name);

    List<Season> findAllByActive(boolean isActive);
}
