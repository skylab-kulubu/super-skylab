package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.CompetitorEventResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CompetitorEventResultDao extends JpaRepository<CompetitorEventResult, Long> {

    CompetitorEventResult findByCompetitorIdAndEventId(String competitorId, int eventId);

    boolean existsByCompetitorIdAndEventId(String competitorId, int eventId);

    @Query("SELECT cer FROM CompetitorEventResult cer " +
            "JOIN FETCH cer.event " + // Event'leri tek seferde çek
            "WHERE cer.competitor.id = :competitorId " +
            "ORDER BY cer.event.date DESC")
    List<CompetitorEventResult> findAllByCompetitorIdWithEvent(@Param("competitorId") String competitorId);

    List<CompetitorEventResult> findAllByEventId(int eventId);

    @Query("SELECT COUNT(cer) > 0 " +
            "FROM CompetitorEventResult cer " +
            "WHERE cer.competitor.id = :competitorId " +
            "AND cer.event.season.id = :seasonId")
    boolean existsByCompetitorAndSeason(
            @Param("competitorId") String competitorId,
            @Param("seasonId") int seasonId
    );




}
