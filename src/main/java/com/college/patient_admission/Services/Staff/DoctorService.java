package com.college.patient_admission.Services.Staff;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.college.patient_admission.Models.Gender;
import com.college.patient_admission.Models.Staff.Doctor;
import com.college.patient_admission.Models.Staff.Staff;
import com.college.patient_admission.Models.Staff.StaffAuth;
import com.college.patient_admission.Repository.Staff.DoctorRepo;
import com.college.patient_admission.Repository.Staff.StaffAuthRepo;
import com.college.patient_admission.Repository.Staff.StaffRepo;

@Service
public class DoctorService {
	private final StaffAuthRepo staffAuthRepo;
	private final StaffRepo staffRepo;
	private final DoctorRepo doctorRepo;
	
	public DoctorService(StaffAuthRepo staffAuthRepo, StaffRepo staffRepo, DoctorRepo doctorRepo) {
		this.staffAuthRepo = staffAuthRepo;
		this.staffRepo = staffRepo;
		this.doctorRepo = doctorRepo;
	}
	
	@Transactional
    public Doctor register(String name, String email, String phone,
                               String address, LocalDate dob, Gender gender, String qualification, double salary,
                               LocalDate startDate, String specialisation, String licenseNumber) {

        if (staffAuthRepo.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("Email already in use: " + email);
        }

        Doctor doctor = new Doctor(name, phone, address, dob, gender, qualification, salary, startDate, specialisation, licenseNumber);

        StaffAuth auth = new StaffAuth();
        auth.setEmail(email);
        auth.setStaff(doctor);

        doctor.setAuth(auth);

        return doctorRepo.save(doctor);
    }
	
	public Doctor getDoctorById(Long id) {
		return doctorRepo.findById(id).orElseThrow();
	}
    
    public long getDoctorCount() { return doctorRepo.count(); }
    
    @Transactional
    public void updateDetails(Long id, String email, String phone, String address) {
        Staff staff = staffRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found"));
        if(!(staff instanceof Doctor)) {
        	System.out.println("Not a doctor");
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

    public List<Doctor> getAllDoctors() {
        return doctorRepo.findAll();
    }
    
}
