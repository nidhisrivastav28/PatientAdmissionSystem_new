package com.college.patient_admission.Controller.Patient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
// import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.college.patient_admission.Models.Appointment;
import com.college.patient_admission.Models.Gender;
import com.college.patient_admission.Models.Patient.Patient;
import com.college.patient_admission.Models.Staff.Doctor;
import com.college.patient_admission.Repository.AppointmentRepo;
import com.college.patient_admission.Repository.Patient.PatientRepo;
import com.college.patient_admission.Repository.Staff.DoctorRepo;
import com.college.patient_admission.Services.AppointmentService;
import com.college.patient_admission.Services.Patient.PatientService;
import com.college.patient_admission.Services.Staff.DoctorService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private AppointmentRepo appointmentRepo;

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private DoctorService doctorService;

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
        model.addAttribute("activePage", "dashboard");

        List<Doctor> doctors = doctorService.getAllDoctors();
        model.addAttribute("doctors", doctors);

        return "patient/dashboard";
    }

    // Appointments page
    @GetMapping("/appointments/{id}")
    public String appointments(@PathVariable Long id, Model model) {
        Patient patient = patientService.findById(id);
        if (patient == null) {
            return "redirect:/patient/login";
        }
        model.addAttribute("patient", patient);
        // Upcoming / all appointments
        model.addAttribute(
                "appointments",
                appointmentService.getAppointmentsForPatient(id));

        // PAST APPOINTMENTS (yahin add hoga)
        // model.addAttribute("pastAppointments",appointmentRepo.findByPatient_IdAndAppointmentDateLessThanEqual(patient.getId(), LocalDate.now()));
        model.addAttribute("appointments", appointmentRepo.findByPatient_Id(patient.getId()));

        List<Doctor> doctors = doctorService.getAllDoctors();
        if (doctors == null) {
            doctors = new ArrayList<>();
        }
        model.addAttribute("doctors", doctors);
        model.addAttribute("activePage", "appointments");

        return "patient/appointments";
    }

    @PostMapping("/appointments/request")
    public String requestAppointment(
            @RequestParam Long patientId,
            @RequestParam Long doctorId,
            @RequestParam String appointmentDate,
            @RequestParam String from,
            @RequestParam String reason,
            RedirectAttributes redirectAttributes) {

        appointmentService.saveRequest(patientId, doctorId, appointmentDate, from, reason);

        redirectAttributes.addFlashAttribute("successMessage", "A new request sent to the hospital!");

        return "redirect:/patient/appointments/" + patientId;
    }

    // Prescriptions page
    @GetMapping("/report/{id}")
    public String getPatientReport(@PathVariable Long id, Model model) {

        Patient patient = patientService.findById(id);
        if (patient == null) {
            return "redirect:/patient/login";
        }
        // Fetch Prescription
        // Prescription prescription = prescriptionService.getByPatientId(patientId);

        // Fetch Medical Report
        // MedicalReport report = medicalReportService.getByPatientId(patientId);

        // Add data to model
        // model.addAttribute("prescription", prescription);
        // model.addAttribute("report", report);

        // For sidebar active highlight
        model.addAttribute("patient", patient);
        model.addAttribute("activePage", "prescriptions");

        return "patient/report"; // HTML page name
    }

    // Billing page
    @GetMapping("/billing/{id}")
    public String billing(@PathVariable Long id, Model model) {

        Patient patient = patientService.findById(id);
        if (patient == null) {
            return "redirect:/patient/login";
        }
        model.addAttribute("patient", patient);
        // model.addAttribute("bills", patientService.getBillsByPatient(id));
        model.addAttribute("activePage", "billing");
        return "patient/billing";
    }

    @GetMapping("/profile/{id}")
    public String profile(@PathVariable Long id, Model model) {
        Patient patient = patientService.getPatientById(id);
        if (patient == null) {
            return "redirect:/patient/login";
        }
        model.addAttribute("patient", patient);
        model.addAttribute("activePage", "profile");
        return "patient/profile";
    }

    // Feedback page
    @GetMapping("/feedback/{id}")
    public String feedback(@PathVariable Long id, Model model) {
        Patient patient = patientService.findById(id);
        if (patient == null) {
            return "redirect:/patient/login";
        }
        model.addAttribute("patient", patient);
        model.addAttribute("activePage", "feedback");
        return "patient/feedback";
    }

    // Logout
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/patient/login";
    }
}