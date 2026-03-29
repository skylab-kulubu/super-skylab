package com.skylab.superapp.core.identity.ldap;


import com.skylab.superapp.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;

import javax.naming.Name;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.BasicAttributes;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;


@Service
public class LdapService {

    private final LdapTemplate ldapTemplate;
    private final Logger logger = LoggerFactory.getLogger(LdapService.class);


    public LdapService(LdapTemplate ldapTemplate) {
        this.ldapTemplate = ldapTemplate;
    }

    public boolean usernameExists(String username){
        logger.info("Checking if username '{}' exists in LDAP", username);
        try {
            return !ldapTemplate.search(
                    "ou=people",
                    new EqualsFilter("uid", username).encode(),
                    (Object ctx) -> ctx
            ).isEmpty();
        } catch (Exception e) {
            logger.error("Error checking if username exists in LDAP: {}", e.getMessage());
            return false;
        }
    }



    public String promoteAndCreateUser(User user, String generatedUsername, String rawPassword) {
        logger.info("Promoting user to LDAP with username: {}", generatedUsername);

        Name dn = LdapNameBuilder.newInstance()
                .add("ou", "people")
                .add("uid", generatedUsername)
                .build();


        BasicAttributes attributes = new BasicAttributes();
        BasicAttribute objectClass = new BasicAttribute("objectClass");
        objectClass.add("top");
        objectClass.add("person");
        objectClass.add("organizationalPerson");
        objectClass.add("inetOrgPerson");

        attributes.put(objectClass);
        attributes.put("uid", generatedUsername);
        attributes.put("sn", user.getLastName());
        attributes.put("givenName", user.getFirstName());
        attributes.put("cn", user.getFirstName() + " " + user.getLastName());
        //attributes.put("mail", user.getEmail());
        attributes.put("employeeNumber", user.getSkyNumber());
        attributes.put("userPassword", rawPassword);

        ldapTemplate.bind(dn, null, attributes);
        logger.info("Successfully promoted user to LDAP with username: {}", generatedUsername);

        return generatedUsername;
    }


    public void addUserToGroup(String username, String groupName){
        logger.info("Adding user '{}' to LDAP group '{}'", username, groupName);

        String userDnString = "uid=" + username + ",ou=people,dc=yildizskylab,dc=com";

        Name groupDn = LdapNameBuilder.newInstance()
                .add("ou", "groups")
                .add("cn", groupName)
                .build();


        ModificationItem[] mods = new ModificationItem[1];
        mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE, new BasicAttribute("member", userDnString));

        try {
            ldapTemplate.modifyAttributes(groupDn, mods);
            logger.info("User {} added to group {} successfully", username, groupName);
        } catch (Exception e) {
            logger.error("Failed to add user to group or already member: {}", e.getMessage());
        }
    }


    public void removeUserFromGroup(String username, String groupName) {
        logger.info("Removing user {} from group: {}", username, groupName);

        String userDnString = "uid=" + username + ",ou=people,dc=yildizskylab,dc=com";
        Name groupDn = LdapNameBuilder.newInstance()
                .add("ou", "groups")
                .add("cn", groupName)
                .build();

        ModificationItem[] mods = new ModificationItem[1];
        mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE, new BasicAttribute("member", userDnString));

        try {
            ldapTemplate.modifyAttributes(groupDn, mods);
        } catch (Exception e) {
            logger.error("Failed to remove user from group: {}", e.getMessage());
        }
    }

    public void deleteUser(String username) {
        logger.info("Deleting user from LDAP with username: {}", username);
        try {
            Name dn = LdapNameBuilder.newInstance()
                    .add("ou", "people")
                    .add("uid", username)
                    .build();

            ldapTemplate.unbind(dn);
            logger.info("Successfully deleted user '{}' from LDAP", username);
        } catch (Exception e) {
            logger.error("Failed to delete user '{}' from LDAP: {}", username, e.getMessage());
            throw new RuntimeException("An error occurred while deleting the user from LDAP: " + e.getMessage());
        }
    }




}