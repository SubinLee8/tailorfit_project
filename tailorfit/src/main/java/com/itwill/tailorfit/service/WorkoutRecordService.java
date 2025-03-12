package com.itwill.tailorfit.service;

import org.springframework.stereotype.Service;

@Service
public class WorkoutRecordService {	

	public Double calcuateCalories(String workoutType, Double distance, Integer duration, Double weight) {
		Double MET=null;
		if(workoutType.equals("Run")) {
			MET=10.0;
		}else if(workoutType.equals("Walk")) {
			MET=3.5;
		}
		Double caloriesBurned=MET*weight*duration/3600*5;
		return caloriesBurned;
	}
}
