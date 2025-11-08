package com.college.patient_admission.Controller.Staff;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.college.patient_admission.Models.Staff.Doctor;
import com.college.patient_admission.Models.Staff.Nurse;
import com.college.patient_admission.Models.Staff.Receptionist;
import com.college.patient_admission.Models.Staff.Staff;
import com.college.patient_admission.Models.Staff.StaffAuth;
import com.college.patient_admission.Services.Staff.StaffAuthService;

import jakarta.servlet.http.HttpSession;

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
        if (role == null || role.isEmpty()) {
            role = "Staff";
        }
        try {
            StaffAuth staffAuth = staffAuthService.getAuthByEmail(email);

            if (staffAuth == null) {
                model.addAttribute("error", "Email not registered! Contact Admin.");
                model.addAttribute("role", capitalize(role));
                return "staffs/commonLogin";
            }

            // ✅ Check if this email belongs to the selected role
            Staff staff = staffAuth.getStaff();
            boolean roleMatches = false;

            switch (role.toLowerCase()) {
                case "admin":
                    roleMatches = true;
                    break;
                case "doctor":
                    roleMatches = staff instanceof Doctor;
                    break;
                case "nurse":
                    roleMatches = staff instanceof Nurse;
                    break;
                case "receptionist":
                    roleMatches = staff instanceof Receptionist;
                    break;
            }

            if (!roleMatches) {
                model.addAttribute("error", "This email does not belong to a " + capitalize(role) + "!");
                model.addAttribute("role", capitalize(role));
                model.addAttribute("info", "You can visit to change your role");
                model.addAttribute("role", capitalize(role));
                return "staffs/commonLogin";
            }

            // ✅ Proceed with OTP only if role matches
            if (staffAuth.getPassword() == null || staffAuth.getPassword().isEmpty()) {
                generatedOtp = String.format("%06d", new Random().nextInt(900000));

                // Send OTP mail
                SimpleMailMessage msg = new SimpleMailMessage();
                msg.setTo(email);
                msg.setFrom("www.nidhisrivastav@gmail.com");
                msg.setSubject("Your Staff OTP - Patient Admission System");
                msg.setText("Your OTP for password setup is: " + generatedOtp);
                mailSender.send(msg);

                System.out.println("OTP sent successfully to " + email);

                model.addAttribute("otpSent", true);
                model.addAttribute("email", email);
                model.addAttribute("role", capitalize(role));
                model.addAttribute("success", "OTP sent to your email!");
            } else {
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
            @RequestParam String role, // role user select kare
            @RequestParam String username,
            @RequestParam String password,
            Model model,
            HttpSession session) {

        StaffAuth staffAuth = staffAuthService.getAuthByEmail(username);

        if (staffAuth != null) {
            Staff staff = staffAuth.getStaff();
            boolean roleMatches = false;

            switch (role.toLowerCase()) {
                case "admin":
                    roleMatches = true;
                    break;
                case "doctor":
                    roleMatches = staff instanceof Doctor;
                    break;
                case "nurse":
                    roleMatches = staff instanceof Nurse;
                    break;
                case "receptionist":
                    roleMatches = staff instanceof Receptionist;
                    break;
            }

            // ✅ Password check: admin plain, others BCrypt
            boolean passwordMatches;
            if (role.equalsIgnoreCase("admin")) {
                passwordMatches = staffAuth.getPassword().equals(password); // plain text for admin
            } else {
                passwordMatches = new BCryptPasswordEncoder().matches(password, staffAuth.getPassword());
            }

            if (!roleMatches || !passwordMatches) {
                model.addAttribute("error", "Invalid email or password!");
                return "staffs/commonLogin";
            }

            session.setAttribute("loggedInStaff", staff);
            session.setAttribute("role", capitalize(role));

            Long staffId = (staff != null) ? staff.getID() : 0L; // admin can have dummy ID

            if (staff instanceof Doctor)
                return "redirect:/doctor/dashboard/" + staffId;
            if (staff instanceof Nurse)
                return "redirect:/nurse/dashboard/" + staffId;
            if (staff instanceof Receptionist)
                return "redirect:/receptionist/dashboard/" + staffId;
            if (role.equalsIgnoreCase("admin"))
                return "redirect:/admin/dashboard";

            return "redirect:/staff/dashboard/" + staffId;
        } else {
            model.addAttribute("error", "Invalid email or password!");
            return "staffs/commonLogin";
        }

    }

    private String capitalize(String text) {
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}
