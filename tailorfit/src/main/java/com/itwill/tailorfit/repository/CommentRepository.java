package com.itwill.tailorfit.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.tailorfit.domain.Comment;

public interface CommentRepository extends JpaRepository<Comment, Long>{

}
