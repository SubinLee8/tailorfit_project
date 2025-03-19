package com.itwill.tailorfit.controller;

import java.net.UnknownHostException;
import java.net.http.HttpRequest;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itwill.tailorfit.domain.BodyMetric;
import com.itwill.tailorfit.dto.BodymetricCreateDto;
import com.itwill.tailorfit.dto.MemberSignupDto;
import com.itwill.tailorfit.service.BodyMetricsService;
import com.itwill.tailorfit.service.MemberService;

import jakarta.servlet.http.HttpServletRequest;
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

	@GetMapping("/verify")
	public String verifyUser(@RequestParam String token) {
		String result = memberService.verifyUser(token);
		if (result.equals("trainer")) {
			// verified 페이지로 이동하게 수정
			return "redirect:/member/signin?verified";
		}
		// unverified 페이지로 이동하게 수정
		return "redirect:/member/bodymetrics?firsttry";
	}

	@GetMapping("/signup")
	public void toSignup(HttpServletRequest request) {
		request.getSession().invalidate();
	}

	@PostMapping("/signup")
	public String createMember(MemberSignupDto dto,HttpServletRequest request) throws UnknownHostException {
		log.info("memberSignupdto={}", dto);
		memberService.createMember(dto, request);
		// unverified 페이지로 이동하게 수정
		return "/member/unverified";
	}

	@PreAuthorize("hasRole('GUEST')")
	@PostMapping("/bodymetrics")
	public String createBodymetrics(BodymetricCreateDto dto) {
		log.info("dto={}", dto);
		Long id = bodyMetricService.create(dto);
		// 운동 플랜 변경하기
		memberService.updatePlan(dto);

		return "redirect:/";
	}

	@GetMapping("/member/googlelogin")
	public void loginWithGoolge() {

	}
}
