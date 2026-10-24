package com.example.employeemanager.controller;

import com.example.employeemanager.model.Employee;
import com.example.employeemanager.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class EmployeeController {

    private final EmployeeService service;

    public EmployeeController(EmployeeService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/employees";
    }

    @GetMapping("/employees")
    public String listEmployees(@RequestParam(required = false) String keyword, Model model) {
        model.addAttribute("employees", service.findAll(keyword));
        model.addAttribute("employee", new Employee());
        model.addAttribute("keyword", keyword);
        return "employees";
    }

    @PostMapping("/employees")
    public String saveEmployee(@Valid @ModelAttribute("employee") Employee employee,
                               BindingResult result,
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (service.emailExists(employee.getEmail())) {
            result.rejectValue("email", "error.employee", "This email is already registered");
        }
        if (result.hasErrors()) {
            model.addAttribute("employees", service.findAll(null));
            return "employees";
        }
        service.save(employee);
        redirectAttributes.addFlashAttribute("successMessage", "Employee saved successfully!");
        return "redirect:/employees";
    }

    @GetMapping("/employees/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("employee", service.findById(id));
        return "edit-employee";
    }

    @PostMapping("/employees/{id}/update")
    public String updateEmployee(@PathVariable Long id,
                                 @Valid @ModelAttribute("employee") Employee employee,
                                 BindingResult result,
                                 RedirectAttributes redirectAttributes) {
        if (service.emailExistsForOther(employee.getEmail(), id)) {
            result.rejectValue("email", "error.employee", "This email is already registered");
        }
        if (result.hasErrors()) {
            employee.setId(id);
            return "edit-employee";
        }
        employee.setId(id);
        service.save(employee);
        redirectAttributes.addFlashAttribute("successMessage", "Employee updated successfully!");
        return "redirect:/employees";
    }

    @PostMapping("/employees/{id}/delete")
    public String deleteEmployee(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        service.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "Employee deleted successfully!");
        return "redirect:/employees";
    }
}
