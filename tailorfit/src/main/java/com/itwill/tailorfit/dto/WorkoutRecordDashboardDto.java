package com.itwill.tailorfit.dto;

import java.time.LocalDateTime;

import com.itwill.tailorfit.domain.WorkoutRecord;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkoutRecordDashboardDto {
	private String username;
	private LocalDateTime workoutDate;
	private String workoutType;
	private Integer workoutDuration; // 단위:초
	private Double distance; // 단위:미터
	private Double avgSpeed; // 단위:km/h
	private Double caloriesBurned;
	
}
