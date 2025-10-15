package com.reliaquest.api.service;

import com.reliaquest.api.client.ApiClient;
import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.Employee;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service layer that contains the core business logic for employee operations.
 */
@Service
public class EmployeeService {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    private final ApiClient apiClient;

    /**
     * Constructor.
     *
     * @param apiClient the client used to communicate with the mock employee API
     */
    public EmployeeService(final ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Responsible for fetching all employees from the mock API
     */
    public List<Employee> getAllEmployees() {
        logger.info("Service: Fetching all employees");
        List<Employee> employees = apiClient.fetchAllEmployees();
        logger.info("Service: Retrieved {} employees", employees.size());
        return employees;
    }

    /**
     * Searches for employees whose names contain the search string (case-insensitive).
     *
     * @param searchString the text to search for in employee names
     * @return a list of employees whose names match the search criteria
     */
    public List<Employee> searchEmployeesByName(final String searchString) {
        logger.info("Service: Searching employees with name containing '{}'", searchString);
        List<Employee> employees = apiClient.fetchAllEmployees();
        String lowerCaseSearchString = searchString.toLowerCase();
        List<Employee> matchedEmployees = employees.stream()
                .filter(emp -> emp.getName().toLowerCase().contains(lowerCaseSearchString)).toList();
        logger.info("Service: Found {} employees matching search '{}'", matchedEmployees.size(), searchString);
        return matchedEmployees;
    }

    /**
     * Retrieves a single employee by their unique identifier.
     *
     * @param id the unique identifier of the employee
     * @return the employee with the given ID, or null if not found
     */
    public Employee getEmployeeById(final String id) {
        logger.info("Service: Fetching employee with id {}", id);
        List<Employee> employees = apiClient.fetchAllEmployees();
        for (Employee employee : employees) {
            if (employee.getId().equals(id)) {
                logger.info("Service: Found employee with id {}", id);
                return employee;
            }
        }
        logger.warn("Service: Employee with id {} not found", id);
        return null;
    }

    /**
     * Finds the highest salary among all employees.
     *
     * @return an Optional containing the highest salary, or empty if no employees exist
     */
    public Optional<Integer> getHighestSalary() {
        logger.info("Service: Fetching highest salary");
        List<Employee> employees = apiClient.fetchAllEmployees();
        return employees.stream().map(Employee::getSalary).max(Integer::compareTo);
    }

    /**
     * Returns the names of the top 10 highest-earning employees, sorted by salary descending.
     *
     * @return a list of up to 10 employee names, ordered by salary from highest to lowest
     */
    public List<String> getTopTenHighestEarningEmployeeNames() {
        logger.info("Service: Fetching top 10 highest earning employee names");
        List<Employee> employees = apiClient.fetchAllEmployees();
        return employees.stream().sorted((e1, e2) -> Integer.compare(e2.getSalary(), e1.getSalary())).limit(10)
                .map(Employee::getName).toList();
    }

    /**
     * Creates a new employee in the system.
     *
     * @param employeeRequest the employee data to create
     * @return the newly created employee with its assigned ID
     */
    public Employee createEmployee(final CreateEmployeeRequest employeeRequest) {
        logger.info("Service: Creating employee: {}", employeeRequest.getName());
        
        Employee createdEmployee = apiClient.createEmployee(employeeRequest);
        logger.info("Service: Successfully created employee with id: {}", createdEmployee.getId());
        
        return createdEmployee;
    }

    /**
     * Deletes an employee by their ID.
     *
     * @param id the unique identifier of the employee to delete
     * @return the name of the deleted employee, or null if not found
     */
    public String deleteEmployeeById(final String id) {
        logger.info("Deleting employee with id: {}", id);

        // The mock api delete endpoint
        // only returns a boolean, not the deleted employee.
        // So we need to make two api calls
        Employee employee = getEmployeeById(id);
        if (employee == null) {
            logger.warn("Employee with id: {} not found for deletion", id);
            return null;
        }

        final String employeeName = employee.getName();
        final boolean deleted = apiClient.deleteEmployee(employeeName);
        if (!deleted) {
            logger.warn("Failed to delete employee with id: {} (name: {})", id, employeeName);
            return null;
        }

        logger.info("Successfully deleted employee: {} (id: {})", employeeName, id);
        return employeeName;
    }
}
