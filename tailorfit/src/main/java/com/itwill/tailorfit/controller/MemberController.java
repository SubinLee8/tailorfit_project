package com.itwill.tailorfit.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.itwill.tailorfit.domain.BodyMetric;
import com.itwill.tailorfit.dto.BodymetricCreateDto;
import com.itwill.tailorfit.service.BodyMetricsService;
import com.itwill.tailorfit.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
public class MemberController {
		private final BodyMetricsService bodyMetricService;
		private final MemberService memberService;
	
	@GetMapping("/signin")
	public void toLogin() {
		log.info("login");
	}
	
	@GetMapping("/bodymetrics")
	public void toSetBodyMetrics() {
		
	}
	
	@PreAuthorize("hasRole('GUEST')")
	@PostMapping("/bodymetrics")
	public String createBodymetrics(BodymetricCreateDto dto) {
		log.info("dto={}",dto);
		Long id=bodyMetricService.create(dto);
		//운동 플랜 변경하기
		memberService.updatePlan(dto);
		
		return "redirect:/";
	}
	
	@GetMapping("/member/googlelogin")
	public void loginWithGoolge() {
		
	}
}
