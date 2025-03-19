package com.itwill.tailorfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.tailorfit.domain.EmailAuth;

public interface EmailAuthRepository extends JpaRepository<EmailAuth, Long>{
	EmailAuth findByAuthToken(String authToken);
}
