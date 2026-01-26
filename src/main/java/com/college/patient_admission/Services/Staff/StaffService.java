package com.college.patient_admission.Services.Staff;

import java.util.NoSuchElementException;

import org.springframework.stereotype.Service;

import com.college.patient_admission.Models.Staff.Staff;
import com.college.patient_admission.Repository.Staff.StaffRepo;

@Service
public class StaffService {
	private final StaffRepo staffRepo;
	
	public StaffService(StaffRepo staffRepo) {
		this.staffRepo = staffRepo;
	}
    
    public Staff getStaffById(Long id) {
        return staffRepo.findById(id)
            .orElseThrow(() -> new NoSuchElementException("Staff not found"));
    }
}
