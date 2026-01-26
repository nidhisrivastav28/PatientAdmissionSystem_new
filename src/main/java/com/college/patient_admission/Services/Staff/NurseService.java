package com.college.patient_admission.Services.Staff;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.college.patient_admission.Models.Gender;
import com.college.patient_admission.Models.Staff.Nurse;
import com.college.patient_admission.Models.Staff.Shift;
import com.college.patient_admission.Models.Staff.Staff;
import com.college.patient_admission.Models.Staff.StaffAuth;
import com.college.patient_admission.Repository.Staff.NurseRepo;
import com.college.patient_admission.Repository.Staff.StaffAuthRepo;
import com.college.patient_admission.Repository.Staff.StaffRepo;

@Service
public class NurseService {

	StaffAuthRepo staffAuthRepo;
	private final StaffRepo staffRepo;
	NurseRepo nurseRepo;
	
	public NurseService(StaffAuthRepo staffAuthRepo, StaffRepo staffRepo, NurseRepo nurseRepo) {
		this.staffAuthRepo = staffAuthRepo;
		this.staffRepo = staffRepo;
		this.nurseRepo = nurseRepo; 
	}
    
    @Transactional
    public Nurse register(String name, String email, String phone,
                               String address, LocalDate dob, Gender gender, String qualification, double salary,
                               LocalDate startDate, int experience, String department, Shift shift) {

        if (staffAuthRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + email);
        }

        Nurse nurse = new Nurse(name, phone, address, dob, gender, qualification, salary, startDate, experience, department, shift);

        StaffAuth auth = new StaffAuth();
        auth.setEmail(email);
        auth.setStaff(nurse);

        nurse.setAuth(auth);

        return nurseRepo.save(nurse);
    }
    
    public Nurse getNurseById(Long id) {
		return nurseRepo.findById(id).orElseThrow();
	}
    
    @Transactional
    public void updateDetails(Long id, String email, String phone, String address) {
        Staff staff = staffRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        if(!(staff instanceof Nurse)) {
        	System.out.println("Not a nurse");
        	return;
        }
        boolean updated = false;
        if (email != null) {
            staff.getAuth().setEmail(email);
            updated = true;
        }
        if (phone != null) {
            staff.setPhone(phone);
            updated = true;
        }
        
        if(address != null) {
        	staff.setAddress(address);
            updated = true;
        }

        if (updated) {
            staffRepo.save(staff); // optional; save() on managed entity is safe but not required
        }
    }
    
    public List<Nurse> getAllNurses(){
    	return nurseRepo.findAll();
    }
}
