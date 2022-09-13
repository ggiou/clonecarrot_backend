package com.example.intermediate.repository;

import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByModifiedAtDesc(); //수정된 날짜로 찾기
    List<Post> findByMember(Member member); //회원 별 찾기

    Optional<Post> findById(Long id);

    Optional<Post> findByPostImgUrl(String postImgUrl); //게시글 이미지 별 찾기
}
