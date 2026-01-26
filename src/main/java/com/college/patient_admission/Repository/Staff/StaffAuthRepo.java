package com.college.patient_admission.Repository.Staff;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.college.patient_admission.Models.Staff.StaffAuth;

public interface StaffAuthRepo extends JpaRepository<StaffAuth, Long> {
    Optional<StaffAuth> findByEmail(String email);
    Optional<StaffAuth> findByStaff_ID(Long staffId);
}