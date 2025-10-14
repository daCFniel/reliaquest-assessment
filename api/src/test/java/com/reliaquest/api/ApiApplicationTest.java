package com.reliaquest.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.reliaquest.api.dto.Employee;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

/**
 * Integration Tests
 * These tests require the Mock Server to be running
 * ./gradlew server:bootRun
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiApplicationTest {

    @LocalServerPort
    private int port;

    private RestClient restClient;

    @BeforeEach
    void setUp() {
        restClient = RestClient.builder()
                .baseUrl("http://localhost:" + port + "/api/v1")
                .build();
    }

    @Test
    @DisplayName("GET /api/v1 should return all employees from mock API")
    void getAllEmployees_ShouldReturnEmployeeList() {
        List<Employee> employees = restClient.get().uri("").retrieve().body(new ParameterizedTypeReference<>() {});

        assertThat(employees).isNotNull();
        assertThat(employees).isNotEmpty();

        Employee firstEmployee = employees.get(0);
        assertThat(firstEmployee.getId()).isNotNull();
        assertThat(firstEmployee.getName()).isNotNull();
        assertThat(firstEmployee.getSalary()).isNotNull();
        assertThat(firstEmployee.getAge()).isNotNull();
    }

    @Test
    @DisplayName("GET /api/v1/search/{name} should return employees matching search string")
    void getEmployeesByNameSearch_ShouldReturnMatchingEmployees() {
        List<Employee> allEmployees = restClient.get().uri("").retrieve().body(new ParameterizedTypeReference<>() {});

        String searchName = allEmployees.get(0).getName().substring(0, 3);

        List<Employee> matchingEmployees =
                restClient.get().uri("/search/" + searchName).retrieve().body(new ParameterizedTypeReference<>() {});

        assertThat(matchingEmployees).isNotNull();
        matchingEmployees.forEach(emp -> assertThat(emp.getName().toLowerCase()).contains(searchName.toLowerCase()));
    }

    @Test
    @DisplayName("GET /api/v1/search/{name} should return empty list when no matches")
    void getEmployeesByNameSearch_WhenNoMatches_ShouldReturnEmptyList() {
        List<Employee> employees = restClient
                .get()
                .uri("/search/NonExistentName123456")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        assertThat(employees).isEmpty();
    }

    @Test
    @DisplayName("GET /api/v1/{id} should return employee by ID")
    void getEmployeeById_WhenValidId_ShouldReturnEmployee() {
        List<Employee> allEmployees = restClient.get().uri("").retrieve().body(new ParameterizedTypeReference<>() {});

        String validId = allEmployees.get(0).getId();

        Employee employee = restClient.get().uri("/" + validId).retrieve().body(Employee.class);

        assertThat(employee).isNotNull();
        assertThat(employee.getId()).isEqualTo(validId);
    }

    @Test
    @DisplayName("GET /api/v1/{id} should return 404 when employee not found")
    void getEmployeeById_WhenInvalidId_ShouldReturn404() {
        // When/Then
        assertThatThrownBy(() ->
                        restClient.get().uri("/invalid-id-12345").retrieve().body(Employee.class))
                .isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    @DisplayName("GET /api/v1/highestSalary should return the highest salary")
    void getHighestSalary_ShouldReturnMaxSalary() {
        Integer highestSalary =
                restClient.get().uri("/highestSalary").retrieve().body(Integer.class);

        assertThat(highestSalary).isNotNull();
        assertThat(highestSalary).isGreaterThan(0);

        List<Employee> allEmployees = restClient.get().uri("").retrieve().body(new ParameterizedTypeReference<>() {});

        int actualMax =
                allEmployees.stream().mapToInt(Employee::getSalary).max().orElse(0);

        assertThat(highestSalary).isEqualTo(actualMax);
    }

    @Test
    @DisplayName("GET /api/v1/topTenHighestEarningEmployeeNames should return top 10 names")
    void getTopTenHighestEarningEmployeeNames_ShouldReturnTop10() {
        List<String> topTenNames = restClient
                .get()
                .uri("/topTenHighestEarningEmployeeNames")
                .retrieve()
                .body(new ParameterizedTypeReference<>() {});

        assertThat(topTenNames).isNotNull();
        assertThat(topTenNames).hasSizeLessThanOrEqualTo(10);

        List<Employee> allEmployees = restClient.get().uri("").retrieve().body(new ParameterizedTypeReference<>() {});

        List<String> expectedTop10 = allEmployees.stream()
                .sorted((e1, e2) -> e2.getSalary().compareTo(e1.getSalary()))
                .limit(10)
                .map(Employee::getName)
                .toList();

        assertThat(topTenNames).isEqualTo(expectedTop10);
    }

    @Test
    @DisplayName("POST /api/v1 should create a new employee")
    void createEmployee_WithValidData_ShouldCreateEmployee() {
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
    }

    @Test
    @DisplayName("DELETE /api/v1/{id} should delete employee and return name")
    void deleteEmployeeById_WhenValidId_ShouldDeleteAndReturnName() {
        Map<String, String> newEmployee = new HashMap<>();
        newEmployee.put("name", "Employee To Delete");
        newEmployee.put("salary", "50000");
        newEmployee.put("age", "25");
        newEmployee.put("title", "Temporary");

        Employee createdEmployee =
                restClient.post().uri("").body(newEmployee).retrieve().body(Employee.class);

        String employeeId = createdEmployee.getId();

        String deletedEmployeeName =
                restClient.delete().uri("/" + employeeId).retrieve().body(String.class);

        assertThat(deletedEmployeeName).isEqualTo("Employee To Delete");
    }

    @Test
    @DisplayName("DELETE /api/v1/{id} should return 404 when employee not found")
    void deleteEmployeeById_WhenInvalidId_ShouldReturn404() {
        assertThatThrownBy(() ->
                        restClient.delete().uri("/invalid-id-99999").retrieve().body(String.class))
                .isInstanceOf(HttpClientErrorException.NotFound.class);
    }

    @Test
    @DisplayName("Spring Context should load successfully")
    void contextLoads() {
        assertThat(restClient).isNotNull();
    }
}
