package com.example.intermediate.service;

import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.PostListResponseDto;
import com.example.intermediate.controller.response.PostResponseDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.*;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.ImageMapperRepository;
import com.example.intermediate.repository.LikeRepository;
import com.example.intermediate.repository.PostRepository;
import com.example.intermediate.repository.StateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final AmazonS3Service amazonS3Service;
    private final ImageMapperRepository imageMapperRepository;
    private final LikeRepository likeRepository;
    private final StateRepository stateRepository;
    private final StateService stateService;
    private final TokenProvider tokenProvider;

    @Transactional
    public ResponseDto<?> createPost(PostRequestDto postRequestDto, HttpServletRequest request) {
        if (null == request.getHeader("RefreshToken")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }
        //로그인된 회원인지 확인

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }
        //user가 유효한지 확인 = 로그인된 회원 확인

        String postImageUrl;
        MultipartFile multipartFile = postRequestDto.getFile();

        if (!multipartFile.isEmpty()) { // 삽입 할 이미지가 있다면
            ResponseDto<?> responseDto = amazonS3Service.uploadFile(multipartFile); // s3 파일업로드
            if (!responseDto.isSuccess())
                return responseDto;
            ImageMapper imageMapper = (ImageMapper) responseDto.getData();
            postImageUrl = imageMapper.getImageUrl();
        } else { //삽입 할 이미지가 없다면
            postImageUrl = null;
        }


        ResponseDto<?> stateResponseDto = stateService.createState(request);
        State states = (State) stateResponseDto.getData();
        String state = states.getState();

        Post post = Post.builder()
                .title(postRequestDto.getTitle())
                .price(postRequestDto.getPrice())
                .postImgUrl(postImageUrl)
                .location(postRequestDto.getLocation())
                .member(member)
                .tag(postRequestDto.getTag())
                .likeCount(0)
                .viewCount(0)
                .state(state)
                .content(postRequestDto.getContent())
                .build();
        postRepository.save(post);
        return ResponseDto.success(
                PostResponseDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .price(post.getPrice())
                        .postImgUrl(post.getPostImgUrl())
                        .location(post.getLocation())
                        .author(post.getMember().getNickname())
                        .tag(post.getTag())
                        .likeCount(post.getLikeCount())
                        .viewCount(post.getViewCount())
                        .state(post.getState())
                        .content(post.getContent())
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build()
        );
    }

    @Transactional(readOnly = true)
    public ResponseDto<?> getAllPost() {
        List<Post> posts = postRepository.findAllByOrderByModifiedAtDesc();
        List<PostListResponseDto> postsList = new ArrayList<>();
        for (Post post : posts) {
            postsList.add(PostListResponseDto.builder()
                    .postId(post.getId())
                    .price(post.getPrice())
                    .location(post.getLocation())
                    .state(post.getState())
                    .title(post.getTitle())
                    .viewCount(post.getViewCount())
                    .likeCount(post.getLikeCount())
                    .postImgUrl(post.getPostImgUrl())
                    .createdAt(post.getCreatedAt())
                    .modifiedAt(post.getModifiedAt())
                    .build());
        }
        return ResponseDto.success(postsList);
    }

    @Transactional
    public ResponseDto<?> getPost(Long postId) {
        Optional<Post> posts = postRepository.findById(postId);
        Post post = posts.get();
        if (null == post.getId()) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }
        post.view();


        return ResponseDto.success(
                PostResponseDto.builder()
                        .postId(post.getId())
                        .title(post.getTitle())
                        .author(post.getMember().getNickname())
                        .state(post.getState())
                        .tag(post.getTag())
                        .price(post.getPrice())
                        .content(post.getContent())
                        .location(post.getLocation())
                        .postImgUrl(post.getPostImgUrl())
                        .likeCount(post.getLikeCount())
                        .viewCount(post.getViewCount())
                        .createdAt(post.getCreatedAt())
                        .modifiedAt(post.getModifiedAt())
                        .build());
    }

    public ResponseDto<?> updatePost(PostRequestDto postRequestDto, HttpServletRequest request) throws UnsupportedEncodingException {
        if (null == request.getHeader("RefreshToken")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Post post = isPresentPost(postRequestDto.getPostId());
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        if (post.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 수정할 수 있습니다.");
        }

        String postImageUrl;
        MultipartFile multipartFile = postRequestDto.getFile();

        if (post.getPostImgUrl() == null) { //이전 게시글에 이미지가 없는 경우
            if (!multipartFile.isEmpty()) { // 삽입 할 이미지가 있다면
                ResponseDto<?> responseDto = amazonS3Service.uploadFile(multipartFile); // s3 파일업로드
                if (!responseDto.isSuccess())
                    return responseDto;
                ImageMapper imageMapper = (ImageMapper) responseDto.getData();
                postImageUrl = imageMapper.getImageUrl();
            } else { //삽입 할 이미지가 없다면
                postImageUrl = null;
            }
        } else { //이전 게시글에 이미지가 있는 경우
            Optional<ImageMapper> optionalImageMapper = imageMapperRepository.findByImageUrl(post.getPostImgUrl());
            ImageMapper image = optionalImageMapper.get();
            // s3의 경우 name = key 값을 알아야 삭제 가능
            if (amazonS3Service.removeFile(image.getName()))
                return ResponseDto.fail("BAD_REQUEST", "삭제 오류 발생.");
            if (!multipartFile.isEmpty()) { // 삽입 할 이미지가 있다면
                ResponseDto<?> responseDto = amazonS3Service.uploadFile(multipartFile); // s3 파일업로드
                if (!responseDto.isSuccess())
                    return responseDto;
                ImageMapper imageMapper = (ImageMapper) responseDto.getData();
                postImageUrl = imageMapper.getImageUrl();
            } else { //삽입 할 이미지가 없다면
                postImageUrl = null;
            }
        }

        post.update(postRequestDto, postImageUrl);

        postRepository.save(post);

        return ResponseDto.success(post);

    }

    @Transactional

    public ResponseDto<?> deletePost(Long postId, HttpServletRequest request) {

        if (null == request.getHeader("RefreshToken")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        if (null == request.getHeader("Authorization")) {
            return ResponseDto.fail("MEMBER_NOT_FOUND",
                    "로그인이 필요합니다.");
        }

        Member member = validateMember(request);
        if (null == member) {
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        Post post = isPresentPost(postId);
        if (null == post) {
            return ResponseDto.fail("NOT_FOUND", "존재하지 않는 게시글 id 입니다.");
        }

        if (post.validateMember(member)) {
            return ResponseDto.fail("BAD_REQUEST", "작성자만 삭제할 수 있습니다.");
        }

        if (postRepository.findById(postId).get().getPostImgUrl() != null) {

            Optional<ImageMapper> optionalImageMapper = imageMapperRepository.findByImageUrl(post.getPostImgUrl());
            ImageMapper image = optionalImageMapper.get();

            if (amazonS3Service.removeFile(image.getName()))
                return ResponseDto.fail("BAD_REQUEST", "삭제 오류 발생.");
        }

        stateRepository.deleteById(postId);
        likeRepository.deleteAllByPostId(postId);
        postRepository.delete(post);

        return ResponseDto.success(post.getTitle() + "가 성공적으로 삭제 되었습니다.");
    }

    @Transactional
    public Member validateMember(HttpServletRequest request) {
        if (!tokenProvider.validateToken(request.getHeader("RefreshToken"))) {
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }


    @Transactional(readOnly = true)
    public Post isPresentPost(Long postId) {
        Optional<Post> optionalPost = postRepository.findById(postId);
        return optionalPost.orElse(null);
    }
}
