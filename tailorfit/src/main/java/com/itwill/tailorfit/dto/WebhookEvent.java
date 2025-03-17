package com.itwill.tailorfit.dto;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
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
	@JsonProperty("aspect_type")
	private String aspectType;
	
//	@JsonProperty("aspect_type")
//    private Long eventTime;
	@JsonProperty("object_id")
    private Long objectId;
	
	@JsonProperty("object_type")
    private String objectType;
	
	@JsonProperty("owner_id")
    private Long ownerId;
    //private Long subscriptionId;
}
