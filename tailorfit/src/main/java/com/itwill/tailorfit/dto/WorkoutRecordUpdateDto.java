package com.itwill.tailorfit.dto;

import java.time.LocalDateTime;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class WorkoutRecordUpdateDto {
	private Long id;
	private Long userId;
	private String title;
	private LocalDateTime workoutDate;
	private String country;
	private String workoutType;
	private Integer workoutDuration; // 단위:초
	private Double distance; // 단위:미터
	private Double avgSpeed; // 단위:km/h
	private Double startLat;
	private Double startLng;
	private Double caloriesBurned;
	private Boolean isPrivate=false;
}
