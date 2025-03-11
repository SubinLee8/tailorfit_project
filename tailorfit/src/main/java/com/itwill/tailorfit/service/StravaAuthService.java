package com.itwill.tailorfit.service;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.itwill.tailorfit.domain.Member;
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
	private final WorkoutRecordRepository workoutRepo;
	
	public void saveActivities(String accessToken) throws JsonMappingException, JsonProcessingException {
		String activitiesUrl = "https://www.strava.com/api/v3/athlete/activities?per_page=30";

		HttpHeaders headers = new HttpHeaders();
		headers.add("Authorization", "Bearer " + accessToken);
		headers.add("Accept", "application/json");

		// HttpEntity<Object> 사용, 해당 url에 get요청으로 헤더를 담은 entity를 전송하고 리턴값은 string이다.
		HttpEntity<Object> entity = new HttpEntity<>(headers);

		ResponseEntity<String> response = restTemplate.exchange(activitiesUrl, HttpMethod.GET, entity, String.class);

		// 응답 로그 출력
		log.info("activitiesResponse: {}", response.getBody());
		
		//유저 id 찾기
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		String username = authentication.getName();
		Long userid=memberRepo.findByUsername(username).orElseThrow().getId();
		 
		// ** workoutRepo 저장 **
		// ObjectMapper 생성
        ObjectMapper objectMapper = new ObjectMapper();
        
        // body를 JsonNode로 변환
        JsonNode activities = objectMapper.readTree(response.getBody());
        
        for (JsonNode activity : activities) {
        	// 필요한 값들 추출
            Double distance = activity.get("distance").asDouble();
            int movingTime = activity.get("moving_time").asInt();
            String sportType = activity.get("sport_type").asText();
            if (sportType!="Run"||sportType!="Walk") {
            	continue;
            }
            long id = activity.get("id").asLong();
            String startDateLocal = activity.get("start_date_local").asText();
            String locationCountry = activity.get("location_country").asText();
            JsonNode startLatLng = activity.get("start_latlng");
            double startLat = startLatLng.get(0).asDouble();  
            double startLng = startLatLng.get(1).asDouble();
            double averageSpeed = activity.get("average_speed").asDouble();
        	
            
        }	
	}
}
