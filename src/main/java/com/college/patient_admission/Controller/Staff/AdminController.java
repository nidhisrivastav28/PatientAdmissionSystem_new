package com.college.patient_admission.Controller.Staff;

import com.college.patient_admission.DTO.DoctorDTO;
import com.college.patient_admission.DTO.PatientDTO;
import com.college.patient_admission.DTO.PatientForm;
import com.college.patient_admission.DTO.StaffForm;
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
import com.college.patient_admission.Services.Staff.*;

import jakarta.servlet.http.HttpSession;

// import org.springframework.boot.autoconfigure.graphql.GraphQlProperties.Http;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	private StaffAuthService staffAuthService;
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
		this.staffAuthService = staffAuthService;
		this.patientService = patientService;
		this.nurseService = nurseService;
		this.receptionistService = receptionistService;
	}

	@GetMapping("/dashboard")
	public String showDashboard(Model model, HttpSession session) {
		if(session.getAttribute("loggedInStaff") == null 
		|| session.getAttribute("role") == null 
		|| !session.getAttribute("role").equals("Admin")) {
			model.addAttribute("error", "Please login to access the Admin Dashboard.");
			return "index";
		}

		StaffAuth admin = adminService.getAdmin(); // transactional, gets default admin

		model.addAttribute("adminName", admin.getStaff().getName());
		model.addAttribute("adminEmail", admin.getEmail());

		List<Map<String, Object>> statusList = new ArrayList<>();
		statusList.add(Map.of("label", "All Doctors", "value", doctorService.getDoctorCount()));
		statusList.add(Map.of("label", "All Patients", "value", patientService.getPatientCount()));
		statusList.add(Map.of("label", "New Booking", "value", appointmentService.getAppointmentsCountByCreatedOn(LocalDate.now(), "APPROVED")));
		statusList.add(Map.of("label", "Today Sessions", "value", appointmentService.getAppointmentsCountByAppointmentDate(LocalDate.now())));

		model.addAttribute("statusList", statusList);
		
		List<Appointment> appointments = appointmentService.getAppointmentsByAppointmentDate(LocalDate.now());
		model.addAttribute("appointments", appointments);

		model.addAttribute("activePage", "dashboard");
		return "admin/dashboard";
	}

	@PostMapping("/staff/create")
	public String createStaff(
			@Valid @ModelAttribute("staffForm") StaffForm form,
			BindingResult result,
			RedirectAttributes redirectAttributes) {

		if (result.hasErrors()) {
			redirectAttributes.addFlashAttribute(
					"org.springframework.validation.BindingResult.staffForm", result);
			redirectAttributes.addFlashAttribute("staffForm", form);
			redirectAttributes.addFlashAttribute("openModal", true);
			return "redirect:/admin/staff?role=" + form.getRole();
		}

		LocalDate start = LocalDate.now();

		switch (form.getRole().toUpperCase()) {
			case "DOCTOR":
				doctorService.register(
						form.getName(), form.getEmail(), form.getPhone(), form.getAddress(),
						form.getDob(), form.getGender(), form.getQualification(), form.getSalary(),
						start, form.getExperience(), form.getSpecialisation(), form.getLicenseNumber()
				);
				break;
			case "NURSE":
				nurseService.register(
						form.getName(), form.getEmail(), form.getPhone(), form.getAddress(),
						form.getDob(), form.getGender(), form.getQualification(), form.getSalary(),
						start, form.getExperience(), form.getDepartment(), form.getShift()
				);
				break;
			case "RECEPTIONIST":
				receptionistService.register(
						form.getName(), form.getEmail(), form.getPhone(), form.getAddress(),
						form.getDob(), form.getGender(), form.getQualification(), form.getSalary(),
						start, form.getExperience(), form.getDesk(), form.getShift()
				);
				break;
			default:
				throw new IllegalArgumentException("Unknown role: " + form.getRole());
		}

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

			if (!model.containsAttribute("staffForm")) {
				model.addAttribute("staffForm", new StaffForm());
			}
	
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
			
			model.addAttribute("activePage", "staff");
		    return "admin/staff";
		}

	@GetMapping("/patients")
	public String showPatientDetails(Model model) {
		StaffAuth admin = adminService.getAdmin(); // transactional, gets default admin
		model.addAttribute("adminName", admin.getStaff().getName());
		model.addAttribute("adminEmail", admin.getEmail());

		model.addAttribute("patients", patientService.getAllPatients());

		if (!model.containsAttribute("patientForm")) {
			model.addAttribute("patientForm", new PatientForm());
		}
		
		model.addAttribute("activePage", "patients");
		return "admin/patients";
	}

	@GetMapping("/info")
	public String showInfo(Model model) {
		StaffAuth admin = adminService.getAdmin(); // transactional, gets default admin
		model.addAttribute("adminName", admin.getStaff().getName());
		model.addAttribute("adminEmail", admin.getEmail());
		
		model.addAttribute("activePage", "info");
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
			@RequestParam(required = false) String endDate ,
			@RequestParam(required = false) String experience) {
		LocalDate end = null;
		if (endDate != null && !endDate.isEmpty()) {
			end = LocalDate.parse(endDate, DateTimeFormatter.ISO_DATE);
		}


		adminService.updateStaff(id, salary, end,experience);

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

		model.addAttribute("activePage", "appointments");
	    return "admin/appointments";
	}

	@GetMapping("/patients/{patient_id}/details")
	public String showPatientDetailsPage(@PathVariable Long patient_id, Model model) {
//		StaffAuth admin = staffAuthService.getAuthByStaffById(1);

//		model.addAttribute("adminId", admin_id);
		model.addAttribute("adminName", "Default Admin");
		model.addAttribute("adminEmail", "admin@college.com");

		model.addAttribute("patient", patientService.getPatientById(patient_id));

		return "admin/patient-details";
	}

	@GetMapping("/logout")
	public String logout(HttpSession session, Model model) {
		session.invalidate();
		model.addAttribute("info", "Logged out successfully.");
		return "index";
	}
}