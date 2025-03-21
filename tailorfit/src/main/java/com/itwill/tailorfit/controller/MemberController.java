package com.itwill.tailorfit.controller;

import java.net.UnknownHostException;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itwill.tailorfit.domain.Member;
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
	public String verifyUser(@RequestParam(name = "token") String token) {
		String result = memberService.verifyUser(token);
		if (result.equals("trainer")) {
			// verified 페이지로 이동하게 수정
			return "redirect:/member/signin?verified";
		}
		// unverified 페이지로 이동하게 수정
		return "redirect:/member/signin?tobodymetrics";
	}

	@GetMapping("/signup")
	public void toSignup(HttpServletRequest request) {
		request.getSession().invalidate();
	}

	@GetMapping("/stravaverified")
	public void toStravaVerfied() {

	}

	@PostMapping("/signup")
	public String createMember(MemberSignupDto dto, HttpServletRequest request) throws UnknownHostException {
		long startTime = System.nanoTime();
		log.info("memberSignupdto={}", dto);
		memberService.createMember(dto, request);
		long endTime = System.nanoTime(); // 종료 시간 기록
		long duration = (endTime - startTime) / 1_000_000; // 밀리초 단위 변환
		System.out.println("⏱ 회원가입 처리 시간: " + duration + " ms");

		return "member/unverified";
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
