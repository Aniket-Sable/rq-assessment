package com.reliaquest.api.service;

import com.reliaquest.api.controller.EmployeeController;
import com.reliaquest.api.model.Employee;
import com.reliaquest.api.model.EmployeeResponse;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import com.reliaquest.api.model.EmployeeDetails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static com.reliaquest.api.constant.EmployeeConstant.API_ENDPOINT_BY_ID;
import static com.reliaquest.api.constant.EmployeeConstant.API_ENDPOINT_NAME;

@Service
public class EmployeeService implements IEmployeeService{

    private static final Logger logger = LoggerFactory.getLogger(EmployeeController.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${employee.service.url}")
    private String employeeServiceURL = "";

    public ResponseEntity<List<Employee>> getAllEmployees() {

        try{
            String URL = employeeServiceURL.concat(API_ENDPOINT_NAME);
            logger.info("EmployeeService - getAllEmployees() - URL: {}", URL);
            EmployeeResponse empList = restTemplate.getForObject(URL, EmployeeResponse.class);
            if (empList == null || empList.getData() == null || empList.getData().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).body(Collections.emptyList());
            }
            List<Employee> allEmployees = empList.getData();
            logger.info("EmployeeService - getAllEmployees() - allEmployees: {}", allEmployees);
            return new ResponseEntity<>(allEmployees, HttpStatus.OK);
        }catch (HttpClientErrorException.NotFound e) {
            logger.error("EmployeeService - getAllEmployees() - API endpoint not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (HttpServerErrorException e) {
            logger.error("EmployeeService - getAllEmployees() - Too many requests: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e) {
            logger.error("EmployeeService - getAllEmployees() - Unexpected error while fetching employees: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<List<Employee>> getEmployeesByNameSearch(String searchString) {
        try{
            String URL = employeeServiceURL.concat(API_ENDPOINT_NAME);
            logger.info("EmployeeService - getEmployeesByNameSearch() - URL: {}", URL);
            EmployeeResponse empList = restTemplate.getForObject(URL, EmployeeResponse.class);
            if (empList == null || empList.getData() == null || empList.getData().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            List<Employee> allEmployees = empList.getData();
            logger.info("EmployeeService - getEmployeesByNameSearch() - allEmployees: {}", allEmployees);
            List<Employee> filteredEmployees = allEmployees.stream()
                    .filter(employee -> employee.getName()
                            .toLowerCase()
                            .contains(searchString.toLowerCase()))
                    .collect(Collectors.toList());
            logger.info("EmployeeService - getEmployeesByNameSearch() - filteredEmployees: {}", filteredEmployees);
            if (filteredEmployees.isEmpty()) {
                logger.info("EmployeeService - getEmployeesByNameSearch() - No employees found matching search string: {}", searchString);
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return ResponseEntity.ok(filteredEmployees);
        }catch (HttpClientErrorException.NotFound e) {
            logger.error("EmployeeService - getEmployeesByNameSearch() - API endpoint not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (HttpServerErrorException e) {
            logger.error("EmployeeService - getEmployeesByNameSearch() - Too many requests: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e) {
            logger.error("EmployeeService - getEmployeesByNameSearch() - Unexpected error while fetching employees: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Employee> getEmployeeById(String id) {
        try{
            String ApiName = API_ENDPOINT_BY_ID.concat(id);
            String URL = employeeServiceURL.concat(ApiName);
            logger.info("EmployeeService - getEmployeeById() - URL: {}", URL);
            EmployeeDetails employeeDetails = restTemplate.getForObject(URL, EmployeeDetails.class);
            return new ResponseEntity<>(employeeDetails.getData(), HttpStatus.OK);
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("EmployeeService - getEmployeeById() - API endpoint not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (HttpServerErrorException e) {
            logger.error("EmployeeService - getEmployeeById() - Too many requests: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e) {
            logger.error("EmployeeService - getEmployeeById() - Unexpected error while fetching employees: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<Integer> getHighestSalaryOfEmployees() {
        try{
            String URL = employeeServiceURL.concat(API_ENDPOINT_NAME);
            logger.info("EmployeeService - getHighestSalaryOfEmployees() - URL: {}", URL);
            EmployeeResponse empList = restTemplate.getForObject(URL, EmployeeResponse.class);
            List<Employee> allEmployees = empList.getData();
            logger.info("EmployeeService - getHighestSalaryOfEmployees() - allEmployees: {}", allEmployees);
            if (empList == null || empList.getData() == null || empList.getData().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            Employee highestPaidEmployee = allEmployees.stream()
                    .max(Comparator.comparing(Employee::getSalary))
                    .orElse(null);
            logger.info("EmployeeService - getHighestSalaryOfEmployees() - highestPaidEmployee: {}", highestPaidEmployee);
            return ResponseEntity.ok(highestPaidEmployee.getSalary());
        }catch (HttpClientErrorException.NotFound e) {
            logger.error("EmployeeService - getHighestSalaryOfEmployees() - API endpoint not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (HttpServerErrorException e) {
            logger.error("EmployeeService - getHighestSalaryOfEmployees() - Too many requests: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e) {
            logger.error("EmployeeService - getHighestSalaryOfEmployees() - Unexpected error while fetching employee with highest salary: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    public ResponseEntity<List<String>> getTopTenHighestEarningEmployeeNames() {
        try{
            String URL = employeeServiceURL.concat(API_ENDPOINT_NAME);
            logger.info("EmployeeService - getTopTenHighestEarningEmployeeNames() - URL: {}", URL);
            EmployeeResponse empList = restTemplate.getForObject(URL, EmployeeResponse.class);
            if (empList == null || empList.getData() == null || empList.getData().isEmpty()) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            List<Employee> allEmployees = empList.getData();
            logger.info("EmployeeService - getTopTenHighestEarningEmployeeNames() - allEmployees: {}", allEmployees);
            List<String> topTenEmployeeNames = allEmployees.stream()
                    .sorted(Comparator.comparing(Employee::getSalary).reversed())
                    .limit(10) // Take top 10
                    .map(Employee::getName)
                    .collect(Collectors.toList());
            logger.info("EmployeeService - getTopTenHighestEarningEmployeeNames() - Top ten highest earning employee names: {}", topTenEmployeeNames);
            return ResponseEntity.ok(topTenEmployeeNames);

        }catch (HttpClientErrorException.NotFound e) {
            logger.error("EmployeeService - getTopTenHighestEarningEmployeeNames() - API endpoint not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (HttpServerErrorException e) {
            logger.error("EmployeeService - getTopTenHighestEarningEmployeeNames() - Too many requests: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e) {
            logger.error("EmployeeService - getTopTenHighestEarningEmployeeNames() - Unexpected error while fetching employees: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    public ResponseEntity<Employee> createEmployee(Object employeeInput) {
        try{
            String URL = employeeServiceURL.concat(API_ENDPOINT_NAME);
            logger.info("EmployeeService - createEmployee() - URL: {}", URL);
            EmployeeDetails emp = restTemplate.postForObject(URL,employeeInput, EmployeeDetails.class);
            if (emp == null || emp.getData() == null) {
                return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
            }
            return new ResponseEntity<>(emp.getData(), HttpStatus.CREATED);
        }catch (HttpClientErrorException.BadRequest e) {
            logger.error("EmployeeService - createEmployee() - Invalid request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }catch (HttpServerErrorException e) {
            logger.error("EmployeeService - createEmployee() - Too many requests: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e) {
            logger.error("EmployeeService - createEmployee() - Unexpected error while creating employee: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    public ResponseEntity<String> deleteEmployeeById(String id) {
        try{
            String ApiName = API_ENDPOINT_BY_ID + id;
            String URL = employeeServiceURL.concat(ApiName);
            logger.info("EmployeeService - deleteEmployeeById() - URL: {}", URL);
            EmployeeDetails employeeDetails = restTemplate.getForObject(URL, EmployeeDetails.class);
            logger.info("EmployeeService - deleteEmployeeById() - employee: {}", employeeDetails);
            if (null == employeeDetails) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            restTemplate.exchange(URL, HttpMethod.DELETE, null, void.class);
            return ResponseEntity.ok(employeeDetails.getData().getName());
        }catch (HttpClientErrorException.NotFound e) {
            logger.error("EmployeeService - deleteEmployeeById() - API endpoint not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }catch (HttpServerErrorException e) {
            logger.error("EmployeeService - deleteEmployeeById() - Too many requests: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }catch (Exception e) {
            logger.error("EmployeeService - deleteEmployeeById() - Unexpected error while deleting the employee: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }
}
