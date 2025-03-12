package com.itwill.tailorfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.tailorfit.domain.BodyMetric;
import com.itwill.tailorfit.domain.Member;

public interface BodyMetricRepository extends JpaRepository<BodyMetric, Long> {
	BodyMetric findByMember(Member member);
}
