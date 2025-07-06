package com.skylab.superapp.business.concretes;

import com.skylab.superapp.business.abstracts.UserService;
import com.skylab.superapp.core.constants.UserMessages;
import com.skylab.superapp.core.exceptions.*;
import com.skylab.superapp.core.utilities.mail.EmailService;
import com.skylab.superapp.dataAccess.UserDao;
import com.skylab.superapp.entities.DTOs.Auth.ChangePassword;
import com.skylab.superapp.entities.DTOs.User.CreateUserDto;
import com.skylab.superapp.entities.DTOs.User.UpdateUserDto;
import com.skylab.superapp.entities.Role;
import com.skylab.superapp.entities.User;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service
public class UserManager implements UserService {

    private final UserDao userDao;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom secureRandom = new SecureRandom();


    public UserManager(UserDao userDao, PasswordEncoder passwordEncoder,@Lazy EmailService emailService) {
        this.userDao = userDao;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Override
    public void addUser(CreateUserDto createUserDto) {
        if(createUserDto.getUsername() == null || createUserDto.getPassword() == null) {
            throw new UsernameorOrPasswordCannotBeNullException();
        }

        if(createUserDto.getEmail() == null) {
            throw new EmailCannotBeNullException();
        }

        if(userDao.existsByUsername(createUserDto.getUsername())) {
            throw new UserAlreadyExistsException();
        }

        User user = User.builder()
                .authorities(Set.of(Role.ROLE_USER))
                .username(createUserDto.getUsername())
                .email(createUserDto.getEmail())
                .password(passwordEncoder.encode(createUserDto.getPassword()))
                .createdAt(new Date())
                .lastLogin(new Date())
                .build();

        userDao.save(user);
        emailService.sendMail(user.getEmail(), "SKY LAB HESABINIZ OLUŞTURULDU", "SKY LAB GİRİŞİ İÇİN KULLANICI ADINIZ: "+user.getUsername()+"\n"+ "ŞİFRENİZ: " + createUserDto.getPassword() + "\n" +
                "GİRİŞ YAPTIKTAN SONRA ŞİFRENİZİ DEĞİŞTİRİNİZ!");
    }

    @Override
    public void deleteUser(int id) {
        var user = userDao.findById(id);
        if(user.isEmpty()) {
            throw new UserNotFoundException();
        }

        userDao.deleteById(id);
    }

    @Override
    public void changePassword(ChangePassword changePassword) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        var username = authentication.getName();

        var userResult = userDao.findByUsername(username);
        if(userResult.isEmpty()) {
            throw new UserNotFoundException();
        }
        var user = userResult.get();

        if(!passwordEncoder.matches(changePassword.getOldPassword(), user.getPassword())) {
            throw new OldPasswordIncorrectException();
        }

        if(changePassword.getNewPassword() == null || changePassword.getNewPassword().isEmpty()) {
            throw new NewPasswordCannotBeNullException();
        }

        if(changePassword.getNewPassword().length() < 6) {
            throw new PasswordTooShortException();
        }

        if(!changePassword.getNewPassword().equals(changePassword.getConfirmPassword())) {
            throw new PasswordsDoNotMatchException();

        }

        user.setPassword(passwordEncoder.encode(changePassword.getNewPassword()));
        userDao.save(user);

        emailService.sendMail(user.getEmail(), "SKY LAB HESABINIZIN ŞİFRESİ DEĞİŞTİRİLDİ",
                user.getUsername() + " KULLANICI ADLI SKY LAB HESABINIZIN ŞİFRESİ DEĞİŞTİRİLDİ! BU İŞLEMİ SİZ YAPMADIYSANIZ ŞİFRENİZİ SIFIRLAYINIZ!");
    }

    @Override
    public void resetPassword(CreateUserDto createUserDto) {
        if(createUserDto.getUsername() == null) {
            throw new UsernameCannotBeNullException();
        }

        var userResult = userDao.findByUsername(createUserDto.getUsername());
        if(userResult.isEmpty()) {
            throw new UserNotFoundException();
        }

        var user = userResult.get();


        String finalPassword;
        if (createUserDto.getPassword() == null || createUserDto.getPassword().isEmpty()) {
            finalPassword = generateRandomPassword();
        } else {
            finalPassword = createUserDto.getPassword();
        }

        user.setPassword(passwordEncoder.encode(finalPassword));
        userDao.save(user);

        emailService.sendMail(
                user.getEmail(),
                "SKY LAB HESABINIZIN ŞİFRESİ DEĞİŞTİRİLDİ",
                "SKY LAB GİRİŞİ İÇİN KULLANICI ADINIZ: " + user.getUsername() + "\n" +
                        "ŞİFRENİZ: " + finalPassword + "\n" +
                        "GİRİŞ YAPTIKTAN SONRA ŞİFRENİZİ DEĞİŞTİRİNİZ!"
        );
    }

