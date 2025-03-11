package com.itwill.tailorfit.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@Configuration
public class SecurityConfig {
	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
//        .authorizeHttpRequests(auth -> auth
//            .requestMatchers("/").permitAll() // 모든 사용자 접근 허용
//            .anyRequest().authenticated() // 그 외의 요청은 로그인 필요
//        )
        .formLogin(login->login.loginPage("/member/signin"))
//        .oauth2Login(oauth2 -> oauth2
//            .loginPage("/member/signin") 
//        )
        .logout(logout -> logout
            .logoutSuccessUrl("/") // 로그아웃 후 리디렉션할 URL
        )
        .csrf(csrf -> csrf.disable()); // CSRF 비활성화 (필요에 따라 설정)
		
		return http.build();
	}
}
