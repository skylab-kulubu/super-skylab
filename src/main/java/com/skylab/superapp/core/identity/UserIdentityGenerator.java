package com.skylab.superapp.core.identity;

import com.skylab.superapp.core.identity.ldap.LdapService;
import com.skylab.superapp.dataAccess.UserDao;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;

@Service
public class UserIdentityGenerator {


    private final UserDao userDao;

    public UserIdentityGenerator(UserDao userDao) {
        this.userDao = userDao;
    }


    public synchronized String generateNextSkyNumber(){

        Integer maxNumber = userDao.findMaxSkyNumberValue();

        long nextNumber = (maxNumber != null ? maxNumber : 0) + 1;

        if (nextNumber > 9999999){
            throw new RuntimeException("Max sky number reached");
        }

        return String.format("SKY-%07d", nextNumber);

    }

    public String generateLdapUsername(String firstName, String lastName, LdapService ldapService){
        String cleanFirstName = normalize(firstName);
        String cleanLastName = normalize(lastName);


        String baseUsername = cleanFirstName + "." + cleanLastName;
        String finalUsername = baseUsername;
        int counter = 1;

        while (ldapService.usernameExists(finalUsername)){
            finalUsername = baseUsername + counter;
            counter++;
        }

        return finalUsername;

    }


    private String normalize(String text){
        if (text == null) return "";

        String turkishReplaced = text
                .replace('ı', 'i')
                .replace('İ', 'i')
                .replace('ğ', 'g')
                .replace('Ğ', 'g')
                .replace('ü', 'u')
                .replace('Ü', 'u')
                .replace('ş', 's')
                .replace('Ş', 's')
                .replace('ö', 'o')
                .replace('Ö', 'o')
                .replace('ç', 'c')
                .replace('Ç', 'c');

        String normalized = Normalizer.normalize(turkishReplaced, Normalizer.Form.NFD);

        return normalized.replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9]", "")
                .toLowerCase(Locale.ENGLISH);
    }
}
