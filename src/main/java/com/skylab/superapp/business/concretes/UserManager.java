package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.ImageService;
import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.exceptions.*;
import com.skylab.superapp.core.mappers.UserMapper;
import com.skylab.superapp.core.utilities.ldap.LdapService;
import com.skylab.superapp.core.utilities.mail.EmailService;
import com.skylab.superapp.dataAccess.UserProfileDao;
import com.skylab.superapp.entities.DTOs.User.*;
import com.skylab.superapp.entities.Image;
import com.skylab.superapp.entities.LdapUser;
import com.skylab.superapp.entities.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.security.SecureRandom;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserManager implements UserService {

    private final UserProfileDao userProfileDao;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final UserMapper userMapper;
    private final Logger logger = LoggerFactory.getLogger(UserManager.class);
    private final LdapService ldapService;
    private final ImageService imageService;


    public UserManager(UserProfileDao userProfileDao, @Lazy EmailService emailService, UserMapper userMapper,
                       LdapService ldapService, @Lazy ImageService imageService) {
        this.userProfileDao = userProfileDao;
        this.emailService = emailService;
        this.userMapper = userMapper;
        this.ldapService = ldapService;
        this.imageService = imageService;
    }

    @Override
    @Transactional
    public UserDto addUser(CreateUserRequest createUserRequest) {
        logger.info("Saving user with username: {}", createUserRequest.getUsername());


            LdapUser ldapUser = ldapService.createUser(
                    createUserRequest.getUsername(),
                    createUserRequest.getFirstName(),
                    createUserRequest.getLastName(),
                    createUserRequest.getEmail(),
                    createUserRequest.getPassword());
            logger.info("Saved user in LDAP with username: {}", createUserRequest.getUsername());


        UserProfile savedProfile;
        try{
            UserProfile userProfile = new UserProfile();
            userProfile.setLdapSkyNumber(ldapUser.getEmployeeNumber());
            savedProfile = userProfileDao.save(userProfile);
            logger.info("Saved user with username: {}", createUserRequest.getUsername());
        }catch (Exception e){
            logger.error("Error occurred while saving user profile, removing from ldap: {}", e.getMessage());
            try{
                ldapService.deleteUser(ldapUser.getEmployeeNumber());
                logger.info("Rolled back LDAP user creation for username: {}", createUserRequest.getUsername());
            }catch (Exception ex){
                logger.error("Failed to roll back LDAP user creation for username: {}: {}", createUserRequest.getUsername(), ex.getMessage());
            }
            throw new RuntimeException("Failed to save user profile");
        }

        UserDto userDto = userMapper.toDto(savedProfile, ldapUser);
        return userDto;
    }

    @Transactional
    @Override
    public void deleteUser(UUID id) {
        logger.info("Deleting user with id: {}", id);

       UserProfile profileToDelete = userProfileDao.findById(id).orElseThrow(() -> new ResourceNotFoundException(UserMessages.USER_NOT_FOUND));

       String employeeNumber = profileToDelete.getLdapSkyNumber();
       logger.info("Deleting user from LDAP with employee number: {}", employeeNumber);

       userProfileDao.delete(profileToDelete);

       logger.info("Deleted user profile from database with id: {}", id);

       try{
              ldapService.deleteUser(employeeNumber);
              logger.info("Deleted user from LDAP with employee number: {}", employeeNumber);
       }catch (Exception exception){
              logger.error("Error occurred while deleting user from LDAP, employeeNumber: {} Exception: {}", employeeNumber, exception.getMessage());
              throw new RuntimeException("Failed to delete user from LDAP");
       }
    }


    @Override
    public List<UserDto> getAllUsers() {
        var allProfiles = userProfileDao.findAll();

        if (allProfiles.isEmpty()){
            return List.of();
        }

        List<String> ldapSkyNumbers = allProfiles.stream()
                .map(UserProfile::getLdapSkyNumber)
                .toList();

        List<LdapUser> allIdentities = ldapService.findAllByEmployeeNumbers(ldapSkyNumbers);

        Map<String, LdapUser> identityMap = allIdentities.stream()
                .collect(Collectors.toMap(LdapUser::getEmployeeNumber, user -> user));

        return allProfiles.stream()
                .map(profile -> {
                    LdapUser identity = identityMap.get(profile.getLdapSkyNumber());
                    if (identity == null){
                        return null;
                    }
                    return userMapper.toDto(profile, identity);
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

    }

    @Override
    public UserDto getUserById(UUID id) {
        UserProfile profile = userProfileDao.findById(id).orElseThrow(() -> new ResourceNotFoundException(UserMessages.USER_NOT_FOUND));

        LdapUser ldapUser = ldapService.findByEmployeeNumber(profile.getLdapSkyNumber());

        return userMapper.toDto(profile, ldapUser);
    }

    @Override
    public UserDto getUserByUsername(String username) {
       LdapUser ldapUser = ldapService.findByUsername(username);

       UserProfile profile = userProfileDao.findByLdapSkyNumber(ldapUser.getEmployeeNumber()).orElseThrow(() -> new ResourceNotFoundException(UserMessages.USER_NOT_FOUND));

         return userMapper.toDto(profile, ldapUser);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        LdapUser ldapUser = ldapService.findByEmail(email);

        UserProfile profile = userProfileDao.findByLdapSkyNumber(ldapUser.getEmployeeNumber()).orElseThrow(() -> new ResourceNotFoundException(UserMessages.USER_NOT_FOUND));

        return userMapper.toDto(profile, ldapUser);
    }

    @Override
    public void addRoleToUser(String username, String role) {
        logger.info("Adding role {} to user with username: {}", role, username);
        LdapUser ldapUser = ldapService.findByUsername(username);

        if (ldapUser == null){
            logger.info("User with username: {} not found in LDAP", username);
            throw new ResourceNotFoundException(UserMessages.USER_NOT_FOUND);
        }

        ldapService.addUserToGroup(ldapUser.getEmployeeNumber(), role);

    }
    @Override
    public UserProfile getAuthenticatedUserEntity() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found in context");
        }

        Object principal = authentication.getPrincipal();
        String skyNumber;

        if (principal instanceof Jwt jwt){
            skyNumber = jwt.getClaimAsString("sky_number");
            if (skyNumber == null){
                logger.error("Jwt does not contain sky_number claim");
                throw new RuntimeException("Jwt does not contain sky_number claim");
            }

        }else {
            throw new RuntimeException("Principal is not of type Jwt");
        }

        return userProfileDao.findByLdapSkyNumber(skyNumber)
                .orElseThrow(() -> {
            logger.warn("Authenticated user with sky_number {} not found in local DB.", skyNumber);
            return new ResourceNotFoundException(UserMessages.USER_NOT_FOUND);
        });

    }

    @Override
    public void uploadProfilePictureOfAuthenticatedUser(MultipartFile imageFile) {
        logger.info("Uploading profile picture for authenticated user");

        UserProfile user = getAuthenticatedUserEntity();
         logger.info("Retrieved authenticated user with id: {}", user.getId());

         Image image = imageService.uploadImage(imageFile);

         user.setProfilePicture(image);

         userProfileDao.save(user);

            logger.info("Profile picture uploaded and associated with user id: {}", user.getId());

    }

    @Override
    public UserProfile getUserEntityById(UUID id) {
        return userProfileDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(UserMessages.USER_NOT_FOUND));
    }

    @Override
    public UserDto updateAuthenticatedUser(UpdateUserRequest updateUserRequest) {

        UserProfile profile = getAuthenticatedUserEntity();
        LdapUser ldapUser = ldapService.findByEmployeeNumber(profile.getLdapSkyNumber());


        if(updateUserRequest.getFirstName() != null && !updateUserRequest.getFirstName().isBlank()){
            ldapUser.setFirstName(updateUserRequest.getFirstName());
        }

        if (updateUserRequest.getLastName() != null && !updateUserRequest.getLastName().isBlank()){
            ldapUser.setLastName(updateUserRequest.getLastName());
        }

        ldapUser.setFullName(ldapUser.getFirstName() + " " + ldapUser.getLastName());


        ldapService.updateUserAttributes(ldapUser);

        profile.setLinkedin(updateUserRequest.getLinkedin());
        profile.setUniversity(updateUserRequest.getUniversity());
        profile.setFaculty(updateUserRequest.getFaculty());
        profile.setDepartment(updateUserRequest.getDepartment());
        userProfileDao.save(profile);

        return userMapper.toDto(profile, ldapUser);


    }

    @Override
    public UserDto updateUser(UUID userId, UpdateUserRequest updateUserRequest) {
        // TODO
        return null;
    }

    @Override
    public void changePassword(UUID userId, String newPassword) {

        UserProfile profile = userProfileDao.findById(userId).orElseThrow(() -> new ResourceNotFoundException(UserMessages.USER_NOT_FOUND));

        ldapService.changePassword(profile.getLdapSkyNumber(), newPassword);

    }

    @Override
    public UserDto getAuthenticatedUser() {
        UserProfile profile = getAuthenticatedUserEntity();
        LdapUser ldapUser = ldapService.findByEmployeeNumber(profile.getLdapSkyNumber());

        return userMapper.toDto(profile, ldapUser);
    }

}
