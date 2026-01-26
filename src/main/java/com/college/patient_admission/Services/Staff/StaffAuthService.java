package com.college.patient_admission.Services.Staff;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.college.patient_admission.Models.Staff.StaffAuth;
import com.college.patient_admission.Repository.Staff.StaffAuthRepo;


@Service
public class StaffAuthService {
	@Autowired
	private StaffAuthRepo staffAuthRepo;
	
	@Autowired
	private JavaMailSender mailSender;
	private Map<String, String> otpStorage = new HashMap<>();

	public StaffAuthService(StaffAuthRepo staffAuthRepo) {
		this.staffAuthRepo = staffAuthRepo;
	}
	
	public StaffAuth getAuthByStaffById(Long id) {
		return staffAuthRepo.findByStaff_ID(id)
				.orElseThrow(() -> new RuntimeException("Default admin not found"));
	}

	// for sending otp to user
	public String sendOtp(String email){
		Optional<StaffAuth> staff = staffAuthRepo.findByEmail(email);
		if(staff.isEmpty()){
			return "Email not found";
		}
		String otp = String.valueOf((int) (Math.random() *900000) + 100000);
		otpStorage.put(email,otp);

		try{
			SimpleMailMessage msg = new SimpleMailMessage();
			msg.setTo(email);
			msg.setSubject("Your OTP Code");
			msg.setText("Your OTP for password setup is: "+otp);
			mailSender.send(msg);
			return "OTP sent successfully!";
		}catch(Exception e){
			return "Failed to send OYP: "+e.getMessage();
		}
	}
	// Verifing the otp
	public boolean verifyOTP(String email, String otp){
		return otpStorage.containsKey(email) && otpStorage.get(email).equals(otp);
	}
	// Setting password after otp verification
	public String setPswd(String email, String pswd){
		Optional<StaffAuth> staffOpt = staffAuthRepo.findByEmail(email);
		if(staffOpt.isEmpty()){
			return "Email not found!";
		}
		StaffAuth staff = staffOpt.get();
		staff.setPassword(new BCryptPasswordEncoder().encode(pswd));
		staffAuthRepo.save(staff);

		otpStorage.remove(email);
		return "Password set successfully!";
	}

	public StaffAuth getAuthByEmail(String email) {
        return staffAuthRepo.findByEmail(email).orElse(null);
    }

    public void save(StaffAuth staff) {
        staffAuthRepo.save(staff);
    }
}
