package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<User, Integer> {
    Optional<User> findById(int id);

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByEmail(String email);

    List<User> findAllByAuthorities(String role);

    @Query("SELECT u FROM User u JOIN u.authorities a WHERE a IN :roleNames")
    List<User> findAllByAuthorities_NameIn(@Param("roleNames") List<String> roleNames);

}
