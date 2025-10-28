package com.college.patient_admission.Controller.Staff;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.college.patient_admission.Models.Staff.StaffAuth;
import com.college.patient_admission.Services.Staff.StaffAuthService;

@Controller
@RequestMapping("/staff")
public class CommonStaffController {
    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private StaffAuthService staffAuthService;

    private String generatedOtp;

    @GetMapping("/login")
    public String showLoginPage(@RequestParam(required = false) String role, Model model) {
        if (role == null || role.isEmpty())
            role = "Staff";
        model.addAttribute("role", capitalize(role));
        return "staffs/commonLogin";
    }

    // OTP vala Part starts
    @PostMapping("/send-otp")
    public String sendOtp(@RequestParam String email, @RequestParam String role, Model model) {
        if (role == null || role.isEmpty())
            role = "Staff";
        try {
            StaffAuth staff = staffAuthService.getAuthByEmail(email);
            if (staff == null) {
                model.addAttribute("error", "Email not registered! Contact Admin.");
            } else if (staff.getPassword() == null || staff.getPassword().isEmpty()) {
                // Generate OTP
                generatedOtp = String.format("%06d", new Random().nextInt(900000));

                // Send mail
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setTo(email);
                msg.setFrom("www.nidhisrivastav@gmail.com"); // must match your Gmail
                msg.setSubject("Your Staff OTP - Patient Admission System");
                msg.setText("Your OTP for password setup is: " + generatedOtp);
                mailSender.send(msg);

                System.out.println("OTP sent successfully to " + email);

                model.addAttribute("otpSent", true);
                model.addAttribute("email", email);
                model.addAttribute("role", capitalize(role));
            } else {
                // Password already set → disable OTP section
                model.addAttribute("passwordAlreadySet", true);
                model.addAttribute("email", email);
                model.addAttribute("role", capitalize(role));
                model.addAttribute("error", "Password already set! Please login.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("error", "Mail sending failed: " + e.getMessage());
        }

        return "staffs/commonLogin";
    }

    @PostMapping("/verify-otp")
    public String verifyOtp(@RequestParam String email, @RequestParam String otp, @RequestParam String role,
            Model model) {
        if (role == null || role.isEmpty())
            role = "Staff";
        if (otp.equals(generatedOtp)) {
            model.addAttribute("email", email);
            model.addAttribute("role", role);
            model.addAttribute("otpVerified", true);
        } else {
            model.addAttribute("email", email);
            model.addAttribute("role", role);
            model.addAttribute("error", "Invalid OTP!");
        }
        return "staffs/commonLogin";
    }

    @PostMapping("/set-password")
    public String setPassword(@RequestParam String email, @RequestParam String password, @RequestParam String role,
            Model model) {
        if (role == null || role.isEmpty())
            role = "Staff";
        StaffAuth staff = staffAuthService.getAuthByEmail(email);
        if (staff != null) {
            staff.setPassword(new BCryptPasswordEncoder().encode(password));
            staffAuthService.save(staff);
            model.addAttribute("success", "Password set successfully! Now login.");
        } else {
            model.addAttribute("error", "Staff not found!");
        }
        return "staffs/commonLogin";
    }

    @PostMapping("/login")
    public String handleLogin(
            @RequestParam String role,
            @RequestParam String username,
            @RequestParam String password,
            Model model) {
        if (role == null || role.isEmpty())
            role = "Staff";
        // Dummy logic — replace with your actual service/database validation
        StaffAuth staff = staffAuthService.getAuthByEmail(username);

        if (staff != null && new BCryptPasswordEncoder().matches(password, staff.getPassword())) {
            Long staffId = staff.getStaff().getID();
//             System.out.println("Input password: " + password);
// System.out.println("Stored hashed password: " + staff.getPassword());
// System.out.println("Password match: " + new BCryptPasswordEncoder().matches(password, staff.getPassword()));
// System.out.println("Role: " + role);

            switch (role.toLowerCase()) {
                case "doctor":
                    return "redirect:/doctor/dashboard/" + staffId;
                case "nurse":
                    return "redirect:/nurse/dashboard/" + staffId;
                case "receptionist":
                    return "redirect:/reception/dashboard/" + staffId;
                default:
                    return "redirect:/staff/dashboard/" + staffId;
            }
        } else {
            model.addAttribute("role", capitalize(role));
            model.addAttribute("error", "Invalid email or password!");
            return "staffs/commonLogin";
        }

    }

    private String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
