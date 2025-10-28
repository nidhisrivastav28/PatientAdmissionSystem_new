package com.college.patient_admission;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.college.patient_admission.Models.Gender;
import com.college.patient_admission.Services.Staff.AdminService;

@Component
public class DefaultAdminLoader implements CommandLineRunner {

    private final AdminService adminService;

    public DefaultAdminLoader(AdminService adminService) {
        this.adminService = adminService;
    }

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        String defaultEmail = "admin@college.com";

        // Check if default admin exists
        if (adminService.getAuthRepo().findByEmail(defaultEmail).isEmpty()) {

            // Create default admin
            adminService.createAdmin(
                "Default Admin",          // name
                defaultEmail,             // email
                "admin123",               // password
                "1234567890",             // phone
                "123 Admin Street",       // address
                LocalDate.of(1980, 1, 1),// date of birth
                Gender.MALE,              // gender
                "B.Tech",				  // qualification		
                50000.0,                  // salary
                LocalDate.now()           // start date
            );

            System.out.println("Default admin created: " + defaultEmail);
        }
    }
}