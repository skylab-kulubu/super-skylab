package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionDao extends JpaRepository<Session, Integer> {
}
