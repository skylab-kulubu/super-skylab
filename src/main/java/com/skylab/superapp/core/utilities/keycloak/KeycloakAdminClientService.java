package com.skylab.superapp.core.utilities.keycloak;

import com.skylab.superapp.core.exceptions.KeycloakException;
import com.skylab.superapp.core.utilities.keycloak.dtos.UserKeycloakRequest;
import com.skylab.superapp.core.utilities.keycloak.dtos.UserUpdateKeycloakRequest;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service

public class KeycloakAdminClientService {

    private final Keycloak keycloak;

    @Value("${keycloak.realm}")
    private String realm;

    private final Logger logger = LoggerFactory.getLogger(KeycloakAdminClientService.class);


    public KeycloakAdminClientService(Keycloak keycloak) {
        this.keycloak = keycloak;
    }


    public String createUser(UserKeycloakRequest userKeycloakRequest){
        logger.info("Creating user in Keycloak for username: {}", userKeycloakRequest.getUsername());
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setUsername(userKeycloakRequest.getUsername());
        userRepresentation.setEmail(userKeycloakRequest.getEmail());
        userRepresentation.setFirstName(userKeycloakRequest.getFirstName());
        userRepresentation.setLastName(userKeycloakRequest.getLastName());

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setTemporary(false);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);
        credentialRepresentation.setValue(userKeycloakRequest.getPassword());
        userRepresentation.setCredentials(java.util.Collections.singletonList(credentialRepresentation));


        UsersResource usersResource = keycloak.realm(realm).users();
        try(Response response = usersResource.create(userRepresentation)) {

            if (response.getStatus() == 201){
                String location = response.getLocation().toString();
                String userId = location.substring(location.lastIndexOf('/') + 1);
                logger.info("User created in Keycloak. ID: {}", userId);


                try{
                    logger.info("Assigning default 'user' role to user ID: {}", userId);
                    assignRealmRole(userId, KeycloakRole.USER);
                    logger.info("User successfully created and role assigned: userId={}", userId);
                }catch (KeycloakException e){
                    logger.error("User was created (id: {}) but failed to assign role: {}", userId, e.getMessage());

                    throw new KeycloakException("User created but role assignment failed: " + e.getMessage());
                }

                return userId;


            }else {
                String errorMessage = response.readEntity(String.class);
                logger.error("Failed to create user in Keycloak: {}", errorMessage);
                throw new KeycloakException("User creation failed: " + errorMessage);
            }

        }
    }

    public void assignRealmRole(String userId, KeycloakRole keycloakRole) {

        try{
            UserResource usersResource = keycloak.realm(realm).users().get(userId);

            RoleRepresentation realmRole = keycloak.realm(realm).roles().get(keycloakRole.getRoleName()).toRepresentation();

            usersResource.roles().realmLevel().add(Collections.singletonList(realmRole));
            logger.info("Role '{}' successfully assigned to user '{}'", keycloakRole.getRoleName(), userId);
        }catch (NotFoundException e){
            try{
                keycloak.realm(realm).users().get(userId);
            }catch (NotFoundException userNotFound){
                logger.warn("Failed to assign role. User not found with ID: {}", userId);
                throw new KeycloakException("Cannot assign role, user not found with ID: " + userId);
            }
            logger.warn("Failed to assign role. Role not found with name: {}", keycloakRole.getRoleName());
            throw new KeycloakException("Cannot assign role, role not found with name: " + keycloakRole.getRoleName());
        }

    }


    public void updateUser(String keycloakUserId, UserUpdateKeycloakRequest userKeycloakRequest){
        try{
            UserResource userResource = keycloak.realm(realm).users().get(keycloakUserId);
            UserRepresentation user = userResource.toRepresentation();


            if (userKeycloakRequest.getUsername() != null && !userKeycloakRequest.getUsername().isBlank()){
                logger.info("Updating username for user with ID: {}", keycloakUserId);
                user.setUsername(userKeycloakRequest.getUsername());
            }

            if (userKeycloakRequest.getEmail() != null && !userKeycloakRequest.getEmail().isBlank()){
                logger.info("Updating email for user with ID: {}", keycloakUserId);
                user.setEmail(userKeycloakRequest.getEmail());
            }

            if (userKeycloakRequest.getFirstName() != null && !userKeycloakRequest.getFirstName().isBlank()){
                logger.info("Updating firstName for user with ID: {}", keycloakUserId);
                user.setFirstName(userKeycloakRequest.getFirstName());
            }

            if (userKeycloakRequest.getLastName() != null && !userKeycloakRequest.getLastName().isBlank()){
                logger.info("Updating lastName for user with ID: {}", keycloakUserId);
                user.setLastName(userKeycloakRequest.getLastName());
            }

            userResource.update(user);
            logger.info("User updated in Keycloak. ID: {}", keycloakUserId);
        }catch (NotFoundException e){
            logger.warn("Attempted to update non-existent user in Keycloak. ID: {}", keycloakUserId);
            throw new KeycloakException("User not found with ID: " + keycloakUserId);
        }catch (ClientErrorException e){

            if (e.getResponse().getStatus() == 409){
                logger.warn("Attempted to update user with conflicting email or username in Keycloak. ID: {}", keycloakUserId);
                throw new KeycloakException("Update failed due to conflict: " + e.getMessage());
            }

            throw new KeycloakException("Username or email update failed: " + e.getMessage());

        }

    }

    public void deleteUser(String keycloakUserId){
        try{

            UserResource userResource = keycloak.realm(realm).users().get(keycloakUserId);
            userResource.remove();
            logger.info("User deleted from Keycloak. ID: {}", keycloakUserId);

        }catch (NotFoundException e){
            logger.warn("Attempted to delete non-existent user in Keycloak. ID: {}", keycloakUserId);
            throw new KeycloakException("User not found with ID: " + keycloakUserId);
        }
    }

    public List<String> getUserRoles(String userId){
        try{
            List<RoleRepresentation> roles = keycloak.realm(realm).users().get(userId).roles().realmLevel().listAll();

            return roles.stream()
                    .map(RoleRepresentation::getName)
                    .collect(Collectors.toList());
        }catch (NotFoundException e){
            logger.warn("Attempted to fetch roles for non-existent user in Keycloak. ID: {}", userId);
            throw new KeycloakException("User not found with ID: " + userId);
        }

    }

    public void resetPassword(String keycloakUserId, String newPassword){
        logger.info("Resetting password for user with ID: {}", keycloakUserId);

        try{
            UserResource userResource = keycloak.realm(realm).users().get(keycloakUserId);

            CredentialRepresentation credential = new CredentialRepresentation();
            credential.setTemporary(false);
            credential.setType(CredentialRepresentation.PASSWORD);
            credential.setValue(newPassword);

            userResource.resetPassword(credential);

            logger.info("Password reset successfully for user with ID: {}", keycloakUserId);
        }catch (NotFoundException e){
            logger.warn("Attempted to reset password for non-existent user in Keycloak. ID: {}", keycloakUserId);
            throw new KeycloakException("User not found with ID: " + keycloakUserId);
        }
    }


}
