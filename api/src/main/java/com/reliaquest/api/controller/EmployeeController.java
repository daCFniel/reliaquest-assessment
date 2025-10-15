package com.reliaquest.api.controller;

import com.reliaquest.api.dto.CreateEmployeeRequest;
import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.service.EmployeeService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller that handles HTTP requests for employee operations.
 */
@RestController
@RequestMapping("/api/v1")
@Validated
public class EmployeeController implements IEmployeeController<Employee, CreateEmployeeRequest> {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    private final EmployeeService employeeService;

    public EmployeeController(final EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        logger.info("Received request to get all employees");
        List<Employee> employees = employeeService.getAllEmployees();
        logger.debug("Returning {} employees", employees.size());
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        logger.info("Received request to search employees by name: {}", searchString);
        List<Employee> employees = employeeService.searchEmployeesByName(searchString);
        logger.debug("Found {} employees matching '{}'", employees.size(), searchString);
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        logger.info("Received request to get employee by id: {}", id);
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            logger.warn("Employee with id {} not found", id);
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        logger.info("Received request to get highest salary");
        Optional<Integer> highestSalary = employeeService.getHighestSalary();
        logger.debug("Highest salary: {}", highestSalary);
        return highestSalary
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        logger.info("Received request to get top 10 highest earning employees");
        List<String> topEarners = employeeService.getTopTenHighestEarningEmployeeNames();
        logger.debug("Returning {} top earners", topEarners != null ? topEarners.size() : 0);
        return ResponseEntity.ok(topEarners);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(CreateEmployeeRequest employeeInput) {
        logger.info("Received request to create employee: {}", employeeInput.getName());
        Employee employee = employeeService.createEmployee(employeeInput);
        logger.info("Successfully created employee with id: {}", employee.getId());
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        logger.info("Received request to delete employee by id: {}", id);
        String deletedEmployeeName = employeeService.deleteEmployeeById(id);
        if (deletedEmployeeName == null) {
            logger.warn("Employee with id {} not found for deletion", id);
            return ResponseEntity.notFound().build();
        }
        logger.info("Successfully deleted employee: {}", deletedEmployeeName);
        return ResponseEntity.ok(deletedEmployeeName);
    }
}
