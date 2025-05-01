package com.skylab.superapp.business.constants;

import com.skylab.superapp.entities.DTOs.Photo.GetPhotoDto;
import org.springframework.http.HttpStatus;

import java.util.List;

public class PhotoMessages {
    public static String PhotoAddedSuccess = "Fotoğraf başarıyla eklendi!";
    public static String PhotoNotFound = "Fotoğraf bulunamadı!";
    public static String PhotoDeletedSuccess = "Fotoğraf silindi.";
    public static String PhotoFoundSuccess = "Fotoğraf bulundu.";
    public static String NoPhotosFound = "Hiç fotoğraf bulunamadı!";
    public static String PhotosFoundSuccess = "Fotoğraflar bulundu.";
    public static String PhotoUrlCannotBeNull = "Fotoğraf URL'si boş olamaz!";
    public static String TenantCannotBeNull = "Tenant boş olamaz!";
    public static String UserNotAuthorized = "Kullanıcı yetkilendirilmedi!";
}
