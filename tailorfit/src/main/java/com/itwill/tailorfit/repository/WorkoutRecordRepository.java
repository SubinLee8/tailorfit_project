package com.itwill.tailorfit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.itwill.tailorfit.domain.WorkoutRecord;

public interface WorkoutRecordRepository extends JpaRepository<WorkoutRecord, Long> {
	Page<WorkoutRecord> findByMemberUsername(String username, Pageable pageable);
	
	@Query("select w from WorkoutRecord w where w.isPrivate='N'")
	Page<WorkoutRecord> findOnlyPublic(Pageable Pageable);
}
