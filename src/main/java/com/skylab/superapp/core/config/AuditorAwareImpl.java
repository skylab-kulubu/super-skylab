package com.skylab.superapp.core.config;

import com.skylab.superapp.dataAccess.UserDao;
import com.skylab.superapp.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuditorAwareImpl implements AuditorAware<User> {

    private final UserDao userDao;

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal().equals("anonymousUser")) {
            log.debug("Auditing skipped: No authenticated user found in SecurityContext.");
            return Optional.empty();
        }

        try {
            Jwt jwt = (Jwt) authentication.getPrincipal();
            UUID userId = UUID.fromString(jwt.getSubject());

            log.debug("Auditor identification initiated. UserId: {}", userId);

            Optional<User> user = userDao.findById(userId);
            if (user.isEmpty()) {
                log.warn("Auditor not found in database. UserId: {}", userId);
            }

            return user;
        } catch (Exception e) {
            log.error("Error occurred while identifying auditor. ErrorMessage: {}", e.getMessage());
            return Optional.empty();
        }
    }
}