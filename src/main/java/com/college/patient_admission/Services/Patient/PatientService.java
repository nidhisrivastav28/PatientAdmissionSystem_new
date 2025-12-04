package com.college.patient_admission.Services.Patient;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.college.patient_admission.Models.Appointment;
import com.college.patient_admission.Models.Patient.Patient;
import com.college.patient_admission.Repository.AppointmentRepo;
// import com.college.patient_admission.Repository.AppointmentRepo;
import com.college.patient_admission.Repository.Patient.PatientRepo;

@Service
public class PatientService {

    @Autowired
    private PatientRepo patientRepo;

    @Autowired(required = false)
    private AppointmentRepo appointmentRepo;

    public List<Appointment> getAppointmentsByPatient(Long patientId) {
        return appointmentRepo.findByPatient_Id(patientId);
    }

    // Not present in current code but needed for completeness
    // @Autowired(required = false)
    // private BillRepo billRepo;

    // @Autowired(required = false)
    // private ReportRepo reportRepo;

    // @Autowired(required = false)
    // private PrescriptionRepo prescriptionRepo;

    // Registration Method
    public Patient registerPatient(Patient patient, String cpswd) {
        // Check if passwords match
        if (!patient.getPpswd().equals(cpswd)) {
            throw new RuntimeException("Passwords do not match");
        }

        // Check if email already exists
        if (patientRepo.findByPemail(patient.getPemail()).isPresent()) {
            throw new RuntimeException("Email already registered");
        }

        // Save patient to DB
        return patientRepo.save(patient);
    }

    // Login Validation
    public Patient validateUser(String email, String pswd) {
        // Fetch patient by email
        Patient user = patientRepo.findByPemail(email).orElse(null);

        // Check password
        if (user != null && user.getPpswd().equals(pswd)) {
            return user;
        }
        return null;
    }

    // Get Patient by ID
    public Patient getPatientById(Long id) {
        return patientRepo.findById(id).orElseThrow(null);
    }

    public Object getRecentAppointments(Long patientId) {
        // abhi koi logic nahi (future ke liye placeholder)
        return null;
    }

    public Object getPendingBills(Long patientId) {
        // abhi koi logic nahi (future ke liye place holder)
        return null;
    }

    // Get Patient Count
    public long getPatientCount() {
        return patientRepo.count();
    }

    // CHANGE METHOD FOR PATIENTS
    public List<Patient> getAllPatients() {
        return patientRepo.findAll();
    }

    public Patient findById(Long id) {
        return patientRepo.findById(id).orElse(null);
    }

}