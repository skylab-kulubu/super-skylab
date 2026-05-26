package com.skylab.superapp.core.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "skymail")
@Getter
@Setter
public class SkyMailProperties {

    private Map<String, String> templates = new HashMap<>();
}
