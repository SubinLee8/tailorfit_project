package com.itwill.tailorfit.dto;

import java.time.LocalDateTime;

import com.itwill.tailorfit.domain.Member;
import com.itwill.tailorfit.domain.WorkoutRecord;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class WorkoutRecordItemDto {
	private Long id;
	private String title;
	private Member member;
	private Integer likeCount;
	private LocalDateTime workoutDate;
	private String country;
	private String workoutType;
	private Integer workoutDuration; // 단위:초
	private Double distance;
	private Long stravaId;
	private Double avgSpeed;
	private Double startLat;
	private Double startLng;
	private Double caloriesBurned;
	private String isPrivate;
	
	public static WorkoutRecordItemDto fromEntity(WorkoutRecord record) {
		return WorkoutRecordItemDto.builder().member(record.getMember())
				.title(record.getTitle()).workoutDuration(record.getWorkoutDuration())
				.id(record.getId()).workoutDate(record.getWorkoutDate()).country(record.getCountry()).likeCount(record.getLikeCount())
				.workoutType(record.getWorkoutType()).distance(record.getDistance()).stravaId(record.getStravaId()).avgSpeed(record.getAvgSpeed())
				.startLat(record.getStartLat()).startLng(record.getStartLng()).caloriesBurned(record.getCaloriesBurned()).isPrivate(record.getIsPrivate())
				.build();
	}
}
