package com.itwill.tailorfit.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.itwill.tailorfit.domain.Member;
import com.itwill.tailorfit.domain.MemberRole;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
public class MemberRepositoryTest {
	@Autowired
	private MemberRepository memberRepo;
	
	@Autowired
	private PasswordEncoder encoder;
	
	//@Test
	public void testPasswordEncoder() {
		assertThat(encoder).isNotNull();
	}
	
	//@Test //@Transactoinal을 넣어주면 롤백이 되어 저장되지 않음!
	public void testMemberSave() {
		Member m1= Member.builder().birthDate(LocalDate.of(2000, 8, 16)).email("admin2@gmail.com")
				.gender("F").nickname("크하").password(encoder.encode("2222")).username("admin2").build();
		m1.addRole(MemberRole.ATHLETE);
		Member entity=memberRepo.save(m1);
		log.info("생성된 entity={}",entity);
	}
}
