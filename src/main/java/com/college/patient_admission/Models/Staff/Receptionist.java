package com.college.patient_admission.Models.Staff;

import java.time.LocalDate;

import com.college.patient_admission.Models.Gender;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;

@Entity
@PrimaryKeyJoinColumn(name = "staff_id")
@Data
public class Receptionist extends Staff{
	@Column(nullable=false)
	private String desk;
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Shift shift;
	
	public Receptionist() {}
	
	public Receptionist(String name, String phone, String address, LocalDate dob, Gender gender, String qualification, double salary, LocalDate startDate, String desk, Shift shift) {
		super(name, phone, address, dob, gender, qualification, salary, startDate);
		this.desk = desk;
		this.shift = shift;
	}

	public String getDesk() {
		return desk;
	}

	public void setDesk(String desk) {
		this.desk = desk;
	}

	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;
	}
}
