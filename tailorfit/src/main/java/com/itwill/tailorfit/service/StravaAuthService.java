package com.itwill.tailorfit.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itwill.tailorfit.domain.BodyMetric;
import com.itwill.tailorfit.domain.Member;
import com.itwill.tailorfit.domain.WorkoutRecord;
import com.itwill.tailorfit.repository.BodyMetricRepository;
import com.itwill.tailorfit.repository.MemberRepository;
import com.itwill.tailorfit.repository.WorkoutRecordRepository;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class StravaAuthService {
	@Autowired
	private RestTemplate restTemplate;
	private final MemberRepository memberRepo;
	private final BodyMetricRepository bodyMetricRepo;
	private final WorkoutRecordRepository workoutRepo;
	private final WorkoutRecordService workoutService;

	@Value("${spring.security.oauth2.client.registration.strava.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.strava.client-secret}")
	private String clientSecret;


	public void saveActivities(String accessToken) throws JsonMappingException, JsonProcessingException {
		String activitiesUrl = "https://www.strava.com/api/v3/athlete/activities?per_page=30";

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Accept", "application/json");

		// HttpEntity<Object> 사용, 해당 url에 get요청으로 헤더를 담은 entity를 전송하고 리턴값은 string이다.
		HttpEntity<Object> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(activitiesUrl, HttpMethod.GET, entity, String.class);

		// 유저 id 찾기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		Long userId = memberRepo.findByUsername(username).orElseThrow().getId();

		// ** workoutRepo 저장 **
		// ObjectMapper 생성
		ObjectMapper objectMapper = new ObjectMapper();

		// body를 JsonNode로 변환
		JsonNode activities = objectMapper.readTree(response.getBody());

		for (JsonNode activity : activities) {
			System.out.println(activity.toPrettyString());
			log.info("into loop, activity={}",activity);
			
			Double distance = activity.get("distance").asDouble();
			Integer workoutDuration = activity.get("moving_time").asInt();
			String workoutType = activity.get("sport_type").asText();
			if (!workoutType.equals("Run") && !workoutType.equals("Walk")) {
				continue;
			}
			long stravaId = activity.get("id").asLong();
			String workoutDate = activity.get("start_date_local").asText();
			String country = activity.get("location_country").asText();
			JsonNode startLatLng = activity.get("start_latlng");
			double startLat = startLatLng.get(0).asDouble();
			double startLng = startLatLng.get(1).asDouble();
			double avgSpeed = activity.get("average_speed").asDouble();
			
			Member m=memberRepo.findById(userId).orElseThrow();
			BodyMetric bodyMetric=bodyMetricRepo.findByMember(m);
			
			//isConnectedStrava 변경
			//m.updateStravaStatus("Y");
			
			//칼로리 계산
//			Double caloriesBurned=workoutService.calcuateCalories(workoutType, distance, 
//					workoutDuration, bodyMetric.getWeight());
			
			//DB에 저장
//			WorkoutRecord.builder().distance(distance).workoutDuration(workoutDuration).workoutType(workoutType)
//			.stravaId(stravaId).workoutDate(LocalDateTime.parse(workoutDate, DateTimeFormatter.ISO_DATE_TIME))
//			.country(country).startLat(startLat).startLng(startLng).avgSpeed(avgSpeed).member(m)
//			.isPrivate("Y").caloriesBurned(caloriesBurned).build();
		}
	}
	
	
	//자바스크립트로 구현할까??
	public void registerWebhook(String accessToken) {
		String webhookUrl = "https://www.strava.com/api/v3/push_subscriptions";
		String verifyToken = "your_verify_token";  // Webhook 확인용 토큰

        String body = String.format("{\"callback_url\":\"http://localhost:8080/webhook\",\"verify_token\":\"%s\",\"object_type\":\"activity\",\"aspect_type\":\"create\",\"latency\":600}", verifyToken);

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.exchange(
                webhookUrl,
                HttpMethod.POST,
                entity,
                String.class
        );
	}
}
