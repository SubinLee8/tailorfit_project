package com.itwill.tailorfit.repository;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.itwill.tailorfit.service.WorkoutRecordService;

import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class WorkoutRecordRepositoryTest {
	@Autowired
	private WorkoutRecordService workoutService;
	
	//@Test
//	public void weeklyRun() {
//		List<Integer> data=workoutService.getWeeklyRunDuration(9);
//		log.info("data={}",data);
//	}
}
