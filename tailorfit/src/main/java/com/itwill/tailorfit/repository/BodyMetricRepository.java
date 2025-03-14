package com.itwill.tailorfit.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.tailorfit.domain.BodyMetric;
import com.itwill.tailorfit.domain.Member;

public interface BodyMetricRepository extends JpaRepository<BodyMetric, Long> {
	List<BodyMetric> findByMember(Member member);

	@Query("select b from BodyMetric b where b.member = :member order by b.createdTime desc")
	BodyMetric findByMemberLatest(@Param("member") Member member);

	@Query("SELECT " + "  WEEK(CURRENT_DATE) - WEEK(w.createdTime) AS weekDiff, "
			+ "  COALESCE(AVG(w.weight), 0) AS avgWeight " + // NULL 방지
			"FROM BodyMetric w " + "WHERE w.member.id = :userId " + // WHERE 키워드 추가
			"  AND w.createdTime >= :fourWeeksAgo " + "GROUP BY weekDiff " + "ORDER BY weekDiff ASC") // 정렬 확인 필요
	List<Object[]> findWeeklyWeight(@Param("userId") Long userId, @Param("fourWeeksAgo") LocalDateTime fourWeeksAgo);
}
