package com.itwill.tailorfit.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.WeekFields;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.tailorfit.domain.BodyMetric;
import com.itwill.tailorfit.domain.Member;
import com.itwill.tailorfit.domain.WorkoutRecord;
import com.itwill.tailorfit.dto.WorkoutRecordCreateDto;
import com.itwill.tailorfit.dto.WorkoutRecordDashboardDto;
import com.itwill.tailorfit.dto.WorkoutRecordItemDto;
import com.itwill.tailorfit.dto.WorkoutRecordUpdateDto;
import com.itwill.tailorfit.repository.BodyMetricRepository;
import com.itwill.tailorfit.repository.MemberRepository;
import com.itwill.tailorfit.repository.WorkoutRecordRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class WorkoutRecordService {
	private final WorkoutRecordRepository workoutRepo;
	private final MemberRepository memberRepo;
	private final BodyMetricRepository bodyRepo;

	public Double calcuateCalories(String workoutType, Double distance, Integer duration, Double weight) {
		Double MET = null;
		if (workoutType.equals("Run")) {
			MET = 10.0;
		} else if (workoutType.equals("Walk")) {
			MET = 3.5;
		}
		Double caloriesBurned = MET * weight * duration / 3600 * 5;
		return caloriesBurned;
	}

	@Transactional(readOnly = true)
	public Page<WorkoutRecordItemDto> readMyActivities(Integer pageNo, Sort sort, String username) {
		PageRequest pageable = PageRequest.of(pageNo, 10, sort);
		Page<WorkoutRecord> records = workoutRepo.findByMemberUsername(username, pageable);
		return records.map(WorkoutRecordItemDto::fromEntity);
	}

	@Transactional(readOnly = true)
	public Boolean isThisMyRecord(Long id, String username) {
		WorkoutRecord record = workoutRepo.findById(id).orElseThrow();
		String recordUsername = record.getMember().getUsername();
		if (recordUsername.equals(username)) {
			return true;
		} else
			return false;
	}

	@Transactional(readOnly = true)
	public WorkoutRecordItemDto readById(Long id) {
		WorkoutRecord record = workoutRepo.findById(id).orElseThrow();
		return WorkoutRecordItemDto.fromEntity(record);
	}

	@Transactional
	public void delete(Long id) {
		workoutRepo.deleteById(id);
	}

	@Transactional
	public Long createWorkoutRecord(WorkoutRecordCreateDto dto, String username) {
		Member member = memberRepo.findByUsername(username).orElseThrow();
		Double weight = bodyRepo.findByMemberLatest(member, PageRequest.of(0, 1)).stream().findFirst().orElse(null).getWeight();
		Double calories = calcuateCalories(dto.getWorkoutType(), dto.getDistance(), dto.getWorkoutDuration(), weight);
		WorkoutRecord entity = workoutRepo.save(dto.toEntity(calories, member));
		return entity.getId();
	}

	@Transactional
	public List<Integer> getWeeklyDuration(Long userId,String workoutType) {
		// DB에서 데이터를 가져옵니다.
		LocalDateTime fourWeeksAgo = LocalDateTime.now().minusWeeks(4).with(LocalTime.MIN);
		List<Object[]> results = workoutRepo.findWeeklyRunDuration(userId, fourWeeksAgo, workoutType);

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

	@Transactional
	public void update(WorkoutRecordUpdateDto dto) {
		WorkoutRecord record = workoutRepo.findById(dto.getId()).orElseThrow();
		Member member = memberRepo.findById(dto.getUserId()).orElseThrow();
		BodyMetric body = bodyRepo.findByMemberLatest(member, PageRequest.of(0, 1)).stream().findFirst().orElse(null);
		Double calories = calcuateCalories(dto.getWorkoutType(), dto.getDistance(), dto.getWorkoutDuration(),
				body.getWeight());
		dto.setCaloriesBurned(calories);
		record.update(dto);
	}

	@Transactional(readOnly = true)
	public Page<WorkoutRecordItemDto> readPublicActivities(Integer pageNo, Sort sort) {
		PageRequest pageable = PageRequest.of(pageNo, 10, sort);
		Page<WorkoutRecord> records = workoutRepo.findOnlyPublic(pageable);
		return records.map(WorkoutRecordItemDto::fromEntity);
	}
}
