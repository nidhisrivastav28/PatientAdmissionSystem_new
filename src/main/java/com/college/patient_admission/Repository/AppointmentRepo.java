package com.college.patient_admission.Repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.college.patient_admission.Models.Appointment;
import com.college.patient_admission.Models.Patient.Patient;

public interface AppointmentRepo extends JpaRepository<Appointment, Long> {
	@Query("""
	        SELECT d.doctor.id, MAX(d.to) 
	        FROM Appointment d 
	        GROUP BY d.doctor.id
	    """)
	List<Object[]> findLastEndTimePerDoctor();
	
	long countByCreatedOn(LocalDate createdOn);

    long countByAppointmentDate(LocalDate date);
    
    List<Appointment> findByAppointmentDate(LocalDate date);
    
    // Count of patients for a specific doctor
    @Query("SELECT COUNT(DISTINCT a.patient.ID) FROM Appointment a WHERE a.doctor.ID = :doctorId")
    long countPatientsByDoctor(@Param("doctorId") Long doctorId);
    
    @Query("SELECT DISTINCT a.patient FROM Appointment a WHERE a.doctor.ID = :doctorId")
    List<Patient> findDistinctPatientsByDoctor(@Param("doctorId") Long doctorId);
    
    // List of appointments for specific appointment date and doctor
    List<Appointment> findByDoctor_IDAndAppointmentDate(Long doctorId, LocalDate date);
    
    // Count of appointments for specific date and doctor
    long countByDoctor_IDAndCreatedOn(Long doctorId, LocalDate date);
    
    // All appointments for a specific doctor
    List<Appointment> findByDoctor_ID(Long doctorId);
}
