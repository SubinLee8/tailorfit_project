package com.itwill.tailorfit.service;


import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.itwill.tailorfit.domain.BodyMetric;
import com.itwill.tailorfit.domain.Member;
import com.itwill.tailorfit.domain.MemberRole;
import com.itwill.tailorfit.dto.BodymetricCreateDto;
import com.itwill.tailorfit.repository.BodyMetricRepository;
import com.itwill.tailorfit.repository.MemberRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequiredArgsConstructor
@Service
public class BodyMetricsService {
	private final BodyMetricRepository bodyMetricRepo;
	private final MemberRepository memberRepo;
	
	@Transactional
	public Long create(BodymetricCreateDto dto) {
		String username=dto.getUsername();
		Member m = memberRepo.findByUsername(username).orElseThrow();
		BodyMetric b=dto.toEntity(m);
		BodyMetric result=bodyMetricRepo.save(b);
		//정보 수정하기
		if(m.getHasBodyMetrics().equals("N")) {
			m.updateBodyStatus("Y");
		}
		log.info("{}",m.getAuthorities());
		if(!m.getAuthorities().contains("ROLE_ATHLETE")) {
			m.addRole(MemberRole.ATHLETE);
			memberRepo.save(m);
		}
		return result.getId();
		
	}
	
	@Transactional
	public List<Integer> getWeeklyWeight(Long userId) {
		// DB에서 데이터를 가져옵니다.
		LocalDateTime fourWeeksAgo = LocalDateTime.now().minusWeeks(4).with(LocalTime.MIN);
		List<Object[]> results = bodyMetricRepo.findWeeklyWeight(userId, fourWeeksAgo);

		// 주차별로 값을 저장할 리스트
		List<Integer> durations = Arrays.asList(0, 0, 0, 0);
		for (Object[] result : results) {
			int weekDiff = ((Number) result[0]).intValue(); // 안전한 변환
			int duration = ((Number) result[1]).intValue(); // Long -> int 변환

			int index = 3 - weekDiff;
			durations.set(index, duration);
		}

		return durations;
	}
}
