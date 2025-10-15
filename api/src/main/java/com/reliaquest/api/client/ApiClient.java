package com.reliaquest.api.client;

import com.reliaquest.api.dto.ApiResponse;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.exception.ApiClientException;
import com.reliaquest.api.exception.RateLimitException;
import com.reliaquest.api.exception.ResourceNotFoundException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    @Cacheable(value = "employees", unless = "#result == null || #result.isEmpty()")
    @Retry(name = "employeeAPI")
    @CircuitBreaker(name = "employeeAPI")
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
                            throw new RateLimitException("Rate limit exceeded while fetching employees. Please try again later.");
                        } else if (response.getStatusCode().value() == 404) {
                            logger.warn("Employees endpoint not found");
                            throw new ResourceNotFoundException("Employee endpoint not found");
                        }
                        logger.error("Client error while fetching employees: {}", response.getStatusCode());
                        throw new ApiClientException("Failed to fetch employees: HTTP " + response.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        logger.error("Server error while fetching employees: {}", response.getStatusCode());
                        throw new ApiClientException("External API server error while fetching employees: " + response.getStatusCode());
                    })
                    .body(responseType);

            if (apiResponse == null || apiResponse.getData() == null) {
                logger.error("Received empty or invalid API response while fetching employees");
                throw new ApiClientException("No employee data received from external API");
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

    @CacheEvict(value = "employees", allEntries = true)
    @Retry(name = "employeeAPI")
    @CircuitBreaker(name = "employeeAPI")
    public Employee createEmployee(final CreateEmployeeRequest employeeRequest) {
        logger.info("Creating employee in mock API: {}", employeeRequest.getName());

        try {
            final ParameterizedTypeReference<ApiResponse<Employee>> responseType =
                    new ParameterizedTypeReference<>() {};

            ApiResponse<Employee> apiResponse = restClient
                    .post()
                    .body(employeeRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode().value() == 429) {
                            logger.warn("Rate limit exceeded while creating employee: {}", employeeRequest.getName());
                            throw new RateLimitException("Rate limit exceeded while creating employee. Please try again later.");
                        } else if (response.getStatusCode().value() == 400) {
                            logger.warn("Bad request while creating employee: {}", employeeRequest.getName());
                            throw new ApiClientException("Invalid employee data provided");
                        }
                        logger.error("Client error while creating employee {}: {}", employeeRequest.getName(), response.getStatusCode());
                        throw new ApiClientException("Failed to create employee: HTTP " + response.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        logger.error("Server error while creating employee {}: {}", employeeRequest.getName(), response.getStatusCode());
                        throw new ApiClientException("External API server error while creating employee: " + response.getStatusCode());
                    })
                    .body(responseType);

            if (apiResponse == null || apiResponse.getData() == null) {
                logger.error("Received empty or invalid API response while creating employee: {}", employeeRequest.getName());
                throw new ApiClientException("No employee data received after creation");
            }

            logger.debug("Successfully created employee with id: {}", apiResponse.getData().getId());
            return apiResponse.getData();

        } catch (Exception e) {
            if (e instanceof ApiClientException
                    || e instanceof RateLimitException
                    || e instanceof ResourceNotFoundException) {
                throw e;
            }
            logger.error("Error communicating with external API while creating employee {}: {}", employeeRequest.getName(), e.getMessage(), e);
            throw new ApiClientException("Failed to communicate with external API", e);
        }
    }

    @CacheEvict(value = "employees", allEntries = true)
    @Retry(name = "employeeAPI")
    @CircuitBreaker(name = "employeeAPI")
    public boolean deleteEmployee(final String name) {
        logger.info("Deleting employee from mock API: {}", name);

        try {
            final ParameterizedTypeReference<ApiResponse<Boolean>> responseType =
                    new ParameterizedTypeReference<>() {};

            // Create request body matching DeleteMockEmployeeInput
            var deleteRequest = new java.util.HashMap<String, String>();
            deleteRequest.put("name", name);

            ApiResponse<Boolean> apiResponse = restClient
                    .method(org.springframework.http.HttpMethod.DELETE)
                    .body(deleteRequest)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode().value() == 429) {
                            logger.warn("Rate limit exceeded while deleting employee: {}", name);
                            throw new RateLimitException("Rate limit exceeded while deleting employee. Please try again later.");
                        } else if (response.getStatusCode().value() == 404) {
                            logger.warn("Employee not found for deletion: {}", name);
                            throw new ResourceNotFoundException("Employee '" + name + "' not found");
                        }
                        logger.error("Client error while deleting employee {}: {}", name, response.getStatusCode());
                        throw new ApiClientException("Failed to delete employee: HTTP " + response.getStatusCode());
                    })
                    .onStatus(HttpStatusCode::is5xxServerError, (request, response) -> {
                        logger.error("Server error while deleting employee {}: {}", name, response.getStatusCode());
                        throw new ApiClientException("External API server error while deleting employee: " + response.getStatusCode());
                    })
                    .body(responseType);

            if (apiResponse == null || apiResponse.getData() == null) {
                logger.error("Received empty or invalid API response while deleting employee: {}", name);
                throw new ApiClientException("No confirmation received from delete operation");
            }

            logger.debug("Successfully deleted employee: {}", name);
            return apiResponse.getData();

        } catch (Exception e) {
            if (e instanceof ApiClientException
                    || e instanceof RateLimitException
                    || e instanceof ResourceNotFoundException) {
                throw e;
            }
            logger.error("Error communicating with external API while deleting employee {}: {}", name, e.getMessage(), e);
            throw new ApiClientException("Failed to communicate with external API", e);
        }
    }
}
