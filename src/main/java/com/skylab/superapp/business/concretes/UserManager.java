package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.exceptions.*;
import com.skylab.superapp.core.mappers.UserMapper;
import com.skylab.superapp.core.utilities.mail.EmailService;
import com.skylab.superapp.dataAccess.UserDao;
import com.skylab.superapp.entities.DTOs.User.CreateUserRequest;
import com.skylab.superapp.entities.DTOs.User.UpdateUserRequest;
import com.skylab.superapp.entities.DTOs.User.UserDto;
import com.skylab.superapp.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserManager implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final UserMapper userMapper;
    private final Logger logger = LoggerFactory.getLogger(UserManager.class);


    public UserManager(UserDao userDao, PasswordEncoder passwordEncoder, @Lazy EmailService emailService, UserMapper userMapper) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto addUser(CreateUserRequest createUserRequest, UUID keycloakId) {
        logger.info("Saving user with username: {}", createUserRequest.getUsername());
        if(createUserRequest.getUsername() == null) {
            throw new UsernameorOrPasswordCannotBeNullException();
        }

        if(createUserRequest.getEmail() == null) {
            throw new EmailCannotBeNullException();
        }

        if(userDao.existsByUsername(createUserRequest.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        User user = User.builder()
                .id(keycloakId)
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

        var savedUser = userDao.save(user);
        logger.info("User created with username: {} and email: {}", user.getUsername(), user.getEmail());

        //will change these
        emailService.sendEmailAsync(user.getEmail(), "SKY LAB HESABINIZ OLUŞTURULDU", "SKY LAB GİRİŞİ İÇİN KULLANICI ADINIZ: " + user.getUsername() + "\n" + "ŞİFRENİZ: " + createUserRequest.getPassword() + "\n" +
                "GİRİŞ YAPTIKTAN SONRA ŞİFRENİZİ DEĞİŞTİRİNİZ!");

        return userMapper.toDto(savedUser);

    }

    @Override
    public void deleteUser(UUID id) {
        var user = userDao.findById(id);
        if(user.isEmpty()) {
            throw new UserNotFoundException();
        }

        userDao.deleteById(id);
    }
    /*

    @Override
    public void changePassword(ChangePasswordRequest changePasswordRequest) {
        logger.info("Changing password for user with username: {}", getAuthenticatedUsername());
        var user = getAuthenticatedUserEntity();

        if(!changePasswordRequest.getOldPassword().equals(changePasswordRequest.getNewPassword())) {
            throw new PasswordsDoNotMatchException();

        }

        if(!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new OldPasswordIncorrectException();
        }

        if(changePasswordRequest.getNewPassword() == null || changePasswordRequest.getNewPassword().isEmpty()) {
            throw new NewPasswordCannotBeNullException();
        }

        if(changePasswordRequest.getNewPassword().length() < 6) {
            throw new PasswordTooShortException();
        }

        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        userDao.save(user);
        logger.info("Password changed for user with username: {}", user.getUsername());

            emailService.sendEmailAsync(user.getEmail(), "SKY LAB HESABINIZIN ŞİFRESİ DEĞİŞTİRİLDİ",
                    user.getUsername() + " KULLANICI ADLI SKY LAB HESABINIZIN ŞİFRESİ DEĞİŞTİRİLDİ! BU İŞLEMİ SİZ YAPMADIYSANIZ ŞİFRENİZİ SIFIRLAYINIZ!");
    }

     */

    //password management is handled vy keycloak, this is just for resetting password
    /*
    @Override
    public void resetPassword(ResetPasswordRequest resetPasswordRequest) {
        if(resetPasswordRequest.getUsername() == null) {
            throw new UsernameCannotBeNullException();
        }

        var userResult = userDao.findByUsername(resetPasswordRequest.getUsername());
        if(userResult.isEmpty()) {
            throw new UserNotFoundException();
        }

        var user = userResult.get();
        var finalPassword = generateRandomPassword();

        user.setPassword(finalPassword);

        user.setPassword(passwordEncoder.encode(finalPassword));
        userDao.save(user);

        logger.info("Password reset for user with username: {}", user.getUsername());

        emailService.sendEmailAsync(
                user.getEmail(),
                "SKY LAB HESABINIZIN ŞİFRESİ DEĞİŞTİRİLDİ",
                "SKY LAB GİRİŞİ İÇİN KULLANICI ADINIZ: " + user.getUsername() + "\n" +
                        "ŞİFRENİZ: " + finalPassword + "\n" +
                        "GİRİŞ YAPTIKTAN SONRA ŞİFRENİZİ DEĞİŞTİRİNİZ!"
        );
    }

     */

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
    /*

    @Override
    public void addRoleToUser(String username, Role role) {
        var userResult = userDao.findByUsername(username);
        if(userResult.isEmpty()) {
            throw new UserNotFoundException();
        }
        var user = userResult.get();


        if(user.getAuthorities().contains(role)) {
            throw new RoleAlreadyExistsException();
        }

        user.addRole(role);
        userDao.save(user);
    }

    @Override
    public void removeRoleFromUser(String username, Role role) {
        var userResult = userDao.findByUsername(username);
        if(userResult.isEmpty()) {
            throw new UserNotFoundException();
        }
        var user = userResult.get();


        if(!user.getAuthorities().contains(role)) {
            throw new UserDoesNotHaveRoleException();
        }

        user.removeRole(role);
        userDao.save(user);
    }

     */

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
    public String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal().equals("anonymousUser")){
            throw new UserNotAuthorizedException();
        }
       return authentication.getName();
    }



    /*
    @Override
    public List<UserDto> getStaffsByRole(Role role) {
        return userMapper.toDtoList(userDao.findAllByAuthorities(role.name()));
    }

     */

    @Override
    public List<UserDto> getAllUsersByIds(List<UUID> userIds) {
        return userMapper.toDtoList(userDao.findAllById(userIds));
    }

    @Override
    public User getAuthenticatedUserEntity() {
        var username = getAuthenticatedUsername();
        var userResult = userDao.findByUsername(username);

        if(userResult.isEmpty()) {
            throw new UserNotFoundException();
        }

        return userResult.get();
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
    public UserDto updateAuthenticatedUser(UpdateUserRequest updateUserRequest) {
        logger.info("Updating authenticated user with username: {}", getAuthenticatedUsername());
        var user = getAuthenticatedUserEntity();

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
        var userResult = userDao.findById(userId);
        if (userResult.isEmpty()) {
            throw new UserNotFoundException();
        }
        var user = userResult.get();

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
    public UserDto getAuthenticatedUser() {
        logger.info("Retrieving authenticated user with username: {}", getAuthenticatedUsername());
        var user = getAuthenticatedUserEntity();
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
