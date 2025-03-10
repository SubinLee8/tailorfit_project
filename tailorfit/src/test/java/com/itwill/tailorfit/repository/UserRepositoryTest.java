package com.itwill.tailorfit.repository;

import static org.assertj.core.api.Assertions.assertThat;


import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.itwill.tailorfit.domain.User;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@SpringBootTest
public class UserRepositoryTest {
	@Autowired
	private UserRepository userRepo;
	
	@Test
	public void userRepoTest() {
		//assertThat(userRepo).isNotNull();
		
		List<User> users=userRepo.findAll();
		log.info("users={}",users);
	}
}
