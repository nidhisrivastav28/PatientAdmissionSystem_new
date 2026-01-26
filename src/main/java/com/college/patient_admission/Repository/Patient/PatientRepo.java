package com.college.patient_admission.Repository.Patient;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.patient_admission.Models.Patient.Patient;

import java.util.Optional;

public interface PatientRepo extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPemail(String pemail);
}