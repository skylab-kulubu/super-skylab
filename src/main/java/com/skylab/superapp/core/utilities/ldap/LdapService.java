package com.skylab.superapp.core.utilities.ldap;

import com.skylab.superapp.core.exceptions.ResourceNotFoundException;
import com.skylab.superapp.core.exceptions.ValidationException;
import com.skylab.superapp.dataAccess.LdapGroupDao;
import com.skylab.superapp.dataAccess.LdapUserDao;
import com.skylab.superapp.entities.LdapGroup;
import com.skylab.superapp.entities.LdapUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.filter.EqualsFilter;
import org.springframework.ldap.filter.OrFilter;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.ldap.query.SearchScope;
import org.springframework.ldap.support.LdapNameBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.naming.Name;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.ModificationItem;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LdapService {

    private final LdapUserDao ldapUserDao;
    private final LdapGroupDao ldapGroupDao;
    private final LdapTemplate ldapTemplate;
    private final EmployeeNumberGenerator employeeNumberGenerator;
    private final Logger logger = LoggerFactory.getLogger(LdapService.class);

    public LdapService(LdapUserDao ldapUserDao, LdapGroupDao ldapGroupDao,
                       LdapTemplate ldapTemplate,
                       EmployeeNumberGenerator employeeNumberGenerator) {
        this.ldapUserDao = ldapUserDao;
        this.ldapGroupDao = ldapGroupDao;
        this.ldapTemplate = ldapTemplate;
        this.employeeNumberGenerator = employeeNumberGenerator;
    }

    private LdapUser fixEmployeeNumber(LdapUser user) {
        if (user != null && (user.getEmployeeNumber() == null || !user.getEmployeeNumber().startsWith("SKY-"))) {
            String dn = user.getDn().toString();
            if (dn.startsWith("employeeNumber=")) {
                int commaIndex = dn.indexOf(',');
                if (commaIndex > 0) {
                    user.setEmployeeNumber(dn.substring(15, commaIndex));
                }
            }
        }
        return user;
    }

    public LdapUser createUser(String username, String firstName, String lastName, String email, String password){
        logger.info("Creating user in LDAP for username: {}", username);

        if (ldapUserDao.findByUsername(username).isPresent()){
            throw new ValidationException(LdapMessages.USERNAME_ALREADY_EXISTS);
        }

        if (ldapUserDao.findByEmail(email).isPresent()){
            throw new ValidationException(LdapMessages.EMAIL_ALREADY_EXISTS);
        }

        String employeeNumber = employeeNumberGenerator.generateNext();
        logger.info("Generated employee number: {}", employeeNumber);

        LdapUser user = new LdapUser();
        user.setEmployeeNumber(employeeNumber);
        user.setUsername(username);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setFullName(firstName + " " + lastName);
        user.setEmail(email);

        user.setUserPassword(password);

        LdapUser savedUser = ldapUserDao.save(user);

        logger.info("User created in LDAP: {} ({})", username, employeeNumber);

        addUserToGroup(employeeNumber, "USER");

        return fixEmployeeNumber(savedUser);
    }

    public LdapUser findByEmployeeNumber(String employeeNumber){
        logger.info("Finding user by employee number: {}", employeeNumber);
        return fixEmployeeNumber(
                ldapUserDao.findByEmployeeNumber(employeeNumber)
                        .orElseThrow(() -> new ResourceNotFoundException(LdapMessages.USER_NOT_FOUND))
        );
    }

    public LdapUser findByUsername(String username){
        logger.info("Finding user by username: {}", username);
        return fixEmployeeNumber(
                ldapUserDao.findByUsername(username)
                        .orElseThrow(() -> new ResourceNotFoundException(LdapMessages.USER_NOT_FOUND))
        );
    }

    public LdapUser findByEmail(String email) {
        return fixEmployeeNumber(
                ldapUserDao.findByEmail(email)
                        .orElseThrow(() -> new ResourceNotFoundException(LdapMessages.USER_NOT_FOUND))
        );
    }

    public boolean userExists(String employeeNumber){
        return ldapUserDao.findByEmployeeNumber(employeeNumber).isPresent();
    }

    public boolean usernameExists(String username){
        return ldapUserDao.findByUsername(username).isPresent();
    }

    public void changePassword(String employeeNumber, String newPassword){
        logger.info("Changing password for user with employee number: {}", employeeNumber);
        LdapUser user = findByEmployeeNumber(employeeNumber);

        user.setUserPassword(newPassword);
        ldapUserDao.save(user);

        logger.info("Password changed for user with employee number: {}", employeeNumber);
    }

    public void changeUsername(String employeeNumber, String newUsername){
        logger.info("Changing username for user with employee number: {}", employeeNumber);

        if (usernameExists(newUsername)){
            throw new ValidationException(LdapMessages.USERNAME_ALREADY_EXISTS);
        }

        LdapUser user = findByEmployeeNumber(employeeNumber);
        String oldUsername = user.getUsername();

        user.setUsername(newUsername);
        ldapUserDao.save(user);

        logger.info("Username changed from {} to {} for user with employee number: {}", oldUsername, newUsername, employeeNumber);
    }

    @Transactional
    public void deleteUser(String employeeNumber){
        logger.info("Deleting user with employee number: {}", employeeNumber);

        LdapUser user = findByEmployeeNumber(employeeNumber);

        Set<String> userGroups = getUserGroups(employeeNumber);
        logger.info("Removing user {} from {} groups", employeeNumber, userGroups.size());

        for (String groupName : userGroups){
            removeUserFromGroup(employeeNumber, groupName);
        }

        ldapUserDao.delete(user);
        logger.info("User with employee number: {} deleted successfully", employeeNumber);
    }

    public void addUserToGroup(String employeeNumber, String groupName){
        logger.info("Adding user {} to group: {}", employeeNumber, groupName);

        LdapGroup group = ldapGroupDao.findByRoleName(groupName)
                .orElseThrow(()->{
                    logger.info("Group not found: {}", groupName);
                    return new ResourceNotFoundException(LdapMessages.GROUP_NOT_FOUND);
                });

        logger.info("Found group {}. Current member count: {}", groupName, group.getMembers().size());

        String userDnString = "employeeNumber=" + employeeNumber + ",ou=people,dc=yildizskylab,dc=com";
        Name userDn = LdapNameBuilder.newInstance(userDnString).build();

        boolean alreadyMember = group.getMembers().stream()
                .anyMatch(member -> member.toString().equals(userDnString));

        if (alreadyMember){
            logger.warn("User {} already in group {}", employeeNumber, groupName);
            return;
        }

        ModificationItem[] mods = new ModificationItem[1];
        mods[0] = new ModificationItem(DirContext.ADD_ATTRIBUTE,
                new BasicAttribute("member", userDnString));

        ldapTemplate.modifyAttributes(group.getDn(), mods);

        logger.info("User {} added to group {} successfully", employeeNumber, groupName);
    }

    public void removeUserFromGroup(String employeeNumber, String groupName){
        logger.info("Removing user {} from group: {}", employeeNumber, groupName);

        LdapGroup group = ldapGroupDao.findByRoleName(groupName)
                .orElseThrow(() -> new ResourceNotFoundException("Group not found: " + groupName));

        String userDnString = "employeeNumber=" + employeeNumber + ",ou=people,dc=yildizskylab,dc=com";
        Name userDn = LdapNameBuilder.newInstance(userDnString).build();

        boolean isMember = group.getMembers().stream()
                .anyMatch(member -> member.toString().equals(userDnString));

        if (!isMember) {
            logger.warn("User {} not in group {}", employeeNumber, groupName);
            return;
        }

        ModificationItem[] mods = new ModificationItem[1];
        mods[0] = new ModificationItem(DirContext.REMOVE_ATTRIBUTE,
                new BasicAttribute("member", userDnString));

        ldapTemplate.modifyAttributes(group.getDn(), mods);

        logger.info("User {} removed from group {} successfully", employeeNumber, groupName);
    }

    public Set<String> getUserGroups(String employeeNumber){
        logger.info("Getting groups for user (custom LdapTemplate): {}", employeeNumber);

        findByEmployeeNumber(employeeNumber);

        String userDnString = "employeeNumber=" + employeeNumber + ",ou=people,dc=yildizskylab,dc=com";

        var query = LdapQueryBuilder.query()
                .base("ou=groups")
                .searchScope(SearchScope.ONELEVEL)
                .attributes("cn")
                .filter(new EqualsFilter("member", userDnString));


        List<String> roles = ldapTemplate.search(query,
                (Attributes attrs) -> {
                    if (attrs.get("cn") != null) {
                        return (String) attrs.get("cn").get();
                    }
                    return null;
                }
        );

        return roles.stream().filter(Objects::nonNull).collect(Collectors.toSet());
    }

    public LdapGroup createGroup(String groupName){
        logger.info("Creating group: {}", groupName);

        if (ldapGroupDao.findByRoleName(groupName).isPresent()){
            throw new ValidationException(LdapMessages.GROUP_ALREADY_EXISTS);
        }

        LdapGroup group = new LdapGroup();
        group.setRoleName(groupName);

        Name dummyMember = LdapNameBuilder
                .newInstance("cn=" + groupName + ",ou=groups,dc=yildizskylab,dc=com")
                .build();

        Set<Name> members = new LinkedHashSet<>();
        members.add(dummyMember);
        group.setMembers(members);

        LdapGroup savedGroup = ldapGroupDao.save(group);

        logger.info("Group created: {}", groupName);
        return savedGroup;
    }

    public void deleteGroup(String groupName){
        logger.info("Deleting group: {}", groupName);

        LdapGroup group = ldapGroupDao.findByRoleName(groupName)
                .orElseThrow(() -> new ResourceNotFoundException(LdapMessages.GROUP_NOT_FOUND));

        ldapGroupDao.delete(group);
        logger.info("Group {} deleted successfully", groupName);
    }

    public void cleanupDanglingReferences(){
        logger.info("Starting cleanup of dangling references in LDAP groups");

        List<LdapGroup> allGroups = ldapGroupDao.findAll();
        int cleanedCount = 0;

        for (LdapGroup group : allGroups){
            Set<Name> validMembers = new LinkedHashSet<>();

            for (Name memberDn : group.getMembers()){
                String employeeNumber = extractEmployeeNumber(memberDn);

                if (employeeNumber != null && userExists(employeeNumber)){
                    validMembers.add(memberDn);
                }else {
                    logger.warn("Removing dangling reference {} from group {}", memberDn, group.getRoleName());
                    cleanedCount++;
                }
            }

            if (group.getMembers().size() != validMembers.size()){
                group.setMembers(validMembers);
                ldapGroupDao.save(group);
            }
        }

        logger.info("Cleanup completed. Total dangling references removed: {}", cleanedCount);
    }

    private String extractEmployeeNumber(Name dn) {
        try {
            String dnString = dn.toString();
            if (dnString.startsWith("employeeNumber=")) {
                int commaIndex = dnString.indexOf(',');
                if (commaIndex > 0) {
                    return dnString.substring(15, commaIndex);
                }
            }
        } catch (Exception e) {
            logger.error("Failed to extract employeeNumber from DN: {}", dn, e);
        }
        return null;
    }

    public List<LdapUser> findAllByEmployeeNumbers(List<String> ldapSkyNumbers) {
        if (ldapSkyNumbers == null || ldapSkyNumbers.isEmpty()){
            return List.of();
        }

        OrFilter orFilter = new OrFilter();
        ldapSkyNumbers.forEach(num -> orFilter.or(new EqualsFilter("employeeNumber", num)));

        List<LdapUser> users = ldapUserDao.findAll(LdapQueryBuilder.query().filter(orFilter));
        users.forEach(this::fixEmployeeNumber);

        return users;
    }

    public LdapUser updateUserAttributes(LdapUser user) {
        logger.info("Updating attributes for user in LDAP: {}", user.getEmployeeNumber());

        Name dn = LdapNameBuilder.newInstance(user.getDn().toString()).build();

        ModificationItem[] mods = new ModificationItem[] {
                new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                        new BasicAttribute("givenName", user.getFirstName())),
                new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                        new BasicAttribute("sn", user.getLastName())),
                new ModificationItem(DirContext.REPLACE_ATTRIBUTE,
                        new BasicAttribute("cn", user.getFullName()))
        };

        ldapTemplate.modifyAttributes(dn, mods);

        logger.info("User attributes updated in LDAP: {}", user.getEmployeeNumber());
        return user;
    }

    @Cacheable(value = "ldapGroupMembers", key = "#groupName")
    public List<LdapUser> getUsersByGroupName(String groupName) {
        logger.info("Fetching users for group: {} from LDAP (Cached)", groupName);

        var query = LdapQueryBuilder.query()
                .base("ou=groups")
                .where("objectClass").is("groupOfNames")
                .and("cn").is(groupName);

        try {
            List<String> employeeNumbers = ldapTemplate.search(query, (AttributesMapper<List<String>>) attributes -> {
                List<String> ids = new ArrayList<>();
                if (attributes.get("member") == null) return ids;

                NamingEnumeration<?> members = attributes.get("member").getAll();
                while (members.hasMore()) {
                    String memberDn = (String) members.next();

                    if (memberDn.contains("Directory Manager")) continue;

                    String empNo = extractEmployeeNumberFromString(memberDn);
                    if (empNo != null) {
                        ids.add(empNo);
                    }
                }
                return ids;
            }).stream().flatMap(List::stream).collect(Collectors.toList());

            if (employeeNumbers.isEmpty()) {
                return Collections.emptyList();
            }

            return findAllByEmployeeNumbers(employeeNumbers);

        } catch (Exception e) {
            logger.error("Error fetching group members for {}: {}", groupName, e.getMessage());
            return Collections.emptyList();
        }
    }

    private String extractEmployeeNumberFromString(String dnString) {
        try {
            if (dnString.startsWith("employeeNumber=")) {
                int commaIndex = dnString.indexOf(',');
                if (commaIndex > 0) {
                    return dnString.substring(15, commaIndex);
                }
            }
        } catch (Exception e) {
        }
        return null;
    }


}