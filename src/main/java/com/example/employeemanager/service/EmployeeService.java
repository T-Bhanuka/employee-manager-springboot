package com.example.employeemanager.service;

import com.example.employeemanager.model.Employee;
import com.example.employeemanager.repository.EmployeeRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class EmployeeService {

    private final EmployeeRepository repository;

    public EmployeeService(EmployeeRepository repository) {
        this.repository = repository;
    }

    public List<Employee> findAll(String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            return repository.findByFullNameContainingIgnoreCase(keyword);
        }
        return repository.findAll();
    }

    public Employee findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid employee Id: " + id));
    }

    public boolean emailExists(String email) {
        return repository.existsByEmail(email);
    }

    public boolean emailExistsForOther(String email, Long id) {
        return repository.findByEmail(email)
                .map(emp -> !emp.getId().equals(id))
                .orElse(false);
    }

    public Employee save(Employee employee) {
        return repository.save(employee);
    }

    public void deleteById(Long id) {
        repository.deleteById(id);
    }
}