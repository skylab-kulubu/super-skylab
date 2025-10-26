package com.skylab.superapp.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

@Getter
@Setter
@Entry(objectClasses = {"inetOrgPerson", "top"}, base = "ou=people")
public final class LdapUser {

    @Id
    private Name dn;

    @Attribute(name = "employeeNumber")
    @DnAttribute(value = "employeeNumber", index = 0)
    private String employeeNumber;

    @Attribute(name = "uid")
    private String username;

    @Attribute(name = "cn")
    private String fullName;

    @Attribute(name = "sn")
    private String lastName;

    @Attribute(name = "givenName")
    private String firstName;

    @Attribute(name = "mail")
    private String email;

    @Attribute(name = "telephoneNumber")
    private String phoneNumber;

    @Attribute(name = "userPassword")
    private String userPassword;


}
