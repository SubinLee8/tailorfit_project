package com.itwill.tailorfit.service;

import java.awt.print.Pageable;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itwill.tailorfit.domain.Comment;
import com.itwill.tailorfit.domain.WorkoutRecord;
import com.itwill.tailorfit.dto.CommentCreateDto;
import com.itwill.tailorfit.dto.CommentItemDto;
import com.itwill.tailorfit.dto.CommentUpdateDto;
import com.itwill.tailorfit.repository.CommentRepository;
import com.itwill.tailorfit.repository.MemberRepository;
import com.itwill.tailorfit.repository.WorkoutRecordRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Service
@Slf4j
@RequiredArgsConstructor
public class CommentService {
	private final CommentRepository commentRepo;
	private final MemberRepository memberRepo;
	private final WorkoutRecordRepository workoutRepo;
	
	@Transactional(readOnly=true)
	public Page<CommentItemDto> readByRecord(Long recordId, Sort sort, int p) {
		PageRequest pageable= PageRequest.of(p, 5, sort);
		WorkoutRecord record=workoutRepo.findById(recordId).orElseThrow();
		Page<Comment> comments=commentRepo.findByWorkoutRecordId(recordId,pageable);
		// Comment 객체를 DTO로 변환
	    Page<CommentItemDto> commentDTOs = comments.map(comment -> CommentItemDto.fromEntity(comment));
		return commentDTOs;
	}
	
	@Transactional
	public Long createComment(CommentCreateDto dto) {
		log.info("dto={}",dto);
		String username=dto.getUsername();
		Long recordId=dto.getRecordId();
		Comment comment=commentRepo.save(dto.toEntity(memberRepo.findByUsername(username).orElseThrow()
				,workoutRepo.findById(recordId).orElseThrow()));
		return comment.getId();
	}
	
	@Transactional
	public void updateComment(CommentUpdateDto dto) {
		log.info("dto={}",dto);
		Comment comment=commentRepo.findById(dto.getId()).orElseThrow();
		comment.update(dto.getContent());
	}
	
	@Transactional
	public void delete(Long commentId) {
		commentRepo.deleteById(commentId);
	}

}
