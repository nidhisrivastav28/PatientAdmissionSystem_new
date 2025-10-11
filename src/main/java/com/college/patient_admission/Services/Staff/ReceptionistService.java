package com.college.patient_admission.Services.Staff;

import java.time.LocalDate;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.college.patient_admission.Models.Gender;
import com.college.patient_admission.Models.Staff.Receptionist;
import com.college.patient_admission.Models.Staff.Shift;
import com.college.patient_admission.Models.Staff.Staff;
import com.college.patient_admission.Models.Staff.StaffAuth;
import com.college.patient_admission.Repository.Staff.ReceptionistRepo;
import com.college.patient_admission.Repository.Staff.StaffAuthRepo;
import com.college.patient_admission.Repository.Staff.StaffRepo;

@Service
public class ReceptionistService {

	private final StaffAuthRepo staffAuthRepo;
	private final StaffRepo staffRepo;
	private final ReceptionistRepo receptionistRepo;
	
	public ReceptionistService(StaffAuthRepo staffAuthRepo, StaffRepo staffRepo, ReceptionistRepo receptionistRepo) {
		this.staffAuthRepo = staffAuthRepo;
		this.staffRepo = staffRepo;
		this.receptionistRepo = receptionistRepo;
	}
	
    @Transactional
    public Receptionist register(String name, String email, String phone,
                               String address, LocalDate dob, Gender gender, String qualification, double salary,
                               LocalDate startDate, String desk, Shift shift) {

        if (staffAuthRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + email);
        }

        Receptionist receptionist = new Receptionist(name, phone, address, dob, gender, qualification, salary, startDate, desk, shift);

        StaffAuth auth = new StaffAuth();
        auth.setEmail(email);
        auth.setStaff(receptionist);

        receptionist.setAuth(auth);

        return receptionistRepo.save(receptionist);
    }
	
	public Receptionist getReceptionistById(Long id) {
		return receptionistRepo.findById(id).orElseThrow();
	}
    
    @Transactional
    public void updateDetails(Long id, String email, String phone, String address) {
        Staff staff = staffRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        if(!(staff instanceof Receptionist)) {
        	System.out.println("Not a Receptionist");
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
}
