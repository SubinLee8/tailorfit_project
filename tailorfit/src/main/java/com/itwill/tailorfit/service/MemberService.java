package com.itwill.tailorfit.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.itwill.tailorfit.domain.Member;
import com.itwill.tailorfit.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {
	private final MemberRepository memberRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		log.info("loadUserByUsername(username={})", username);
		
		Optional<Member> entity = memberRepo.findByUsername(username);
		if (entity.isPresent()) {
			// Optional 객체가 null이 아닌 Member 객체를 가지고 있으면
			return entity.get();
		} else {
			throw new UsernameNotFoundException(username + "과 일치하는 사용자 정보가 없음.");
		}
	}

}
