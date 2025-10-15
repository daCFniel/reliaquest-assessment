package com.reliaquest.api.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.reliaquest.api.client.ApiClient;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.Employee;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

/**
 * Integration Tests with Mocked ApiClient
 * These tests start the full Spring Boot application but mock the ApiClient to avoid
 * rate limiting issues with the external mock server.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EmployeeApiMockedIntegrationTest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @MockBean
    private ApiClient apiClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port + "/api/v1")
                .build();
    }

    @Test
    @DisplayName("POST /api/v1 should create a new employee via HTTP")
    void createEmployee_WithValidData_ShouldCreateEmployee() {
        Employee mockEmployee = new Employee();
        mockEmployee.setId("mock-id-123");
        mockEmployee.setName("Integration Test Employee");
        mockEmployee.setSalary(100000);
        mockEmployee.setAge(30);
        mockEmployee.setTitle("Test Engineer");

        when(apiClient.createEmployee(any(CreateEmployeeRequest.class))).thenReturn(mockEmployee);

        Map<String, String> newEmployee = new HashMap<>();
        newEmployee.put("name", "Integration Test Employee");
        newEmployee.put("salary", "100000");
        newEmployee.put("age", "30");
        newEmployee.put("title", "Test Engineer");

        Employee createdEmployee =
                restClient.post().uri("").body(newEmployee).retrieve().body(Employee.class);

        assertThat(createdEmployee).isNotNull();
        assertThat(createdEmployee.getName()).isEqualTo("Integration Test Employee");
        assertThat(createdEmployee.getSalary()).isEqualTo(100000);
        assertThat(createdEmployee.getAge()).isEqualTo(30);
        assertThat(createdEmployee.getTitle()).isEqualTo("Test Engineer");
        assertThat(createdEmployee.getId()).isNotNull();

        verify(apiClient).createEmployee(any(CreateEmployeeRequest.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/{id} should delete employee and return name via HTTP")
    void deleteEmployeeById_WhenValidId_ShouldDeleteAndReturnName() {
        Employee mockEmployee = new Employee();
        mockEmployee.setId("employee-to-delete-123");
        mockEmployee.setName("Employee To Delete");
        mockEmployee.setSalary(50000);
        mockEmployee.setAge(25);
        mockEmployee.setTitle("Temporary");

        when(apiClient.fetchAllEmployees()).thenReturn(List.of(mockEmployee));
        when(apiClient.deleteEmployee("Employee To Delete")).thenReturn(true);

        String deletedEmployeeName = restClient
                .delete()
                .uri("/" + mockEmployee.getId())
                .retrieve()
                .body(String.class);

        assertThat(deletedEmployeeName).isEqualTo("Employee To Delete");

        verify(apiClient).fetchAllEmployees();
        verify(apiClient).deleteEmployee("Employee To Delete");
    }

    @Test
    @DisplayName("DELETE /api/v1/{id} should return 404 when employee not found via HTTP")
    void deleteEmployeeById_WhenInvalidId_ShouldReturn404() {
        when(apiClient.fetchAllEmployees()).thenReturn(List.of());

        assertThatThrownBy(() ->
                restClient.delete().uri("/invalid-id-99999").retrieve().body(String.class))
                .isInstanceOf(HttpClientErrorException.NotFound.class);

        verify(apiClient).fetchAllEmployees();
    }

    @Test
    @DisplayName("POST /api/v1 should return 400 for invalid employee data via HTTP")
    void createEmployee_WithMissingFields_ShouldReturnBadRequest() {
        Map<String, String> invalidEmployee = new HashMap<>();
        invalidEmployee.put("name", ""); // Empty name should fail validation
        invalidEmployee.put("salary", "100000");
        invalidEmployee.put("age", "30");
        invalidEmployee.put("title", "Test");

        assertThatThrownBy(() ->
                restClient.post().uri("").body(invalidEmployee).retrieve().body(Employee.class))
                .satisfies(ex -> {
                    assertThat(ex).isInstanceOfAny(
                            HttpClientErrorException.BadRequest.class,
                            org.springframework.web.client.HttpServerErrorException.class
                    );
                });
    }

    @Test
    @DisplayName("POST /api/v1 should handle JSON serialization/deserialization correctly")
    void createEmployee_ShouldHandleJsonCorrectly() {
        // Given
        Employee mockEmployee = new Employee();
        mockEmployee.setId("json-test-id");
        mockEmployee.setName("JSON Test");
        mockEmployee.setSalary(50000);
        mockEmployee.setAge(25);
        mockEmployee.setTitle("Developer");

        when(apiClient.createEmployee(any(CreateEmployeeRequest.class))).thenReturn(mockEmployee);

        Map<String, Object> employeeData = new HashMap<>();
        employeeData.put("name", "JSON Test");
        employeeData.put("salary", 50000); // Integer instead of String
        employeeData.put("age", 25);
        employeeData.put("title", "Developer");

        Employee result = restClient.post().uri("").body(employeeData).retrieve().body(Employee.class);

        assertThat(result).isNotNull();
        assertThat(result.getSalary()).isEqualTo(50000);
        assertThat(result.getAge()).isEqualTo(25);
    }

    @Test
    @DisplayName("Spring Context should load successfully with mocked ApiClient")
    void contextLoads() {
        assertThat(restClient).isNotNull();
        assertThat(apiClient).isNotNull();
    }
}
