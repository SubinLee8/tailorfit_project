package com.itwill.tailorfit.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/member")
public class MemberController {
	
	@GetMapping("/signin")
	public void toLogin() {
		log.info("login");
	}
}
