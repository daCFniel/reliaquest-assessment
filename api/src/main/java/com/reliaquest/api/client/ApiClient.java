package com.reliaquest.api.client;

import com.reliaquest.api.dto.ApiResponse;
import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.exception.ApiClientException;
import com.reliaquest.api.exception.RateLimitException;
import com.reliaquest.api.exception.ResourceNotFoundException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Client for communicating with the mock employee API.
 */
@Component
public class ApiClient {

    private static final Logger logger = LoggerFactory.getLogger(ApiClient.class);

    private final RestClient restClient;

    public ApiClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    public List<Employee> fetchAllEmployees() {
        logger.info("Fetching all employees from mock API");

        try {
            final ParameterizedTypeReference<ApiResponse<List<Employee>>> responseType =
                    new ParameterizedTypeReference<>() {};

            ApiResponse<List<Employee>> apiResponse = restClient
                    .get()
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode().value() == 429) {
                            logger.warn("Rate limit exceeded while fetching employees");
                            throw new RateLimitException("Rate limit exceeded. Please try again later.");
                        } else if (response.getStatusCode().value() == 404) {
                            logger.warn("Employees endpoint not found");
                            throw new ResourceNotFoundException("Employees not found");
                        }
                        logger.error("Client error while fetching employees: {}", response.getStatusCode());
                        throw new ApiClientException("Client error: " + response.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        logger.error("Server error while fetching employees: {}", response.getStatusCode());
                        throw new ApiClientException("Server error from external API: " + response.getStatusCode());
                    })
                    .body(responseType);

            if (apiResponse == null || apiResponse.getData() == null) {
                logger.error("Received empty or invalid API response");
                throw new ApiClientException("Empty or invalid API response");
            }

            logger.debug(
                    "Successfully fetched {} employees", apiResponse.getData().size());
            return apiResponse.getData();

        } catch (Exception e) {
            if (e instanceof ApiClientException
                    || e instanceof RateLimitException
                    || e instanceof ResourceNotFoundException) {
                throw e;
            }
            logger.error("Error communicating with external API: {}", e.getMessage(), e);
            throw new ApiClientException("Failed to communicate with external API", e);
        }
    }

    public Employee createEmployee(final Employee employee) {
        // TODO: implement
        return null;
    }

    public boolean deleteEmployee(final String id) {
        return false;
    }
}
