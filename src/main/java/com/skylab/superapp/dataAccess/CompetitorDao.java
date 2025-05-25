package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Competitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompetitorDao extends JpaRepository<Competitor, String> {
    Competitor findByName(String name);

    boolean existsByName(String name);

    boolean existsById(String id);

    Competitor findByTenantAndName(String tenant, String name);

    List<Competitor> findAllByTenant(String tenant);

    @Query("SELECT DISTINCT c FROM Competitor c " +
            "JOIN FETCH c.eventResults cer " + // Performans için
            "JOIN FETCH cer.event e " +
            "WHERE e.season.id = :seasonId")
    List<Competitor> findCompetitorsBySeasonId(@Param("seasonId") int seasonId);
}
