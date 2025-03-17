package com.itwill.tailorfit.dto;

import java.time.LocalDateTime;

import com.itwill.tailorfit.domain.Member;
import com.itwill.tailorfit.domain.StravaAuth;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class StravaAuthCreateDto {
	private Member member;
	private LocalDateTime expiredAt;
	private String accessToken;
	private Long ownerId;
	private String refreshToken;

	public StravaAuth toEntity() {
		return StravaAuth.builder().member(member).accessToken(accessToken).refreshToken(refreshToken).ownerId(ownerId)
				.expiredAt(expiredAt).build();
	}
}
