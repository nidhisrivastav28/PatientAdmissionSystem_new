package com.college.patient_admission.Services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.college.patient_admission.Models.Appointment;
import com.college.patient_admission.Models.Patient.Patient;
import com.college.patient_admission.Models.Staff.Doctor;
import com.college.patient_admission.Repository.AppointmentRepo;

@Service
public class AppointmentService {
	private final AppointmentRepo appointmentRepo;
	
	public AppointmentService(AppointmentRepo appointmentRepo) {
		this.appointmentRepo = appointmentRepo;
	}
	
	public List<Appointment> getAllAppointments() {
	    return appointmentRepo.findAll();
	}
	
	public Appointment createDoctorAppointment(LocalDate createdOn, LocalDate appointmentDate, 
			LocalTime from, LocalTime to, String reason, Doctor doctor, Patient patient) {
		Appointment doctorAppointments = new Appointment(createdOn, appointmentDate, from, to, reason, doctor, patient);
		return appointmentRepo.save(doctorAppointments);
	}
	
	public Map<Long, Map<LocalDate, LocalTime>> getLastEndTimePerDoctorPerDate() {
	    Map<Long, Map<LocalDate, LocalTime>> result = new HashMap<>();

	    List<Appointment> allAppointments = appointmentRepo.findAll();

	    for (Appointment app : allAppointments) {
	        long docId = app.getDoctor().getID();
	        LocalDate date = app.getAppointmentDate();
	        LocalTime endTime = app.getTo();

	        result
	            .computeIfAbsent(docId, k -> new HashMap<>())
	            .merge(date, endTime, (oldTime, newTime) ->
	                newTime.isAfter(oldTime) ? newTime : oldTime
	            );
	    }

	    return result;
	}

	
	public Long getAppointmentsCountByCreatedOn(LocalDate date) {
		return appointmentRepo.countByCreatedOn(date);
	}
	
	public List<Appointment> getAppointmentsByAppointmentDate(LocalDate date) {
		return appointmentRepo.findByAppointmentDate(date);
	}
	
	public Long getAppointmentsCountByAppointmentDate(LocalDate date) {
		return appointmentRepo.countByAppointmentDate(date);
	}
	
	public Long countPatientsByDoctor(Long id) {
		return appointmentRepo.countPatientsByDoctor(id);
	}
	
	public List<Patient> getPatientsPerDoctor(Long id){
		return appointmentRepo.findDistinctPatientsByDoctor(id);
	}
	
	public Long countBookingsByDoctorAndDate(Long id, LocalDate date) {
		return appointmentRepo.countByDoctor_IDAndCreatedOn(id, date);
	}
	
	public List<Appointment> getBookingsByDoctorAndDate(Long id, LocalDate date) {
		return appointmentRepo.findByDoctor_IDAndAppointmentDate(id, date);
	}
	
	public List<Appointment> getAppointmentsByDoctorId(Long id){
		return appointmentRepo.findByDoctor_ID(id);
	}
}
