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
    private final UserSecurityUtils userSecurityUtils;

    @Override
    @Transactional
    public User getAuthenticatedUserEntity() {
        log.debug("Retrieving authenticated user entity.");
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            log.warn("Authentication failed: No authenticated user found in context.");
            throw new AccessDeniedException(UserMessages.USER_NOT_AUTHENTICATED);
        }

        if (!(authentication.getPrincipal() instanceof Jwt jwt)) {
            log.error("Authentication failed: Principal is not JWT. ActualType: {}", authentication.getPrincipal().getClass().getName());
            throw new RuntimeException(UserMessages.PRINCIPAL_IS_NOT_JWT);
        }

        UUID userId = UUID.fromString(jwt.getClaimAsString("sub"));
        log.debug("Authenticated user detected. UserId: {}", userId);

        User currentUser = userDao.findById(userId).orElseGet(() -> {
            log.info("Creating local shadow copy for new authenticated user. UserId: {}", userId);

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
                log.error("Keycloak sync failed: Could not update skyNumber. UserId: {}, Error: {}", userId, e.getMessage(), e);
            }

            return userDao.save(newUser);
        });

        if (currentUser.getDepartment() == null || currentUser.getDepartment().trim().isEmpty()) {
            log.debug("Fetching missing department from MS Graph. UserId: {}", userId);
            try {
                String msToken = keycloakAdminService.getObsBrokerToken(jwt.getTokenValue());
                if (msToken != null) {
                    String fetchedDepartment = microsoftGraphService.fetchUserDepartment(msToken);

                    if (fetchedDepartment != null && !fetchedDepartment.isBlank()) {
                        currentUser.setDepartment(fetchedDepartment);
                        userDao.save(currentUser);
                        keycloakAdminService.updateUserAttribute(userId, "department", fetchedDepartment);

                        log.info("Department fetched and updated from MS Graph successfully. UserId: {}, Department: {}", userId, fetchedDepartment);
                    } else {
                        log.warn("MS Graph fetch warning: Empty department returned. UserId: {}", userId);
                    }
                }
            } catch (Exception e) {
                log.error("MS Graph sync failed: Could not fetch department. UserId: {}, Error: {}", userId, e.getMessage(), e);
            }
        }

        return currentUser;
    }

    @Override
    public List<UserDto> getAllUsers(String email, List<String> roles) {
        log.debug("Retrieving users. EmailFilter: {}, RolesFilter: {}", email, roles);

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
                log.debug("User retrieval: No users found in Keycloak for roles. Roles: {}", roles);
                return Collections.emptyList();
            }

            if (hasEmailFilter) {
                users = userDao.findByEmailContainingIgnoreCaseAndIdIn(email, authorizedUserIds);
            } else {
                users = userDao.findAllById(authorizedUserIds);
            }
        } else {
            if (hasEmailFilter) {
                users = userDao.findByEmailContainingIgnoreCase(email);
            } else {
                users = userDao.findAll();
            }
        }

        log.info("Users retrieved successfully. TotalCount: {}", users.size());

        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserById(UUID id) {
        log.debug("Retrieving user. UserId: {}", id);

        User user = userDao.findById(id).orElseThrow(() -> {
            log.error("User retrieval failed: Resource not found. UserId: {}", id);
            return new ResourceNotFoundException(UserMessages.USER_NOT_FOUND);
        });

        return userMapper.toDto(user);
    }

    @Override
    @Transactional
    public UserDto updateAuthenticatedUser(UpdateUserRequest updateUserRequest) {
        log.info("Initiating authenticated user update.");

        User currentUser = getAuthenticatedUserEntity();

        if (updateUserRequest.getLinkedin() != null) currentUser.setLinkedin(updateUserRequest.getLinkedin());
        if (updateUserRequest.getUniversity() != null) currentUser.setUniversity(updateUserRequest.getUniversity());
        if (updateUserRequest.getFaculty() != null) currentUser.setFaculty(updateUserRequest.getFaculty());
        if (updateUserRequest.getDepartment() != null) currentUser.setDepartment(updateUserRequest.getDepartment());

        currentUser = userDao.save(currentUser);
        log.info("Authenticated user profile updated successfully. UserId: {}", currentUser.getId());

        return userMapper.toDto(currentUser);
    }

    @Transactional
    @Override
    public void promoteUserToLdap(UUID userId, String targetRole, String initialPassword) {
        log.info("Initiating LDAP user promotion. UserId: {}, TargetRole: {}", userId, targetRole);

        User user = userDao.findById(userId).orElseThrow(() -> {
            log.error("LDAP promotion failed: User not found. UserId: {}", userId);
            return new ResourceNotFoundException(UserMessages.USER_NOT_FOUND);
        });

        if (user.isLdapUser()) {
            log.warn("LDAP promotion failed: User is already an LDAP user. UserId: {}", userId);
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

        log.info("LDAP promotion completed successfully. UserId: {}, TargetRole: {}", userId, targetRole);
    }

    @Override
    public UserDto getAuthenticatedUser() {
        log.debug("Retrieving authenticated user DTO.");
        User currentUser = getAuthenticatedUserEntity();
        return userMapper.toDto(currentUser);
    }

    @Override
    public List<UserDto> getAllUsersByIds(List<UUID> ids) {
        log.debug("Retrieving users by ID batch. RequestedCount: {}", ids.size());
        List<User> users = userDao.findAllById(ids);
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public Image uploadProfilePictureOfAuthenticatedUser(MultipartFile image) {
        log.info("Initiating profile picture upload for authenticated user.");

        User currentUser = getAuthenticatedUserEntity();
        Image savedImage = imageService.uploadImage(image);

        currentUser.setProfilePicture(savedImage);
        userDao.save(currentUser);

        log.info("Profile picture uploaded and assigned successfully. UserId: {}", currentUser.getId());

        return savedImage;
    }

    @Override
    @Transactional
    public UserDto updateUser(UUID id, UpdateUserRequest updateUserRequest) {
        log.info("Initiating user profile update. UserId: {}", id);

        User user = userDao.findById(id).orElseThrow(() -> {
            log.error("User update failed: Resource not found. UserId: {}", id);
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
            log.info("User name updated locally, initiating Keycloak sync. UserId: {}", id);
            keycloakAdminService.updateUserFullName(id, user.getFirstName(), user.getLastName());
        }

        user = userDao.save(user);
        log.info("User updated successfully. UserId: {}", id);

        return userMapper.toDto(user);
    }

    @Override
    public void deleteUser(UUID id) {
        log.info("Initiating user deletion. UserId: {}", id);

        User user = userDao.findById(id).orElseThrow(() -> {
            log.error("User deletion failed: Resource not found. UserId: {}", id);
            return new ResourceNotFoundException(UserMessages.USER_NOT_FOUND);
        });

        if (user.isLdapUser()) {
            log.info("User is LDAP user, initiating LDAP deletion. UserId: {}, Username: {}", id, user.getUsername());
            ldapService.deleteUser(user.getUsername());
            log.info("User deleted from LDAP successfully. UserId: {}", id);
        }

        keycloakAdminService.deleteUser(id);
        userDao.deleteById(id);

        log.info("User deleted successfully. UserId: {}", id);
    }

    @Override
    public List<UserDto> getUsersByRoleNames(Set<String> roles) {
        log.debug("Retrieving users by roles. Roles: {}", roles);

        if (roles == null || roles.isEmpty()) {
            log.debug("User retrieval by roles aborted: No roles provided.");
            return Collections.emptyList();
        }

        Set<UUID> authorizedUserIds = new HashSet<>();
        for (String role : roles) {
            authorizedUserIds.addAll(keycloakAdminService.getUserIdsByRoleName(role));
        }

        if (authorizedUserIds.isEmpty()) {
            log.debug("User retrieval: No matched users for provided roles. Roles: {}", roles);
            return Collections.emptyList();
        }

        List<User> users = userDao.findAllById(authorizedUserIds);

        log.info("Users by roles retrieved successfully. TotalCount: {}", users.size());

        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public User getUserEntityById(UUID id) {
        log.debug("Retrieving user entity. UserId: {}", id);

        return userDao.findById(id).orElseThrow(() -> {
            log.error("User entity retrieval failed: Resource not found. UserId: {}", id);
            return new ResourceNotFoundException(UserMessages.USER_NOT_FOUND);
        });
    }

    @Override
    @Transactional
    public void syncUserFromKeycloak(String userId, UserRepresentation keycloakUser) {
        UUID uuid = UUID.fromString(userId);

        User localUser = userDao.findById(uuid).orElseGet(() -> {
            log.info("Keycloak sync: New user detected. Creating local shadow copy. UserId: {}", userId);
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
            log.info("Keycloak sync: Assigned new SkyNumber. UserId: {}, SkyNumber: {}", userId, newSkyNumber);
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
        log.info("Keycloak sync completed successfully. UserId: {}", userId);
    }

    private String getAttributeSafe(Map<String, List<String>> attributes, String key) {
        if (attributes != null && attributes.containsKey(key)) {
            List<String> values = attributes.get(key);
            if (values != null && !values.isEmpty()) return values.get(0);
        }
        return "";
    }
}