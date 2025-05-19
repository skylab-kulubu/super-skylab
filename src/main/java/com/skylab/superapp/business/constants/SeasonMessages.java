package com.skylab.superapp.business.constants;

import org.springframework.http.HttpStatus;

public class SeasonMessages {
    public static String NameCannotBeNull = "Sezon adı boş bırakılamaz!";
    public static String NameAlreadyExists = "Sezon adı zaten mevcut!";
    public static String SeasonAddedSuccess = "Sezon başarıyla eklendi!";
    public static String SeasonNotFound = "Sezon bulunamadı!";
    public static String SeasonDeletedSuccess = "Sezon silindi.";
    public static String TenantCannotBeNull = "Tenant adı boş bırakılamaz!";
    public static String SeasonListedSuccess = "Sezonlar listelendi.";
    public static String CompetitorAlreadyInSeason = "Bu yarışmacı zaten bu sezonda mevcut!";
    public static String CompetitorAddedSuccess = "Yarışmacı başarıyla sezona eklendi!";
    public static String CompetitorNotInSeason = "Bu yarışmacı bu sezonda mevcut değil!";
    public static String CompetitorRemovedSuccess = "Yarışmacı başarıyla sezondan çıkarıldı!";
}
