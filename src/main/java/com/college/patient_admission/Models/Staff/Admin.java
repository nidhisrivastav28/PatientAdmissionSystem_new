package com.college.patient_admission.Models.Staff;

import java.time.LocalDate;

import com.college.patient_admission.Models.Gender;

import jakarta.persistence.Entity;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;

@Entity
@PrimaryKeyJoinColumn(name = "staff_id")
@Data
public class Admin extends Staff{
	
	public Admin() {}
	
	public Admin(String name, String phone, String address, LocalDate dob, Gender gender, String qualification, double salary, LocalDate startDate, int experience) {
		super(name, phone, address, dob, gender, qualification, salary, startDate, 0);
	}
}
