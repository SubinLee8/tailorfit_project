package com.itwill.tailorfit.domain;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
@NoArgsConstructor
@Getter
@Builder
@AllArgsConstructor
@Entity
@Table(name="strava_auth")
public class StravaAuth {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "member_id", nullable = false)
	private Member member;

	@Column(name = "refresh_token", length = 20, nullable = false)
	private String refreshToken;

	@Column(name = "access_token", length = 20, nullable = false)
	private String accessToken;
	
	@Column(name="owner_id")
	private Long ownerId;

	@Column(name = "expired_at", nullable = false)
	private LocalDateTime expiredAt;
	
	public StravaAuth updateTokens(String newRefreshToken, String newAccessToken, LocalDateTime expiredAt) {
		this.refreshToken=newRefreshToken;
		this.accessToken=newAccessToken;
		this.expiredAt=expiredAt;
		return this;
	}
}
