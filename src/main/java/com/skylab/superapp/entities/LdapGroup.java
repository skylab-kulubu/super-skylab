package com.skylab.superapp.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;
import java.util.LinkedHashSet;
import java.util.Set;

@Getter
@Setter
@Entry(objectClasses = {"groupOfNames", "top"}, base = "ou=groups")
public final class LdapGroup {

    @Id
    private Name dn;

    @Attribute(name = "cn")
    @DnAttribute(value = "cn", index=0)
    private String roleName;

    @Attribute(name = "member")
    private Set<Name> members = new LinkedHashSet<>();


}
