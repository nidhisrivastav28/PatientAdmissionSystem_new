package com.college.patient_admission.Controller.Patient;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.college.patient_admission.Models.Patient.Patient;
import com.college.patient_admission.Services.Patient.PatientService;

import jakarta.servlet.http.HttpSession;

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
            Model model,
            HttpSession session) {

        Patient patient = patientService.validateUser(pemail, ppswd);
        if (patient != null) {
            session.setAttribute("loggedInPatient", patient);
            session.setAttribute("patientId", patient.getId());
            session.setAttribute("role", "patient");
            return "redirect:/patient/dashboard/" + patient.getId();
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
    @GetMapping("/dashboard/{id}")
    public String dashboard(@PathVariable Long id, Model model, HttpSession session) {
        // Try to get patient from session first
        Patient patient = (Patient) session.getAttribute("loggedInPatient");

        if (patient == null || !patient.getId().equals(id)) {
            // Fallback to DB fetch
            patient = patientService.getPatientById(id);
            if (patient == null) {
                return "redirect:/patient/login";
            }
            session.setAttribute("loggedInPatient", patient);
        }

        // Now add both to model
        model.addAttribute("patient", patient);
        model.addAttribute("patientId", patient.getId());

        return "patient/dashboard";
    }

    // Appointments page
    // @GetMapping("/appointments/{id}")
    // public String appointments(@PathVariable Long id, Model model) {
    // Patient patient = patientService.findById(id);
    // if (patient == null) {
    // return "redirect:/patient/login";
    // }

    // model.addAttribute("patient", patient);
    // model.addAttribute("appointments",
    // patientService.getAppointmentsByPatient(id));

    // return "patient/appointments";
    // }

    // Prescriptions page
    // @GetMapping("/prescriptions/{id}")
    // public String prescriptions(@PathVariable Long id, Model model) {
    // Patient patient = patientService.findById(id);
    // if (patient == null) {
    // return "redirect:/patient/login";
    // }

    // model.addAttribute("patient", patient);
    // model.addAttribute("prescriptions",
    // patientService.getPrescriptionsByPatient(id));

    // return "patient/prescriptions";
    // }

    // Reports page
    // @GetMapping("/reports/{id}")
    // public String reports(@PathVariable Long id, Model model) {
    // Patient patient = patientService.findById(id);
    // if (patient == null) {
    // return "redirect:/patient/login";
    // }
    // model.addAttribute("patient", patient);
    // model.addAttribute("reports", patientService.getReportsByPatient(id));
    // return "patient/reports";
    // }

    // Billing page
    // @GetMapping("/billing/{id}")
    // public String billing(@PathVariable Long id, Model model) {
    // Patient patient = patientService.findById(id);
    // if (patient == null) {
    // return "redirect:/patient/login";
    // }
    // model.addAttribute("patient", patient);
    // model.addAttribute("bills", patientService.getBillsByPatient(id));
    // return "patient/billing";
    // }

    @GetMapping("/profile/{id}")
    public String profile(@PathVariable Long id, Model model) {
        Patient patient = patientService.getPatientById(id);
        if (patient == null) {
            return "redirect:/patient/login";
        }
        model.addAttribute("patient", patient); 
        return "patient/profile";
    }

    // Feedback page
    // @GetMapping("/feedback/{id}")
    // public String feedback(@PathVariable Long id, Model model) {
    // Patient patient = patientService.findById(id);
    // if (patient == null) {
    // return "redirect:/patient/login";
    // }
    // model.addAttribute("patient", patient);
    // return "patient/feedback";
    // }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/patient/login";
    }
}