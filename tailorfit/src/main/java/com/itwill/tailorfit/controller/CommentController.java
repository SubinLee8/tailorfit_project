package com.itwill.tailorfit.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.itwill.tailorfit.domain.Comment;
import com.itwill.tailorfit.dto.CommentCreateDto;
import com.itwill.tailorfit.dto.CommentItemDto;
import com.itwill.tailorfit.dto.CommentUpdateDto;
import com.itwill.tailorfit.service.CommentService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@RequiredArgsConstructor
@Slf4j
@Controller
@RequestMapping("/api/comment")
public class CommentController {
	private final CommentService commentService;

	@GetMapping("/all/{recordId}")
	public ResponseEntity<PagedModel<CommentItemDto>> getCommentList(@PathVariable Long recordId,
			@RequestParam(defaultValue = "0", name="p") int p) {
		// 최종 수정 시간의 내림차순으로 정렬된, 한 페이지에 출력할 댓글 목록을 가져옴.
		Page<CommentItemDto> comments = commentService.readByRecord(recordId, Sort.by("modifiedTime").descending(), p);
		return ResponseEntity.ok(new PagedModel<>(comments));
	}
	
	@PostMapping()
	public ResponseEntity<Long> postComment(@RequestBody CommentCreateDto dto){
		Long id=commentService.createComment(dto);
		return ResponseEntity.ok(id);
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<Long> modifyComment(@PathVariable Long id,@RequestBody CommentUpdateDto dto){
		commentService.updateComment(dto);
		return ResponseEntity.ok(id);
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<Long> deleteComment(@PathVariable Long id){
		commentService.delete(id);
		return ResponseEntity.ok(id);
	}
}
