package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserDao extends JpaRepository<User, UUID>, JpaSpecificationExecutor<User> {

    Optional<User> findById(UUID id);

    Optional<User> findBySkyNumber(String skyNumber);

    List<User> findAllBySkyNumberIn(List<String> skyNumbers);

    @Query("SELECT MAX(CAST(SUBSTRING(u.skyNumber, 5) AS integer)) FROM User u")
    Integer findMaxSkyNumberValue();


    @Query("""
        SELECT u FROM User u
        WHERE LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(u.schoolEmail) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
           OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :search, '%'))
    """)
    List<User> searchUsers(@Param("search") String search);


    @Query("""
        SELECT u FROM User u
        WHERE u.id IN :ids
          AND (LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.schoolEmail) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%'))
            OR LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :search, '%')))
    """)
    List<User> searchUsersInIds(@Param("search") String search, @Param("ids") Set<UUID> ids);

}