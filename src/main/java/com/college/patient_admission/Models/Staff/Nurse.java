package com.college.patient_admission.Models.Staff;

import java.time.LocalDate;
import java.util.List;

import com.college.patient_admission.Models.Gender;
import com.college.patient_admission.Models.Schedule;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;

@Entity
@PrimaryKeyJoinColumn(name = "staff_id")
@Data
public class Nurse extends Staff{
	@Column(nullable=false)
	private String department;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Shift shift;
	
	@OneToMany(mappedBy = "nurse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedule;
	
	public Nurse() {}
	
	public Nurse(String name, String phone, String address, LocalDate dob, Gender gender, String qualification, double salary, LocalDate startDate, String department, Shift shift) {
		super(name, phone, address, dob, gender, qualification, salary, startDate);
		this.department = department;
		this.shift = shift;
	}

	public String getDepartment() {
		return department;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public Shift getShift() {
		return shift;
	}

	public void setShift(Shift shift) {
		this.shift = shift;
	}
}
