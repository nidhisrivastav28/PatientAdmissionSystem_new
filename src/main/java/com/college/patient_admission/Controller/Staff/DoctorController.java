package com.college.patient_admission.Controller.Staff;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.college.patient_admission.DTO.PatientForm;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.college.patient_admission.DTO.DoctorDTO;
import com.college.patient_admission.DTO.PatientDTO;
import com.college.patient_admission.Models.Appointment;
import com.college.patient_admission.Models.Patient.Patient;
import com.college.patient_admission.Models.Staff.Doctor;
import com.college.patient_admission.Models.Staff.Staff;
import com.college.patient_admission.Models.Staff.StaffAuth;
import com.college.patient_admission.Services.AppointmentService;
import com.college.patient_admission.Services.Patient.PatientService;
import com.college.patient_admission.Services.Staff.AdminService;
import com.college.patient_admission.Services.Staff.DoctorService;
import com.college.patient_admission.Services.Staff.StaffAuthService;
import com.college.patient_admission.Services.Staff.StaffService;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	public String showDashboard(@PathVariable Long id, Model model,HttpSession session) {
		
		if(session.getAttribute("loggedInStaff") == null || session.getAttribute("role") == null || !session.getAttribute("role").equals("Doctor")) {
			model.addAttribute("error", "Please login to access the Doctor Dashboard.");
			return "index";
		}

		StaffAuth doctor = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("doctorId", id);
		model.addAttribute("doctorName", doctor.getStaff().getName());
		model.addAttribute("doctorEmail", doctor.getEmail());

		List<Map<String, Object>> statusList = new ArrayList<>();
		statusList.add(Map.of("label", "My Patients", "value", appointmentService.countPatientsByDoctor(id)));
		statusList.add(Map.of("label", "Today's Booking", "value", appointmentService.countBookingsByDoctorAndDate(id, LocalDate.now(), "APPROVED")));
		statusList.add(Map.of("label", "Today's Sessions", "value", appointmentService.getAppointmentsCountByAppointmentDate(LocalDate.now())));

		model.addAttribute("statusList", statusList);
		
		List<Appointment> appointments = appointmentService.getBookingsByDoctorAndDate(id, LocalDate.now());
		model.addAttribute("appointments", appointments);

		model.addAttribute("activePage", "dashboard");
		return "doctor/dashboard";
	}
	
	@GetMapping("/appointments/{id}")
	public String showPatients(@PathVariable Long id, Model model) {
		StaffAuth doctor = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("doctorId", id);
		model.addAttribute("doctorName", doctor.getStaff().getName());
		model.addAttribute("doctorEmail", doctor.getEmail());
		
		List<Appointment> appointments = appointmentService.getUpcomingAppointments(id, "APPROVED");
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

		model.addAttribute("activePage", "appointments");
		return "doctor/appointments";
	}
	
	@GetMapping("/patients/{id}")
	public String showPatientDetails(@PathVariable Long id, Model model) {
		StaffAuth doctor = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("doctorId", id);
		model.addAttribute("doctorName", doctor.getStaff().getName());
		model.addAttribute("doctorEmail", doctor.getEmail());
		model.addAttribute("patients", appointmentService.getPatientsPerDoctor(id));

		model.addAttribute("activePage", "patients");
		return "doctor/patients";
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
		
		model.addAttribute("activePage", "aboutme");
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

		model.addAttribute("activePage", "info");
		return "doctor/info";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session, Model model) {
		session.invalidate();
		model.addAttribute("info", "Logged out successfully.");
		return "index";
	}

}
