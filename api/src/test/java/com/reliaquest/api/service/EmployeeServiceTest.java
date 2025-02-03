package com.reliaquest.api.service;

import com.reliaquest.api.model.Employee;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.reliaquest.api.model.EmployeeDetails;
import com.reliaquest.api.model.EmployeeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.*;
import java.util.stream.Collectors;


@SpringBootTest
public class EmployeeServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EmployeeService employeeService;

    private String validId;
    private String invalidId;
    private String apiUrl;
    private EmployeeDetails mockEmployeeDetails;
    private String employeeId;
    private List<Employee> mockEmployees;
    private Employee mockEmployeeInput;
    private String url;
    private String searchString;
    private String searchApiUrl;

    @BeforeEach
    void setUp() {
        validId = "123";
        invalidId = "456";
        apiUrl = "http://localhost:8080/api/v1/employee/" + validId;
        employeeId = "550e8400-e29b-41d4-a716-446655440000";
        url = "/api/v1/employee";
        searchApiUrl = "/api/v1/employee/";
        searchString = "Doe";
        mockEmployeeDetails = new EmployeeDetails();
        mockEmployees = Arrays.asList(
                new Employee(UUID.fromString(employeeId), "John Doe", 80000, 30, "Engineer", "john@example.com"),
                new Employee(UUID.fromString(employeeId), "Jane Doe", 90000, 28, "Manager", "jane@example.com")
        );
        mockEmployeeInput = new Employee(UUID.fromString(employeeId), "John Doe", 90000, 30, "Software Engineer", "johndoe@example.com");
        mockEmployeeDetails.setData(new Employee(UUID.fromString(employeeId), "John Doe", 90000, 30, "Software Engineer", "johndoe@example.com"));
    }

    @Test
    void testGetAllEmployees_Success() {
        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setData(mockEmployees);
        when(restTemplate.getForObject(url, EmployeeResponse.class)).thenReturn(employeeResponse);
        ResponseEntity<List<Employee>> response = employeeService.getAllEmployees();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(2, response.getBody().size());
    }

    @Test
    void testGetAllEmployees_NoContent() {
        EmployeeResponse emptyEmployeeResponse = new EmployeeResponse();
        emptyEmployeeResponse.setData(Collections.emptyList());
        when(restTemplate.getForObject(url, EmployeeResponse.class)).thenReturn(emptyEmployeeResponse);
        ResponseEntity<List<Employee>> response = employeeService.getAllEmployees();
        assertNotNull(response);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertEquals(Collections.emptyList(), response.getBody());
    }

    @Test
    void testGetAllEmployees_TooManyRequests() {
        when(restTemplate.getForObject(url, EmployeeResponse.class))
                .thenThrow(new HttpServerErrorException(HttpStatus.TOO_MANY_REQUESTS));
        ResponseEntity<List<Employee>> response = employeeService.getAllEmployees();
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetAllEmployees_InternalServerError() {
        when(restTemplate.getForObject(url, EmployeeResponse.class))
                .thenThrow(new RuntimeException("Internal Server Error"));
        ResponseEntity<List<Employee>> response = employeeService.getAllEmployees();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetEmployeesByNameSearch_Success() {
        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setData(mockEmployees);
        when(restTemplate.getForObject(url, EmployeeResponse.class)).thenReturn(employeeResponse);
        ResponseEntity<List<Employee>> response = employeeService.getEmployeesByNameSearch(searchString);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<Employee> filteredEmployees = response.getBody();
        assertNotNull(filteredEmployees);
        assertEquals(2, filteredEmployees.size());
    }

    @Test
    void testGetEmployeesByNameSearch_NoMatches() {
        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setData(mockEmployees);
        when(restTemplate.getForObject(searchApiUrl, EmployeeResponse.class)).thenReturn(employeeResponse);
        ResponseEntity<List<Employee>> response = employeeService.getEmployeesByNameSearch(searchString);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetEmployeesByNameSearch_EmptyEmployeeList() {
        EmployeeResponse emptyResponse = new EmployeeResponse();
        emptyResponse.setData(Collections.emptyList());
        when(restTemplate.getForObject(url, EmployeeResponse.class)).thenReturn(emptyResponse);
        ResponseEntity<List<Employee>> response = employeeService.getEmployeesByNameSearch("Doe");
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetEmployeesByNameSearch_TooManyRequests() {
        when(restTemplate.getForObject(url, EmployeeResponse.class))
                .thenThrow(new HttpServerErrorException(HttpStatus.TOO_MANY_REQUESTS));
        ResponseEntity<List<Employee>> response = employeeService.getEmployeesByNameSearch("Doe");
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void testGetEmployeeById_Success() {
        when(restTemplate.getForObject( searchApiUrl + employeeId, EmployeeDetails.class)).thenReturn(mockEmployeeDetails);
        ResponseEntity<Employee> response = employeeService.getEmployeeById(employeeId);
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("John Doe", response.getBody().getName());
        assertEquals(Integer.valueOf(90000), response.getBody().getSalary());
    }

    @Test
    void testGetEmployeeById_InternalServerError() {
        when(restTemplate.getForObject(url + employeeId, EmployeeDetails.class))
                .thenThrow(new RuntimeException("Internal Server Error"));
        ResponseEntity<Employee> response = employeeService.getEmployeeById(employeeId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetHighestSalaryOfEmployees_NoContent() {
        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setData(Collections.emptyList());
        when(restTemplate.getForObject(url, EmployeeResponse.class)).thenReturn(employeeResponse);
        ResponseEntity<Integer> response = employeeService.getHighestSalaryOfEmployees();
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetHighestSalaryOfEmployees_TooManyRequests() {
        when(restTemplate.getForObject(url, EmployeeResponse.class))
                .thenThrow(new HttpServerErrorException(HttpStatus.TOO_MANY_REQUESTS));
        ResponseEntity<Integer> response = employeeService.getHighestSalaryOfEmployees();
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetHighestSalaryOfEmployees_InternalServerError() {
        when(restTemplate.getForObject(url, EmployeeResponse.class))
                .thenThrow(new RuntimeException("Internal Server Error"));
        ResponseEntity<Integer> response = employeeService.getHighestSalaryOfEmployees();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_Success() {
        List<String> expectedTopTenNames = mockEmployees.stream()
                .sorted(Comparator.comparing(Employee::getSalary).reversed())
                .limit(10)
                .map(Employee::getName)
                .collect(Collectors.toList());

        EmployeeResponse employeeResponse = new EmployeeResponse();
        employeeResponse.setData(mockEmployees);
        when(restTemplate.getForObject(url, EmployeeResponse.class)).thenReturn(employeeResponse);
        ResponseEntity<List<String>> response = employeeService.getTopTenHighestEarningEmployeeNames();
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedTopTenNames, response.getBody());
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_NoContent() {
        EmployeeResponse emptyResponse = new EmployeeResponse();
        emptyResponse.setData(Collections.emptyList());
        when(restTemplate.getForObject(url, EmployeeResponse.class)).thenReturn(emptyResponse);
        ResponseEntity<List<String>> response = employeeService.getTopTenHighestEarningEmployeeNames();
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_TooManyRequests() {
        when(restTemplate.getForObject(url, EmployeeResponse.class))
                .thenThrow(new HttpServerErrorException(HttpStatus.TOO_MANY_REQUESTS));
        ResponseEntity<List<String>> response = employeeService.getTopTenHighestEarningEmployeeNames();
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testGetTopTenHighestEarningEmployeeNames_InternalServerError() {
        when(restTemplate.getForObject(url, EmployeeResponse.class))
                .thenThrow(new RuntimeException("Internal Server Error"));
        ResponseEntity<List<String>> response = employeeService.getTopTenHighestEarningEmployeeNames();
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testCreateEmployee_Success() {
        EmployeeDetails employeeDetails = new EmployeeDetails();
        employeeDetails.setData(mockEmployeeInput);
        when(restTemplate.postForObject(url, mockEmployeeInput, EmployeeDetails.class)).thenReturn(employeeDetails);
        ResponseEntity<Employee> response = employeeService.createEmployee(mockEmployeeInput);
        assertNotNull(response);
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertEquals(mockEmployeeInput, response.getBody());
    }

    @Test
    void testCreateEmployee_NoContent() {
        when(restTemplate.postForObject(url, mockEmployeeInput, EmployeeDetails.class)).thenReturn(null);
        ResponseEntity<Employee> response = employeeService.createEmployee(mockEmployeeInput);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testCreateEmployee_TooManyRequests() {
        when(restTemplate.postForObject(url, mockEmployeeInput, EmployeeDetails.class))
                .thenThrow(new HttpServerErrorException(HttpStatus.TOO_MANY_REQUESTS));
        ResponseEntity<Employee> response = employeeService.createEmployee(mockEmployeeInput);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testCreateEmployee_InternalServerError() {
        when(restTemplate.postForObject(url, mockEmployeeInput, EmployeeDetails.class))
                .thenThrow(new RuntimeException("Internal Server Error"));
        ResponseEntity<Employee> response = employeeService.createEmployee(mockEmployeeInput);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteEmployeeById_NotFound() {
        when(restTemplate.getForObject(searchApiUrl + employeeId, EmployeeDetails.class))
                .thenReturn(null);
        ResponseEntity<String> response = employeeService.deleteEmployeeById(employeeId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteEmployeeById_TooManyRequests() {
        when(restTemplate.getForObject(searchApiUrl + employeeId, EmployeeDetails.class))
                .thenThrow(new HttpServerErrorException(HttpStatus.TOO_MANY_REQUESTS));
        ResponseEntity<String> response = employeeService.deleteEmployeeById(employeeId);
        assertEquals(HttpStatus.TOO_MANY_REQUESTS, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    void testDeleteEmployeeById_InternalServerError() {
        when(restTemplate.getForObject(searchApiUrl + employeeId, EmployeeDetails.class))
                .thenThrow(new RuntimeException("Internal Server Error"));
        ResponseEntity<String> response = employeeService.deleteEmployeeById(employeeId);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertNull(response.getBody());
    }
}

