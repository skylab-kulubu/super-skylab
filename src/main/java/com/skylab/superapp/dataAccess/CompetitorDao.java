package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CompetitorDao extends JpaRepository<Competitor, UUID> {

    @Query("SELECT c FROM Competitor c WHERE c.event.type.name= :eventTypeName")
    List<Competitor> findByEventTypeName(@Param("eventTypeName") String eventTypeName);

    @Query("SELECT c FROM Competitor c WHERE c.event.type.id = :eventTypeId")
    List<Competitor> findByEventTypeId(UUID eventTypeId);

    @Query("SELECT c FROM Competitor c WHERE c.event.season.id = :seasonId")
    List<Competitor> findBySeasonId(@Param("seasonId") UUID seasonId);

    @Query("SELECT c FROM Competitor c WHERE c.event.type.name = :eventTypeName AND c.event.season.id = :seasonId")
    List<Competitor> findLeaderboardByEventTypeAndSeasonId(@Param("eventTypeName") String eventTypeName, @Param("seasonId") UUID seasonId);

    @Query("SELECT c FROM Competitor c WHERE c.event.type.name = :eventTypeName")
    List<Competitor> findLeaderboardByEventType(@Param("eventTypeName") String eventTypeName);

    @Query("""
    SELECT c.user, SUM(c.points) as totalPoints
    FROM Competitor c 
    WHERE c.event.type.name = :eventTypeName 
    GROUP BY c.user 
    ORDER BY totalPoints DESC
    """)
    List<UserProfile> findLeaderboardWithTotalPointsByEventType(@Param("eventTypeName") String eventTypeName);

    @Query("SELECT c FROM Competitor c WHERE c.event.id = :eventId AND c.isWinner = true")
    Optional<Competitor> findByEventIdAndIsWinnerTrue(@Param("eventId") UUID eventId);

    boolean existsByUserIdAndEventId(UUID userId, UUID eventId);

    @Query("SELECT c FROM Competitor c WHERE c.event.id = :eventId")
    List<Competitor> findByEventId(@Param("eventId") UUID eventId);

    @Query("SELECT c FROM Competitor c WHERE c.user.id = :userId")
    List<Competitor> findByUserId(@Param("userId") UUID userId);

    @Query("SELECT DISTINCT c.user.id FROM Competitor c")
    List<UUID> findDistinctUserIds();

    @Query("SELECT DISTINCT c.user.id FROM Competitor c WHERE c.event.type.id = :eventTypeId")
    List<UUID> findDistinctUserIdsByEventTypeId(@Param("eventTypeId") UUID eventTypeId);

    @Query("SELECT SUM(c.points) FROM Competitor c WHERE c.user.id = :userId")
    Double getTotalPointsByUserId(@Param("userId") UUID userId);

    @Query("SELECT SUM(c.points) FROM Competitor c WHERE c.user.id = :userId AND c.event.type.id = :eventTypeId")
    Double getTotalPointsByUserIdAndEventTypeId(@Param("userId") UUID userId, @Param("eventTypeId") UUID eventTypeId);

    @Query("SELECT COUNT(c) FROM Competitor c WHERE c.user = :user")
    int getTotalCompetitionCountByUserId(UserProfile user);

    @Query("SELECT COUNT(c) FROM Competitor c WHERE c.user.id = :userId AND c.event.type.id = :eventTypeId")
    int getTotalCompetitionCountByUserIdAndEventTypeId(@Param("userId") UUID userId, @Param("eventTypeId") UUID eventTypeId);

    List<Competitor> findCompetitorsByUser(UserProfile user);

    List<Competitor> findAllByEventType(EventType eventType);

    @Query("SELECT c FROM Competitor c WHERE c.event.competition =:competition ORDER BY c.points DESC")
    List<Competitor> findLeaderboardByCompetition(Competition competition);

    @Query("SELECT c FROM Competitor c WHERE c.event =:event")
    Competitor findCompetitorByEvent(Event event);

    @Query("SELECT c FROM Competitor c WHERE c.event =:event and c.isWinner = true")
    Competitor findWinnerOfEvent(Event event);

    @Query("SELECT COALESCE(SUM(c.points), 0) FROM Competitor c WHERE c.event.competition = :competition AND c.user = :user")
    double findUsersTotalPointsInCompetition(
            UserProfile user,
            Competition competition
    );

    boolean existsByUserAndEvent(UserProfile user, Event event);
}