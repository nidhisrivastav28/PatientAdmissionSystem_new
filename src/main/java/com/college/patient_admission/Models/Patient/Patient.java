package com.college.patient_admission.Models.Patient;
import java.time.LocalDate;
import java.util.List;

import com.college.patient_admission.Models.Appointment;
import com.college.patient_admission.Models.Gender;
import com.college.patient_admission.Models.Schedule;
import com.fasterxml.jackson.annotation.JsonManagedReference;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "Pname", nullable = false)
    private String pname;

    @Column(name = "Pemail", unique = true, nullable = false)
    private String pemail;

	@Column(name = "PphnNo", length = 15, nullable = false)
    private String pphnno;

    @Column(name = "Pdob")
    private LocalDate pdob;

    @Column(name = "Pgender")
	@Enumerated(EnumType.STRING)
    private Gender pgender;

    @Column(name = "Ppswd", nullable = false)
    private String ppswd;
    
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
	@JsonManagedReference
    private List<Appointment> appointments;
    
    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Schedule> schedule;
    
    public Patient() {}
    
    public Patient(String pname, String pemail, String pphnno, LocalDate pdob, Gender pgender, String ppswd) {
    	this.pname = pname;
    	this.pemail = pemail;
    	this.pphnno = pphnno;
    	this.pdob = pdob;
    	this.pgender = pgender;
    	this.ppswd = ppswd;
    }

    public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getPname() {
		return pname;
	}

	public void setPname(String pname) {
		this.pname = pname;
	}

	public String getPemail() {
		return pemail;
	}

	public void setPemail(String pemail) {
		this.pemail = pemail;
	}

	public String getPphnno() {
		return pphnno;
	}

	public void setPphnno(String pphnno) {
		this.pphnno = pphnno;
	}

	public LocalDate getPdob() {
		return pdob;
	}

	public void setPdob(LocalDate pdob) {
		this.pdob = pdob;
	}

	public Gender getPgender() {
		return pgender;
	}

	public void setPgender(Gender pgender) {
		this.pgender = pgender;
	}

	public String getPpswd() {
		return ppswd;
	}

	public void setPpswd(String ppswd) {
		this.ppswd = ppswd;
	}

}