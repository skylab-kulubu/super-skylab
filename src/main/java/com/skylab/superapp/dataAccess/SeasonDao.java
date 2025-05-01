package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Season;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeasonDao extends JpaRepository<Season, Integer> {
    Season findById(int id);

    Season findByName(String name);

    boolean existsByName(String name);

    boolean existsById(int id);

    List<Season> findAllByTenant(String tenant);

    List<Season> findAllByTenantAndIsActiveTrue(String tenant);


}
