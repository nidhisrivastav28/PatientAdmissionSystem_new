package com.college.patient_admission.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.college.patient_admission.Models.Patient;
import com.college.patient_admission.Models.Schedule;

public interface ScheduleRepo extends JpaRepository<Schedule, Long> {

	long countByScheduleDate(LocalDate scheduleDate);

    @Query("SELECT COUNT(DISTINCT s.patient.ID) FROM Schedule s WHERE s.nurse.ID = :nurseId")
    long countPatientsByNurse(@Param("nurseId") Long nurseId);
    
    @Query("SELECT DISTINCT s.patient FROM Schedule s WHERE s.nurse.ID = :nurseId")
    List<Patient> findDistinctPatientsByNurse(@Param("nurseId") Long nurseId);
    
    long countByNurse_IDAndScheduleDate(Long nurseId, LocalDate date);
    
    List<Schedule> findByNurse_IDAndScheduleDate(Long nurseId, LocalDate date);
    
    List<Schedule> findByNurse_ID(Long nurseId);
}
