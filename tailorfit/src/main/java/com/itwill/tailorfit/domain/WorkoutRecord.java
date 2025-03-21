package com.itwill.tailorfit.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import com.itwill.tailorfit.dto.WorkoutRecordUpdateDto;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@NoArgsConstructor
@Getter
@ToString(callSuper = true) //super class인 createdtime, modifiedTime도 toString으로	
@Table(name="workout_records")
@Entity
public class WorkoutRecord extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch=FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private Member member;
    
    @Column(name="like_count")
    private Integer likeCount=0;
    
    private String title;
    
    @Column(name="workout_date")
    private LocalDateTime workoutDate;
    
    private String country;
    
    @Column(name="workout_type")
    private String workoutType;
    
    @Column(name="workout_duration")
    private Integer workoutDuration; //단위:초

    private Double distance;
    
    @Column(name="strava_id")
    private Long stravaId;
    
    @Column(name="avg_speed")
    private Double avgSpeed;
    
    @Column(name="start_lat")
    private Double startLat;
    
    @Column(name="start_lng")
    private Double startLng;
    
    @Column(name="calories_burned")
    private Double caloriesBurned;
    
    @Column(name="is_private")
    private String isPrivate;

    public WorkoutRecord update(WorkoutRecordUpdateDto dto) {
    	this.title=dto.getTitle();
    	this.workoutDate=dto.getWorkoutDate();
    	this.country=dto.getCountry();
    	this.workoutType=dto.getWorkoutType();
    	this.workoutDuration=dto.getWorkoutDuration();
    	this.distance=dto.getDistance();
    	this.avgSpeed=dto.getAvgSpeed();
    	this.startLat=dto.getStartLat();
    	this.startLng=dto.getStartLng();
    	this.caloriesBurned=dto.getCaloriesBurned();
    	if(dto.getIsPrivate()) {
    		this.isPrivate="Y";
    	}
    	else {
    		this.isPrivate="N";
    	}
    	return this;
    }
}

