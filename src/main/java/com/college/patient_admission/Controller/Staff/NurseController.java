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

import com.college.patient_admission.DTO.NurseDTO;
import com.college.patient_admission.DTO.PatientDTO;
import com.college.patient_admission.Models.Patient;
import com.college.patient_admission.Models.Schedule;
import com.college.patient_admission.Models.Staff.Nurse;
import com.college.patient_admission.Models.Staff.Staff;
import com.college.patient_admission.Models.Staff.StaffAuth;
import com.college.patient_admission.Services.PatientService;
import com.college.patient_admission.Services.ScheduleService;
import com.college.patient_admission.Services.Staff.NurseService;
import com.college.patient_admission.Services.Staff.StaffAuthService;
import com.college.patient_admission.Services.Staff.StaffService;

@Controller
@RequestMapping("/nurse")
public class NurseController {
	private StaffService staffService;
	private StaffAuthService staffAuthService;
	private final PatientService patientService;
	public final NurseService nurseService;
	private final ScheduleService scheduleService;
	
	public NurseController(StaffService staffService,
			StaffAuthService staffAuthService, PatientService patientService,
			NurseService nurseService, ScheduleService scheduleService) {
		this.staffService = staffService;
		this.staffAuthService = staffAuthService;
		this.patientService = patientService;
		this.nurseService = nurseService;
		this.scheduleService = scheduleService;
	}

	@GetMapping("/dashboard/{id}")
	public String showDashboard(@PathVariable Long id, Model model) {
		StaffAuth nurse = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("nurseId", id);
		model.addAttribute("nurseName", nurse.getStaff().getName());
		model.addAttribute("nurseEmail", nurse.getEmail());

		List<Map<String, Object>> statusList = new ArrayList<>();
		statusList.add(Map.of("label", "My Patients", "value", scheduleService.countPatientsByNurse(id)));
		statusList.add(Map.of("label", "My Schedule", "value", scheduleService.countScheduleByNurseAndDate(id, LocalDate.now())));

		model.addAttribute("statusList", statusList);
		
		List<Schedule> schedules = scheduleService.getScheduleByNurseAndDate(id, LocalDate.now());
		model.addAttribute("schedules", schedules);

		return "nurse/dashboard";
	}
	
	@GetMapping("/patients/{id}")
	public String showPatientDetails(@PathVariable Long id, Model model) {
		StaffAuth nurse = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("nurseId", id);
		model.addAttribute("nurseName", nurse.getStaff().getName());
		model.addAttribute("nurseEmail", nurse.getEmail());
		
		model.addAttribute("patients", scheduleService.getPatientsByNurse(id));
		return "nurse/patients";
	}
	
	@PostMapping("/patients/{id}/create")
	public String createAppointment(@PathVariable Long id, @RequestParam String name, @RequestParam String gender,
			@RequestParam String dob, @RequestParam String phone, 
			@RequestParam String email, @RequestParam String password){
		
		Patient patient = new Patient(name, email, phone, dob, gender, password);
		patientService.registerPatient(patient, password);
		
		return "redirect:/nurse/patients/{id}";
	}
	
	@GetMapping("/schedules/{id}")
	public String showPatients(@PathVariable Long id, Model model) {
		StaffAuth nurse = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("nurseId", id);
		model.addAttribute("nurseName", nurse.getStaff().getName());
		model.addAttribute("nurseEmail", nurse.getEmail());
		
		List<Schedule> schedule = scheduleService.getScheduleByNurseID(id);
		model.addAttribute("schedules", schedule);
		
		List<NurseDTO> nurseDTOs = nurseService.getAllNurses().stream()
	            .map(n -> new NurseDTO(n.getID(), n.getName()))
	            .toList();
	    model.addAttribute("nurses", nurseDTOs);
	    
	    List<PatientDTO> patientDTOs = patientService.getAllPatients().stream()
	            .map(p -> new PatientDTO(p.getId(), p.getPname()))
	            .toList();
	    model.addAttribute("patients", patientDTOs);

		return "nurse/schedule";
	}
	
	@PostMapping("/schedules/{id}/create")
	public String createSchedule(@PathVariable Long id, @RequestParam Long n_id, @RequestParam Long p_id,
			@RequestParam String date, @RequestParam String time, 
			@RequestParam String task){
		
		Staff staff = staffService.getStaffById(n_id);
		if(!(staff instanceof Nurse)) {
			System.out.print("Not a nurse");
			return "/schedules/"+ id;
		}
		
		Patient patient = patientService.getPatientById(p_id);
		LocalDate schedule_date = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
		LocalTime schedule_time = LocalTime.parse(time);
		
		scheduleService.createSchedule(schedule_date, schedule_time, task, (Nurse) staff, patient);
		
		return "redirect:/nurse/schedules/" + id;
	}
	
	@GetMapping("/aboutme/{id}")
	public String showAboutMe(@PathVariable Long id, Model model) {
		StaffAuth nurse = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("nurseId", id);
		model.addAttribute("nurseName", nurse.getStaff().getName());
		model.addAttribute("nurseEmail", nurse.getEmail());
		
		model.addAttribute("nurse", nurseService.getNurseById(id));
		return "nurse/aboutme";
	}
	
	@PostMapping("/aboutme/{id}/update")
	public String updateMyDetails(@PathVariable Long id,
			@RequestParam(required = false) String email,
			@RequestParam(required = false) String phone,
			@RequestParam(required = false) String address) {

		nurseService.updateDetails(id, email, phone, address);

		return "redirect:/nurse/aboutme/" + id; // redirect to the same page
	}
	
	@GetMapping("/info/{id}")
	public String getInfo(@PathVariable Long id, Model model) {
		StaffAuth nurse = staffAuthService.getAuthByStaffById(id);

		model.addAttribute("nurseId", id);
		model.addAttribute("nurseName", nurse.getStaff().getName());
		model.addAttribute("nurseEmail", nurse.getEmail());
		
		return "nurse/info";
	}
}
