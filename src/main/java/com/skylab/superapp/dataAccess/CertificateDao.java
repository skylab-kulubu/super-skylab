package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.Certificate;
import com.skylab.superapp.entities.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CertificateDao extends JpaRepository<Certificate, UUID> {
    List<Certificate> findAllByEvent(Event event);
    List<Certificate> findAllByOwners_Id(UUID userId);
}
