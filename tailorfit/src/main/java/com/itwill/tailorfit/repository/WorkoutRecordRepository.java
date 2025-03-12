package com.itwill.tailorfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.tailorfit.domain.WorkoutRecord;
import com.itwill.tailorfit.domain.WorkoutRoutine;

public interface WorkoutRecordRepository extends JpaRepository<WorkoutRecord, Long> {

}
