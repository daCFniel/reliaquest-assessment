package com.reliaquest.api.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.service.EmployeeService;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class EmployeeControllerMockTest {

    @Mock
    private EmployeeService employeeService;

    @InjectMocks
    private EmployeeController employeeController;

    private Employee testEmployee1;
    private Employee testEmployee2;

    @BeforeEach
    void setUp() {
        testEmployee1 = new Employee();
        testEmployee1.setId("1");
        testEmployee1.setName("John Doe");
        testEmployee1.setSalary(50000);
        testEmployee1.setAge(30);
        testEmployee1.setTitle("Developer");
        testEmployee1.setEmail("john@example.com");

        testEmployee2 = new Employee();
        testEmployee2.setId("2");
        testEmployee2.setName("Jane Smith");
        testEmployee2.setSalary(75000);
        testEmployee2.setAge(35);
        testEmployee2.setTitle("Senior Developer");
        testEmployee2.setEmail("jane@example.com");
    }

    @Test
    @DisplayName("Should return all employees successfully")
    void getAllEmployees_ShouldReturnAllEmployees() {
        List<Employee> expectedEmployees = Arrays.asList(testEmployee1, testEmployee2);
        when(employeeService.getAllEmployees()).thenReturn(expectedEmployees);

        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsExactly(testEmployee1, testEmployee2);
        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    @DisplayName("Should return empty list when no employees exist")
    void getAllEmployees_WhenNoEmployees_ShouldReturnEmptyList() {
        when(employeeService.getAllEmployees()).thenReturn(List.of());

        ResponseEntity<List<Employee>> response = employeeController.getAllEmployees();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        verify(employeeService, times(1)).getAllEmployees();
    }

    @Test
    @DisplayName("Should return employees matching name search")
    void getEmployeesByNameSearch_ShouldReturnMatchingEmployees() {
        String searchString = "John";
        List<Employee> expectedEmployees = List.of(testEmployee1);
        when(employeeService.searchEmployeesByName(searchString)).thenReturn(expectedEmployees);

        ResponseEntity<List<Employee>> response = employeeController.getEmployeesByNameSearch(searchString);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
        assertThat(response.getBody().get(0).getName()).contains("John");
        verify(employeeService, times(1)).searchEmployeesByName(searchString);
    }

    @Test
    @DisplayName("Should return empty list when no employees match search")
    void getEmployeesByNameSearch_WhenNoMatch_ShouldReturnEmptyList() {
        String searchString = "NonExistent";
        when(employeeService.searchEmployeesByName(searchString)).thenReturn(List.of());

        ResponseEntity<List<Employee>> response = employeeController.getEmployeesByNameSearch(searchString);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
        verify(employeeService, times(1)).searchEmployeesByName(searchString);
    }

    @Test
    @DisplayName("Should return employee by ID")
    void getEmployeeById_ShouldReturnEmployee() {
        String employeeId = "1";
        when(employeeService.getEmployeeById(employeeId)).thenReturn(testEmployee1);

        ResponseEntity<Employee> response = employeeController.getEmployeeById(employeeId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(testEmployee1);
        assertThat(response.getBody().getId()).isEqualTo(employeeId);
        verify(employeeService, times(1)).getEmployeeById(employeeId);
    }

    @Test
    @DisplayName("Should return 404 when employee not found by ID")
    void getEmployeeById_WhenNotFound_ShouldReturn404() {
        String employeeId = "999";
        when(employeeService.getEmployeeById(employeeId)).thenReturn(null);

        ResponseEntity<Employee> response = employeeController.getEmployeeById(employeeId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(employeeService, times(1)).getEmployeeById(employeeId);
    }

    @Test
    @DisplayName("Should return highest salary")
    void getHighestSalaryOfEmployees_ShouldReturnHighestSalary() {
        Integer expectedHighestSalary = 75000;
        when(employeeService.getHighestSalary()).thenReturn(Optional.of(expectedHighestSalary));

        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedHighestSalary);
        verify(employeeService, times(1)).getHighestSalary();
    }

    @Test
    @DisplayName("Should return NOT FOUND when no employees exist")
    void getHighestSalaryOfEmployees_WhenNoEmployees_ShouldReturn404() {
        when(employeeService.getHighestSalary()).thenReturn(Optional.empty());

        ResponseEntity<Integer> response = employeeController.getHighestSalaryOfEmployees();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(employeeService, times(1)).getHighestSalary();
    }

    @Test
    @DisplayName("Should return top 10 highest earning employee names")
    void getTopTenHighestEarningEmployeeNames_ShouldReturnTop10Names() {
        List<String> expectedNames = Arrays.asList("Jane Smith", "John Doe");
        when(employeeService.getTopTenHighestEarningEmployeeNames()).thenReturn(expectedNames);

        ResponseEntity<List<String>> response = employeeController.getTopTenHighestEarningEmployeeNames();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(2);
        assertThat(response.getBody()).containsExactly("Jane Smith", "John Doe");
        verify(employeeService, times(1)).getTopTenHighestEarningEmployeeNames();
    }

    @Test
    @DisplayName("Should create employee successfully")
    void createEmployee_ShouldCreateAndReturnEmployee() {
        CreateEmployeeRequest employeeInput = new CreateEmployeeRequest("New Employee", 60000, 28, "Junior Developer");
        Employee createdEmployee = new Employee();
        createdEmployee.setId("3");
        createdEmployee.setName("New Employee");
        createdEmployee.setSalary(60000);
        createdEmployee.setAge(28);
        createdEmployee.setTitle("Junior Developer");

        when(employeeService.createEmployee(employeeInput)).thenReturn(createdEmployee);

        ResponseEntity<Employee> response = employeeController.createEmployee(employeeInput);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(createdEmployee);
        assertThat(response.getBody().getName()).isEqualTo("New Employee");
        verify(employeeService, times(1)).createEmployee(employeeInput);
    }

    @Test
    @DisplayName("Should delete employee and return employee name")
    void deleteEmployeeById_ShouldDeleteAndReturnName() {
        String employeeId = "1";
        String employeeName = "John Doe";
        when(employeeService.deleteEmployeeById(employeeId)).thenReturn(employeeName);

        ResponseEntity<String> response = employeeController.deleteEmployeeById(employeeId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(employeeName);
        verify(employeeService, times(1)).deleteEmployeeById(employeeId);
    }

    @Test
    @DisplayName("Should return 404 when employee to delete not found")
    void deleteEmployeeById_WhenNotFound_ShouldReturn404() {
        String employeeId = "999";
        when(employeeService.deleteEmployeeById(employeeId)).thenReturn(null);

        ResponseEntity<String> response = employeeController.deleteEmployeeById(employeeId);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        verify(employeeService, times(1)).deleteEmployeeById(employeeId);
    }
}
