package com.itwill.tailorfit.controller;

import java.net.UnknownHostException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.itwill.tailorfit.domain.Member;
import com.itwill.tailorfit.domain.MemberRole;
import com.itwill.tailorfit.dto.BodymetricCreateDto;
import com.itwill.tailorfit.dto.MemberSignupDto;
import com.itwill.tailorfit.repository.MemberRepository;
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
	private final MemberRepository memberRepo;

	@GetMapping("/signin")
	public void toLogin() {
		log.info("login");
	}

	@GetMapping("/bodymetrics")
	public String toSetBodyMetrics(@AuthenticationPrincipal UserDetails userDetails,
			@RequestParam(value = "firsttry", required = false) String firsttry) {
		String username = userDetails.getUsername();
		Member member = memberRepo.findByUsername(username)
				.orElseThrow(() -> new IllegalArgumentException("User not found"));
		// 처음 방문했고, 아직 body metrics 설정이 안 되어 있다면 리디렉트
		if ("N".equals(member.getHasBodyMetrics()) && firsttry == null) {
			return "redirect:/member/bodymetrics?firsttry";
		}
		return "member/bodymetrics";
	}

	@GetMapping("/verify")
	public String verifyUser(@RequestParam(name = "token") String token, @RequestParam(name = "email") String email,
			@RequestParam(name = "selectedRole") String selectedRole)
			throws JsonMappingException, JsonProcessingException {
		if (memberRepo.findByEmail(email).getRoles().contains(MemberRole.GUEST)) {
			// 이미 이메일 인증이 완료된 유저
			log.info("게스트");
			return "redirect:/";
		}
		String result = memberService.verifyUser(email, token);
		log.info("result={}", result);
		if (result.equals("false")) {
			// 인증되지 않은 유저
			log.info("인증 안됨");
			return "redirect:/member/unverified?email=" + email + "&selectedRole=" + selectedRole;

		}
		if (result.equals("trainer")) {
			// verified 페이지로 이동하게 수정
			return "redirect:/member/signin?verified";
		}

		// unverified 페이지로 이동하게 수정
		return "redirect:/member/signin?tobodymetrics";
	}

	@GetMapping("/resendemail")
	public String resendEmail(@RequestParam(name = "email") String email, HttpServletRequest request,
			@RequestParam(name = "selectedRole") String selectedRole)
			throws JsonProcessingException, UnknownHostException {
		memberService.resendEmail(email, request, selectedRole);
		return "redirect:/member/unverified?email=" + email + "&selectedRole=" + selectedRole;
	}

	@GetMapping("/signup")
	public void toSignup(HttpServletRequest request) {
		request.getSession().invalidate();
	}

	@GetMapping("/stravaverified")
	public void toStravaVerfied() {

	}

	@GetMapping("/unverified")
	public String unverified(@RequestParam("email") String email, Model model,
			@RequestParam("selectedRole") String selectedRole) {
		log.info("roles={}", memberRepo.findByEmail(email).getRoles());
		if (memberRepo.findByEmail(email).getRoles().contains(MemberRole.GUEST)) {
			// 이미 이메일 인증이 완료된 유저
			log.info("게스트");
			return "redirect:/";
		}
		model.addAttribute("email", email);
		model.addAttribute("selectedRole", selectedRole);
		log.info("selectedRole={}", selectedRole);
		log.info("email={}", email);
		return "member/unverified";
	}

	@PostMapping("/signup")
	public String createMember(MemberSignupDto dto, HttpServletRequest request)
			throws UnknownHostException, JsonProcessingException {
		long startTime = System.nanoTime();
		log.info("memberSignupdto={}", dto);
		memberService.createMember(dto, request);
		long endTime = System.nanoTime(); // 종료 시간 기록
		long duration = (endTime - startTime) / 1_000_000; // 밀리초 단위 변환
		System.out.println("⏱ 회원가입 처리 시간: " + duration + " ms");

		return "redirect:/member/unverified?email=" + dto.getEmail() + "&selectedRole=" + dto.getRole();
	}

	@PreAuthorize("hasRole('GUEST')")
	@PostMapping("/bodymetrics")
	public String createBodymetrics(BodymetricCreateDto dto, HttpServletRequest request) {
		log.info("dto={}", dto);
		Long id = bodyMetricService.create(dto);
		// 운동 플랜 변경하기
		memberService.updatePlan(dto);
		if (dto.getFirst().equals("Y")) {
			request.getSession().invalidate();
			// 로그아웃 후 특정 페이지로 리다이렉트
			return "redirect:/member/signin";
		}

		return "redirect:/";
	}

	@PostMapping("/checkUsername")
	public ResponseEntity<Boolean> checkUsername(@RequestBody Map<String, String> request) {
		String username = request.get("username");
		log.info("username={}", username);
		boolean isDuplicate = memberService.isUsernameDuplicate(username);
		log.info("isDuplicateusername: {}", isDuplicate);

		return ResponseEntity.ok(isDuplicate);
	}

	@PostMapping("/checkNickname")
	public ResponseEntity<Boolean> checkNickname(@RequestBody Map<String, String> request) {
		String nickname = request.get("nickname");
		boolean isDuplicate = memberService.isNicknameDuplicate(nickname);
		log.info("isDuplicatenickname: {}", isDuplicate);

		return ResponseEntity.ok(isDuplicate);
	}

	@PostMapping("/checkEmail")
	public ResponseEntity<Boolean> checkEmail(@RequestBody Map<String, String> request) {
		String email = request.get("email");
		boolean isDuplicate = memberService.isEmailDuplicate(email);
		log.info("isDuplicateemail: {}", isDuplicate);

		return ResponseEntity.ok(isDuplicate);
	}
}
