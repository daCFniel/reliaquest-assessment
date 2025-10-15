package com.reliaquest.api.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import com.reliaquest.api.client.ApiClient;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.Employee;
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

/**
 * Business Logic Tests
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    @Mock
    private ApiClient mockApiClient;

    @InjectMocks
    private EmployeeService employeeService;

    private Employee employee1;
    private Employee employee2;
    private Employee employee3;
    private List<Employee> allEmployees;

    @BeforeEach
    void setUp() {
        employee1 = new Employee();
        employee1.setId("1");
        employee1.setName("John Doe");
        employee1.setSalary(50000);
        employee1.setAge(30);
        employee1.setTitle("Developer");

        employee2 = new Employee();
        employee2.setId("2");
        employee2.setName("Jane Smith");
        employee2.setSalary(75000);
        employee2.setAge(35);
        employee2.setTitle("Senior Developer");

        employee3 = new Employee();
        employee3.setId("3");
        employee3.setName("Bob Johnson");
        employee3.setSalary(60000);
        employee3.setAge(28);
        employee3.setTitle("Developer");

        allEmployees = Arrays.asList(employee1, employee2, employee3);
    }

    @Test
    @DisplayName("getAllEmployees should return all employees from API")
    void getAllEmployees_ShouldReturnAllEmployees() {
        when(mockApiClient.fetchAllEmployees()).thenReturn(allEmployees);

        List<Employee> result = employeeService.getAllEmployees();

        assertThat(result).hasSize(3);
        assertThat(result).containsExactly(employee1, employee2, employee3);
        verify(mockApiClient, times(1)).fetchAllEmployees();
    }

    @Test
    @DisplayName("searchEmployeesByName should find employees with matching names")
    void searchEmployeesByName_ShouldReturnMatchingEmployees() {
        when(mockApiClient.fetchAllEmployees()).thenReturn(allEmployees);

        List<Employee> result = employeeService.searchEmployeesByName("John");

        assertThat(result).isNotNull();
        assertThat(result).hasSize(2); // John Doe and Bob Johnson
        assertThat(result).extracting(Employee::getName).containsExactlyInAnyOrder("John Doe", "Bob Johnson");
    }

    @Test
    @DisplayName("searchEmployeesByName should be case-insensitive")
    void searchEmployeesByName_ShouldBeCaseInsensitive() {
        when(mockApiClient.fetchAllEmployees()).thenReturn(allEmployees);

        List<Employee> result = employeeService.searchEmployeesByName("JANE");

        assertThat(result).isNotNull();
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("Jane Smith");
    }

    @Test
    @DisplayName("searchEmployeesByName should return empty list when no matches")
    void searchEmployeesByName_WhenNoMatches_ShouldReturnEmptyList() {
        when(mockApiClient.fetchAllEmployees()).thenReturn(allEmployees);

        List<Employee> result = employeeService.searchEmployeesByName("NonExistent");

        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("getEmployeeById should return employee when ID exists")
    void getEmployeeById_WhenIdExists_ShouldReturnEmployee() {
        when(mockApiClient.fetchAllEmployees()).thenReturn(allEmployees);

        Employee result = employeeService.getEmployeeById("2");

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo("2");
        assertThat(result.getName()).isEqualTo("Jane Smith");
    }

    @Test
    @DisplayName("getEmployeeById should return null when ID doesn't exist")
    void getEmployeeById_WhenIdNotFound_ShouldReturnNull() {
        when(mockApiClient.fetchAllEmployees()).thenReturn(allEmployees);

        Employee result = employeeService.getEmployeeById("999");

        assertThat(result).isNull();
    }

    @Test
    @DisplayName("getHighestSalary should return highest salary")
    void getHighestSalary_ShouldReturnHighestSalary() {
        when(mockApiClient.fetchAllEmployees()).thenReturn(allEmployees);

        Optional<Integer> result = employeeService.getHighestSalary();

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(Optional.of(75000));
    }

    @Test
    @DisplayName("getHighestSalary should return empty optional when no employees")
    void getHighestSalary_WhenNoEmployees_ShouldReturnEmptyOptional() {
        when(mockApiClient.fetchAllEmployees()).thenReturn(List.of());

        Optional<Integer> result = employeeService.getHighestSalary();

        assertThat(result).isEqualTo(Optional.empty());
    }

    @Test
    @DisplayName("getTopTenHighestEarningEmployeeNames should return names sorted by salary")
    void getTopTenHighestEarningEmployeeNames_ShouldReturnTop10Names() {
        when(mockApiClient.fetchAllEmployees()).thenReturn(allEmployees);

        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();

        assertThat(result).isNotNull();
        assertThat(result).hasSize(3); // We only have 3 employees
        assertThat(result).containsExactly("Jane Smith", "Bob Johnson", "John Doe");
    }

    @Test
    @DisplayName("getTopTenHighestEarningEmployeeNames should limit to 10 employees")
    void getTopTenHighestEarningEmployeeNames_ShouldLimitTo10() {
        List<Employee> manyEmployees = Arrays.asList(
                createEmployee("2", "Emp2", 95000),
                createEmployee("3", "Emp3", 90000),
                createEmployee("4", "Emp4", 85000),
                createEmployee("5", "Emp5", 80000),
                createEmployee("6", "Emp6", 75000),
                createEmployee("7", "Emp7", 70000),
                createEmployee("8", "Emp8", 65000),
                createEmployee("9", "Emp9", 60000),
                createEmployee("1", "Emp1", 100000),
                createEmployee("10", "Emp10", 55000),
                createEmployee("11", "Emp11", 50000),
                createEmployee("12", "Emp12", 45000),
                createEmployee("13", "Emp13", 40000),
                createEmployee("14", "Emp14", 35000),
                createEmployee("15", "Emp15", 30000));
        when(mockApiClient.fetchAllEmployees()).thenReturn(manyEmployees);

        List<String> result = employeeService.getTopTenHighestEarningEmployeeNames();

        assertThat(result).hasSize(10); // Should only return 10, not 15
        assertThat(result.get(0)).isEqualTo("Emp1"); // Highest earner first
    }

    @Test
    @DisplayName("createEmployee should create employee via API")
    void createEmployee_ShouldCreateEmployee() {
        CreateEmployeeRequest input = new CreateEmployeeRequest("New Employee", 80000, 32, "Manager");

        Employee createdEmployee = new Employee();
        createdEmployee.setId("4");
        createdEmployee.setName("New Employee");
        createdEmployee.setSalary(80000);
        createdEmployee.setAge(32);
        createdEmployee.setTitle("Manager");

        when(mockApiClient.createEmployee(any())).thenReturn(createdEmployee);

        Employee result = employeeService.createEmployee(input);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("New Employee");
    }

    @Test
    @DisplayName("deleteEmployeeById should delete and return employee name")
    void deleteEmployeeById_ShouldDeleteAndReturnName() {
        when(mockApiClient.fetchAllEmployees()).thenReturn(allEmployees);

        when(mockApiClient.deleteEmployee("John Doe")).thenReturn(true);

        String result = employeeService.deleteEmployeeById("1");

        assertThat(result).isNotNull();
        assertThat(result).isEqualTo("John Doe");
    }

    @Test
    @DisplayName("deleteEmployeeById should return null when employee not found")
    void deleteEmployeeById_WhenNotFound_ShouldReturnNull() {
        when(mockApiClient.fetchAllEmployees()).thenReturn(allEmployees);

        String result = employeeService.deleteEmployeeById("999");

        assertThat(result).isNull();
    }

    private Employee createEmployee(String id, String name, Integer salary) {
        Employee emp = new Employee();
        emp.setId(id);
        emp.setName(name);
        emp.setSalary(salary);
        emp.setAge(30);
        emp.setTitle("Developer");
        return emp;
    }
}
