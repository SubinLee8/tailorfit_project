package com.itwill.tailorfit.service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
		String username = dto.getUsername();
		Member m = memberRepo.findByUsername(username).orElseThrow();
		BodyMetric b = dto.toEntity(m);
		BodyMetric result = bodyMetricRepo.save(b);
		// 정보 수정하기
		if (m.getHasBodyMetrics().equals("N")) {
			m.updateBodyStatus("Y");
		}
		log.info("{}", m.getAuthorities());
		if (!m.getAuthorities().contains("ROLE_ATHLETE")) {
			m.addRole(MemberRole.ATHLETE);
			memberRepo.save(m);
		}
		return result.getId();

	}

	@Transactional
	public Map<String, List<?>> getLatestWeights(Long userId) {
	    // DB에서 데이터를 가져옵니다.
	    Pageable pageable = PageRequest.of(0, 4); // 4개만 가져오기
	    List<Object[]> results = bodyMetricRepo.findLatestWeights(userId, pageable);

	    List<String> createdTimes = new ArrayList<>();
	    List<Double> weights = new ArrayList<>();

	    LocalDateTime now = LocalDateTime.now(); // 현재 시간

	    for (Object[] result : results) {
	        LocalDateTime createdTime = (LocalDateTime) result[0];
	        weights.add((Double) result[1]);

	        // 현재 시간과의 차이 계산
	        long daysAgo = ChronoUnit.DAYS.between(createdTime, now);

	        // 날짜 차이에 따라 변환
	        String timeAgo = "";
	        if (daysAgo == 0) {
	            timeAgo = "오늘";
	        } else if (daysAgo == 1) {
	            timeAgo = "1일 전";
	        } else {
	            timeAgo = daysAgo + "일 전";
	        }

	        createdTimes.add(timeAgo); // 변환된 시간 추가
	    }

	    // 4개가 안 되면 부족한 개수를 채우기
	    while (createdTimes.size() < 4) {
	        createdTimes.add("없음"); // 날짜는 "없음"으로 채우기
	        weights.add(0.0); // 몸무게는 0.0으로 채우기
	    }

	    // Map에 담아서 반환
	    Map<String, List<?>> response = new HashMap<>();
	    response.put("createdTimes", createdTimes);
	    response.put("weights", weights);
	    
	    log.info("Created Times: {}", createdTimes);  // 로그에서 정확히 출력되는지 확인
	    log.info("Response: {}", response);  // response에 담긴 값도 확인

	    return response;
	}


}
