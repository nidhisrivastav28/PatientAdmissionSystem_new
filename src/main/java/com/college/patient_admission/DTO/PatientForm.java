package com.college.patient_admission.DTO;

import com.college.patient_admission.Models.Gender;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class PatientForm {
    @NotBlank(message = "Name is required")
    @Pattern(
            regexp = "^[A-Za-z ]+$",
            message = "Name must contain only letters and spaces"
    )
    private String name;

    @Email(message = "Invalid email format")
    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank(message = "Phone no. is required")
    @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits")
    private String phone;


    private String password;

    @NotNull(message = "Date of birth is required")
    @PastOrPresent(message = "Date of birth cannot be in the future")
    private LocalDate dob;

    @NotNull(message = "Gender is required")
    private Gender gender;
}