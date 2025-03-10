package com.itwill.tailorfit.repository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@SpringBootTest
public class UserRepositoryTest {
	@Autowired
	private UserRepository userRepo;
	
	@Test
	public void usertEST() {
		log.info("{}",userRepo.findAll());
	}
}