    @Override
    public List<User> getAllUsers() {
        return userDao.findAll();
        /*
        if(result.isEmpty()) {
            throw new UserNotFoundException();
        }

         */
    }

    @Override
    public User getUserById(int id) {
        var result = userDao.findById(id);
        if(result.isEmpty()) {
            throw new UserNotFoundException();
        }

       return result.get();
    }

    @Override
    public User getUserByUsername(String username) {
        var result = userDao.findByUsername(username);
        if(result.isEmpty()) {
            throw new UserNotFoundException();
        }

       return result.get();
    }

    @Override
    public User getUserByEmail(String email) {
        var result = userDao.findByEmail(email);
        if(result.isEmpty()) {
            throw new UserNotFoundException();
        }

        return result.get();

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

    @Override
    public void setLastLoginWithUsername(String username) {
        var userResult = userDao.findByUsername(username);
        if(userResult.isEmpty()) {
            throw new UserNotFoundException();
        }
        var user = userResult.get();

        user.setLastLogin(new Date());
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

    @Override
    public List<User> getAllStaffs() {
        return userDao.findAllByAuthorities_NameIn(List.of(Role.ROLE_ADMIN.name(), Role.ROLE_BIZBIZE_ADMIN.name(), Role.ROLE_AGC_ADMIN.name(), Role.ROLE_GECEKODU_ADMIN.name()));
        /*
        if(result.isEmpty()) {
            throw new UserNotFoundException();
        }

         */
    }

    @Override
    public List<User> getStaffsByRole(Role role) {
        return userDao.findAllByAuthorities(role.name());
        /*
        if(result.isEmpty()) {
            throw new UserNotFoundException();
        }

         */
    }

    @Override
    public List<User> getAllUsersByIds(List<Integer> userIds) {
        return userDao.findAllById(userIds);

        /*
        if (result.isEmpty()) {
            throw new UserNotFoundException();
        }

         */

    }

    @Override
    public User getAuthenticatedUser() {
        var username = getAuthenticatedUsername();
        var userResult = userDao.findByUsername(username);

        if(userResult.isEmpty()) {
            throw new UserNotFoundException();
        }

        return userResult.get();
    }

    @Override
    public void updateAuthenticatedUser(UpdateUserDto updateUserDto) {
        var user = getAuthenticatedUser();

        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setEmail(updateUserDto.getEmail());
        user.setLinkedin(updateUserDto.getLinkedin());
        user.setUniversity(updateUserDto.getUniversity());
        user.setDepartment(updateUserDto.getDepartment());
        user.setFaculty(updateUserDto.getFaculty());

        userDao.save(user);

        emailService.sendMail(user.getEmail(), "SKY LAB HESABINIZ GÜNCELLENDİ",
                "SKY LAB GİRİŞİ İÇİN KULLANICI ADINIZ: " + user.getUsername() + "\n" +
                        "HESAP BİLGİLERİNİZ GÜNCELLENDİ!");
    }

    @Override
    public void updateUser(int userId, UpdateUserDto updateUserDto) {
        var userResult = userDao.findById(userId);
        if (userResult.isEmpty()) {
            throw new UserNotFoundException();
        }
        var user = userResult.get();

        user.setFirstName(updateUserDto.getFirstName());
        user.setLastName(updateUserDto.getLastName());
        user.setEmail(updateUserDto.getEmail());
        user.setLinkedin(updateUserDto.getLinkedin());
        user.setUniversity(updateUserDto.getUniversity());
        user.setDepartment(updateUserDto.getDepartment());
        user.setFaculty(updateUserDto.getFaculty());

        userDao.save(user);

        emailService.sendMail(user.getEmail(), "SKY LAB HESABINIZ GÜNCELLENDİ",
                "SKY LAB GİRİŞİ İÇİN KULLANICI ADINIZ: " + user.getUsername() + "\n" +
                        "HESAP BİLGİLERİNİZ GÜNCELLENDİ!");
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        var user = userDao.findByUsername(username);
        if(user.isEmpty()) {
            throw new UsernameNotFoundException(UserMessages.USER_NOT_FOUND);
        }

        return user.get();
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
