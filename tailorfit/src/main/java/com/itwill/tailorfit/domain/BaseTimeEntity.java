package com.itwill.tailorfit.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Getter
@ToString
@EqualsAndHashCode
@EntityListeners(AuditingEntityListener.class)
// -> 엔터티 (최초)생성 시간, 최종 수정 시간을 처리하는 리스너 설정.
@MappedSuperclass // 다른 엔터티클래스의 상위 클래스로 사용됨. 분리된 테이블과 매핑되는 엔터티는 아님.
public class BaseTimeEntity {
	@CreatedDate
	private LocalDateTime createdTime;

	@LastModifiedDate
	private LocalDateTime modifiedTime;

}
