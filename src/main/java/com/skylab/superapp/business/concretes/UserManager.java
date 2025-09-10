package com.skylab.superapp.business.concretes;

import com.nimbusds.jwt.SignedJWT;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.exceptions.*;
import com.skylab.superapp.core.mappers.UserMapper;
import com.skylab.superapp.core.utilities.keycloak.KeycloakAdminClientService;
import com.skylab.superapp.core.utilities.keycloak.KeycloakRole;
import com.skylab.superapp.core.utilities.keycloak.KeycloakService;
import com.skylab.superapp.core.utilities.keycloak.dtos.UserKeycloakRequest;
import com.skylab.superapp.core.utilities.keycloak.dtos.UserUpdateKeycloakRequest;
import com.skylab.superapp.core.utilities.mail.EmailService;
import com.skylab.superapp.dataAccess.UserDao;
import com.skylab.superapp.entities.DTOs.User.*;
import com.skylab.superapp.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserManager implements UserService {

    private final UserDao userDao;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final UserMapper userMapper;
    private final Logger logger = LoggerFactory.getLogger(UserManager.class);
    private final KeycloakAdminClientService keycloakAdminClientService;


    public UserManager(UserDao userDao, @Lazy EmailService emailService, UserMapper userMapper, KeycloakAdminClientService keycloakAdminClientService) {
        this.userDao = userDao;
        this.emailService = emailService;
        this.userMapper = userMapper;
        this.keycloakAdminClientService = keycloakAdminClientService;
    }

    @Override
    @Transactional
    public UserDto addUser(CreateUserRequest createUserRequest) {
        logger.info("Saving user with username: {}", createUserRequest.getUsername());

        if(createUserRequest.getUsername() == null || createUserRequest.getPassword() == null) {
            throw new UsernameorOrPasswordCannotBeNullException();
        }

        if(createUserRequest.getEmail() == null) {
            throw new EmailCannotBeNullException();
        }

        if(userDao.existsByUsername(createUserRequest.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        if (userDao.existsByEmail(createUserRequest.getEmail())) {
            throw new UserAlreadyExistsException();
        }

        UserKeycloakRequest userKeycloakRequest = new UserKeycloakRequest();
        userKeycloakRequest.setUsername(createUserRequest.getUsername());
        userKeycloakRequest.setEmail(createUserRequest.getEmail());
        userKeycloakRequest.setPassword(createUserRequest.getPassword());
        userKeycloakRequest.setFirstName(createUserRequest.getFirstName());
        userKeycloakRequest.setLastName(createUserRequest.getLastName());


        logger.info("Creating user in keycloak");
        String keycloakUserId = null;
        try {
            logger.info("Creating user in Keycloak for username: {}", userKeycloakRequest.getUsername());
            keycloakUserId = keycloakAdminClientService.createUser(userKeycloakRequest);
            logger.info("Created user in Keycloak for username: {}", userKeycloakRequest.getUsername());

            User user = User.builder()
                    .id(UUID.fromString(keycloakUserId))
                    .username(createUserRequest.getUsername())
                    .firstName(createUserRequest.getFirstName())
                    .lastName(createUserRequest.getLastName())
                    .email(createUserRequest.getEmail())
                    .linkedin(createUserRequest.getLinkedin())
                    .birthday(LocalDateTime.now())
                    .university(createUserRequest.getUniversity())
                    .faculty(createUserRequest.getFaculty())
                    .department(createUserRequest.getDepartment())
                    .build();

            User savedUser = userDao.save(user);
            logger.info("User saved successfully to local database with ID: {}", savedUser.getId());


            emailService.sendEmailAsync(user.getEmail(), "SKY LAB HESABINIZ OLUŞTURULDU", "SKY LAB GİRİŞİ İÇİN KULLANICI ADINIZ: " + user.getUsername() + "\n" + "ŞİFRENİZ: " + createUserRequest.getPassword() + "\n" +
                    "GİRİŞ YAPTIKTAN SONRA ŞİFRENİZİ DEĞİŞTİRİNİZ!");

            return userMapper.toDto(savedUser);

        } catch (Exception e){
            if (keycloakUserId != null){
                logger.warn("Error occurred after Keycloak user creation. Rolling back Keycloak user with ID: {}", keycloakUserId);
                keycloakAdminClientService.deleteUser(keycloakUserId);
            }
            logger.error("User creation failed: {}", e.getMessage());

            throw new RuntimeException("User creation failed: " + e.getMessage(), e);
        }

    }

    @Transactional
    @Override
    public void deleteUser(UUID id) {
        logger.info("Deleting user with id: {}", id);

        if (!userDao.existsById(id)) {
            throw new UserNotFoundException();
        }

        logger.info("Deleting user in keycloak");
        keycloakAdminClientService.deleteUser(id.toString());
        logger.info("Deleted user in keycloak");

        userDao.deleteById(id);
        logger.info("Deleted user with id: {}", id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userMapper.toDtoList(userDao.findAll());
    }

    @Override
    public UserDto getUserById(UUID id) {
        var result = userDao.findById(id);
        if(result.isEmpty()) {
            throw new UserNotFoundException();
        }

       return userMapper.toDto(result.get());
    }

    @Override
    public UserDto getUserByUsername(String username) {
        var result = userDao.findByUsername(username);
        if(result.isEmpty()) {
            throw new UserNotFoundException();
        }

       return userMapper.toDto(result.get());
    }

    @Override
    public UserDto getUserByEmail(String email) {
        var result = userDao.findByEmail(email);
        if(result.isEmpty()) {
            throw new UserNotFoundException();
        }

        return userMapper.toDto(result.get());

    }

    /*
    // This method is commented out because it is not used in the current implementation.
    //instead checking users roles to determine tenant access
    @Override
    public boolean tenantCheck(String tenant, String username) {
        var user = userDao.findByUsername(username);
        if(user == null) {
            throw new UserNotFoundException();
        }
        var normalizedTenant = tenant.toUpperCase().trim();
        var requiredTenantRole = "ROLE_" + normalizedTenant + "_ADMIN";
        return user.getAuthorities().stream()
                .map(Role::name)
                .anyMatch(auth -> auth.equals(requiredTenantRole) || auth.equals("ROLE_ADMIN"));
    }

     */

    @Override
    public void addRoleToUser(String username, KeycloakRole role) {
        var user = userDao.findByUsername(username).orElseThrow(UserNotFoundException::new);

        keycloakAdminClientService.assignRealmRole(user.getId().toString(), role);

        userDao.save(user);
    }

    @Override
    public void setLastLoginWithUsername(String username) {
        var userResult = userDao.findByUsername(username);
        if(userResult.isEmpty()) {
            throw new UserNotFoundException();
        }
        var user = userResult.get();

        user.setLastLogin(LocalDateTime.now());
        userDao.save(user);
    }

    @Override
    public List<UserDto> getAllUsersByIds(List<UUID> userIds) {
        return userMapper.toDtoList(userDao.findAllById(userIds));
    }

    @Override
    public User getAuthenticatedUserEntity(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Authorization header missing or invalid");
        }

        String token = authHeader.substring(7);

        String userId;
        try{
            SignedJWT jwt = SignedJWT.parse(token);
            userId = jwt.getJWTClaimsSet().getSubject();
        }catch (Exception e){
            throw new RuntimeException("Invalid JWT token", e);
        }


        var user = userDao.findById(UUID.fromString(userId)).orElseThrow(() ->
                new UserNotFoundException("User not found with id: " + userId));

        return user;

    }

    @Override
    public User getUserEntityById(UUID id) {
        return userDao.findById(id)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User getUserEntityByUsername(String username) {
        return userDao.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User getUserEntityByEmail(String email) {
        return userDao.findByEmail(email)
                .orElseThrow(UserNotFoundException::new);
    }

    @Override
    public UserDto updateAuthenticatedUser(UpdateUserRequest updateUserRequest, HttpServletRequest request) {
        logger.info("Updating authenticated user");
        var user = getAuthenticatedUserEntity(request);

        user.setFirstName(updateUserRequest.getFirstName());
        user.setLastName(updateUserRequest.getLastName());
        user.setLinkedin(updateUserRequest.getLinkedin());
        user.setUniversity(updateUserRequest.getUniversity());
        user.setDepartment(updateUserRequest.getDepartment());
        user.setFaculty(updateUserRequest.getFaculty());

        var updatedUser = userDao.save(user);
        logger.info("Updated authenticated user with username: {}", updatedUser.getUsername());

        emailService.sendEmailAsync(user.getEmail(), "SKY LAB HESABINIZ GÜNCELLENDİ",
                "SKY LAB GİRİŞİ İÇİN KULLANICI ADINIZ: " + user.getUsername() + "\n" +
                        "HESAP BİLGİLERİNİZ GÜNCELLENDİ!");

        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserDto updateUser(UUID userId, UpdateUserRequest updateUserRequest) {
        logger.info("Updating user with id: {}", userId);
        var user = userDao.findById(userId).orElseThrow(UserNotFoundException::new);

        logger.info("Updating user in keycloak");
        UserUpdateKeycloakRequest userKeycloakRequest = new UserUpdateKeycloakRequest();
        userKeycloakRequest.setFirstName(updateUserRequest.getFirstName());
        userKeycloakRequest.setLastName(updateUserRequest.getLastName());

        keycloakAdminClientService.updateUser(userId.toString(), userKeycloakRequest);
        logger.info("Updated user in keycloak");

        user.setFirstName(updateUserRequest.getFirstName());
        user.setLastName(updateUserRequest.getLastName());
        user.setLinkedin(updateUserRequest.getLinkedin());
        user.setUniversity(updateUserRequest.getUniversity());
        user.setDepartment(updateUserRequest.getDepartment());
        user.setFaculty(updateUserRequest.getFaculty());

        var updatedUser = userDao.save(user);
        logger.info("Updated user with id: {}", updatedUser.getId());

        emailService.sendEmailAsync(user.getEmail(), "SKY LAB HESABINIZ GÜNCELLENDİ",
                "SKY LAB GİRİŞİ İÇİN KULLANICI ADINIZ: " + user.getUsername() + "\n" +
                        "HESAP BİLGİLERİNİZ GÜNCELLENDİ!");

        return userMapper.toDto(updatedUser);
    }

    @Override
    public void changePassword(UUID userId, String newPassword) {

        logger.info("Changing password for user with id: {}", userId);
        var user = userDao.findById(userId).orElseThrow(UserNotFoundException::new);

        keycloakAdminClientService.resetPassword(userId.toString(), newPassword);
        logger.info("Changed password for user with id: {}", userId);

    }

    @Override
    public UserDto getAuthenticatedUser(HttpServletRequest request) {
        logger.info("Retrieving authenticated user dto");
        var user = getAuthenticatedUserEntity(request);
        return userMapper.toDto(user);
    }


    public String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = secureRandom.nextInt(chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }
}
