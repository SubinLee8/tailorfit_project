package com.itwill.tailorfit.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

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
import com.itwill.tailorfit.domain.StravaAuth;
import com.itwill.tailorfit.domain.WorkoutRecord;
import com.itwill.tailorfit.dto.StravaAuthCreateDto;
import com.itwill.tailorfit.repository.BodyMetricRepository;
import com.itwill.tailorfit.repository.MemberRepository;
import com.itwill.tailorfit.repository.StravaAuthRepository;
import com.itwill.tailorfit.repository.WorkoutRecordRepository;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Service
@Slf4j
public class StravaAuthService {
	@Autowired
	private RestTemplate restTemplate;

	@Autowired
	ObjectMapper objectMapper;
	private final MemberRepository memberRepo;
	private final BodyMetricRepository bodyMetricRepo;
	private final WorkoutRecordRepository workoutRepo;
	private final WorkoutRecordService workoutService;

	@Value("${spring.security.oauth2.client.registration.strava.client-id}")
	private String clientId;

	@Value("${spring.security.oauth2.client.registration.strava.client-secret}")
	private String clientSecret;

	private final StravaAuthRepository stravaAuthRepo;

	@Transactional
	public void saveActivities(Member member, String accessToken) throws JsonMappingException, JsonProcessingException {
		String activitiesUrl = "https://www.strava.com/api/v3/athlete/activities?per_page=30";

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Accept", "application/json");

		// HttpEntity<Object> 사용, 해당 url에 get요청으로 헤더를 담은 entity를 전송하고 리턴값은 string이다.
		HttpEntity<Object> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = restTemplate.exchange(activitiesUrl, HttpMethod.GET, entity, String.class);

		// ** workoutRepo 저장 **
		// ObjectMapper 생성
		ObjectMapper objectMapper = new ObjectMapper();

		// body를 JsonNode로 변환
		JsonNode activities = objectMapper.readTree(response.getBody());

		for (JsonNode activity : activities) {
			System.out.println(activity.toPrettyString());
			log.info("into loop, activity={}", activity);

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
			String title=activity.get("name").asText();
			double startLat = startLatLng.get(0).asDouble();
			double startLng = startLatLng.get(1).asDouble();
			double avgSpeed = activity.get("average_speed").asDouble();

			BodyMetric bodyMetric = bodyMetricRepo.findByMember(member);

			// isConnectedStrava 변경
			Member m = member.updateStravaStatus("Y");
			memberRepo.save(m);
			// 칼로리 계산
			Double caloriesBurned = workoutService.calcuateCalories(workoutType, distance, workoutDuration,
					bodyMetric.getWeight());

			// DB에 저장
			WorkoutRecord workout = WorkoutRecord.builder().distance(distance).workoutDuration(workoutDuration)
					.workoutType(workoutType).stravaId(stravaId).title(title)
					.workoutDate(LocalDateTime.parse(workoutDate, DateTimeFormatter.ISO_DATE_TIME)).country(country)
					.startLat(startLat).startLng(startLng).avgSpeed(avgSpeed).member(member).isPrivate("Y")
					.caloriesBurned(caloriesBurned).build();
			workoutRepo.save(workout);
		}
	}
	
