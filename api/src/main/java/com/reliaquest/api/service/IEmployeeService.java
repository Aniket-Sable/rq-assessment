package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IEmployeeService {
    ResponseEntity<List<Employee>> getAllEmployees();

    ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString);

    ResponseEntity<Employee> getEmployeeById(String id);

    ResponseEntity<Integer> getHighestSalaryOfEmployees();

    ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames();

    ResponseEntity<Employee> createEmployee(Object employeeInput);

    ResponseEntity<String> deleteEmployeeById(String id);
}
