package com.example.intermediate.repository;

import com.example.intermediate.domain.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;

public interface MemberRepository extends JpaRepository<Member, Long> {

  Optional<Member> findById(Long id);
  Optional<Member> findByNickname(String nickname);
  Optional<Member> findByKakaoId(Long kakaoId);
}
