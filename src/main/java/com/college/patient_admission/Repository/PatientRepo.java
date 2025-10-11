package com.college.patient_admission.Repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.patient_admission.Models.Patient;

import java.util.Optional;

public interface PatientRepo extends JpaRepository<Patient, Long> {
    Optional<Patient> findByPemail(String pemail);
}