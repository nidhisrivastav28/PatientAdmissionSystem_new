package com.college.patient_admission.Controller.Staff;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.college.patient_admission.DTO.DoctorDTO;
import com.college.patient_admission.DTO.PatientDTO;
import com.college.patient_admission.Models.Appointment;
import com.college.patient_admission.Models.Patient;
import com.college.patient_admission.Models.Staff.Doctor;
import com.college.patient_admission.Models.Staff.Staff;
import com.college.patient_admission.Models.Staff.StaffAuth;
import com.college.patient_admission.Services.AppointmentService;
import com.college.patient_admission.Services.PatientService;
import com.college.patient_admission.Services.Staff.AdminService;
import com.college.patient_admission.Services.Staff.DoctorService;
import com.college.patient_admission.Services.Staff.StaffAuthService;
import com.college.patient_admission.Services.Staff.StaffService;

@Controller
@RequestMapping("/doctor")
public class DoctorController {
	private DoctorService doctorService;
	private StaffService staffService;
	private StaffAuthService staffAuthService;
	private final AdminService adminService;
	private final AppointmentService appointmentService;
	private final PatientService patientService;
	
	public DoctorController(DoctorService doctorService, StaffService staffService,
			StaffAuthService staffAuthService, AdminService adminService,
			AppointmentService appointmentService, PatientService patientService) {
		this.doctorService = doctorService;
		this.staffService = staffService;
		this.staffAuthService = staffAuthService;
		this.adminService = adminService;
		this.appointmentService = appointmentService;
		this.patientService = patientService;
	}

	@GetMapping("/dashboard/{id}")
	public String showDashboard(@PathVariable Long id, Model model) {
		StaffAuth doctor = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("doctorId", id);
		model.addAttribute("doctorName", doctor.getStaff().getName());
		model.addAttribute("doctorEmail", doctor.getEmail());

		List<Map<String, Object>> statusList = new ArrayList<>();
		statusList.add(Map.of("label", "My Patients", "value", appointmentService.countPatientsByDoctor(id)));
		statusList.add(Map.of("label", "Today's Booking", "value", appointmentService.countBookingsByDoctorAndDate(id, LocalDate.now())));
		statusList.add(Map.of("label", "Today's Sessions", "value", appointmentService.getAppointmentsCountByAppointmentDate(LocalDate.now())));

		model.addAttribute("statusList", statusList);
		
		List<Appointment> appointments = appointmentService.getBookingsByDoctorAndDate(id, LocalDate.now());
		model.addAttribute("appointments", appointments);

		return "doctor/dashboard";
	}
	
	@GetMapping("/appointments/{id}")
	public String showPatients(@PathVariable Long id, Model model) {
		StaffAuth doctor = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("doctorId", id);
		model.addAttribute("doctorName", doctor.getStaff().getName());
		model.addAttribute("doctorEmail", doctor.getEmail());
		
		List<Appointment> appointments = appointmentService.getAppointmentsByDoctorId(id);
		model.addAttribute("appointments", appointments);
		
		List<DoctorDTO> doctorDTOs = adminService.getAllDoctors().stream()
	            .map(d -> new DoctorDTO(d.getID(), d.getName()))
	            .toList();
	    model.addAttribute("doctors", doctorDTOs);
	    
	    List<PatientDTO> patientDTOs = patientService.getAllPatients().stream()
	            .map(p -> new PatientDTO(p.getId(), p.getPname()))
	            .toList();
	    model.addAttribute("patients", patientDTOs);
	    model.addAttribute("lastEndTimes", appointmentService.getLastEndTimePerDoctorPerDate());

		return "doctor/appointments";
	}
	
	@PostMapping("/appointments/{id}/create")
	public String createAppointment(@PathVariable Long id, @RequestParam Long d_id, @RequestParam Long p_id,
			@RequestParam String date, @RequestParam String from, 
			@RequestParam String to, @RequestParam String reason){
		
		Staff staff = staffService.getStaffById(d_id);
		if(!(staff instanceof Doctor)) {
			System.out.print("Not a doctor");
			return "/appointments/create";
		}
		
		Patient patient = patientService.getPatientById(p_id);
		LocalDate appointment_date = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
		LocalDate createdOn = LocalDate.now();
		LocalTime from_time = LocalTime.parse(from);
		LocalTime to_time = LocalTime.parse(to);
		
		appointmentService.createDoctorAppointment(createdOn, appointment_date, from_time, to_time, reason, (Doctor)staff, patient);
		
		return "redirect:/doctor/appointments/{id}";
	}
	
	@GetMapping("/patients/{id}")
	public String showPatientDetails(@PathVariable Long id, Model model) {
		StaffAuth doctor = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("doctorId", id);
		model.addAttribute("doctorName", doctor.getStaff().getName());
		model.addAttribute("doctorEmail", doctor.getEmail());
		model.addAttribute("patients", appointmentService.getPatientsPerDoctor(id));
		return "doctor/patients";
	}
	
	@PostMapping("/patients/{id}/create")
	public String createAppointment(@PathVariable Long id, @RequestParam String name, @RequestParam String gender,
			@RequestParam String dob, @RequestParam String phone, 
			@RequestParam String email, @RequestParam String password){
		
		Patient patient = new Patient(name, email, phone, dob, gender, password);
		patientService.registerPatient(patient, password);
		
		return "redirect:/doctor/patients/{id}";
	}
	
	@GetMapping("/{doc_id}/patients/{patient_id}/details")
	public String showStaffDetailsPage(@PathVariable Long doc_id, @PathVariable Long patient_id, Model model) {
		StaffAuth doctor = staffAuthService.getAuthByStaffById(doc_id);

		model.addAttribute("doctorId", doc_id);
		model.addAttribute("doctorName", doctor.getStaff().getName());
		model.addAttribute("doctorEmail", doctor.getEmail());
		model.addAttribute("patient", patientService.getPatientById(patient_id));

		return "doctor/patient-details";
	}
	
	@GetMapping("/aboutme/{id}")
	public String showAboutMe(@PathVariable Long id, Model model) {
		StaffAuth doctor = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("doctorId", id);
		model.addAttribute("doctorName", doctor.getStaff().getName());
		model.addAttribute("doctorEmail", doctor.getEmail());
		
		model.addAttribute("doctor", doctorService.getDoctorById(id));
		return "doctor/aboutme";
	}
	
	@PostMapping("/aboutme/{id}/update")
	public String updateMyDetails(@PathVariable Long id,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String phone,
			@RequestParam(required = false) String address) {

		doctorService.updateDetails(id, email, phone, address);

		return "redirect:/doctor/aboutme/" + id; // redirect to the same page
	}
	
	@GetMapping("/info/{id}")
	public String getInfo(@PathVariable Long id, Model model) {
		StaffAuth doctor = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("doctorId", id);
		model.addAttribute("doctorName", doctor.getStaff().getName());
		model.addAttribute("doctorEmail", doctor.getEmail());
		
		return "doctor/info";
	}
}
