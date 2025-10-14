package com.reliaquest.api.client;

import com.reliaquest.api.dto.ApiResponse;
import com.reliaquest.api.dto.Employee;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

/**
 * Client for communicating with the mock employee API.
 */
@Component
public class ApiClient {

    private final RestClient restClient;

    public ApiClient(final RestClient restClient) {
        this.restClient = restClient;
    }

    public List<Employee> fetchAllEmployees() {
        final ParameterizedTypeReference<ApiResponse<List<Employee>>> responseType = new ParameterizedTypeReference<>() {
        };

        final var apiResponse = restClient.get()
                .uri("/employee")
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new IllegalStateException("Failed to fetch employees: " + res.getStatusCode());
                })
                .body(responseType);

        if (apiResponse == null || apiResponse.getData() == null) {
            throw new IllegalStateException("Empty or invalid API response");
        }

        return apiResponse.getData();
    }

    public Employee createEmployee(final Employee employee) {
        // TODO: implement
        return null;
    }

    public boolean deleteEmployee(final String id) {
        return false;
    }
}
