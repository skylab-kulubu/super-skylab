package com.skylab.superapp.core.security.keycloak;

import com.skylab.superapp.entities.DTOs.Auth.AuthRegisterRequest;
import lombok.RequiredArgsConstructor;
import org.apache.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final WebClient.Builder webClientBuilder;

    private final String keycloakBaseUrl = "http://keycloak:8080";
    private final String realm = "e-skylab";
    private final String clientId = "super-skylab";
    private final String clientSecret = "gXnpuKIPYysRPW4elisunkfMZh2GoCCm";



    public boolean createUser(AuthRegisterRequest registerRequest) {
        String token = getAdminAccessToken();
        if (token == null){
            return false;
        }

       WebClient webClient = webClientBuilder.baseUrl(keycloakBaseUrl).build();

        Map<String, Object> userPayload = Map.of(
                "username", registerRequest.getUsername(),
                "email", registerRequest.getEmail(),
                "firstName", registerRequest.getFirstName(),
                "lastName", registerRequest.getLastName(),
                "enabled", true,
                "credentials", new Object[]{
                        Map.of(
                                "type", "password",
                                "value", registerRequest.getPassword(),
                                "temporary", false
                        )
                }
        );

        try{
            webClient.post()
                    .uri("/admin/realms/{realm}/users", realm)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .body(Mono.just(userPayload), Map.class)
                    .retrieve()
                    .toBodilessEntity()
                    .block();

            return true;
        }catch (Exception e){
            return false;
        }
    }


    private String getAdminAccessToken() {
        WebClient webClient = webClientBuilder.baseUrl(keycloakBaseUrl).build();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);

        return webClient.post()
                .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromFormData(formData))
                .retrieve()
                .bodyToMono(Map.class)
                .map(response -> (String) response.get("access_token"))
                .block();
    }
}
