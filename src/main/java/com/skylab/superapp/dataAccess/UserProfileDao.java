package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserProfileDao extends JpaRepository<UserProfile, UUID> {
    Optional<UserProfile> findById(UUID id);

    Optional<UserProfile> findByLdapSkyNumber(String ldapSkyNumber);

    List<UserProfile> findAllByLdapSkyNumberIn(List<String> skyNumbers);

}
