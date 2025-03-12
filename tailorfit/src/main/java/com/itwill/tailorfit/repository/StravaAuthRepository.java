package com.itwill.tailorfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.tailorfit.domain.StravaAuth;

public interface StravaAuthRepository extends JpaRepository<StravaAuth, Long> {
	StravaAuth findByOwnerId(Long ownerId); 
}
