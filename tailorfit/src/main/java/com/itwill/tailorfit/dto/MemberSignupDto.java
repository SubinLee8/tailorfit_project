package com.itwill.tailorfit.dto;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.itwill.tailorfit.domain.Member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class MemberSignupDto {

	private String username;
	private String password;
	private String nickname;
	private String email;
	private String gender;
	private LocalDate birthdate;
	private String role;

	public Member toEntity() {
		return Member.builder().username(username).password(password).nickname(nickname).email(email).hasBodyMetrics("N").isConnectedStrava("N")
				.gender(gender).birthDate(birthdate).build();
	}
}
