package com.college.patient_admission.Services.Staff;

import org.springframework.stereotype.Service;

import com.college.patient_admission.Models.Staff.StaffAuth;
import com.college.patient_admission.Repository.Staff.StaffAuthRepo;

@Service
public class StaffAuthService {
	StaffAuthRepo staffAuthRepo;
	
	public StaffAuthService(StaffAuthRepo staffAuthRepo) {
		this.staffAuthRepo = staffAuthRepo;
	}
	
	public StaffAuth getAuthByStaffById(Long id) {
		return staffAuthRepo.findByStaff_ID(id)
				.orElseThrow(() -> new RuntimeException("Default admin not found"));
	}
}
