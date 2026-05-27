package com.skylab.superapp.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.observation.DefaultServerRequestObservationConvention;
import org.springframework.http.server.observation.ServerRequestObservationContext;


@Configuration
public class ObservabilityConfig {

    @Bean
    public DefaultServerRequestObservationConvention serverSpanNamingConvention() {
        return new DefaultServerRequestObservationConvention() {
            @Override
            public String getContextualName(ServerRequestObservationContext context) {
                String pattern = context.getPathPattern();
                String method = context.getCarrier().getMethod();

                if (pattern != null && !pattern.isEmpty()) {
                    return method + " " + pattern;
                }

                String path = normalizePath(context.getCarrier().getRequestURI());
                return method + " " + path;
            }

            private String normalizePath(String path) {
                if (path == null || path.isEmpty()) {
                    return "/";
                }
                path = path.replaceAll(
                        "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}",
                        "{id}");
                path = path.replaceAll("/\\d+(/|$)", "/{id}$1");
                return path;
            }
        };
    }
}
