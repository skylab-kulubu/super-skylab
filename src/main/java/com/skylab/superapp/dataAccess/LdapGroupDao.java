package com.skylab.superapp.dataAccess;

import com.skylab.superapp.entities.LdapGroup;
import org.springframework.data.ldap.repository.LdapRepository;
import org.springframework.stereotype.Repository;

import javax.naming.Name;
import java.util.List;
import java.util.Optional;

@Repository
public interface LdapGroupDao extends LdapRepository<LdapGroup> {

    Optional<LdapGroup> findByRoleName(String roleName);

    List<LdapGroup> findByMembers(Name members);

}
