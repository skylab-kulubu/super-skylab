package com.skylab.superapp.core.identity;

import com.skylab.superapp.core.identity.ldap.LdapService;
import com.skylab.superapp.dataAccess.UserDao;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.text.Normalizer;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserIdentityGenerator {

    private final UserDao userDao;

    public synchronized String generateNextSkyNumber() {
        log.debug("Fetching maximum sky number from database.");
        Integer maxNumber = userDao.findMaxSkyNumberValue();

        long nextNumber = (maxNumber != null ? maxNumber : 0) + 1;

        if (nextNumber > 9999999) {
            log.error("Sky number limit reached. Current Max: {}", maxNumber);
            throw new RuntimeException("Max sky number reached");
        }

        String skyNumber = String.format("SKY-%07d", nextNumber);
        log.info("Generated next sky number: {}", skyNumber);
        return skyNumber;
    }

    public String generateLdapUsername(String firstName, String lastName, LdapService ldapService) {
        log.debug("Generating LDAP username for: {} {}", firstName, lastName);

        String cleanFirstName = normalize(firstName);
        String cleanLastName = normalize(lastName);

        String baseUsername = cleanFirstName + "." + cleanLastName;
        String finalUsername = baseUsername;
        int counter = 1;

        while (ldapService.usernameExists(finalUsername)) {
            log.warn("LDAP username collision detected: {}. Retrying with suffix.", finalUsername);
            finalUsername = baseUsername + counter;
            counter++;
        }

        log.info("LDAP username generated successfully: {}", finalUsername);
        return finalUsername;
    }

    private String normalize(String text) {
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