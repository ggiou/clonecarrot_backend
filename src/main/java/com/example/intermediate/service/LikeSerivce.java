package com.example.intermediate.service;

import com.example.intermediate.controller.request.LikeRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.Like;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.domain.RefreshToken;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.LikeRepository;
import com.example.intermediate.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class LikeSerivce {
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> post_like(LikeRequestDto likeRequestDto, HttpServletRequest request) {
        Member member = validateMember(request); //현재 로그인 중인 멤버
        Optional<Post> temp = postRepository.findById(likeRequestDto.getPostId());
        if (temp.isEmpty()) {
            return ResponseDto.fail("FAIL-LIKE", "해당 게시글이 존재하지 않습니다.");
        }
        if (Objects.equals(temp.get().getMember().getNickname(), member.getNickname())) {
            return ResponseDto.fail("FAIL-LIKE", "자신이 작성한 게시글은 관심항목에 추가 할 수 없습니다.");
        } //게시글 작성자 like 배제

        List<Like> temp2 = likeRepository.findAllByPostId(likeRequestDto.getPostId());

        if (!temp2.isEmpty()) {
            for (Like like : temp2) {
                boolean likeEquals = Objects.equals(like.getMember().getNickname(), member.getNickname());
                if (likeEquals) {
                    return ResponseDto.fail("FAIL-LIKE", "이미 관심항목에 추가된 게시글 입니다.");
                }
            }
        } //중복 like 배제
        Post post = temp.get();

        post.like(); //총 좋아요 수 올리기

        Like like = Like.builder()
                .member(member)
                .postId(post.getId())
                .build();
        likeRepository.save(like);
        return ResponseDto.success(post.getTitle() + "가 관심 항목에 추가 되었습니다.");
    }

    @Transactional
    public ResponseDto<?> post_dislike(LikeRequestDto likeRequestDto, HttpServletRequest request) {
        Member member = validateMember(request); //현재 로그인 중인 멤버

        Optional<Like> temp = likeRepository.findByIdAndPostId(likeRequestDto.getLikeId(), likeRequestDto.getPostId());
        if (!temp.isPresent()) {
            return ResponseDto.fail("FAIL-DISLIKE", "해당 관심 항목이 존재하지 않습니다.");
        }

        Optional<Post> temp2 = postRepository.findById(likeRequestDto.getPostId());
        if (!temp2.isPresent()) {
            return ResponseDto.fail("FAIL-DISLIKE", "해당 게시글이 존재하지 않습니다.");
        }

        Like like = temp.get();
        if (!Objects.equals(like.getMember().getId(), member.getId())) {
            return ResponseDto.fail("FAIL-DISLIKE", "해당 관심 항목의 작성자가 아닙니다.");
        }

        Post post = temp2.get();
        if (!Objects.equals(like.getPostId(), post.getId())) {
            return ResponseDto.fail("FAIL-DISLIKE", "해당 게시글의 관심항목이 아닙니다.");
        }  //해당 로그인 한 유저가 해당 게시글의 관심항목 작성자가 아닐 경우에는 예외처리 해줘야 함
        post.dislike();

        likeRepository.delete(like);
        return ResponseDto.success(post.getTitle() + "가 관심 항목에서 취소 되었습니다.");
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
