package com.itwill.tailorfit.dto;


import java.time.LocalDateTime;

import com.itwill.tailorfit.domain.Comment;
import com.itwill.tailorfit.domain.Member;
import com.itwill.tailorfit.domain.WorkoutRecord;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class CommentItemDto {
	private Long id;
	private Long recordId;
	private String username;
	private String nickname;
	private String content;
	private LocalDateTime modifiedTime;
	
	public static CommentItemDto fromEntity(Comment comment) {
		return CommentItemDto.builder().id(comment.getId()).recordId(comment.getWorkoutRecord().getId()).username(comment.getMember().getUsername())
				.nickname(comment.getMember().getNickname()).content(comment.getContent()).modifiedTime(comment.getModifiedTime()).build();
	}
}
