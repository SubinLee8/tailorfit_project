package com.itwill.tailorfit.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Member member;
    
    @Column(name="workout_date")
    private LocalDate workoutDate;
    @Column(name="workout_type")
    private String workoutType;
    @Column(name="workout_duration")
    private LocalTime workoutDuration;

    private BigDecimal distance;
    @Column(name="avg_spped")
    private BigDecimal avgSpeed;
    @Column(name="start_lat")
    private BigDecimal startLat;
    @Column(name="start_lng")
    private BigDecimal startLng;
    @Column(name="calories_burned")
    private BigDecimal caloriesBurned;
    
    @Column(name="is_private")
    private String isPrivate;


}

