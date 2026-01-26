package com.college.patient_admission.Repository.Staff;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.patient_admission.Models.Staff.Doctor;
import com.college.patient_admission.Models.Staff.StaffAuth;

public interface DoctorRepo extends JpaRepository<Doctor, Long> {
	Optional<Doctor> findByAuth(StaffAuth auth);
}