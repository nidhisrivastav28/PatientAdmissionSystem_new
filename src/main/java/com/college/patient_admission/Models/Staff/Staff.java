package com.college.patient_admission.Models.Staff;

import java.time.LocalDate;

import com.college.patient_admission.Models.Gender;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Data
public abstract class Staff {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long ID;
	
	@Column(nullable = false)
	private String name;
	
	@Column(nullable = false)
	private String phone;
	
	@Column(nullable = false)
	private String address;
	
	@Column(nullable = false)
	private LocalDate dob;
	
	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Column(nullable = false)
	private String qualification; 
	
	@Column(nullable = false)
	private double salary;
	
	@Column(nullable = false)
	private LocalDate startDate;
	
	@Column 
	private LocalDate endDate;
	
	@OneToOne(mappedBy = "staff", cascade = CascadeType.ALL)
    private StaffAuth auth;
	
	public Staff() {}
	
	public Staff(String name, String phone, String address, LocalDate dob, Gender gender, String qualification, double salary, LocalDate startDate) {
		this.name = name;
		this.phone = phone;
		this.address = address;
		this.dob = dob;
		this.gender = gender;
		this.qualification = qualification;
		this.salary = salary;
		this.startDate = startDate;
	}
	
	public String getQualification() {
		return qualification;
	}

	public void setQualification(String qualification) {
		this.qualification = qualification;
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public LocalDate getDob() {
		return dob;
	}

	public void setDob(LocalDate dob) {
		this.dob = dob;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}

	public StaffAuth getAuth() {
		return auth;
	}

	public void setAuth(StaffAuth auth) {
		this.auth = auth;
	}
}
