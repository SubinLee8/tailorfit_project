package com.itwill.tailorfit.controller;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
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
import com.itwill.tailorfit.domain.Member;
import com.itwill.tailorfit.dto.StravaAuthCreateDto;
import com.itwill.tailorfit.dto.WebhookEvent;
import com.itwill.tailorfit.repository.MemberRepository;
import com.itwill.tailorfit.service.StravaAuthService;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RequiredArgsConstructor
@Slf4j
@Controller
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
	private final MemberRepository memberRepo;
	private Member member;

	@GetMapping("/login")
	public ResponseEntity<String> connectToStrava(@AuthenticationPrincipal UserDetails userDetails) {
		String username = userDetails.getUsername();
		member = memberRepo.findByUsername(username).orElseThrow();
		if (member.getIsConnectedStrava().equals("Y")) {
			log.info("이미 연결되어있음");
			return ResponseEntity.ok(null);
		}
		// 권한 명시가 중요하다. activity:read_all 이 핵심
		String authUrl = "https://www.strava.com/oauth/authorize?client_id=" + clientId
				+ "&response_type=code&redirect_uri=http://localhost:8080/stravaauth/callback&approval_prompt=force&scope=read_all,activity:read_all";
		// Found이므로 302==get요청을 보내며 요청 Location은 authUrl
		return ResponseEntity.status(HttpStatus.FOUND).header("Location", authUrl).build();
	}

	@GetMapping("/callback")
	public String callback(@RequestParam("code") String code, @RequestParam("scope") String scope,
			@AuthenticationPrincipal UserDetails userDetails) throws JsonMappingException, JsonProcessingException {
		log.info("scope={}", scope); // authorization code

		if (!scope.equals("read,activity:read_all,read_all")) {
			return "redirect:/";
		}
		
		
		
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
		LocalDateTime expires_at = LocalDateTime.ofInstant(Instant.ofEpochSecond(unixTimestamp),
				ZoneId.systemDefault());
		log.info("expires_at={}", expires_at);

		// ownerId찾기
		Long ownerId = stravaAuthService.findOwnerId(accessToken);

		// 토큰, 만료시간 테이블에 저장
		stravaAuthService.saveTokens(StravaAuthCreateDto.builder().accessToken(accessToken).refreshToken(refreshToken)
				.ownerId(ownerId).expiredAt(expires_at).member(member).build());

		// DB 저장
		stravaAuthService.saveActivities(member, accessToken);

		// 내 기록으로 넘어갈 수 있도록 조정
		return "redirect:/";
	}

	// 유저 정보의 업데이트가 있을 때 여기로 옴.
	@PostMapping("/subscribe")
	public ResponseEntity<String> handleWebhook(@RequestBody WebhookEvent dto) throws JsonMappingException, JsonProcessingException {
		// create만 받는다.
		if (dto.getAspectType().equals("create") && dto.getObjectType().equals("activity")) {
			// 새로 db에 저장
			String id = dto.getObjectId().toString();
			Long ownerId = dto.getOwnerId();
			
			//accesstoken 찾기
			String accessToken=stravaAuthService.findAccessToken(ownerId);
			
			String url = "https://www.strava.com/api/v3/activities/" + id;
			HttpHeaders headers = new HttpHeaders();
			headers.set("Authorization", "Bearer " + accessToken);
			HttpEntity<Object> entity = new HttpEntity<>(headers);
			return restTemplete.exchange(url, HttpMethod.GET, entity, String.class);
		}
		
		return ResponseEntity.ok("Webhook received successfully! But We don't care :)");
	}

	// Strava의 GET 검증 요청에 응답하는 엔드포인트, 앞으로 사용하지 않는 곳.
	// SecurityConfig에서 모두 접근 가능하도록 바꿔야함 !!
	@GetMapping("/subscribe")
	public ResponseEntity<Map<String, String>> verifyWebhook(@RequestParam("hub.mode") String mode,
			@RequestParam("hub.verify_token") String verifyToken, @RequestParam("hub.challenge") String challenge) {

		log.info("get 요청 도착! 2분 안에 응답 보내야함");
		log.info("verifyToken={}", verifyToken);
		// Strava가 보내는 검증 요청에서 전송된 토큰과 비교
		if ("subscribe".equals(mode) && "STRAVA".equals(verifyToken)) {
			// 검증 요청이 올바른 경우, `hub.challenge` 값을 응답으로 돌려줌
			Map<String, String> response = new HashMap<>();
			response.put("hub.challenge", challenge); // hub.challenge 값을 그대로 반환

			// 응답 상태는 200 OK, 응답 본문은 JSON 형식
			return ResponseEntity.ok(response);
		} else {
			// 검증 실패 시 적절한 응답을 보내도록 할 수도 있음 (예: 400 Bad Request)
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
		}
	}

}
