package com.itwill.tailorfit.service;

import java.net.UnknownHostException;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.itwill.tailorfit.domain.EmailAuth;
import com.itwill.tailorfit.domain.Member;
import com.itwill.tailorfit.domain.MemberRole;
import com.itwill.tailorfit.domain.WorkoutRoutine;
import com.itwill.tailorfit.dto.BodymetricCreateDto;
import com.itwill.tailorfit.dto.MemberSignupDto;
import com.itwill.tailorfit.repository.EmailAuthRepository;
import com.itwill.tailorfit.repository.MemberRepository;
import com.itwill.tailorfit.repository.WorkoutRoutineRepository;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
	private final MemberRepository memberRepo;
	private final WorkoutRoutineRepository workoutRoutineRepo;
	private final EmailAuthService emailAuthService;
	private final EmailAuthRepository emailAuthRepo;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("loadUserByUsername(username={})", username);

		Optional<Member> entity = memberRepo.findByUsername(username);
		if (entity.isPresent()) {
			// Optional 객체가 null이 아닌 Member 객체를 가지고 있으면
			return entity.get();
		} else {
			throw new UsernameNotFoundException(username + "과 일치하는 사용자 정보가 없음.");
		}
	}

	// 회원가입 서비스
	public Member createMember(MemberSignupDto dto, HttpServletRequest request) throws UnknownHostException {
		// 랜덤 인증 토큰 생성
		String token = UUID.randomUUID().toString();

		// 사용자 정보 생성
		String ecodedPassword=passwordEncoder.encode(dto.getPassword());
		dto.setPassword(ecodedPassword);
		Member m1 = dto.toEntity();
		m1.addRole(MemberRole.GUEST);
		Member entity = memberRepo.save(m1);

		// email_auth 테이블에 토큰 추가
		// role: athlete, both, trainer 중 하나
		EmailAuth emailAuth = EmailAuth.builder().member(entity).authToken(token).selectedRole(dto.getRole())
				.build();
		emailAuthRepo.save(emailAuth);

		// 이메일발송
		emailAuthService.sendVerificationEmail(dto.getEmail(), token, request);

		return entity;
	}

	// 이메일 인증
	public String verifyUser(String token) {
		EmailAuth auth = emailAuthRepo.findByAuthToken(token);

		if (auth != null) {
			Member member = auth.getMember();
			String selectedRole = auth.getSelectedRole();
			if (selectedRole.equals("athlete")) {
				emailAuthRepo.delete(auth);
				return "athlete";
			} else if (selectedRole.equals("trainer")) {
				member.addRole(MemberRole.TRAINER);
				memberRepo.save(member);
				emailAuthRepo.delete(auth);
				return "trainer";
			} else if (selectedRole.equals("both")) {
				member.addRole(MemberRole.TRAINER);
				memberRepo.save(member);
				emailAuthRepo.delete(auth);
				return "both";
			}
		}
		return "false";
	}

	public void updatePlan(BodymetricCreateDto dto) {
		Member member = memberRepo.findByUsername(dto.getUsername()).orElseThrow();
		Double height = dto.getHeight();
		Double weight = dto.getWeight();
		Long plan = calculateWorkoutPlan(height, weight);
		log.info("plan={}", plan);
		WorkoutRoutine routine = workoutRoutineRepo.findById(plan).orElseThrow();
		Member m = member.updatePlan(routine);
		memberRepo.save(m);
	}

	private Long calculateWorkoutPlan(Double height, Double weight) {
		// BMI 계산
		height = height / 100;
		double bmi = weight / (height * height);
		log.info("bmi={}", bmi);
		// 범위에 따른 반환값 결정
		if (bmi < 16) {
			return 1L; // 심각한 저체중
		} else if (bmi >= 16 && bmi < 18.5) {
			return 2L; // 저체중
		} else if (bmi >= 18.5 && bmi < 22) {
			return 3L; // 정상
		} else if (bmi >= 22 && bmi < 25) {
			return 4L; // 정상
		} else if (bmi >= 25 && bmi < 30) {
			return 5L; // 과체중
		} else if (bmi >= 30 && bmi < 35) {
			return 6L; // 비만
		} else {
			return 7L; // 고도 비만
		}

	}

}
