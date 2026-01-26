package com.college.patient_admission.Repository.Staff;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.patient_admission.Models.Staff.Admin;
import com.college.patient_admission.Models.Staff.StaffAuth;

public interface AdminRepo extends JpaRepository<Admin, Long> {
    Optional<Admin> findByAuth(StaffAuth auth);
}