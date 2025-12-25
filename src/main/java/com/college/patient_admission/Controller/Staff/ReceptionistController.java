package com.college.patient_admission.Controller.Staff;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.college.patient_admission.Models.Patient.Patient;
import com.college.patient_admission.Models.Staff.Doctor;
import com.college.patient_admission.Models.Staff.Staff;
import com.college.patient_admission.Models.Staff.StaffAuth;
import com.college.patient_admission.Repository.AppointmentRepo;
import com.college.patient_admission.Services.AppointmentService;
import com.college.patient_admission.Services.Patient.PatientService;
import com.college.patient_admission.Services.Staff.AdminService;
import com.college.patient_admission.Services.Staff.ReceptionistService;
import com.college.patient_admission.Services.Staff.StaffAuthService;
import com.college.patient_admission.Services.Staff.StaffService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/receptionist")
public class ReceptionistController {
	private ReceptionistService receptionistService;
	private StaffService staffService;
	private StaffAuthService staffAuthService;
	private final AdminService adminService;
	private final PatientService patientService;
	
	@Autowired
    AppointmentRepo appointmentRepo;

    @Autowired
    AppointmentService appointmentService;

	 
	public ReceptionistController(ReceptionistService receptionistService, 
			StaffService staffService, StaffAuthService staffAuthService, 
			AdminService adminService, AppointmentService appointmentService, 
			PatientService patientService) {
		this.receptionistService = receptionistService;
		this.staffService = staffService;
		this.staffAuthService = staffAuthService;
		this.adminService = adminService;
		this.appointmentService = appointmentService;
		this.patientService = patientService;
	}

	@GetMapping("/dashboard/{id}")
	public String showDashboard(@PathVariable Long id, Model model, HttpSession session) {
		if(session.getAttribute("loggedInStaff") == null || session.getAttribute("role") == null || !session.getAttribute("role").equals("Receptionist")) {
			model.addAttribute("error", "Please login to access the Receptionist Dashboard.");
			return "staffs/commonLogin";
		}
 
		model.addAttribute("session", session);

		StaffAuth receptionist = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("receptionistId", id);
		model.addAttribute("receptionistName", receptionist.getStaff().getName());
		model.addAttribute("receptionistEmail", receptionist.getEmail());

		List<Map<String, Object>> statusList = new ArrayList<>();
		statusList.add(Map.of("label", "New Patients", "value", patientService.getPatientCount()));
		statusList.add(Map.of("label", "New Booking", "value", appointmentService.getAppointmentsCountByCreatedOn(LocalDate.now())));

		model.addAttribute("statusList", statusList);
		
		List<Appointment> appointments = appointmentService.getAppointmentsByAppointmentDate(LocalDate.now());
		model.addAttribute("appointments", appointments);
		
		model.addAttribute("activePage", "appointments");
		return "receptionist/dashboard";
	}
	
	@GetMapping("/patients/{id}")
	public String showPatientDetails(@PathVariable Long id, Model model) {
		StaffAuth receptionist = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("receptionistId", id);
		model.addAttribute("receptionistName", receptionist.getStaff().getName());
		model.addAttribute("receptionistEmail", receptionist.getEmail());
		
		model.addAttribute("patients", patientService.getAllPatients());
		
		model.addAttribute("activePage", "appointments");
		return "receptionist/patients";
	}
	
	@PostMapping("/patients/{id}/create")
	public String createAppointment(@PathVariable Long id, @RequestParam String name, @RequestParam String gender,
			@RequestParam String dob, @RequestParam String phone, 
			@RequestParam String email, @RequestParam String password){
		
		Patient patient = new Patient(name, email, phone, dob, gender, password);
		patientService.registerPatient(patient, password);
		
		return "redirect:/receptionist/patients/{id}";
	}
	
	@GetMapping("/{receptionist_id}/patients/{patient_id}/details")
	public String showStaffDetailsPage(@PathVariable Long receptionist_id, @PathVariable Long patient_id, Model model) {
		StaffAuth receptionist = staffAuthService.getAuthByStaffById(receptionist_id);

		model.addAttribute("doctorId", receptionist_id);
		model.addAttribute("doctorName", receptionist.getStaff().getName());
		model.addAttribute("doctorEmail", receptionist.getEmail());
		
		model.addAttribute("patient", patientService.getPatientById(patient_id));

		return "receptionist/patient-details";
	}
	
	@GetMapping("/appointments/{id}")
	public String showAppointments(@PathVariable Long id, Model model) {
		StaffAuth receptionist = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("receptionistId", id);
		model.addAttribute("receptionistName", receptionist.getStaff().getName());
		model.addAttribute("receptionistEmail", receptionist.getEmail());
		
		List<Appointment> appointments = appointmentService.getAllAppointments();
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
		return "receptionist/appointments";
	}
	
	@PostMapping("/appointments/{id}/create")
	public String createAppointment(@PathVariable Long id, @RequestParam Long d_id, @RequestParam Long p_id,
			@RequestParam String date, @RequestParam String from, 
			@RequestParam String to, @RequestParam String reason){
		
		Staff staff = staffService.getStaffById(d_id);
		if(!(staff instanceof Doctor)) {
			throw new IllegalArgumentException("Selected staff is not a doctor");
		}
		
		Patient patient = patientService.getPatientById(p_id);
		LocalDate appointment_date = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
		LocalDate createdOn = LocalDate.now();
		LocalTime from_time = LocalTime.parse(from);
		LocalTime to_time = LocalTime.parse(to);
		
		appointmentService.createDoctorAppointment(createdOn, appointment_date, from_time, to_time, reason, (Doctor)staff, patient);
		
		return "redirect:/receptionist/appointments/" + id;
	}
	
	// Show all appointments
    @GetMapping("/appointments")
    public String showAppointments(Model model) {

        List<Appointment> list = appointmentRepo.findAll();
        model.addAttribute("appointments", list);

        return "reception/appointments"; 
    }

    // Approve request
    @PostMapping("/appointments/approve/{id}")
    public String approve(@PathVariable Long id) {
        appointmentService.updateStatus(id, "APPROVED");
        return "redirect:/reception/appointments";
    }

    // Reject request
    @PostMapping("/appointments/reject/{id}")
    public String reject(@PathVariable Long id) {
        appointmentService.updateStatus(id, "REJECTED");
        return "redirect:/reception/appointments";
    }

	@GetMapping("/aboutme/{id}")
	public String showAboutMe(@PathVariable Long id, Model model) {
		StaffAuth receptionist = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("receptionistId", id);
		model.addAttribute("receptionistName", receptionist.getStaff().getName());
		model.addAttribute("receptionistEmail", receptionist.getEmail());
		
		model.addAttribute("receptionist", receptionistService.getReceptionistById(id));
		
		model.addAttribute("activePage", "about");
		return "receptionist/aboutme";
	}
	
	@PostMapping("/aboutme/{id}/update")
	public String updateMyDetails(@PathVariable Long id,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String phone,
			@RequestParam(required = false) String address) {

		receptionistService.updateDetails(id, email, phone, address);

		return "redirect:/receptionist/aboutme/" + id; // redirect to the same page
	}
	
	@GetMapping("/info/{id}")
	public String getInfo(@PathVariable Long id, Model model) {
		StaffAuth receptionist = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("receptionistId", id);
		model.addAttribute("receptionistName", receptionist.getStaff().getName());
		model.addAttribute("receptionistEmail", receptionist.getEmail());
		
		model.addAttribute("activePage", "info");
		return "receptionist/info";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session, Model model) {
		session.invalidate();
		model.addAttribute("info", "Logged out successfully.");
		return "index";
	}
}
