package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.exceptions.*;
import com.skylab.superapp.core.identity.UserIdentityGenerator;
import com.skylab.superapp.core.identity.keycloak.KeycloakAdminService;
import com.skylab.superapp.core.identity.ldap.LdapService;
import com.skylab.superapp.core.mappers.UserMapper;
import com.skylab.superapp.core.utilities.microsoftGraph.MicrosoftGraphService;
import com.skylab.superapp.core.utilities.security.UserSecurityUtils;
import com.skylab.superapp.dataAccess.UserDao;
import com.skylab.superapp.entities.DTOs.User.*;
import com.skylab.superapp.entities.Image;
import com.skylab.superapp.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserManager implements UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;
    private final ImageService imageService;

    private final UserIdentityGenerator userIdentityGenerator;
    private final LdapService ldapService;
    private final KeycloakAdminService keycloakAdminService;
    private final MicrosoftGraphService microsoftGraphService;

    private final UserSecurityUtils  userSecurityUtils;


    @Override
    @Transactional
    public User getAuthenticatedUserEntity() {
        log.info("Retrieving authenticated user entity from database");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("No authenticated user found in security context");
            throw new AccessDeniedException(UserMessages.USER_NOT_AUTHENTICATED);
        }

        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            log.error("Authentication principal is not of type Jwt, actual type: {}", authentication.getPrincipal().getClass().getName());
            throw new RuntimeException(UserMessages.PRINCIPAL_IS_NOT_JWT);
        }

        UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));
        log.info("Authenticated user detected with userId: {}, checking if user exists in database", userId);

        User currentUser = userDao.findById(userId).orElseGet(() -> {
            log.info("New authenticated user detected (not synced by RabbitMQ yet), creating user in database for userId: {}", userId);

            String generatedSkyNumber = userIdentityGenerator.generateNextSkyNumber();

            User newUser = User.builder()
                    .id(userId)
                    .firstName(jwt.getClaimAsString("given_name"))
                    .lastName(jwt.getClaimAsString("family_name"))
                    .email(jwt.getClaimAsString("email"))
                    .username(jwt.getClaimAsString("preferred_username"))
                    .university(jwt.getClaimAsString("university"))
                    .skyNumber(generatedSkyNumber)
                    .isLdapUser(false)
                    .build();

            try {
                keycloakAdminService.updateUserAttribute(userId, "skyNumber", generatedSkyNumber);
            } catch (Exception e) {
                log.error("Error syncing skyNumber to Keycloak synchronously: {}", e.getMessage());
            }

            return userDao.save(newUser);
        });

        if (currentUser.getDepartment() == null || currentUser.getDepartment().trim().isEmpty()) {
            log.info("Department is missing for user {}, attempting to fetch from MS Graph", userId);
            try {
                String msToken = keycloakAdminService.getObsBrokerToken(jwt.getTokenValue());
                if (msToken != null) {
                    String fetchedDepartment = microsoftGraphService.fetchUserDepartment(msToken);

                    if (fetchedDepartment != null && !fetchedDepartment.isBlank()) {
                        log.info("Fetched department from MS Graph: {}", fetchedDepartment);

                        currentUser.setDepartment(fetchedDepartment);
                        userDao.save(currentUser);

                        keycloakAdminService.updateUserAttribute(userId, "department", fetchedDepartment);
                    } else {
                        log.warn("MS Graph returned empty department for user {}", userId);
                    }
                }
            } catch (Exception e) {
                log.error("Error fetching/syncing department from MS Graph: {}", e.getMessage());
            }
        }

        return currentUser;
    }

    @Override
    public List<UserDto> getAllUsers(String email, List<String> roles) {
        log.info("Getting users. Email filter: {}, Roles filter: {}", email, roles);

        userSecurityUtils.checkRead();

        List<User> users;
        boolean hasEmailFilter = email != null && !email.isBlank();
        boolean hasRoleFilter = roles != null && !roles.isEmpty();

        if (hasRoleFilter) {
            Set<UUID> authorizedUserIds = new HashSet<>();
            for (String role : roles) {
                authorizedUserIds.addAll(keycloakAdminService.getUserIdsByRoleName(role));
            }

            if (authorizedUserIds.isEmpty()) {
                log.info("No users found in Keycloak matching the given roles: {}", roles);
                return Collections.emptyList();
            }

            if (hasEmailFilter) {
                users = userDao.findByEmailContainingIgnoreCaseAndIdIn(email, authorizedUserIds);
            } else {
                users = userDao.findAllById(authorizedUserIds);
            }
        }
        else {
            if (hasEmailFilter) {
                users = userDao.findByEmailContainingIgnoreCase(email);
            } else {
                users = userDao.findAll();
            }
        }

        log.info("Found {} users matching the criteria", users.size());

        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(UUID id){
        log.info("Getting user by id: {}", id);

        User user = userDao.findById(id).orElseThrow(() -> {
            log.error("User with id: {} not found in database", id);
            return new ResourceNotFoundException(UserMessages.USER_NOT_FOUND);
        });

        log.info("User with id: {} found in database, mapping to UserDto", id);

        return userMapper.toDto(user);
    }


    @Override
    @Transactional
    public UserDto updateAuthenticatedUser(UpdateUserRequest updateUserRequest){
        log.info("Updating authenticated user with data: {}", updateUserRequest);

        User currentUser = getAuthenticatedUserEntity();

        if (updateUserRequest.getLinkedin() != null) currentUser.setLinkedin(updateUserRequest.getLinkedin());
        if (updateUserRequest.getUniversity() != null) currentUser.setUniversity(updateUserRequest.getUniversity());
        if (updateUserRequest.getFaculty() != null) currentUser.setFaculty(updateUserRequest.getFaculty());
        if (updateUserRequest.getDepartment() != null) currentUser.setDepartment(updateUserRequest.getDepartment());


        currentUser = userDao.save(currentUser);
        log.info("Authenticated user profile updated successfully");

        return userMapper.toDto(currentUser);
    }


    @Transactional
    @Override
    public void promoteUserToLdap(UUID userId, String targetRole, String initialPassword){
        log.info("Promoting user with id: {} to role: {}", userId, targetRole);

        User user = userDao.findById(userId).orElseThrow(() -> {
            log.error("User with id: {} not found in database", userId);
            return new ResourceNotFoundException(UserMessages.USER_NOT_FOUND);
        });

        if (user.isLdapUser()){
            log.error("User with id: {} is already an LDAP user", userId);
            throw new RuntimeException(UserMessages.USER_ALREADY_LDAP_USER);
        }

        String ldapUsername = userIdentityGenerator.generateLdapUsername(
                user.getFirstName(),
                user.getLastName(),
                ldapService
        );

        ldapService.promoteAndCreateUser(user, ldapUsername, initialPassword);

        ldapService.addUserToGroup(ldapUsername, targetRole);

        keycloakAdminService.linkUserToLdap(userId, ldapUsername);




        user.setLdapUser(true);
        user.setUsername(ldapUsername);
        userDao.save(user);

        log.info("User with id: {} promoted to LDAP user, setting role to: {}", userId, targetRole);


    }

    @Override
    public UserDto getAuthenticatedUser() {
        log.info("Getting authenticated user");
        User currentUser = getAuthenticatedUserEntity();
        log.info("Authenticated user found with id: {}, mapping to UserDto", currentUser.getId());
        return userMapper.toDto(currentUser);
    }

    @Override
    public List<UserDto> getAllUsersByIds(List<UUID> ids) {
        log.info("Getting users by ids: {}", ids);
        List<User> users = userDao.findAllById(ids);
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Image uploadProfilePictureOfAuthenticatedUser(MultipartFile image) {
        log.info("Uploading profile picture of authenticated user");

            User currentUser = getAuthenticatedUserEntity();
            Image savedImage = imageService.uploadImage(image);

            currentUser.setProfilePicture(savedImage);

            userDao.save(currentUser);
            log.info("Profile picture uploaded successfully for user with id: {}", currentUser.getId());

            return savedImage;

    }

    @Override
    @Transactional
    public UserDto updateUser(UUID id, UpdateUserRequest updateUserRequest) {
        log.info("Updating user with id: {} with data: {}", id, updateUserRequest);

            User user = userDao.findById(id).orElseThrow(() -> {
                log.error("User with id: {} not found in database", id);
                return new ResourceNotFoundException(UserMessages.USER_NOT_FOUND);
            });

        boolean nameChanged = false;

        if (updateUserRequest.getFirstName() != null && !updateUserRequest.getFirstName().isBlank() && !updateUserRequest.getFirstName().equals(user.getFirstName())) {
            user.setFirstName(updateUserRequest.getFirstName());
            nameChanged = true;
        }

        if (updateUserRequest.getLinkedin() != null) user.setLinkedin(updateUserRequest.getLinkedin());
        if (updateUserRequest.getUniversity() != null) user.setUniversity(updateUserRequest.getUniversity());
        if (updateUserRequest.getFaculty() != null) user.setFaculty(updateUserRequest.getFaculty());
        if (updateUserRequest.getDepartment() != null) user.setDepartment(updateUserRequest.getDepartment());

        if (nameChanged) {
            log.info("Name changed, syncing with Keycloak for user: {}", id);
            keycloakAdminService.updateUserFullName(id, user.getFirstName(), user.getLastName());
        }


        user = userDao.save(user);
        log.info("User with id: {} updated successfully", id);

        return userMapper.toDto(user);

    }

    @Override
    public void deleteUser(UUID id) {
        log.info("Deleting user with id: {}", id);

        User user = userDao.findById(id).orElseThrow(() -> {
            log.error("User with id: {} not found in database", id);
            return new ResourceNotFoundException(UserMessages.USER_NOT_FOUND);
        });

        if (user.isLdapUser()){
            log.info("User with id: {} is an LDAP user, deleting from LDAP", id);
            ldapService.deleteUser(user.getUsername());;
            log.info("User with id: {} deleted from LDAP successfully", id);
        }

        keycloakAdminService.deleteUser(id);

        userDao.deleteById(id);

        log.info("User with id: {} deleted successfully", id);

    }

    @Override
    public List<UserDto> getUsersByRoleNames(Set<String> roles) {
        log.info("Getting users by role names: {}", roles);

        if (roles == null || roles.isEmpty()){
            log.info("No roles provided, returning empty list");
            return Collections.emptyList();
        }

        Set<UUID> authorizedUserIds = new HashSet<>();

        for (String role : roles){
            authorizedUserIds.addAll(keycloakAdminService.getUserIdsByRoleName(role));
        }

        if (authorizedUserIds.isEmpty()){
            log.info("No users found with any of the provided roles: {}", roles);
            return Collections.emptyList();
        }


        List<User> users = userDao.findAllById(authorizedUserIds);

        log.info("Found {} users with provided roles: {}, mapping to UserDto", users.size(), roles);
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserEntityById(UUID id) {
        log.info("Getting user entity by id: {}", id);

        return userDao.findById(id).orElseThrow(() -> {
            log.error("User with id: {} not found in database", id);
            return new ResourceNotFoundException(UserMessages.USER_NOT_FOUND);
        });
    }

    @Override
    @Transactional
    public void syncUserFromKeycloak(String userId, UserRepresentation keycloakUser) {
        UUID uuid = UUID.fromString(userId);

        User localUser = userDao.findById(uuid).orElseGet(() -> {
            log.info("New user detected via sync: {}. Creating local shadow copy.", userId);
            return User.builder().id(uuid).build();
        });

        Map<String, List<String>> attributes = keycloakUser.getAttributes();
        String skyNumberInKeycloak = getAttributeSafe(attributes, "skyNumber");
        if (skyNumberInKeycloak.isEmpty()) {
            skyNumberInKeycloak = getAttributeSafe(attributes, "sky_number");
        }

        if (!skyNumberInKeycloak.isEmpty()) {
            localUser.setSkyNumber(skyNumberInKeycloak);
        } else if (localUser.getSkyNumber() == null) {
            String newSkyNumber = userIdentityGenerator.generateNextSkyNumber();
            localUser.setSkyNumber(newSkyNumber);

            keycloakAdminService.updateUserAttribute(uuid, "skyNumber", newSkyNumber);
            log.info("Assigned NEW SkyNumber {} to user {}", newSkyNumber, userId);
        }

        localUser.setFirstName(keycloakUser.getFirstName());
        localUser.setLastName(keycloakUser.getLastName());
        localUser.setEmail(keycloakUser.getEmail());
        localUser.setUsername(keycloakUser.getUsername());

        if (attributes != null) {
            localUser.setDepartment(getAttributeSafe(attributes, "department"));
            localUser.setUniversity(getAttributeSafe(attributes, "university"));
            localUser.setFaculty(getAttributeSafe(attributes, "faculty"));
        }

        userDao.save(localUser);
        log.info("User {} successfully synced and saved via UserManager", userId);
    }

    private String getAttributeSafe(Map<String, List<String>> attributes, String key) {
        if (attributes != null && attributes.containsKey(key)) {
            List<String> values = attributes.get(key);
            if (values != null && !values.isEmpty()) return values.get(0);
        }
        return "";
    }


}
