package com.itwill.tailorfit.dto;

import com.itwill.tailorfit.domain.BodyMetric;
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
public class BodymetricCreateDto {
	private Double height;
	private Double weight;
	private String username;
	private String first = "N";

	public BodyMetric toEntity(Member member) {
		return BodyMetric.builder().weight(weight).height(height).member(member).build();
	}
}
