package com.skylab.superapp.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "opa")
@Getter
@Setter
public class OpaProperties {
    private String url;
}
