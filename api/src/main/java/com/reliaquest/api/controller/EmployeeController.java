package com.reliaquest.api.controller;

import com.reliaquest.api.dto.Employee;
import com.reliaquest.api.service.EmployeeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * REST controller that handles HTTP requests for employee operations.
 */
@RestController
@RequestMapping("/api/v1") // Added this as the interface doesn't specify the base bath
public class EmployeeController implements IEmployeeController<Employee, Map<String, String>> {

    private final EmployeeService employeeService;

    public EmployeeController(final EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Override
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> employees = employeeService.getAllEmployees();

        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        List<Employee> employees = employeeService.searchEmployeesByName(searchString);
        return ResponseEntity.ok(employees);
    }

    @Override
    public ResponseEntity<Employee> getEmployeeById(String id) {
        Employee employee = employeeService.getEmployeeById(id);
        if (employee == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        Integer highestSalary = employeeService.getHighestSalary();
        return ResponseEntity.ok(highestSalary);
    }

    @Override
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        List<String> topEarners = employeeService.getTopTenHighestEarningEmployeeNames();
        return ResponseEntity.ok(topEarners);
    }

    @Override
    public ResponseEntity<Employee> createEmployee(Map<String, String> employeeInput) {
        Employee employee = employeeService.createEmployee(employeeInput);
        return ResponseEntity.ok(employee);
    }

    @Override
    public ResponseEntity<String> deleteEmployeeById(String id) {
        String deletedEmployeeName = employeeService.deleteEmployeeById(id);
        if (deletedEmployeeName == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(deletedEmployeeName);
    }
}
