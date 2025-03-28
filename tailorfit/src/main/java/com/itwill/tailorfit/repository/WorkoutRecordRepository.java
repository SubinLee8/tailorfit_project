package com.itwill.tailorfit.repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.tailorfit.domain.Member;
import com.itwill.tailorfit.domain.WorkoutRecord;

public interface WorkoutRecordRepository extends JpaRepository<WorkoutRecord, Long> {
	Page<WorkoutRecord> findByMemberUsername(String username, Pageable pageable);

	@Query("select w from WorkoutRecord w where w.isPrivate='N'")
	Page<WorkoutRecord> findOnlyPublic(Pageable Pageable);

	List<WorkoutRecord> findByMember(Member member);

	@Query(value = "SELECT " + "  TIMESTAMPDIFF(WEEK, w.workout_date, CURRENT_DATE) AS weekDiff, "
			+ "  SUM(w.workout_duration) AS totalDuration " + "FROM workout_records w "
			+ "WHERE w.workout_type = :workoutType " + "  AND w.user_id = :userId "
			+ "  AND w.workout_date >= :fourWeeksAgo " + "GROUP BY weekDiff " + "HAVING weekDiff BETWEEN 0 AND 3 "
			+ "ORDER BY weekDiff ASC", nativeQuery = true) 
	List<Object[]> findWeeklyRunDuration(@Param("userId") Long userId,
			@Param("fourWeeksAgo") LocalDateTime fourWeeksAgo, @Param("workoutType") String workoutType);

	@Query("SELECT " + "  WEEK(CURRENT_DATE) - WEEK(w.workoutDate) AS weekDiff, "
			+ "  SUM(w.workoutDuration) AS totalDuration " + "FROM WorkoutRecord w " + "WHERE w.workoutType = 'Walk' "
			+ "  AND w.member.id = :userId " + "  AND w.workoutDate >= :fourWeeksAgo " + // 날짜 파라미터 추가
			"GROUP BY weekDiff " + "ORDER BY weekDiff ASC")
	List<Object[]> findWeeklyWalkDuration(@Param("userId") Long userId,
			@Param("fourWeeksAgo") LocalDateTime fourWeeksAgo);

}
