package com.college.patient_admission.Repository.Staff;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.patient_admission.Models.Staff.Nurse;
import com.college.patient_admission.Models.Staff.StaffAuth;

public interface NurseRepo extends JpaRepository<Nurse, Long> {
	Optional<Nurse> findByAuth(StaffAuth auth);
}