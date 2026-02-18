package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.File;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FileDao extends JpaRepository<File, UUID> {
}
