package com.college.patient_admission.Models;

import java.time.LocalDate;
import java.time.LocalTime;

import com.college.patient_admission.Models.Patient.Patient;
import com.college.patient_admission.Models.Staff.Nurse;
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
public class Schedule {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long ID;
	
	@Column(nullable = false)
	private LocalDate scheduleDate;
	
	@Column(name = "at_time", nullable = false)
	private LocalTime time;
	
	@Column(nullable = false)
	private String task;

	@ManyToOne
    @JoinColumn(name = "nurse_id")
	@JsonBackReference
    private Nurse nurse;

	@ManyToOne
    @JoinColumn(name = "patient_id")
    private Patient patient;
	
	public Schedule() {}
	
	public Schedule(LocalDate scheduleDate, LocalTime time, String task, Nurse nurse, Patient patient) {
		this.scheduleDate = scheduleDate;
		this.time = time;
		this.task = task;
		this.nurse = nurse;
		this.patient = patient;
	}

	public long getID() {
		return ID;
	}

	public void setID(long iD) {
		ID = iD;
	}

	public LocalDate getScheduleDate() {
		return scheduleDate;
	}

	public void setScheduleDate(LocalDate scheduleDate) {
		this.scheduleDate = scheduleDate;
	}

	public LocalTime getTime() {
		return time;
	}

	public void setTime(LocalTime time) {
		this.time = time;
	}

	public String getTask() {
		return task;
	}

	public void setTask(String task) {
		this.task = task;
	}

	public Nurse getNurse() {
		return nurse;
	}

	public void setNurse(Nurse nurse) {
		this.nurse = nurse;
	}

	public Patient getPatient() {
		return patient;
	}

	public void setPatient(Patient patient) {
		this.patient = patient;
	}

}
