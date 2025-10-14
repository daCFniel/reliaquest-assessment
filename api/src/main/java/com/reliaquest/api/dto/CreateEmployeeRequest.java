package com.reliaquest.api.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * DTO for creating a new employee.
 * Includes validation constraints as specified in the API documentation.
 */
public class CreateEmployeeRequest {

    @NotBlank(message = "Name is required and cannot be blank")
    private String name;

    @NotNull(message = "Salary is required") @Min(value = 1, message = "Salary must be greater than zero")
    private Integer salary;

    @NotNull(message = "Age is required") @Min(value = 16, message = "Age must be at least 16")
    @Max(value = 75, message = "Age must not exceed 75")
    private Integer age;

    @NotBlank(message = "Title is required and cannot be blank")
    private String title;

    public CreateEmployeeRequest(String name, Integer salary, Integer age, String title) {
        this.name = name;
        this.salary = salary;
        this.age = age;
        this.title = title;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSalary() {
        return salary;
    }

    public void setSalary(Integer salary) {
        this.salary = salary;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
