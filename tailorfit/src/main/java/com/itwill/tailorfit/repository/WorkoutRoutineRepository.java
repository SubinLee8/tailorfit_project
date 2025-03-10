package com.itwill.tailorfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.tailorfit.domain.WorkoutRoutine;

public interface WorkoutRoutineRepository extends JpaRepository<WorkoutRoutine, Long> {
	
}
