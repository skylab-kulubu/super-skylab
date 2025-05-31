package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.business.constants.UserMessages;
import com.skylab.superapp.core.results.*;
import com.skylab.superapp.core.utilities.mail.EmailService;
import com.skylab.superapp.dataAccess.UserDao;
import com.skylab.superapp.entities.DTOs.Auth.ChangePassword;
import com.skylab.superapp.entities.DTOs.User.CreateUserDto;
import com.skylab.superapp.entities.DTOs.User.GetUserDto;
import com.skylab.superapp.entities.Role;
import com.skylab.superapp.entities.User;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class UserManager implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    public UserManager(UserDao userDao, PasswordEncoder passwordEncoder, EmailService emailService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public Result addUser(CreateUserDto createUserDto) {
        if(createUserDto.getUsername() == null || createUserDto.getPassword() == null) {
            return new ErrorResult(UserMessages.UsernameCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        if(userDao.existsByUsername(createUserDto.getUsername())) {
            return new ErrorResult(UserMessages.UsernameAlreadyExists, HttpStatus.BAD_REQUEST);
        }

        User user = User.builder()
                .authorities(Set.of(Role.ROLE_USER))
                .username(createUserDto.getUsername())
                .email(createUserDto.getEmail())
                .password(passwordEncoder.encode(createUserDto.getPassword() == null ? generateRandomPassword() : createUserDto.getPassword()))
                .createdAt(new Date())
                .lastLogin(new Date())
                .build();

        userDao.save(user);
        emailService.sendMail(user.getEmail(), "SKY LAB HESABINIZ OLUŞTURULDU", "SKY LAB GİRİŞİ İÇİN KULLANICI ADINIZ: "+user.getUsername()+"\n"+ "ŞİFRENİZ: " + createUserDto.getPassword() + "\n" +
                "GİRİŞ YAPTIKTAN SONRA ŞİFRENİZİ DEĞİŞTİRİNİZ!");
        return new SuccessResult(UserMessages.UserAddedSuccess, HttpStatus.CREATED);
    }

    @Override
    public Result deleteUser(int id) {
        var user = userDao.findById(id);
        if(user == null) {
            return new ErrorResult(UserMessages.UserNotFound, HttpStatus.NOT_FOUND);
        }

        userDao.deleteById(id);
        return new SuccessResult(UserMessages.UserDeletedSuccess, HttpStatus.OK);
    }

    @Override
    public Result changePassword(ChangePassword changePassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getName();

        var user = userDao.findByUsername(username);
        if(user == null) {
            return new ErrorResult(UserMessages.UserNotFound, HttpStatus.NOT_FOUND);
        }
        if(!passwordEncoder.matches(changePassword.getOldPassword(), user.getPassword())) {
            return new ErrorResult(UserMessages.OldPasswordIncorrect, HttpStatus.BAD_REQUEST);
        }

        if(changePassword.getNewPassword() == null || changePassword.getNewPassword().isEmpty()) {
            return new ErrorResult(UserMessages.NewPasswordCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        if(changePassword.getNewPassword().length() < 6) {
            return new ErrorResult(UserMessages.NewPasswordTooShort, HttpStatus.BAD_REQUEST);
        }

        if(!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            return new ErrorResult(UserMessages.PasswordsDoNotMatch, HttpStatus.BAD_REQUEST);

        }

        user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
        userDao.save(user);
        return new SuccessResult(UserMessages.PasswordChangedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<String> resetPassword(CreateUserDto createUserDto) {
        if(createUserDto.getUsername() == null) {
            return new ErrorDataResult<>(UserMessages.UsernameCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        var user = userDao.findByUsername(createUserDto.getUsername());
        if(user == null) {
            return new ErrorDataResult<>(UserMessages.UserNotFound, HttpStatus.NOT_FOUND);
        }

        //generate random password if password is null
        if(createUserDto.getPassword() == null || createUserDto.getPassword().isEmpty()) {
            var randomPassword = generateRandomPassword();
            user.setPassword(passwordEncoder.encode(randomPassword));
            userDao.save(user);
            emailService.sendMail(user.getEmail(), "SKY LAB HESABINIZIN ŞİFRESİ DEĞİŞTİRİLDİ", "SKY LAB GİRİŞİ İÇİN KULLANICI ADINIZ: "+user.getUsername()+"\n"+ "ŞİFRENİZ: " + randomPassword+ "\n" +
                    "GİRİŞ YAPTIKTAN SONRA ŞİFRENİZİ DEĞİŞTİRİNİZ!");
            return new SuccessDataResult<>(randomPassword, UserMessages.PasswordResetSuccess, HttpStatus.OK);
        } else
            user.setPassword(passwordEncoder.encode(createUserDto.getPassword()));

        userDao.save(user);
        emailService.sendMail(user.getEmail(), "SKY LAB HESABINIZIN ŞİFRESİ DEĞİŞTİRİLDİ", "SKY LAB GİRİŞİ İÇİN KULLANICI ADINIZ: "+user.getUsername()+"\n"+ "ŞİFRENİZ: " + createUserDto.getPassword() + "\n" +
                "GİRİŞ YAPTIKTAN SONRA ŞİFRENİZİ DEĞİŞTİRİNİZ!");
        return new SuccessDataResult<>(UserMessages.PasswordChangedSuccess, HttpStatus.OK);
    }

    @Override
    public Result changeAuthenticatedUserPassword(String newPassword) {
        if (newPassword == null || newPassword.isEmpty()) {
            return new ErrorResult(UserMessages.NewPasswordCannotBeNull, HttpStatus.BAD_REQUEST);
        }

        if (newPassword.length() < 6) {
            return new ErrorResult(UserMessages.NewPasswordTooShort, HttpStatus.BAD_REQUEST);
        }

        var loggedInUsername = getAuthenticatedUsername();
        if (!loggedInUsername.isSuccess()) {
            return new ErrorResult(loggedInUsername.getMessage(), loggedInUsername.getHttpStatus());
        }

        var loggedInUser = userDao.findByUsername(loggedInUsername.getData());
        if (loggedInUser == null) {
            return new ErrorResult(UserMessages.UserNotFound, HttpStatus.NOT_FOUND);
        }


        if (passwordEncoder.matches(newPassword, loggedInUser.getPassword())){
            return new ErrorResult(UserMessages.NewPasswordCannotBeSameAsOld, HttpStatus.BAD_REQUEST);
        }

        loggedInUser.setPassword(passwordEncoder.encode(newPassword));
        userDao.save(loggedInUser);

        emailService.sendMail(loggedInUser.getEmail(), "SKY LAB HESABINIZIN ŞİFRESİ DEĞİŞTİRİLDİ", loggedInUser.getUsername() + " KULLANICI ADLI SKY LAB HESABINIZIN ŞİFRESİ DEĞİŞTİRİLDİ! BU İŞLEMİ SİZ YAPMADIYSANIZ ŞİFRENİZİ SIFIRLAYINIZ!");

        return new SuccessResult(UserMessages.PasswordChangedSuccess, HttpStatus.OK);

    }

    @Override
    public DataResult<List<GetUserDto>> getAllUsers() {
        var result = userDao.findAll();
        if(result.isEmpty()) {
            return new ErrorDataResult<>(UserMessages.UsersNotFound, HttpStatus.NOT_FOUND);
        }

        var returnUsers = GetUserDto.buildListGetUserDto(result);
        return new SuccessDataResult<>(returnUsers, UserMessages.UsersListedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<User> getUserEntityById(int id) {
        var result = userDao.findById(id);
        if(result == null) {
            return new ErrorDataResult<>(UserMessages.UserNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<>(result, UserMessages.UserFoundSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<GetUserDto> getUserById(int id) {
        var result = userDao.findById(id);
        if(result == null) {
            return new ErrorDataResult<>(UserMessages.UserNotFound, HttpStatus.NOT_FOUND);
        }

        var returnUser = new GetUserDto(result);
        return new SuccessDataResult<>(returnUser, UserMessages.UserFoundSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<User> getUserEntityByUsername(String username) {
        var result = userDao.findByUsername(username);
        if(result == null) {
            return new ErrorDataResult<>(UserMessages.UserNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<>(result, UserMessages.UserFoundSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<User> getUserEntityByEmail(String email) {
        var result = userDao.findByEmail(email);
        if(!result.isPresent()) {
            return new ErrorDataResult<>(UserMessages.UserNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<>(result.get(), UserMessages.UserFoundSuccess, HttpStatus.OK);
    }

    @Override
    public boolean tenantCheck(String tenant, String username) {
        var user = userDao.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException(UserMessages.UserNotFound);
        }
        var normalizedTenant = tenant.toUpperCase().trim();
        var requiredTenantRole = "ROLE_" + normalizedTenant + "_ADMIN";
        return user.getAuthorities().stream()
                .map(Role::name)
                .anyMatch(auth -> auth.equals(requiredTenantRole) || auth.equals("ROLE_ADMIN"));
    }

    @Override
    public Result addRoleToUser(String username, Role role) {
        var user = userDao.findByUsername(username);
        if(user == null) {
            return new ErrorResult(UserMessages.UserNotFound, HttpStatus.NOT_FOUND);
        }

        if(user.getAuthorities().contains(role)) {
            return new ErrorResult(UserMessages.RoleAlreadyExists, HttpStatus.BAD_REQUEST);
        }

       user.addRole(role);
        userDao.save(user);
        return new SuccessResult(UserMessages.RoleAddedSuccess, HttpStatus.OK);
    }

    @Override
    public Result removeRoleFromUser(String username, Role role) {
        var user = userDao.findByUsername(username);
        if(user == null) {
            return new ErrorResult(UserMessages.UserNotFound, HttpStatus.NOT_FOUND);
        }

        if(!user.getAuthorities().contains(role)) {
            return new ErrorResult(UserMessages.RoleAlreadyExists, HttpStatus.BAD_REQUEST);
        }

        user.removeRole(role);
        userDao.save(user);
        return new SuccessResult(UserMessages.RoleRemovedSuccess, HttpStatus.OK);
    }

    @Override
    public Result setLastLoginWithUsername(String username) {
        var user = userDao.findByUsername(username);
        if(user == null) {
            return new ErrorResult(UserMessages.UserNotFound, HttpStatus.NOT_FOUND);
        }

        user.setLastLogin(new Date());
        userDao.save(user);
        return new SuccessResult(UserMessages.LastLoginUpdated, HttpStatus.OK);
    }

    @Override
    public DataResult<String> getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication.getPrincipal().equals("anonymousUser")){
            return new ErrorDataResult<>("anonymousUser", UserMessages.userIsNotAuthenticatedPleaseLogin, HttpStatus.UNAUTHORIZED);
        }
        return new SuccessDataResult<>(authentication.getName(), UserMessages.AuthenticatedUsername, HttpStatus.OK);
    }

    @Override
    public DataResult<List<User>> getAllStaffs() {
        var result = userDao.findAllByAuthorities_NameIn(List.of(Role.ROLE_ADMIN.name(), Role.ROLE_BIZBIZE_ADMIN.name(), Role.ROLE_AGC_ADMIN.name(), Role.ROLE_GECEKODU_ADMIN.name()));
        if(result.isEmpty()) {
            return new ErrorDataResult<>(UserMessages.StaffsNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<>(result, UserMessages.StaffsListedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<User>> getStaffsByRole(Role role) {
        var result = userDao.findAllByAuthorities_Name(role.name());
        if(result.isEmpty()) {
            return new ErrorDataResult<>(UserMessages.StaffsNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<>(result, UserMessages.StaffsListedSuccess, HttpStatus.OK);
    }

    @Override
    public DataResult<List<User>> getAllUserByIds(List<Integer> userIds) {
        var result = userDao.findAllById(userIds);

        if (result.isEmpty()) {
            return new ErrorDataResult<>(UserMessages.UsersNotFound, HttpStatus.NOT_FOUND);
        }

        return new SuccessDataResult<>(result, UserMessages.UsersListedSuccess, HttpStatus.OK);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userDao.findByUsername(username);
        if(user == null) {
            throw new UsernameNotFoundException(UserMessages.UserNotFound);
        }

        return user;
    }

    public String generateRandomPassword() {
        //generate random password with letters, numbers and special characters
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
        StringBuilder password = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            int index = (int) (Math.random() * chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }
}
