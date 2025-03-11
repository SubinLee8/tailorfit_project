package com.itwill.tailorfit.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itwill.tailorfit.service.StravaAuthService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@RequiredArgsConstructor
@Slf4j
@RestController
@RequestMapping("/stravaauth")
public class StravaAuthController {
	@Value("${spring.security.oauth2.client.registration.strava.client-id}")
	private String clientId;
	@Value("${spring.security.oauth2.client.registration.strava.client-secret}")
	private String clientSecret;

	@Autowired
	RestTemplate restTemplete;

	@Autowired
	ObjectMapper objectMapper;
	
	private final StravaAuthService stravaAuthService;

	@GetMapping("/login")
	public ResponseEntity<String> connectToStrava() {
		log.info("clientId={}", clientId);
		// 권한 명시가 중요하다. activity:read_all 이 핵심
		String authUrl = "https://www.strava.com/oauth/authorize?client_id=" + clientId
				+ "&response_type=code&redirect_uri=http://localhost:8080/stravaauth/callback&approval_prompt=force&scope=read_all,activity:read_all";
		// Found이므로 302==get요청을 보내며 요청 Location은 authUrl
		return ResponseEntity.status(HttpStatus.FOUND).header("Location", authUrl).build();
	}

	@GetMapping("/callback")
	public String callback(@RequestParam("code") String code) throws JsonMappingException, JsonProcessingException {
		log.info("code={}", code); // authorization code

		String url = "https://www.strava.com/oauth/token?client_id=" + clientId + "&client_secret=" + clientSecret
				+ "&code=" + code + "&grant_type=authorization_code";

		ResponseEntity<String> response = restTemplete.postForEntity(url, null, String.class);
		String body = response.getBody();
		String accessToken = objectMapper.readTree(body).get("access_token").asText();
		String refreshToken = objectMapper.readTree(body).get("refresh_token").asText();
		log.info("accessToken={}", accessToken);
		log.info("refreshToken={}", refreshToken);

		String timestamp = objectMapper.readTree(body).get("expires_at").toString();
		Long unixTimestamp = Long.parseLong(timestamp);
		// 토큰 만료시간을 현재 시간대의 localDateTime 타입으로 변환
		LocalDateTime dateTimeLocal = LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTimestamp),
				ZoneId.systemDefault());
		log.info("dateTimeLocal={}", dateTimeLocal);
		
		// DB 저장
		stravaAuthService.saveActivities(accessToken);
		return "redirect: /";
	}
	
	@PostMapping("/webhook")
    public String handleWebhook(@RequestBody String payload, @RequestParam String verify_token) {
        // Verify Token을 검증합니다
        if (!verify_token.equals("your_verify_token")) {
            return "Unauthorized";
        }

        // 웹훅에서 보낸 데이터 처리 (운동 기록)
        System.out.println("Received Webhook: " + payload);

        // 운동 기록을 데이터베이스에 저장하거나 다른 작업을 진행할 수 있습니다.
        return "Webhook Received";
    }

}
