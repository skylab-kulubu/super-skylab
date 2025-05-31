package com.skylab.superapp.business.constants;

import com.skylab.superapp.entities.User;
import org.springframework.http.HttpStatus;

import java.util.List;

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
    public static String OldPasswordIncorrect = "Eski şifre yanlış!";
    public static String NewPasswordCannotBeNull = "Yeni şifre boş bırakılamaz!";
    public static String PasswordChangedSuccess = "Şifre başarıyla değiştirildi!";
    public static String PasswordResetSuccess = "Şifre sıfırlama işlemi başarılı!";
    public static String NewPasswordTooShort = "Yeni şifre en az 6 karakter olmalıdır!";
    public static String PasswordsDoNotMatch = "Yeni şifreler eşleşmiyor!";
    public static String NewPasswordCannotBeSameAsOld = "Yeni şifre eski şifre ile aynı olamaz!";
    public static String AuthenticatedUsername = "AuthenticatedUsername returned";
    public static String StaffsNotFound = "Hiç personel bulunamadı!";
    public static String StaffsListedSuccess = "Personeller listelendi.";
    public static String competitorNotFound = "Yarışmacı bulunamadı!";
}
