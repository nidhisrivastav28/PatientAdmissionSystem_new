package com.college.patient_admission.Services.Patient;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.college.patient_admission.Models.Patient.Patient;
import com.college.patient_admission.Repository.Patient.PatientRepo;

@Service
public class PatientService {

    @Autowired
    private PatientRepo patientRepo;

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
    public Patient validateUser(String email, String pswd){
        // Fetch patient by email
        Patient user = patientRepo.findByPemail(email).orElse(null);

        // Check password
        if(user != null && user.getPpswd().equals(pswd)){
            return user;
        }
        return null;
    }
    
    public Patient getPatientById(Long id) {
		return patientRepo.findById(id).orElseThrow();
	}
    
    public long getPatientCount() { return patientRepo.count(); }
    
    //CHANGE METHOD FOR PATIENTS
    public List<Patient> getAllPatients(){
    	return patientRepo.findAll();
    }


}