package com.college.patient_admission.Controller;

// import com.patient_admission.Services.patient.PatientService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

// Common Controller (landing page)
@Controller
public class CommonController {

    // Landing Page
    @GetMapping("/")
    public String homePage(){
        return "index";
    }


}
