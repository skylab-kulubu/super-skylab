package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.*;
import com.skylab.superapp.entities.DTOs.Competitor.LeaderboardDto;
import com.skylab.superapp.entities.DTOs.Competitor.LeaderboardScoreDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompetitorDao extends JpaRepository<Competitor, UUID> {

    @Query("SELECT new com.skylab.superapp.entities.DTOs.Competitor.LeaderboardScoreDto(" +
            "c.user.id, " +
            "SUM(c.score), " +
            "COUNT(c)) " +
            "FROM Competitor c " +
            "WHERE c.event.type.name = :eventTypeName " +
            "GROUP BY c.user.id " +
            "ORDER BY SUM(c.score) DESC")
    List<LeaderboardScoreDto> getLeaderboardScoresByEventType(@Param("eventTypeName") String eventTypeName);

    @Query("SELECT new com.skylab.superapp.entities.DTOs.Competitor.LeaderboardScoreDto(" +
            "c.user.id, " +
            "SUM(c.score), " +
            "COUNT(c)) " +
            "FROM Competitor c " +
            "WHERE c.event.type.name = :eventTypeName " +
            "AND c.event.season.id = :seasonId " +
            "GROUP BY c.user.id " +
            "ORDER BY SUM(c.score) DESC")
    List<LeaderboardScoreDto> getLeaderboardScoresBySeasonAndEventType(@Param("eventTypeName") String eventTypeName,
                                                                       @Param("seasonId") UUID seasonId);

    @Query("SELECT c FROM Competitor c WHERE c.event.id = :eventId")
    List<Competitor> findByEventId(@Param("eventId") UUID eventId);

    @Query("SELECT c FROM Competitor c WHERE c.user.id = :userId")
    List<Competitor> findByUserId(@Param("userId") UUID userId);

    List<Competitor> findCompetitorsByUser(UserProfile user);

    List<Competitor> findAllByEventType(EventType eventType);

    @Query("SELECT c FROM Competitor c WHERE c.event =:event and c.isWinner = true")
    Competitor findWinnerOfEvent(Event event);

    boolean existsByUserAndEvent(UserProfile user, Event event);
}