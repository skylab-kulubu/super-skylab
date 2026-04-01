package com.skylab.superapp.core.utilities.sync;

import com.skylab.superapp.core.identity.keycloak.KeycloakAdminService;
import com.skylab.superapp.core.utilities.microsoftGraph.MicrosoftGraphService;
import com.skylab.superapp.dataAccess.UserDao;
import com.skylab.superapp.entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class UserSyncService {

    private final Logger logger = LoggerFactory.getLogger(UserSyncService.class);
    private final KeycloakAdminService keycloakAdminService;
    private final MicrosoftGraphService microsoftGraphService;
    private final UserDao userDao;

    public UserSyncService(KeycloakAdminService keycloakAdminService,
                           MicrosoftGraphService microsoftGraphService,
                           UserDao userDao) {
        this.keycloakAdminService = keycloakAdminService;
        this.microsoftGraphService = microsoftGraphService;
        this.userDao = userDao;
    }

    @Async
    @Transactional
    public void syncExternalApisAsync(UUID userId, String jwtToken, String skyNumber) {
        logger.info("Syncing user {} with external APIs in background...", userId);

        try {
            keycloakAdminService.updateUserAttribute(userId, "skyNumber", skyNumber);
            logger.info("skyNumber '{}' added to e-skylab", skyNumber);

            String msToken = keycloakAdminService.getObsBrokerToken(jwtToken);
            if (msToken != null) {
                String userDepartment = microsoftGraphService.fetchUserDepartment(msToken);

                if (userDepartment != null && !userDepartment.isBlank()) {
                    keycloakAdminService.updateUserAttribute(userId, "department", userDepartment);
                    logger.info("Department has been updated in Keycloak for user {}: {}", userId, userDepartment);

                    User user = userDao.findById(userId).orElse(null);
                    if (user != null) {
                        user.setDepartment(userDepartment);
                        userDao.save(user);
                        logger.info("Department {} saved to database for user {}", userDepartment, userId);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error syncing user {} with external APIs: {}", userId, e.getMessage());
        }
    }
}