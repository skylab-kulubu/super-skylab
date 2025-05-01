package com.skylab.superapp.business.constants;

import org.springframework.http.HttpStatus;

public class EventMessages {
    public static String TenantCannotBeNull = "Tenant boş bırakılamaz!";
    public static String EventCreatedSuccess = "Etkinlik başarıyla oluşturuldu!";
    public static String EventNotFound = "Etkinlik bulunamadı!";
    public static String EventDeleteSuccess = "Etkinlik Silindi.";
    public static String EventGetSuccess = "Etkinlik Bulundu";
    public static String UserNotAuthorized = "Bu etkinliği oluşturmak için yetkiniz yok!";
    public static String EventUpdateSuccess = "Etkinlik güncellendi.";
    public static String EventPhotosAddedSuccess = "Etkinlik fotoğrafları başarıyla eklendi!";
    public static String PhotoAlreadyAdded = "Bu fotoğraf zaten etkinliğe eklenmiş!";
}
