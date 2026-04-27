package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface CertificateDao extends JpaRepository<Certificate, UUID> {
}
