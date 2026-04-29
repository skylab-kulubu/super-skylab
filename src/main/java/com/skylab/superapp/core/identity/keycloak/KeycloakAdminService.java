package com.skylab.superapp.core.identity.keycloak;

import com.skylab.superapp.core.properties.KeycloakProperties;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class KeycloakAdminService {

    private final Keycloak keycloak;
    private final KeycloakProperties keycloakProperties;
    private final JdbcTemplate keycloakJdbc;

    public KeycloakAdminService(Keycloak keycloak,
                                KeycloakProperties keycloakProperties,
                                @Qualifier("keycloakJdbcTemplate") JdbcTemplate keycloakJdbc) {
        this.keycloak = keycloak;
        this.keycloakProperties = keycloakProperties;
        this.keycloakJdbc = keycloakJdbc;
    }

    public void updateUserFullName(UUID userId, String firstName, String lastName) {
        log.info("Updating user full name in Keycloak. UserId: {}, NewName: {} {}", userId, firstName, lastName);

        try {
            UserResource userResource = keycloak.realm(keycloakProperties.getRealm()).users().get(userId.toString());
            UserRepresentation user = userResource.toRepresentation();

            user.setFirstName(firstName);
            user.setLastName(lastName);

            userResource.update(user);
            log.info("Successfully updated user full name in Keycloak. UserId: {}", userId);

        } catch (Exception e) {
            log.error("Failed to update user full name in Keycloak. UserId: {}, Error: {}", userId, e.getMessage());
            throw new RuntimeException("An error occurred while updating user full name!");
        }
    }

    public void linkUserToLdap(UUID userId, String generatedLdapUsername) {
        log.info("Linking Keycloak user to LDAP via DB. UserId: {}, LdapUsername: {}", userId, generatedLdapUsername);

        String providerId = keycloakProperties.getLdapProviderId();
        String ldapDn = "uid=" + generatedLdapUsername + ",ou=people,dc=yildizskylab,dc=com";
        String now = String.valueOf(System.currentTimeMillis());

        try {
            log.debug("Cleaning existing LDAP attributes for UserId: {}", userId);
            keycloakJdbc.update("DELETE FROM user_attribute WHERE user_id = ? AND name IN ('LDAP_ID', 'LDAP_ENTRY_DN', 'createTimestamp', 'modifyTimestamp')", userId.toString());

            insertAttribute(userId, "LDAP_ID", generatedLdapUsername);
            insertAttribute(userId, "LDAP_ENTRY_DN", ldapDn);
            insertAttribute(userId, "createTimestamp", now);
            insertAttribute(userId, "modifyTimestamp", now);

            log.debug("Updating federation link in user_entity. UserId: {}, ProviderId: {}", userId, providerId);
            keycloakJdbc.update("UPDATE user_entity SET federation_link = ?, username = ? WHERE id = ?",
                    providerId, generatedLdapUsername, userId.toString());

            try {
                keycloakJdbc.update("INSERT INTO federated_user (id, storage_provider_id, realm_id) SELECT id, ?, realm_id FROM user_entity WHERE id = ?",
                        providerId, userId.toString());
            } catch (Exception ignored) {
                log.debug("User already exists in federated_user table. UserId: {}", userId);
            }

            log.info("Successfully linked user to LDAP. UserId: {}", userId);

        } catch (Exception e) {
            log.error("Database error while linking user to LDAP. UserId: {}, Error: {}", userId, e.getMessage());
            throw new RuntimeException("Exception while trying to link user: ", e);
        }
    }

    private void insertAttribute(UUID userId, String name, String value) {
        keycloakJdbc.update("INSERT INTO user_attribute (id, name, value, user_id) VALUES (?, ?, ?, ?)",
                UUID.randomUUID().toString(), name, value, userId.toString());
    }

    public void deleteUser(UUID id) {
        log.info("Initiating Keycloak user deletion. UserId: {}", id);

        try {
            Response response = keycloak.realm(keycloakProperties.getRealm()).users().delete(id.toString());
            int status = response.getStatus();

            if (status >= 200 && status < 300) {
                log.info("User deleted from Keycloak successfully. UserId: {}", id);
            } else if (status == 404) {
                log.warn("User not found in Keycloak during deletion. UserId: {}", id);
            } else {
                log.error("Failed to delete user from Keycloak. UserId: {}, HttpStatus: {}", id, status);
                throw new RuntimeException("Error deleting user from Keycloak! Status: " + status);
            }
            response.close();
        } catch (Exception e) {
            log.error("Unexpected error during Keycloak user deletion. UserId: {}, Error: {}", id, e.getMessage());
            throw new RuntimeException("An error occurred while deleting user from Keycloak!");
        }
    }

    public Set<UUID> getUserIdsByRoleName(String roleName) {
        log.debug("Retrieving user IDs for role: {}", roleName);
        Set<UUID> allUserIds = new HashSet<>();

        try {
            Set<String> rolesToQuery = new HashSet<>();
            rolesToQuery.add(roleName);

            List<RoleRepresentation> allRealmRoles = keycloak.realm(keycloakProperties.getRealm()).roles().list();

            boolean addedNew;
            do {
                addedNew = false;
                for (RoleRepresentation role : allRealmRoles) {
                    if (Boolean.TRUE.equals(role.isComposite()) && !rolesToQuery.contains(role.getName())) {
                        Set<RoleRepresentation> composites = keycloak.realm(keycloakProperties.getRealm())
                                .roles().get(role.getName()).getRoleComposites();

                        boolean containsTarget = composites.stream().anyMatch(c -> rolesToQuery.contains(c.getName()));
                        if (containsTarget) {
                            rolesToQuery.add(role.getName());
                            addedNew = true;
                        }
                    }
                }
            } while (addedNew);

            log.debug("Roles hierarchy resolved for '{}': {}", roleName, rolesToQuery);

            for (String queryRole : rolesToQuery) {
                allUserIds.addAll(getUsersFromRole(queryRole));
            }

            return allUserIds;

        } catch (Exception e) {
            log.error("Error retrieving user IDs for role: {}. Error: {}", roleName, e.getMessage());
            return Collections.emptySet();
        }
    }

    private Set<UUID> getUsersFromRole(String roleName) {
        Set<UUID> ids = new HashSet<>();
        try {
            List<UserRepresentation> members = keycloak.realm(keycloakProperties.getRealm())
                    .roles().get(roleName).getUserMembers();

            members.forEach(u -> ids.add(UUID.fromString(u.getId())));
            return ids;

        } catch (jakarta.ws.rs.NotFoundException e) {
            log.debug("Role '{}' not found in Realm, searching in Clients.", roleName);
            List<org.keycloak.representations.idm.ClientRepresentation> clients =
                    keycloak.realm(keycloakProperties.getRealm()).clients().findAll();

            for (org.keycloak.representations.idm.ClientRepresentation client : clients) {
                try {
                    List<UserRepresentation> members = keycloak.realm(keycloakProperties.getRealm())
                            .clients().get(client.getId())
                            .roles().get(roleName).getUserMembers();

                    members.forEach(u -> ids.add(UUID.fromString(u.getId())));
                    return ids;
                } catch (jakarta.ws.rs.NotFoundException ignored) {}
            }
            log.warn("Role '{}' not found in Realm or any Client!", roleName);
            return ids;
        }
    }

    public void updateUserAttribute(UUID userId, String attributeKey, String attributeValue) {
        log.info("Updating Keycloak user attribute. UserId: {}, Key: {}, Value: {}", userId, attributeKey, attributeValue);

        var userResource = keycloak.realm(keycloakProperties.getRealm()).users().get(userId.toString());
        var user = userResource.toRepresentation();

        Map<String, List<String>> attributes = user.getAttributes();
        if (attributes == null) attributes = new HashMap<>();

        attributes.put(attributeKey, Collections.singletonList(attributeValue));
        user.setAttributes(attributes);
        userResource.update(user);

        log.info("Successfully updated user attribute. UserId: {}, Key: {}", userId, attributeKey);
    }

    public String getObsBrokerToken(String userJwt) {
        log.debug("Requesting OBS broker token from Keycloak.");

        String brokerUrl = keycloakProperties.getServerUrl() +
                "/realms/" + keycloakProperties.getRealm() +
                "/broker/OBS/token";

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(userJwt);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Map> response = restTemplate.exchange(brokerUrl, HttpMethod.GET, entity, Map.class);

            if (response.getBody() != null && response.getBody().containsKey("access_token")) {
                log.info("Successfully retrieved OBS broker token.");
                return (String) response.getBody().get("access_token");
            }
            log.warn("OBS broker token response did not contain access_token.");

        } catch (Exception e) {
            log.error("Failed to retrieve OBS broker token. Error: {}", e.getMessage());
        }
        return null;
    }

    public UserRepresentation getUserById(String userId) {
        log.debug("Fetching Keycloak user by ID: {}", userId);
        try {
            return keycloak.realm(keycloakProperties.getRealm()).users().get(userId).toRepresentation();
        } catch (Exception e) {
            log.error("Failed to fetch user from Keycloak. UserId: {}, Error: {}", userId, e.getMessage());
            return null;
        }
    }
}