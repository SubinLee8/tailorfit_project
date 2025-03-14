package com.itwill.tailorfit.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.itwill.tailorfit.domain.Member;
import com.itwill.tailorfit.domain.WorkoutRoutine;
import com.itwill.tailorfit.dto.BodymetricCreateDto;
import com.itwill.tailorfit.repository.MemberRepository;
import com.itwill.tailorfit.repository.WorkoutRoutineRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
	private final MemberRepository memberRepo;
	private final WorkoutRoutineRepository workoutRoutineRepo;
	
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

	public void updatePlan(BodymetricCreateDto dto) {
		Member member=memberRepo.findByUsername(dto.getUsername()).orElseThrow();
		Double height=dto.getHeight();
		Double weight=dto.getWeight();
		Long plan=calculateWorkoutPlan(height, weight);
		log.info("plan={}",plan);
		WorkoutRoutine routine=workoutRoutineRepo.findById(plan).orElseThrow();
		Member m=member.updatePlan(routine);
		memberRepo.save(m);
	}
	
	private Long calculateWorkoutPlan(Double height, Double weight) {
		 // BMI 계산
		height = height / 100; 
        double bmi = weight / (height * height);
        log.info("bmi={}",bmi);
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
