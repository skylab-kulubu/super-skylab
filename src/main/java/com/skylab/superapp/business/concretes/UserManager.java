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
import com.skylab.superapp.core.utilities.sync.UserSyncService;
import com.skylab.superapp.dataAccess.UserDao;
import com.skylab.superapp.entities.DTOs.User.*;
import com.skylab.superapp.entities.Image;
import com.skylab.superapp.entities.User;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserManager implements UserService {

    private final UserDao userDao;
    private final UserMapper userMapper;
    private final Logger logger = LoggerFactory.getLogger(UserManager.class);
    private final ImageService imageService;


    private final UserIdentityGenerator userIdentityGenerator;
    private final LdapService ldapService;
    private final KeycloakAdminService keycloakAdminService;
    private final MicrosoftGraphService microsoftGraphService;
    private final UserSyncService userSyncService;


    public UserManager(UserDao userDao, UserMapper userMapper, @Lazy ImageService imageService, UserIdentityGenerator userIdentityGenerator, LdapService ldapService, KeycloakAdminService keycloakAdminService, MicrosoftGraphService microsoftGraphService, UserSyncService userSyncService) {
        this.userDao = userDao;
        this.userMapper = userMapper;
        this.imageService = imageService;
        this.userIdentityGenerator = userIdentityGenerator;
        this.ldapService = ldapService;
        this.keycloakAdminService = keycloakAdminService;
        this.microsoftGraphService = microsoftGraphService;
        this.userSyncService = userSyncService;
    }


    @Override
    @Transactional
    public User getAuthenticatedUserEntity(){
        logger.info("Retrieving authenticated user entity from database");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()){
            logger.warn("No authenticated user found in security context");
            throw new AccessDeniedException(UserMessages.USER_NOT_AUTHENTICATED);
        }

        if (!(authentication.getPrincipal() instanceof Jwt jwt)){
            logger.error("Authentication principal is not of type Jwt, actual type: {}", authentication.getPrincipal().getClass().getName());
            throw new RuntimeException(UserMessages.PRINCIPAL_IS_NOT_JWT);
        }

        UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));
        logger.info("Authenticated user detected with userId: {}, checking if user exists in database", userId);

        return userDao.findById(userId).orElseGet(() -> {
            logger.info("New authenticated user detected, creating user in database for userId: {}", userId);

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

            newUser = userDao.save(newUser);
            logger.info("Created new user in database with id: {}", newUser.getId());

            userSyncService.syncExternalApisAsync(userId, jwt.getTokenValue(), generatedSkyNumber);

            return newUser;
        });
    }

    @Override
    public List<UserDto> getAllUsers(String email, List<String> roles){
        logger.info("Getting all users from database");
        List<User> users = userDao.findAll();

      return users.stream()
              .map(userMapper::toDto)
              .collect(Collectors.toList());


    }

    @Override
    public UserDto getUserById(UUID id){
        logger.info("Getting user by id: {}", id);

        User user = userDao.findById(id).orElseThrow(() -> {
            logger.error("User with id: {} not found in database", id);
            return new ResourceNotFoundException(UserMessages.USER_NOT_FOUND);
        });

        logger.info("User with id: {} found in database, mapping to UserDto", id);

        return userMapper.toDto(user);
    }


    @Override
    @Transactional
    public UserDto updateAuthenticatedUser(UpdateUserRequest updateUserRequest){
        logger.info("Updating authenticated user with data: {}", updateUserRequest);

        User currentUser = getAuthenticatedUserEntity();

        if (updateUserRequest.getLinkedin() != null) currentUser.setLinkedin(updateUserRequest.getLinkedin());
        if (updateUserRequest.getUniversity() != null) currentUser.setUniversity(updateUserRequest.getUniversity());
        if (updateUserRequest.getFaculty() != null) currentUser.setFaculty(updateUserRequest.getFaculty());
        if (updateUserRequest.getDepartment() != null) currentUser.setDepartment(updateUserRequest.getDepartment());


        currentUser = userDao.save(currentUser);
        logger.info("Authenticated user profile updated successfully");

        return userMapper.toDto(currentUser);
    }


    @Transactional
    @Override
    public void promoteUserToLdap(UUID userId, String targetRole, String initialPassword){
        logger.info("Promoting user with id: {} to role: {}", userId, targetRole);

        User user = userDao.findById(userId).orElseThrow(() -> {
            logger.error("User with id: {} not found in database", userId);
            return new ResourceNotFoundException(UserMessages.USER_NOT_FOUND);
        });

        if (user.isLdapUser()){
            logger.error("User with id: {} is already an LDAP user", userId);
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

        logger.info("User with id: {} promoted to LDAP user, setting role to: {}", userId, targetRole);


    }

    @Override
    public UserDto getAuthenticatedUser() {
        logger.info("Getting authenticated user");
        User currentUser = getAuthenticatedUserEntity();
        logger.info("Authenticated user found with id: {}, mapping to UserDto", currentUser.getId());
        return userMapper.toDto(currentUser);
    }

    @Override
    public List<UserDto> getAllUsersByIds(List<UUID> ids) {
        logger.info("Getting users by ids: {}", ids);
        List<User> users = userDao.findAllById(ids);
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Image uploadProfilePictureOfAuthenticatedUser(MultipartFile image) {
        logger.info("Uploading profile picture of authenticated user");

            User currentUser = getAuthenticatedUserEntity();
            Image savedImage = imageService.uploadImage(image);

            currentUser.setProfilePicture(savedImage);

            userDao.save(currentUser);
            logger.info("Profile picture uploaded successfully for user with id: {}", currentUser.getId());

            return savedImage;

    }

    @Override
    @Transactional
    public UserDto updateUser(UUID id, UpdateUserRequest updateUserRequest) {
        logger.info("Updating user with id: {} with data: {}", id, updateUserRequest);

            User user = userDao.findById(id).orElseThrow(() -> {
                logger.error("User with id: {} not found in database", id);
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
            logger.info("Name changed, syncing with Keycloak for user: {}", id);
            keycloakAdminService.updateUserFullName(id, user.getFirstName(), user.getLastName());
        }


        user = userDao.save(user);
        logger.info("User with id: {} updated successfully", id);

        return userMapper.toDto(user);

    }

    @Override
    public void deleteUser(UUID id) {
        logger.info("Deleting user with id: {}", id);

        User user = userDao.findById(id).orElseThrow(() -> {
            logger.error("User with id: {} not found in database", id);
            return new ResourceNotFoundException(UserMessages.USER_NOT_FOUND);
        });

        if (user.isLdapUser()){
            logger.info("User with id: {} is an LDAP user, deleting from LDAP", id);
            ldapService.deleteUser(user.getUsername());;
            logger.info("User with id: {} deleted from LDAP successfully", id);
        }

        keycloakAdminService.deleteUser(id);

        userDao.deleteById(id);

        logger.info("User with id: {} deleted successfully", id);

    }

    @Override
    public List<UserDto> getUsersByRoleNames(Set<String> roles) {
        logger.info("Getting users by role names: {}", roles);

        if (roles == null || roles.isEmpty()){
            logger.info("No roles provided, returning empty list");
            return Collections.emptyList();
        }

        Set<UUID> authorizedUserIds = new HashSet<>();

        for (String role : roles){
            authorizedUserIds.addAll(keycloakAdminService.getUserIdsByRoleName(role));
        }

        if (authorizedUserIds.isEmpty()){
            logger.info("No users found with any of the provided roles: {}", roles);
            return Collections.emptyList();
        }


        List<User> users = userDao.findAllById(authorizedUserIds);

        logger.info("Found {} users with provided roles: {}, mapping to UserDto", users.size(), roles);
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserEntityById(UUID id) {
        logger.info("Getting user entity by id: {}", id);

        return userDao.findById(id).orElseThrow(() -> {
            logger.error("User with id: {} not found in database", id);
            return new ResourceNotFoundException(UserMessages.USER_NOT_FOUND);
        });
    }

    @Override
    @Transactional
    public void syncUserFromKeycloak(String userId, UserRepresentation keycloakUser) {
        UUID uuid = UUID.fromString(userId);

        User localUser = userDao.findById(uuid).orElseGet(() -> {
            logger.info("New user detected via sync: {}. Creating local shadow copy.", userId);
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
            logger.info("Assigned NEW SkyNumber {} to user {}", newSkyNumber, userId);
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
        logger.info("User {} successfully synced and saved via UserManager", userId);
    }

    private String getAttributeSafe(Map<String, List<String>> attributes, String key) {
        if (attributes != null && attributes.containsKey(key)) {
            List<String> values = attributes.get(key);
            if (values != null && !values.isEmpty()) return values.get(0);
        }
        return "";
    }


}
