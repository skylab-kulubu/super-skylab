package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.LdapUser;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LdapUserDao extends LdapRepository<LdapUser> {

    Optional<LdapUser> findByEmployeeNumber(String employeeNumber);

    Optional<LdapUser> findByUsername(String username);

    Optional<LdapUser> findByEmail(String email);

    List<LdapUser> findAll(org.springframework.ldap.query.LdapQuery query);


}
