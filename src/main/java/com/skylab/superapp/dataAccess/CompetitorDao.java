package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Competitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompetitorDao extends JpaRepository<Competitor, Integer> {
    @Query("SELECT c FROM Competitor c WHERE c.eventType.name = :eventTypeName")
    List<Competitor> findByEventTypeName(@Param("eventTypeName") String eventTypeName);

    @Query("SELECT c FROM Competitor c WHERE c.season.id = :seasonId")
    List<Competitor> findBySeasonId(@Param("seasonId") int seasonId);

    @Query("SELECT c FROM Competitor c WHERE c.event.id = :eventId AND c.isWinner = true")
    Optional<Competitor> findByEventIdAndIsWinnerTrue(@Param("eventId") int eventId);

    boolean existsByUserIdAndEventId(int userId, int eventId);

    @Query("SELECT c FROM Competitor c WHERE c.event.id = :eventId")
    List<Competitor> findByEventId(@Param("eventId") int eventId);

    @Query("SELECT DISTINCT c.user.id FROM Competitor c")
    List<Integer> findDistinctUserIds();

    @Query("SELECT DISTINCT c.user.id FROM Competitor c WHERE c.eventType.id = :eventTypeId")
    List<Integer> findDistinctUserIdsByEventTypeId(@Param("eventTypeId") int eventTypeId);

    @Query("SELECT SUM(c.points) FROM Competitor c WHERE c.user.id = :userId")
    Double getTotalPointsByUserId(@Param("userId") int userId);

    @Query("SELECT SUM(c.points) FROM Competitor c WHERE c.user.id = :userId AND c.eventType.id = :eventTypeId")
    Double getTotalPointsByUserIdAndEventTypeId(@Param("userId") int userId, @Param("eventTypeId") int eventTypeId);

    @Query("SELECT COUNT(c) FROM Competitor c WHERE c.user.id = :userId")
    int getTotalCompetitionCountByUserId(@Param("userId") int userId);

    @Query("SELECT COUNT(c) FROM Competitor c WHERE c.user.id = :userId AND c.eventType.id = :eventTypeId")
    int getTotalCompetitionCountByUserIdAndEventTypeId(@Param("userId") int userId, @Param("eventTypeId") int eventTypeId);

    boolean existsByUserId(int userId);
}