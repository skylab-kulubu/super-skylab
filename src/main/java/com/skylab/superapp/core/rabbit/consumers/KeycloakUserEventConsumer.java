package com.skylab.superapp.core.rabbit.consumers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.skylab.superapp.core.properties.KeycloakProperties;
import com.skylab.superapp.dataAccess.UserDao;
import com.skylab.superapp.entities.User;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class KeycloakUserEventConsumer {

    private final Logger logger = LoggerFactory.getLogger(KeycloakUserEventConsumer.class);
    private final UserDao userDao;
    private final ObjectMapper objectMapper;
    private final KeycloakProperties keycloakProperties;


    public KeycloakUserEventConsumer(UserDao userDao, ObjectMapper objectMapper, KeycloakProperties keycloakProperties) {
        this.userDao = userDao;
        this.objectMapper = objectMapper;
        this.keycloakProperties = keycloakProperties;
    }


    @RabbitListener(queues = "skylab.user.events", containerFactory = "keycloakListenerContainerFactory")
    @Transactional
    public void handleKeycloakUserEvent(Message message) {
        try {

            String messagePayload = new String(message.getBody());

            JsonNode rootNode = objectMapper.readTree(messagePayload);

            String resourceType = rootNode.path("resourceType").asText();
            String operationType = rootNode.path("operationType").asText();

            if (!"USER".equals(resourceType) || !"UPDATE".equals(operationType)) {
                return;
            }

            JsonNode authDetails = rootNode.path("authDetails");
            String clientId = authDetails.path("clientId").asText();

            if (keycloakProperties.getClientId().equals(clientId)) {
                logger.debug("Ignore the event from Keycloak for user update because the event is triggered by our own application. Client ID: {}", clientId);
                return;
            }

            String representationStr = rootNode.path("representation").asText();
            JsonNode representationNode = objectMapper.readTree(representationStr);

            String userIdStr = representationNode.path("id").asText();
            UUID userId = UUID.fromString(userIdStr);


            User localUser = userDao.findById(userId).orElse(null);
            if (localUser == null) {
                logger.warn("Couldnt find local user with ID {} for Keycloak update event. Skipping synchronization.", userId);
                return;
            }

            boolean isUpdated = false;

            String newFirstName = representationNode.path("firstName").asText();
            if (!newFirstName.equals(localUser.getFirstName()) && !newFirstName.isEmpty()) {
                localUser.setFirstName(newFirstName);
                isUpdated = true;
            }

            String newLastName = representationNode.path("lastName").asText();
            if (!newLastName.equals(localUser.getLastName()) && !newLastName.isEmpty()) {
                localUser.setLastName(newLastName);
                isUpdated = true;
            }

            JsonNode attributesNode = representationNode.path("attributes");

            String newDepartment = getAttributeSafe(attributesNode, "department");
            if (!newDepartment.isEmpty() && !newDepartment.equals(localUser.getDepartment())) {
                localUser.setDepartment(newDepartment);
                isUpdated = true;
            }

            String newUniversity = getAttributeSafe(attributesNode, "university");
            if (!newUniversity.isEmpty() && !newUniversity.equals(localUser.getUniversity())) {
                localUser.setUniversity(newUniversity);
                isUpdated = true;
            }

            String newFaculty = getAttributeSafe(attributesNode, "faculty");
            if (!newFaculty.isEmpty() && !newFaculty.equals(localUser.getFaculty())) {
                localUser.setFaculty(newFaculty);
                isUpdated = true;
            }


            if (isUpdated) {
                userDao.save(localUser);
                logger.info("User {} updated in local database based on Keycloak event", userId);
            }

        } catch (Exception e) {
            logger.error("Error processing Keycloak user event from RabbitMQ: {}", e.getMessage());
        }
    }



    private String getAttributeSafe(JsonNode attributesNode, String attributeName) {
        if(attributesNode != null && !attributesNode.isMissingNode()) {
            JsonNode targetNode = attributesNode.path(attributeName);
            if (targetNode.isArray() && !targetNode.isEmpty()) {
                return targetNode.get(0).asText();
            }
        }
        return "";
    }


}
