package com.itwill.tailorfit.dto;

import java.time.LocalDateTime;

import com.itwill.tailorfit.domain.Member;
import com.itwill.tailorfit.domain.WorkoutRecord;

import jakarta.persistence.Column;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class WorkoutRecordCreateDto {
	private String username;
	private String title;
	private LocalDateTime workoutDate;
	private String country;
	private String workoutType;
	private Integer workoutDuration; // 단위:초
	private Double distance; // 단위:미터
	private Double avgSpeed; // 단위:km/h
	private Double startLat;
	private Double startLng;
	private Boolean isPrivate=false;

	public WorkoutRecord toEntity(Double caloriesBurned, Member member) {
		String newPrivate="N";
		if(isPrivate) {
			newPrivate="Y";
		}
		return WorkoutRecord.builder().likeCount(0).workoutType(workoutType).member(member).title(title).workoutDate(workoutDate).country(country).workoutDuration(workoutDuration).caloriesBurned(caloriesBurned)
				.distance(distance).avgSpeed(avgSpeed).startLat(startLat).startLng(startLng).isPrivate(newPrivate).build();
	}
}
