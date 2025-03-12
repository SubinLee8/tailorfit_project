package com.itwill.tailorfit.dto;

import java.time.LocalDateTime;

import com.itwill.tailorfit.domain.Member;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class WebhookEvent {
	private String aspectType;
    private Long eventTime;
    private Long objectId;
    private String objectType;
    private Long ownerId;
    private Long subscriptionId;
}
