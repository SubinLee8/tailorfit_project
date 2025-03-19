package com.itwill.tailorfit.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.itwill.tailorfit.domain.BodyMetric;
import com.itwill.tailorfit.domain.Member;

public interface BodyMetricRepository extends JpaRepository<BodyMetric, Long> {
	List<BodyMetric> findByMember(Member member);

	@Query("SELECT b FROM BodyMetric b WHERE b.member = :member ORDER BY b.createdTime DESC")
	List<BodyMetric> findByMemberLatest(@Param("member") Member member, Pageable pageable);


	@Query("SELECT bm.createdTime, bm.weight FROM BodyMetric bm WHERE bm.member.id = :userId " +
		       "GROUP BY bm.weight ORDER BY bm.createdTime DESC")
	List<Object[]> findLatestWeights(@Param("userId") Long userId, Pageable pageable);
}
