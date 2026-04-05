package com.skylab.superapp.core.identity.keycloak;

import com.skylab.superapp.core.properties.KeycloakProperties;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;


@Service
public class KeycloakAdminService {

    private final Keycloak keycloak;
    private final Logger logger = LoggerFactory.getLogger(KeycloakAdminService.class);
    private final KeycloakProperties keycloakProperties;
    private final JdbcTemplate keycloakJdbc;


    public KeycloakAdminService(Keycloak keycloak, KeycloakProperties keycloakProperties, @Qualifier("keycloakJdbcTemplate") JdbcTemplate keycloakJdbc) {
        this.keycloak = keycloak;
        this.keycloakProperties = keycloakProperties;
        this.keycloakJdbc = keycloakJdbc;
    }

    public void updateUserFullName(UUID userId, String firstName, String lastName){
        logger.info("Updating Keycloak user {} full name to {} {}", userId, firstName, lastName);

        try {
            UserResource userResource = keycloak.realm(keycloakProperties.getRealm()).users().get(userId.toString());
            UserRepresentation user = userResource.toRepresentation();

            user.setFirstName(firstName);
            user.setLastName(lastName);

            userResource.update(user);
            logger.info("Successfully updated Keycloak user {} full name to {} {}", userId, firstName, lastName);

        } catch (Exception e){
            logger.error("Error updating Keycloak user {} full name to {} {}", userId, firstName, lastName);
            throw new RuntimeException("An error occured while updating user!");
        }
    }




    public void linkUserToLdap(UUID userId, String generatedLdapUsername) {
        logger.info("Linking Keycloak user {} to LDAP with 'davsum' method via DB", userId);

        String providerId = keycloakProperties.getLdapProviderId();
        String ldapDn = "uid=" + generatedLdapUsername + ",ou=people,dc=yildizskylab,dc=com";
        String now = String.valueOf(System.currentTimeMillis());

        try {
            keycloakJdbc.update("DELETE FROM user_attribute WHERE user_id = ? AND name IN ('LDAP_ID', 'LDAP_ENTRY_DN', 'createTimestamp', 'modifyTimestamp')", userId.toString());

            insertAttribute(userId, "LDAP_ID", generatedLdapUsername);
            insertAttribute(userId, "LDAP_ENTRY_DN", ldapDn);
            insertAttribute(userId, "createTimestamp", now);
            insertAttribute(userId, "modifyTimestamp", now);

            keycloakJdbc.update("UPDATE user_entity SET federation_link = ?, username = ? WHERE id = ?",
                    providerId, generatedLdapUsername, userId.toString());

            try {
                keycloakJdbc.update("INSERT INTO federated_user (id, storage_provider_id, realm_id) SELECT id, ?, realm_id FROM user_entity WHERE id = ?",
                        providerId, userId.toString());
            } catch (Exception ignored) {}

            //LMAO
            logger.info("Successfully linked Keycloak user {} to LDAP.", userId);

        } catch (Exception e) {
            logger.error("DB error: {}", e.getMessage());
            throw new RuntimeException("Exception while trying to link: ", e);
        }
    }

    private void insertAttribute(UUID userId, String name, String value) {
        keycloakJdbc.update("INSERT INTO user_attribute (id, name, value, user_id) VALUES (?, ?, ?, ?)",
                UUID.randomUUID().toString(), name, value, userId.toString());
    }

    public void deleteUser(UUID id) {
        logger.info("Deleting Keycloak user with id {}", id);

        try {
            Response response = keycloak.realm(keycloakProperties.getRealm()).users().delete(id.toString());
            if (response.getStatus() >= 200 && response.getStatus() < 300) {
                logger.info("Successfully deleted user {} from Keycloak", id);
            } else if (response.getStatus() == 404) {
                logger.warn("User {} not found in Keycloak. It might have already been deleted.", id);
            } else {
                logger.error("Failed to delete user {} from Keycloak. HTTP Status: {}", id, response.getStatus());
                throw new RuntimeException("An error occurred while deleting user from Keycloak! HTTP Status: " + response.getStatus());
            }


            response.close();
        }catch (Exception e){
            logger.error("Error deleting Keycloak user with id {}", id, e);
            throw new RuntimeException("An error occured while deleting user from Keycloak!");
        }

        }

    public Set<UUID> getUserIdsByRoleName(String roleName) {
        logger.info("Getting user ids by role name {} (including inherited)", roleName);

        try {
            Set<UUID> allUserIds = new HashSet<>();
            Set<String> rolesToQuery = new HashSet<>();
            rolesToQuery.add(roleName);

            List<RoleRepresentation> allRoles = keycloak.realm(keycloakProperties.getRealm()).roles().list();

            boolean addedNew;
            do {
                addedNew = false;
                for (RoleRepresentation role : allRoles) {
                    if (Boolean.TRUE.equals(role.isComposite()) && !rolesToQuery.contains(role.getName())) {

                        Set<RoleRepresentation> composites = keycloak.realm(keycloakProperties.getRealm())
                                .roles().get(role.getName()).getRoleComposites();

                        boolean containsTarget = composites.stream()
                                .anyMatch(c -> rolesToQuery.contains(c.getName()));

                        if (containsTarget) {
                            rolesToQuery.add(role.getName());
                            addedNew = true;
                        }
                    }
                }
            } while (addedNew);

            logger.info("Target role '{}' is also provided by these inherited roles: {}", roleName, rolesToQuery);

            for (String roleToQuery : rolesToQuery) {
                List<UserRepresentation> members = keycloak.realm(keycloakProperties.getRealm())
                        .roles().get(roleToQuery).getUserMembers();

                for (UserRepresentation user : members) {
                    allUserIds.add(UUID.fromString(user.getId()));
                }
            }

            return allUserIds;

        } catch (Exception e) {
            logger.error("Error getting user ids by role name {}", roleName, e);
            return Collections.emptySet();
        }
    }

    public void updateUserAttribute(UUID userId, String attributeKey, String attributeValue){
        logger.info("Updating Keycloak user {} attribute {} to {}", userId, attributeKey, attributeValue);

        var userResource = keycloak.realm(keycloakProperties.getRealm()).users().get(userId.toString());
        var user = userResource.toRepresentation();

        Map<String, List<String>> attributes = user.getAttributes();
        if (attributes == null){
            attributes = new HashMap<>();
        }

        attributes.put(attributeKey, Collections.singletonList(attributeValue));
        user.setAttributes(attributes);
        userResource.update(user);

        logger.info("Successfully updated Keycloak user {} attribute {} to {}", userId, attributeKey, attributeValue);
    }

    public String getObsBrokerToken(String userJwt) {
        logger.info("Getting OBS broker token from Keycloak for user with JWT");

        String brokerUrl = keycloakProperties.getServerUrl() +
                "/realms/" + keycloakProperties.getRealm() +
                "/broker/OBS/token";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(userJwt);

            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();

            ResponseEntity<Map> response = restTemplate.exchange(
                    brokerUrl,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getBody() != null && response.getBody().containsKey("access_token")) {
                logger.info("Successfully retrieved OBS token from Keycloak broker.");
                return (String) response.getBody().get("access_token");
            }

        } catch (Exception e) {
            logger.error("Error occured, details::", e);
        }

        return null;
    }


    public UserRepresentation getUserById(String userId) {
        try {
            return keycloak.realm(keycloakProperties.getRealm())
                    .users()
                    .get(userId)
                    .toRepresentation();
        } catch (Exception e) {
            logger.error("Error fetching user by ID {} from Keycloak: {}", userId, e.getMessage());
            return null;
        }
    }
}
