package com.college.patient_admission.Models.Staff;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class StaffAuth {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long ID;
	
	@Column(unique = true, nullable = false)
	private String email;
	
	@Column
	private String password;
	
	@OneToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Staff getStaff() {
		return staff;
	}

	public void setStaff(Staff staff) {
		this.staff = staff;
	}
}
