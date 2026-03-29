package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDao extends JpaRepository<User, UUID> , JpaSpecificationExecutor<User> {
    Optional<User> findById(UUID id);

    Optional<User> findBySkyNumber(String skyNumber);

    List<User> findAllBySkyNumberIn(List<String> skyNumbers);

    @Query("SELECT MAX(CAST(SUBSTRING(u.skyNumber, 5) AS integer)) FROM User u")
    Integer findMaxSkyNumberValue();

}
