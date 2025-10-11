package com.college.patient_admission.Models;

import java.time.LocalDate;
import java.time.LocalTime;

import com.college.patient_admission.Models.Staff.Doctor;
import com.fasterxml.jackson.annotation.JsonBackReference;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Entity
@Data
public class Appointment {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long ID;
	
	@Column(nullable = false)
	private LocalDate createdOn;
	
	@Column(nullable = false)
	private LocalDate appointmentDate;
	
	@Column(name = "from_time", nullable = false)
	private LocalTime from;
	
	@Column(name = "to_time", nullable = false)
	private LocalTime to;
	
	@Column(nullable = false)
	private String reason;

	@ManyToOne
    @JoinColumn(name = "doctor_id")
	@JsonBackReference
    private Doctor doctor;

	@ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
	
	public Appointment() {}
	
	public Appointment(LocalDate createdOn, LocalDate appointmentDate, LocalTime from, 
			LocalTime to, String reason, Doctor doctor, Patient patient) {
		this.createdOn = createdOn;
		this.appointmentDate = appointmentDate;
		this.from = from;
		this.to = to;
		this.reason = reason;
		this.doctor = doctor;
		this.patient = patient;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public LocalDate getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(LocalDate createdOn) {
		this.createdOn = createdOn;
	}

	public LocalDate getAppointmentDate() {
		return appointmentDate;
	}

	public void setAppointmentDate(LocalDate appointmentDate) {
		this.appointmentDate = appointmentDate;
	}

	public LocalTime getFrom() {
		return from;
	}

	public void setFrom(LocalTime from) {
		this.from = from;
	}

	public LocalTime getTo() {
		return to;
	}

	public void setTo(LocalTime to) {
		this.to = to;
	}

	public String getReason() {
		return reason;
	}

	public void setReason(String reason) {
		this.reason = reason;
	}

	public Doctor getDoctor() {
		return doctor;
	}

	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
}
