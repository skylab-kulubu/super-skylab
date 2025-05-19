package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserDao extends JpaRepository<User, Integer> {
    User findById(int id);

    User findByUsername(String username);

    boolean existsByUsername(String username);

    Optional<User> findByEmail(String email);
}
