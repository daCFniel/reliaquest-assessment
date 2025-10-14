package com.reliaquest.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Spring configuration class for application-wide beans.
 */
@Configuration
public class AppConfig {

    @Value("${api.mock.base-url}")
    private String mockApiBaseUrl;

    /**
     * Creates a RestClient bean for making HTTP requests to the mock employee API.
     *
     * @return a RestClient instance configured with the base URL
     */
    @Bean
    public RestClient restClient() {
        return RestClient.builder().baseUrl(mockApiBaseUrl).build();
    }
}
