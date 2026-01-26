package com.college.patient_admission.Services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.college.patient_admission.Models.Appointment;
import com.college.patient_admission.Models.Patient.Patient;
import com.college.patient_admission.Models.Staff.Doctor;
import com.college.patient_admission.Repository.AppointmentRepo;
import com.college.patient_admission.Repository.Patient.PatientRepo;
import com.college.patient_admission.Repository.Staff.DoctorRepo;

@Service
public class AppointmentService {
	private final AppointmentRepo appointmentRepo;

	@Autowired
	private DoctorRepo doctorRepo;

	@Autowired
	private PatientRepo patientRepo;

	public AppointmentService(AppointmentRepo appointmentRepo) {
		this.appointmentRepo = appointmentRepo;
	}

	public List<Appointment> getAllAppointments() {
		return appointmentRepo.findAll();
	}

	public Appointment createDoctorAppointment(LocalDate createdOn, LocalDate appointmentDate,
			LocalTime from, LocalTime to, String reason, Doctor doctor, Patient patient, String status) {
		Appointment doctorAppointments = new Appointment(createdOn, appointmentDate, from, to, reason, doctor, patient, status);
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
					.merge(date, endTime, (oldTime, newTime) -> newTime.isAfter(oldTime) ? newTime : oldTime);
		}

		return result;
	}

	public void saveRequest(Long patientId, Long doctorId, String appointmentDate, String from, String reason) {
		Patient patient = patientRepo.findById(patientId).orElse(null);
		Doctor doctor = doctorRepo.findById(doctorId).orElse(null);

		if (patient == null || doctor == null) {
			throw new RuntimeException("Invalid patient or doctor ID");
		}

		Appointment appointment = new Appointment();
		appointment.setCreatedOn(LocalDate.now());
		appointment.setAppointmentDate(LocalDate.parse(appointmentDate));
		appointment.setFrom(LocalTime.parse(from));
		appointment.setTo(LocalTime.parse(from).plusMinutes(30)); // default 30 min slot
		appointment.setReason(reason);
		appointment.setDoctor(doctor);
		appointment.setPatient(patient);

		appointmentRepo.save(appointment);
	}

	public void updateStatus(Long appointmentId, String status) {
    Appointment app = appointmentRepo.findById(appointmentId).orElse(null);

		if (app != null) {
			app.setStatus(status);
			appointmentRepo.save(app);
		}
	}

	public Long getAppointmentsCountByCreatedOn(LocalDate date, String status) {
		return appointmentRepo.countByCreatedOnAndStatus(date, status);
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

	public List<Patient> getPatientsPerDoctor(Long id) {
		return appointmentRepo.findDistinctPatientsByDoctor(id);
	}

	public Long countBookingsByDoctorAndDate(Long id, LocalDate date, String status) {
		return appointmentRepo.countByDoctor_IDAndCreatedOnAndStatus(id, date, status);
	}

	public List<Appointment> getBookingsByDoctorAndDate(Long id, LocalDate date) {
		return appointmentRepo.findByDoctor_IDAndAppointmentDate(id, date);
	}

	public List<Appointment> getAppointmentsByDoctorId(Long id) {
		return appointmentRepo.findByDoctor_ID(id);
	}

	public List<Appointment> getAppointmentsForPatient(Long patientId) {
		return appointmentRepo.findByPatient_Id(patientId);
	}

	public List<Appointment> getUpcomingAppointments(Long doctorId, String status) {
		LocalDate today = LocalDate.now();
		return appointmentRepo
				.findByAppointmentDateGreaterThanEqualAndDoctor_IDAndStatus(
						today, doctorId, status
				);
	}

	public long countUpcomingAppointments(Long doctorId, String status) {
		LocalDate today = LocalDate.now();
		return appointmentRepo
				.countByAppointmentDateGreaterThanEqualAndStatus(
						today, status
				);
	}
}
