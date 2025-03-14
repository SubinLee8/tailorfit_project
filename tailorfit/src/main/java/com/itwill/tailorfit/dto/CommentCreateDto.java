package com.itwill.tailorfit.dto;

import com.itwill.tailorfit.domain.Comment;
import com.itwill.tailorfit.domain.Member;
import com.itwill.tailorfit.domain.WorkoutRecord;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access=AccessLevel.PRIVATE)
public class CommentCreateDto {
	private Long recordId;
    private String username;
    private String content;
    
    public Comment toEntity(Member member, WorkoutRecord workoutRecord) {
    	return Comment.builder().content(content).member(member).workoutRecord(workoutRecord).build();
    }
}
