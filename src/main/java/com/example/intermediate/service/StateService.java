package com.example.intermediate.service;

import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.request.StateRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.Post;
import com.example.intermediate.domain.State;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.PostRepository;
import com.example.intermediate.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class StateService {
    private final PostRepository postRepository;
    private final StateRepository stateRepository;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> createState( HttpServletRequest request){
        Member member = validateMember(request); //현재 로그인 중인 멤버
        State state = State.builder()
                .member(member)
                .state("판매중")
                .build();
        stateRepository.save(state);
        return ResponseDto.success(state);
    }

    @Transactional
    public ResponseDto<?> state_post(Long postId, HttpServletRequest request) {
        Member member = validateMember(request); //현재 로그인 중인 멤버
        Optional<Post> temp = postRepository.findById(postId);
        if (temp.isEmpty()) {
            return ResponseDto.fail("FAIL-STATE", "해당 게시글이 존재하지 않습니다.");
        }
        Post post = temp.get();
        if (post.validateMember(member)) {
            return ResponseDto.fail("FAIL-STATE", "게시글의 작성자만 상태를 변경할 수 있습니다.");
        }
        post.state(); //상태 변경 -> 판매중
        postRepository.save(post);


        Optional<State> temp2 = stateRepository.findById(postId);

        if (temp2.isEmpty()) {
            return ResponseDto.fail("FAIL-STATE", "해당 게시글이 존재하지 않습니다.");
        }
        State state = temp2.get();

        state.state();
        stateRepository.save(state);


        return ResponseDto.success(post.getTitle()+"의 상태가 판매중으로 변경 되었습니다.");
    }

    @Transactional
    public ResponseDto<?> outstate_post(Long postId, HttpServletRequest request) {
        Member member = validateMember(request); //현재 로그인 중인 멤버

        Optional<Post> temp = postRepository.findById(postId);

        if (temp.isEmpty()) {
            return ResponseDto.fail("FAIL-STATE", "해당 게시글이 존재하지 않습니다.");
        }
        Post post = temp.get();

        if (post.validateMember(member)) {
            return ResponseDto.fail("FAIL-STATE", "게시글의 작성자만 상태를 변경할 수 있습니다.");
        }
        post.outstate(); //상태 변경 -> 판매중
        postRepository.save(post);

        Optional<State> temp2 = stateRepository.findById(postId);

        if (temp2.isEmpty()) {
            return ResponseDto.fail("FAIL-STATE", "해당 게시글이 존재하지 않습니다.");
        }

        State state = temp2.get();

        state.outstate();
        stateRepository.save(state);

        return ResponseDto.success(post.getTitle()+"의 상태가 판매완료로 변경 되었습니다.");

    }

        @Transactional
        public Member validateMember(HttpServletRequest request) {
            if(!tokenProvider.validateToken(request.getHeader("RefreshToken"))){
                return null;
            }
            return tokenProvider.getMemberFromAuthentication();
        }


    }


