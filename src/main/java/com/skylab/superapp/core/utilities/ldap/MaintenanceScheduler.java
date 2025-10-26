package com.skylab.superapp.core.utilities.ldap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MaintenanceScheduler {

    private final LdapService ldapService;
    private final Logger logger = LoggerFactory.getLogger(MaintenanceScheduler.class);

    public MaintenanceScheduler(LdapService ldapService) {
        this.ldapService = ldapService;
    }


    @Scheduled(cron = "0 0 3 * * *")
    public void cleanupDanglingReferences(){
        logger.info("Starting cleanup of dangling references...");
        ldapService.cleanupDanglingReferences();
        logger.info("Cleanup of dangling references completed.");
    }
}
