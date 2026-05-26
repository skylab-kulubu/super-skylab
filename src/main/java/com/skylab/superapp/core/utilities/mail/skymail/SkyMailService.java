package com.skylab.superapp.core.utilities.mail.skymail;

import com.skylab.superapp.core.properties.SkyMailProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Slf4j
@Service
public class SkyMailService {

    private final RestTemplate restTemplate;
    private final SkyMailProperties skyMailProperties;
    private final SkyMailTokenProvider tokenProvider;

    private static final String SKYMAIL_BASE_URL = "http://skymail/v1";

    public SkyMailService(@Qualifier("loadBalancedRestTemplate") RestTemplate restTemplate,
                          SkyMailProperties skyMailProperties,
                          SkyMailTokenProvider tokenProvider) {
        this.restTemplate = restTemplate;
        this.skyMailProperties = skyMailProperties;
        this.tokenProvider = tokenProvider;
    }

    public void send(String templateName, String recipientEmail, String recipientFullName, Map<String, Object> bodyVariables) {
        try {
            String templateId = skyMailProperties.getTemplates().get(templateName);

            if (templateId == null) {
                log.warn("SkyMail: Template not configured, skipping mail. TemplateName: {}, Recipient: {}", templateName, recipientEmail);
                return;
            }

            SendSingleMailRequest request = SendSingleMailRequest.builder()
                    .templateId(templateId)
                    .recipientEmail(recipientEmail)
                    .recipientFullName(recipientFullName)
                    .bodyVariables(bodyVariables)
                    .build();

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(tokenProvider.getToken());
            headers.setContentType(MediaType.APPLICATION_JSON);

            restTemplate.postForEntity(
                    SKYMAIL_BASE_URL + "/mail_tasks/single",
                    new HttpEntity<>(request, headers),
                    Map.class
            );

            log.info("SkyMail: Email sent successfully. TemplateName: {}, Recipient: {}", templateName, recipientEmail);
        } catch (Exception e) {
            log.error("SkyMail: Failed to send email. TemplateName: {}, Recipient: {}, Error: {}", templateName, recipientEmail, e.getMessage(), e);
        }
    }
}
