package com.college.patient_admission.Controller.Staff;

import com.college.patient_admission.DTO.DoctorDTO;
import com.college.patient_admission.DTO.PatientDTO;
import com.college.patient_admission.Models.Appointment;
import com.college.patient_admission.Models.Gender;
import com.college.patient_admission.Models.Patient.Patient;
import com.college.patient_admission.Models.Staff.Doctor;
import com.college.patient_admission.Models.Staff.Nurse;
import com.college.patient_admission.Models.Staff.Receptionist;
import com.college.patient_admission.Models.Staff.Shift;
import com.college.patient_admission.Models.Staff.Staff;
import com.college.patient_admission.Models.Staff.StaffAuth;
import com.college.patient_admission.Services.AppointmentService;
import com.college.patient_admission.Services.Patient.PatientService;
import com.college.patient_admission.Services.Staff.AdminService;
import com.college.patient_admission.Services.Staff.DoctorService;
import com.college.patient_admission.Services.Staff.NurseService;
import com.college.patient_admission.Services.Staff.ReceptionistService;
import com.college.patient_admission.Services.Staff.StaffService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminController {

	private final AdminService adminService;
	private final AppointmentService appointmentService;
	private final StaffService staffService;
	private final PatientService patientService;
	private final DoctorService doctorService;
	private final NurseService nurseService;
	private final ReceptionistService receptionistService;

	public AdminController(AdminService adminService, DoctorService doctorService,
			AppointmentService appointmentService, StaffService staffService, 
			PatientService patientService, NurseService nurseService,
			ReceptionistService receptionistService) {
		this.adminService = adminService;
		this.doctorService = doctorService;
		this.appointmentService = appointmentService;
		this.staffService = staffService;
		this.patientService = patientService;
		this.nurseService = nurseService;
		this.receptionistService = receptionistService;
	}

	@GetMapping("/dashboard")
	public String showDashboard(Model model) {
		StaffAuth admin = adminService.getAdmin(); // transactional, gets default admin

		model.addAttribute("adminName", admin.getStaff().getName());
		model.addAttribute("adminEmail", admin.getEmail());

		List<Map<String, Object>> statusList = new ArrayList<>();
		statusList.add(Map.of("label", "All Doctors", "value", doctorService.getDoctorCount()));
		statusList.add(Map.of("label", "All Patients", "value", patientService.getPatientCount()));
		statusList.add(Map.of("label", "New Booking", "value", appointmentService.getAppointmentsCountByCreatedOn(LocalDate.now())));
		statusList.add(Map.of("label", "Today Sessions", "value", appointmentService.getAppointmentsCountByAppointmentDate(LocalDate.now())));

		model.addAttribute("statusList", statusList);
		
		List<Appointment> appointments = appointmentService.getAppointmentsByAppointmentDate(LocalDate.now());
		model.addAttribute("appointments", appointments);

		return "admin/dashboard";
	}
	
	@PostMapping("/staff/create")
	public String createStaff(@RequestParam String role, @RequestParam String name, @RequestParam String email,
			@RequestParam String phone, @RequestParam String address,
			@RequestParam String dob, @RequestParam Gender gender, @RequestParam double salary,
			@RequestParam String qualification,
			// Doctor-specific
			@RequestParam(required = false) String specialisation, @RequestParam(required = false) String licenseNumber,
			// Nurse-specific
			@RequestParam(required = false) String department, @RequestParam(required = false) Shift shift,
			// Receptionist-specific
			@RequestParam(required = false) String desk) {
		// Parse dates
		LocalDate dobDate = LocalDate.parse(dob, DateTimeFormatter.ISO_DATE);
		LocalDate start = LocalDate.now();
		
		
		switch (role.toUpperCase()) {
		    case "DOCTOR": {
		    	doctorService.register(
		    		    name, email, phone, address, dobDate, gender, qualification, salary, start,
		    		    specialisation, licenseNumber
		    	);

		        break;
		    }
		    case "NURSE": {
		        nurseService.register(
		            name, email, phone, address, dobDate, gender, qualification, salary, start,
		            department, shift
		        );
		        break;
		    }
		    case "RECEPTIONIST": {
		        receptionistService.register(
		            name, email, phone, address, dobDate, gender, qualification, salary, start,
		            desk, shift
		        );
		        break;
		    }
		    default:
		        throw new IllegalArgumentException("Unknown role: " + role);
		}


		// Redirect back to staff page after creation
		return "redirect:/admin/staff";
	}

	@GetMapping("/staff")
	public String showStaffDetails(
	        @RequestParam(name = "role", required = false, defaultValue = "DOCTOR") String role,
		        Model model) {
			StaffAuth admin = adminService.getAdmin(); // transactional, gets default admin
	
		    model.addAttribute("adminName", admin.getStaff().getName());
		    model.addAttribute("adminEmail", admin.getEmail());
	
		    List<Gender> genders = Arrays.asList(Gender.values());
		    model.addAttribute("genders", genders);
	
		    List<Shift> shifts = Arrays.asList(Shift.values());
		    model.addAttribute("shifts", shifts);
	
		    role = role.toUpperCase();
		    model.addAttribute("selectedRole", role);
	
		    switch (role) {
		        case "DOCTOR":
		            model.addAttribute("staffList", adminService.getAllDoctors());
		            break;
		        case "NURSE":
		            model.addAttribute("staffList", nurseService.getAllNurses());
		            break;
		        case "RECEPTIONIST":
		            model.addAttribute("staffList", adminService.getAllReceptionists());
		            break;
		        default:
		            model.addAttribute("staffList", List.of());
		    }
	
		    return "admin/staff";
		}

	@GetMapping("/patients")
	public String showPatientDetails(Model model) {
		StaffAuth admin = adminService.getAdmin(); // transactional, gets default admin
		model.addAttribute("adminName", admin.getStaff().getName());
		model.addAttribute("adminEmail", admin.getEmail());

		model.addAttribute("patients", patientService.getAllPatients());
		return "admin/patients";
	}
	
	@PostMapping("/patients/create")
	public String createAppointment(@RequestParam String name, @RequestParam String gender,
			@RequestParam String dob, @RequestParam String phone, 
			@RequestParam String email, @RequestParam String password){
		
		Patient patient = new Patient(name, email, phone, dob, gender, password);
		patientService.registerPatient(patient, password);
		
		return "redirect:/admin/patients";
	}

	@GetMapping("/info")
	public String showInfo(Model model) {
		StaffAuth admin = adminService.getAdmin(); // transactional, gets default admin
		model.addAttribute("adminName", admin.getStaff().getName());
		model.addAttribute("adminEmail", admin.getEmail());

		return "admin/info";
	}

	@GetMapping("/staff/{id}")
	public String showStaffDetailsPage(@PathVariable Long id, Model model) {
		Staff staff = staffService.getStaffById(id);

		StaffAuth staffAuth = adminService.getStaffAuthById(id);
		model.addAttribute("userEmail", staffAuth.getEmail());

		if (staff instanceof Doctor) {
			model.addAttribute("role", "DOCTOR");
		}
		else if (staff instanceof Nurse) {
			model.addAttribute("role", "NURSE");
		}
		else if (staff instanceof Receptionist) {
			model.addAttribute("role", "Receptionist");
		}

		model.addAttribute("staff", staff);

		StaffAuth admin = adminService.getAdmin();
		model.addAttribute("adminName", admin.getStaff().getName());
		model.addAttribute("adminEmail", admin.getEmail());

		return "admin/staff-details";
	}

	@PostMapping("/staff/{id}/update")
	public String updateStaffSalaryAndEndDate(@PathVariable Long id,
			@RequestParam(required = false) Double salary,
			@RequestParam(required = false) String endDate) {
		LocalDate end = null;
		if (endDate != null && !endDate.isEmpty()) {
			end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
		}

		adminService.updateStaff(id, salary, end);

		return "redirect:/admin/staff/" + id; // redirect to the same page
	}
	
	@GetMapping("/appointments")
	public String showAppointmentDetails(Model model) {
		StaffAuth admin = adminService.getAdmin();
	    model.addAttribute("adminName", admin.getStaff().getName());
	    model.addAttribute("adminEmail", admin.getEmail());
	    
	    List<DoctorDTO> doctorDTOs = adminService.getAllDoctors().stream()
	            .map(d -> new DoctorDTO(d.getID(), d.getName()))
	            .toList();
	    model.addAttribute("doctors", doctorDTOs);
	    
	    List<PatientDTO> patientDTOs = patientService.getAllPatients().stream()
	            .map(p -> new PatientDTO(p.getId(), p.getPname()))
	            .toList();
	    model.addAttribute("patients", patientDTOs);
	    model.addAttribute("lastEndTimes", appointmentService.getLastEndTimePerDoctorPerDate());
	    model.addAttribute("appointments", appointmentService.getAllAppointments());

	    return "admin/appointments";
	}
	
	@PostMapping("/appointments/create")
	public String createAppointment(@RequestParam Long d_id, @RequestParam Long p_id,
			@RequestParam String date, @RequestParam String from, 
			@RequestParam String to, @RequestParam String reason){
		
		Staff staff = staffService.getStaffById(d_id);
		if(!(staff instanceof Doctor)) {
			System.out.print("Not a docotor");
			return "/appointments/create";
		}
		
		Patient patient = patientService.getPatientById(p_id);
		LocalDate appointment_date = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
		LocalDate createdOn = LocalDate.now();
		LocalTime from_time = LocalTime.parse(from);
		LocalTime to_time = LocalTime.parse(to);
		
		appointmentService.createDoctorAppointment(createdOn, appointment_date, from_time, to_time, reason, (Doctor)staff, patient);
		
		return "redirect:/admin/appointments";
	}


}