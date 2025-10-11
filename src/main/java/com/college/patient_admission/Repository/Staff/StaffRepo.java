package com.college.patient_admission.Repository.Staff;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.patient_admission.Models.Staff.Staff;
import com.college.patient_admission.Models.Staff.StaffAuth;

public interface StaffRepo extends JpaRepository<Staff, Long> {
    Optional<Staff> findByAuth(StaffAuth auth);
}