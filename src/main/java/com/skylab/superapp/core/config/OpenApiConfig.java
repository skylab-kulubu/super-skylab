package com.skylab.superapp.core.config;


import com.skylab.superapp.core.properties.KeycloakProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class OpenApiConfig {


    private final KeycloakProperties keycloakProperties;

    @Bean
    public OpenAPI customOpenAPI() {
        String authUrl = keycloakProperties.getExternalUrl() + "/realms/" + keycloakProperties.getRealm() + "/protocol/openid-connect/auth";
        String tokenUrl = keycloakProperties.getExternalUrl() + "/realms/" + keycloakProperties.getRealm() + "/protocol/openid-connect/token";

        return new OpenAPI()
                .info(new io.swagger.v3.oas.models.info.Info()
                        .title("Super Skylab API")
                        .version("1.0")
                        .description("Super Skylab Service API"))
                .components(new Components()
                        .addSecuritySchemes("oauth2", new SecurityScheme()
                                .type(SecurityScheme.Type.OAUTH2)
                                .flows(new OAuthFlows()
                                        .authorizationCode(new OAuthFlow()
                                                .authorizationUrl(authUrl)
                                                .tokenUrl(tokenUrl)
                                                .scopes(new io.swagger.v3.oas.models.security.Scopes()
                                                        .addString("openid", "OpenID Connect")
                                                        .addString("profile", "Profile information")
                                                        .addString("email", "Email address")
                                                )
                                        )
                                )
                        )
                )
                .addSecurityItem(new SecurityRequirement().addList("oauth2"));
    }
}