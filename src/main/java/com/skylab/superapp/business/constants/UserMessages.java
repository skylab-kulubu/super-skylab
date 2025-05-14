package com.skylab.superapp.business.constants;

import com.skylab.superapp.entities.User;
import org.springframework.http.HttpStatus;

public class UserMessages {
    public static String UsernameCannotBeNull = "Kullanıcı adı veya şifre boş bırakılamaz!";
    public static String UsernameAlreadyExists = "Kullanıcı adı zaten mevcut!";
    public static String UserAddedSuccess = "Kullanıcı başarıyla eklendi!";
    public static String UserNotFound = "Kullanıcı bulunamadı!";
    public static String UserDeletedSuccess = "Kullanıcı silindi.";
    public static String UsersNotFound = "Hiç kullanıcı bulunamadı!";
    public static String UsersListedSuccess = "Kullanıcılar listelendi.";
    public static String UserFoundSuccess = "Kullanıcı bulundu.";
    public static String RoleAlreadyExists = "Rol zaten mevcut!";
    public static String RoleAddedSuccess = "Rol başarıyla eklendi!";
    public static String RoleRemovedSuccess = "Rol başarıyla kaldırıldı!";
    public static String LastLoginUpdated = "Kullanıcının son girişi güncellendi!";
    public static String userIsNotAuthenticatedPleaseLogin = "Kullanıcı kimliği doğrulanmadı. Lütfen giriş yapın!";
}
