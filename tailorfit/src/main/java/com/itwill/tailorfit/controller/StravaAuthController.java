package com.itwill.tailorfit.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;

import lombok.extern.slf4j.Slf4j;
@Slf4j
@RestController
@RequestMapping("/stravaauth")
public class StravaAuthController {
	 @Value("${spring.security.oauth2.client.registration.strava.client-id}")
	    private String clientId;
	

	@GetMapping("/login")
	public ResponseEntity<String> connectToStrava() {
		log.info("clientId={}",clientId);
		String authUrl = "https://www.strava.com/oauth/authorize?client_id=" + clientId
				+ "&redirect_uri=http://localhost:8080/stravaauth/callback&approval_prompt=force&scope=read";
		return ResponseEntity.status(HttpStatus.FOUND).header("Location", authUrl).build();
	}
	
	@GetMapping("/callback")
	public ResponseEntity<String> callback(@RequestParam String code){
		return null;
	}

}
