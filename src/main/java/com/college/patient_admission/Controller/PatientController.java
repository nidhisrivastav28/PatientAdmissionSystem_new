package com.college.patient_admission.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.college.patient_admission.Models.Patient;
import com.college.patient_admission.Services.PatientService;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    // Login + Signup page
    @GetMapping("/login")
    public String patientLoginSignup(Model model) {
        model.addAttribute("patient", new Patient()); // for signup form binding
        return "patient/login"; // single page with both forms
    }

    // Login handling
    @PostMapping("/login")
    public String login(@RequestParam String pemail,
                        @RequestParam String ppswd,
                        Model model) {

        Patient patient = patientService.validateUser(pemail, ppswd);
        if (patient != null) {
            return "redirect:/patient/dashboard";
        } else {
            model.addAttribute("loginError", "Invalid email or password");
            return "patient/login";
        }
    }

    // Signup handling
    @PostMapping("/register")
    public String register(@ModelAttribute Patient patient,
                           @RequestParam String confirmPassword,
                           Model model) {

        if (!patient.getPpswd().equals(confirmPassword)) {
            model.addAttribute("signupError", "Passwords do not match");
            return "patient/login"; // same page with error
        }

        patientService.registerPatient(patient, confirmPassword);
        model.addAttribute("signupSuccess", "Registration successful! Please login.");
        return "patient/login"; // same page after signup
    }

    // Patient dashboard
    @GetMapping("/dashboard")
    public String dashboard() {
        return "patient/dashboard";
    }
}