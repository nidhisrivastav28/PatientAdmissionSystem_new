package com.college.patient_admission.DTO;

import com.college.patient_admission.Models.Gender;
import com.college.patient_admission.Models.Staff.Shift;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.Period;

@Data
public class StaffForm {

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


    private String address;

    @NotNull(message = "Date of birth is required")
    @AgeRange(min = 18, max = 60, message = "Age must be between 18 and 60 years")
    private LocalDate dob;

    @NotNull(message = "Gender is required")
    private Gender gender;

    private String qualification;

    @Positive(message = "Salary must be positive")
    private Double salary;

    @NotNull(message = "Experience is required")
    private int experience;

    @NotBlank(message = "Role is required")
    private String role;

    // Doctor-specific
    private String specialisation;
    private String licenseNumber;

    // Nurse-specific
    private String department;
    private Shift shift;

    // Receptionist-specific
    private String desk;

    @AssertTrue(message = "Doctor must have specialisation and license number")
    public boolean isDoctorValid() {
        if (!"DOCTOR".equalsIgnoreCase(role)) return true;
        return notBlank(specialisation) && notBlank(licenseNumber);
    }

    @AssertTrue(message = "Nurse must have department and shift")
    public boolean isNurseValid() {
        if (!"NURSE".equalsIgnoreCase(role)) return true;
        return notBlank(department) && shift != null;
    }

    @AssertTrue(message = "Receptionist must have desk and shift")
    public boolean isReceptionistValid() {
        if (!"RECEPTIONIST".equalsIgnoreCase(role)) return true;
        return notBlank(desk) && shift != null;
    }

    private boolean notBlank(String s) {
        return s != null && !s.isBlank();
    }
}


@Documented
@Constraint(validatedBy = AgeRangeValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@interface AgeRange {

    String message() default "Age must be between 18 and 60";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int min();

    int max();
}

class AgeRangeValidator implements ConstraintValidator<AgeRange, LocalDate> {

    private int min;
    private int max;

    @Override
    public void initialize(AgeRange constraintAnnotation) {
        this.min = constraintAnnotation.min();
        this.max = constraintAnnotation.max();
    }

    @Override
    public boolean isValid(LocalDate dob, ConstraintValidatorContext context) {
        if (dob == null) {
            return true; // @NotNull should handle null
        }

        if (dob.isAfter(LocalDate.now())) {
            return false;
        }

        int age = Period.between(dob, LocalDate.now()).getYears();
        return age >= min && age <= max;
    }
}