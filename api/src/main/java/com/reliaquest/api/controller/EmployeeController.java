package com.reliaquest.api.controller;

import com.reliaquest.api.model.Employee;
import java.util.List;

import com.reliaquest.api.service.IEmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class EmployeeController implements IEmployeeController {

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);
    private IEmployeeService employeeService;

    @Autowired
    public EmployeeController(IEmployeeService employeeService){
        this.employeeService=employeeService;
    }

    @Override
    @GetMapping()
    public ResponseEntity<List<Employee>> getAllEmployees() {
        return employeeService.getAllEmployees();
    }

    @Override
    @GetMapping("/search/{searchString}")
    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        logger.info("EmployeeController - getEmployeesByNameSearch() - searchString: {}", searchString);
        return employeeService.getEmployeesByNameSearch(searchString);
    }

    @Override
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable String id) {
        logger.info("EmployeeController - getEmployeeById() - id: {}", id);
        return employeeService.getEmployeeById(id);
    }

    @Override
    @GetMapping("/highestSalary")
    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        return employeeService.getHighestSalaryOfEmployees();
    }

    @Override
    @GetMapping("/topTenHighestEarningEmployeeNames")
    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        return employeeService.getTopTenHighestEarningEmployeeNames();
    }

    @Override
    @PostMapping()
    public ResponseEntity<Employee> createEmployee(Object employeeInput) {
        logger.info("EmployeeController - createEmployee() - employeeInputRequest: {}", employeeInput);
        return employeeService.createEmployee(employeeInput);
    }

    @Override
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteEmployeeById(@PathVariable String id) {
        logger.info("EmployeeController - deleteEmployeeById() - id: {}", id);
        return employeeService.deleteEmployeeById(id);
    }
}
