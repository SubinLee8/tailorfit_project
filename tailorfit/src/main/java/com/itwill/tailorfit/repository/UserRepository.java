package com.itwill.tailorfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.tailorfit.domain.User;

public interface UserRepository extends JpaRepository<User, Long>{

}
