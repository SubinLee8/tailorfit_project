package com.itwill.tailorfit.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.itwill.tailorfit.domain.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
	@EntityGraph(attributePaths = "roles")
	Optional<Member> findByUsername(String username);

	Member findByEmail(String email);

	boolean existsByUsername(String username);

	boolean existsByNickname(String nickname);

	boolean existsByEmail(String email);
}
