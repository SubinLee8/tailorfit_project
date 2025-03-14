package com.itwill.tailorfit.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.tailorfit.domain.Comment;
import com.itwill.tailorfit.domain.WorkoutRecord;

public interface CommentRepository extends JpaRepository<Comment, Long>{
	Page<Comment> findByWorkoutRecord(WorkoutRecord record, Pageable pageable);
	Page<Comment> findByWorkoutRecordId(Long id, Pageable pageable);
}
