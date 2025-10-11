package com.college.patient_admission.Services;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.college.patient_admission.Models.Patient;
import com.college.patient_admission.Models.Schedule;
import com.college.patient_admission.Models.Staff.Nurse;
import com.college.patient_admission.Repository.ScheduleRepo;

@Service
public class ScheduleService {
	private final ScheduleRepo scheduleRepo;
	
	public ScheduleService(ScheduleRepo scheduleRepo) {
		this.scheduleRepo = scheduleRepo;
	}
	
	public List<Schedule> getAllSchedules() {
	    return scheduleRepo.findAll();
	}
	
	public Schedule createSchedule(LocalDate date, LocalTime time, String task, Nurse nurse, Patient patient) {
		Schedule schedule = new Schedule(date, time, task, nurse, patient);
		return scheduleRepo.save(schedule);
	}

	
	public Long getSchedulesCountByDate(LocalDate date) {
		return scheduleRepo.countByScheduleDate(date);
	}
	
	public Long countScheduleByNurseAndDate(Long id, LocalDate date) {
		return scheduleRepo.countByNurse_IDAndScheduleDate(id, date);
	}
	
	public List<Schedule> getScheduleByNurseAndDate(Long id, LocalDate date){
		return scheduleRepo.findByNurse_IDAndScheduleDate(id, date);
	}
	
	public List<Schedule> getScheduleByNurseID(Long id){
		return scheduleRepo.findByNurse_ID(id);
	}
	
	public Long countPatientsByNurse(Long id) {
		return scheduleRepo.countPatientsByNurse(id);
	}
	
	public List<Patient> getPatientsByNurse(Long id){
		return scheduleRepo.findDistinctPatientsByNurse(id);
	}
}
