package com.reliaquest.api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Spring configuration class for application-wide beans.
 */
@Configuration
public class AppConfig {

    /**
     * Creates a RestClient bean for making HTTP requests.
     *
     * @return a RestClient instance managed by Spring
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder().baseUrl("http://localhost:8112/api/v1").build();
    }
}
