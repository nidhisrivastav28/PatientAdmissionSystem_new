package com.college.patient_admission.Repository.Staff;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.patient_admission.Models.Staff.Receptionist;
import com.college.patient_admission.Models.Staff.StaffAuth;

public interface ReceptionistRepo extends JpaRepository<Receptionist, Long> {
	Optional<Receptionist> findByAuth(StaffAuth auth);
}
