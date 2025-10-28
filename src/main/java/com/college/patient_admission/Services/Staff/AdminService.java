package com.college.patient_admission.Services.Staff;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.college.patient_admission.Models.Gender;
import com.college.patient_admission.Models.Staff.Admin;
import com.college.patient_admission.Models.Staff.Doctor;
import com.college.patient_admission.Models.Staff.Receptionist;
import com.college.patient_admission.Models.Staff.Staff;
import com.college.patient_admission.Models.Staff.StaffAuth;
import com.college.patient_admission.Repository.Staff.AdminRepo;
import com.college.patient_admission.Repository.Staff.DoctorRepo;
import com.college.patient_admission.Repository.Staff.ReceptionistRepo;
import com.college.patient_admission.Repository.Staff.StaffAuthRepo;
import com.college.patient_admission.Repository.Staff.StaffRepo;

@Service
public class AdminService {

	@Autowired
	private final StaffAuthRepo authRepo;
    private final AdminRepo adminRepo;
    private final DoctorRepo doctorRepo;
    private final ReceptionistRepo receptionistRepo;
    private final StaffRepo staffRepo;

    public AdminService(StaffAuthRepo authRepo, AdminRepo adminRepo, DoctorRepo doctorRepo, 
    		ReceptionistRepo receptionistRepo, StaffRepo staffRepo) {
        this.authRepo = authRepo;
        this.adminRepo = adminRepo;
        this.doctorRepo = doctorRepo;
        this.receptionistRepo = receptionistRepo;
        this.staffRepo = staffRepo;
    }

	public StaffAuthRepo getAuthRepo() {
		return authRepo;
	}
    
    @Transactional
    public Admin createAdmin(String name, String email, String password, String phone, String address, LocalDate dob, Gender gender, String qualification, double salary, LocalDate startDate) {
    	if (authRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + email);
        }
    	
        Admin admin = new Admin(name, phone, address, dob, gender, qualification, salary, startDate);

        StaffAuth auth = new StaffAuth();
        auth.setEmail(email);
        auth.setPassword(password);
        auth.setStaff(admin);
        
        admin.setAuth(auth);

        adminRepo.save(admin);
        authRepo.save(auth);

        return admin;
    }
    
    @Transactional(readOnly = true)  // ensures lazy-loaded staff is available
    public StaffAuth getAdmin() {
        return authRepo.findByEmail("admin@college.com")
                .orElseThrow(() -> new RuntimeException("Default admin not found"));
    }
    
    public long getBookingCount() { return doctorRepo.count(); }
    
    public long getTodaySessionCount() { return doctorRepo.count(); }
    
    public List<Doctor> getAllDoctors(){
    	return doctorRepo.findAll();
    }
    
    public List<Receptionist> getAllReceptionists(){
    	return receptionistRepo.findAll();
    }
    
    public StaffAuth getStaffAuthById(Long ID) {
    	return authRepo.findByStaff_ID(ID)
                .orElseThrow(() -> new RuntimeException("Record not found"));
    }
    
    @Transactional
    public void updateStaff(Long staffId, Double salary, LocalDate endDate) {
        Staff staff = staffRepo.findById(staffId)
                .orElseThrow(() -> new RuntimeException("Default admin not found"));
        boolean updated = false;
        if (salary != null) {
            staff.setSalary(salary);
            updated = true;
        }
        if (endDate != null) {
            staff.setEndDate(endDate);
            updated = true;
        }

        if (updated) {
            // If @Transactional, this line is optional:
            staffRepo.save(staff); // optional; save() on managed entity is safe but not required
        }
    }



}
