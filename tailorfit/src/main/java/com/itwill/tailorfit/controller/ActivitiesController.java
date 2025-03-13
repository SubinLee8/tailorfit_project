package com.itwill.tailorfit.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itwill.tailorfit.dto.WorkoutRecordCreateDto;
import com.itwill.tailorfit.dto.WorkoutRecordItemDto;
import com.itwill.tailorfit.dto.WorkoutRecordUpdateDto;
import com.itwill.tailorfit.repository.MemberRepository;
import com.itwill.tailorfit.service.WorkoutRecordService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/activities")
public class ActivitiesController {
	private final WorkoutRecordService workoutService;
	
	//ATHELTE만 접근 가능
	@GetMapping("/mylist")
	public void getMyActivities(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(name="p", defaultValue = "0") Integer pageNo, Model model) {
		String username=userDetails.getUsername();
		Page<WorkoutRecordItemDto> lists=workoutService.readMyActivities(pageNo, Sort.by("id").descending(),username);
		model.addAttribute("page", lists);
		model.addAttribute("baseUrl","/activities/mylist");
		log.info("dto={}",lists);
	}
	
	@GetMapping("/delete")
	public String deleteActivity(@RequestParam Long id) {
		workoutService.delete(id);
		return "redirect:/activities/mylist";
	}
	
	@GetMapping("/create")
	public void createActivity() {
		
	}
	
	@PostMapping("/create")
	public String newActivity(WorkoutRecordCreateDto dto) {
		log.info("create dto={}",dto);
		String username=dto.getUsername();
		Long id=workoutService.createWorkoutRecord(dto,username);
		return "redirect:/activities/details?id="+id.toString();
	}
	
	@GetMapping("/details")
	public String getMyActivityDetail(@AuthenticationPrincipal UserDetails userDetails, @RequestParam Long id,
			Model model) {
		//본인 게시글이 아니면 접근 금지
		String username=userDetails.getUsername();
		if(!workoutService.isThisMyRecord(id,username)) {
			return "redirect:/";
		}
		WorkoutRecordItemDto record=workoutService.readById(id);
		model.addAttribute("record", record);
		return "/activities/details";
	}
	
	@GetMapping("/modify")
	public void modifyActivity(@RequestParam Long id, Model model) {
		WorkoutRecordItemDto dto=workoutService.readById(id);
		log.info("dto={}",dto);
		model.addAttribute("record", dto);
	}
	
	@PostMapping("/modify")
	public String updateActivity(WorkoutRecordUpdateDto dto) {
		workoutService.update(dto);
		return "redirect:/activities/details?id="+dto.getId().toString();
	}
	
	//ATHELTE만 접근 가능
	@GetMapping("/mydashboard")
	public void getMyDashBoard() {
		
	}
	
}