	@Transactional
	// 웹훅 이벤트 후 단일 이벤트 저장.
	public void saveActivity(JsonNode activity,Member member) {
		Double distance = activity.get("distance").asDouble();
		Integer workoutDuration = activity.get("moving_time").asInt();
		String workoutType = activity.get("sport_type").asText();
		if (!workoutType.equals("Run") && !workoutType.equals("Walk")) {
			return;
		}
		long stravaId = activity.get("id").asLong();
		String title=activity.get("name").asText();
		String workoutDate = activity.get("start_date_local").asText();
		String country = activity.get("location_country").asText();
		JsonNode startLatLng = activity.get("start_latlng");
		double startLat = startLatLng.get(0).asDouble();
		double startLng = startLatLng.get(1).asDouble();
		double avgSpeed = activity.get("average_speed").asDouble();

		BodyMetric bodyMetric = bodyMetricRepo.findByMember(member);

		// isConnectedStrava 변경
		Member m = member.updateStravaStatus("Y");
		memberRepo.save(m);
		// 칼로리 계산
		Double caloriesBurned = workoutService.calcuateCalories(workoutType, distance, workoutDuration,
				bodyMetric.getWeight());

		// DB에 저장
		WorkoutRecord workout = WorkoutRecord.builder().distance(distance).workoutDuration(workoutDuration)
				.workoutType(workoutType).stravaId(stravaId).title(title).likeCount(workoutDuration).likeCount(0)
				.workoutDate(LocalDateTime.parse(workoutDate, DateTimeFormatter.ISO_DATE_TIME)).country(country)
				.startLat(startLat).startLng(startLng).avgSpeed(avgSpeed).member(member).isPrivate("Y")
				.caloriesBurned(caloriesBurned).build();
		workoutRepo.save(workout);
	}

	public String findAccessToken(Long ownerId) throws JsonMappingException, JsonProcessingException {
		StravaAuth s = stravaAuthRepo.findByOwnerId(ownerId);
		LocalDateTime now = LocalDateTime.now();
		if (s.getExpiredAt().isBefore(now)) {
			// 새로 발급
			return getAccessTokenByRefreshToken(s.getRefreshToken(),ownerId);
		} else {
			return s.getAccessToken();
		}
	}
	
	public Member findMemberByOwnerId(Long ownerId) {
		StravaAuth auth=stravaAuthRepo.findByOwnerId(ownerId);
		return auth.getMember();
		
	}

	public String getAccessTokenByRefreshToken(String oldRefreshToken,Long ownerId) throws JsonMappingException, JsonProcessingException {
		String url = "https://www.strava.com/api/v3/oauth/token";
		Map<String, String> params = new HashMap<>();
		params.put("client_id", clientId);
		params.put("client_secret", clientSecret);
		params.put("refresh_token", oldRefreshToken);
		params.put("grant_type", "refresh_token");
		
		
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Map<String, String>> entity = new HttpEntity<>(params, headers);

		// RestTemplate을 사용하여 POST 요청
		ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		String body = response.getBody();
		String newAccessToken = objectMapper.readTree(body).get("access_token").asText();
		String newRefreshToken = objectMapper.readTree(body).get("refresh_token").asText();
		log.info("accessToken={}", newAccessToken);
		log.info("refreshToken={}", newRefreshToken);

		String timestamp = objectMapper.readTree(body).get("expires_at").toString();
		Long unixTimestamp = Long.parseLong(timestamp);
		// 토큰 만료시간을 현재 시간대의 localDateTime 타입으로 변환
		LocalDateTime expires_at = LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTimestamp),
				ZoneId.systemDefault());
		log.info("expires_at={}", expires_at);
		
		//새로 저장
		StravaAuth s=stravaAuthRepo.findByOwnerId(ownerId);
		s.updateTokens(newRefreshToken, newAccessToken, expires_at);
		
		return newAccessToken;
	}

	@Transactional
	public void saveTokens(StravaAuthCreateDto dto) {
		StravaAuth stravaAuth = dto.toEntity();
		stravaAuthRepo.save(stravaAuth);

	}

	public Long findOwnerId(String accessToken) throws JsonMappingException, JsonProcessingException {
		// owner_id찾기
		HttpHeaders httpHeaders = new HttpHeaders();
		httpHeaders.set("Application", "Bearer" + accessToken);
		String urlForOwnerId = "https://www.strava.com/api/v3/athlete";
		HttpEntity<Object> entity = new HttpEntity<>(httpHeaders);
		ResponseEntity<String> response = restTemplate.exchange(urlForOwnerId, HttpMethod.GET, entity, String.class);
		ObjectMapper objectMapper = new ObjectMapper();
		JsonNode ownerInfo = objectMapper.readTree(response.getBody());
		return ownerInfo.get("id").asLong();
	}
}
