package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Competitor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompetitorDao extends JpaRepository<Competitor, String> {
    Competitor findByName(String name);

    boolean existsByName(String name);

    boolean existsById(String id);

    Competitor findByTenantAndName(String tenant, String name);

    List<Competitor> findAllByTenant(String tenant);

    List<Competitor> findAllBySeasons_Id(int seasonId);

    List<Competitor> findAllBySeasons_IdOrderByTotalPointsDesc(int seasonId);
}
