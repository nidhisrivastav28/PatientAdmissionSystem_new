package com.college.patient_admission.Models.Staff;

import java.time.LocalDate;
import java.util.List;

import com.college.patient_admission.Models.Appointment;
import com.college.patient_admission.Models.Gender;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@PrimaryKeyJoinColumn(name = "staff_id")
@Data
@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler" })
public class Doctor extends Staff {
	@Column(nullable = false)
	private String specialisation;

	@Column(nullable = false)
	private String licenseNumber;

	@Column(length = 255)
	private String experience; // years of experience added by nidhi

	@OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
	private List<Appointment> appointments;

	public Doctor() {
	}

	public String getSpecialisation() {
		return specialisation;
	}

	public void setSpecialisation(String specialisation) {
		this.specialisation = specialisation;
	}

	public String getLicenseNumber() {
		return licenseNumber;
	}

	public void setLicenseNumber(String licenseNumber) {
		this.licenseNumber = licenseNumber;
	}

	public String getExperience() {
		return experience;
	}

	public void setExperience(String experience) {
		this.experience = experience;
	}

	public Doctor(String name, String phone, String address, LocalDate dob, Gender gender, String qualification,
			double salary, LocalDate startDate, String specialisation, String licenseNumber) {
		super(name, phone, address, dob, gender, qualification, salary, startDate);
		this.specialisation = specialisation;
		this.licenseNumber = licenseNumber;
	}
}
