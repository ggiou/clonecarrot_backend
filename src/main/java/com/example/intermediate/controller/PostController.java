package com.example.intermediate.controller;

import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RequiredArgsConstructor
@RestController
public class PostController {

    private final PostService postService;

    @RequestMapping(value = "/api/auth/post", method = RequestMethod.POST, consumes = {"multipart/form-data"})
    public ResponseDto<?> createPost(@ModelAttribute PostRequestDto postRequestDto, HttpServletRequest request) throws IOException{
        return postService.createPost(postRequestDto, request);
    } //회원인 유저만 게시글 작성 가능

    @RequestMapping(value = "/api/post", method = RequestMethod.GET)
    public ResponseDto<?> getAllPosts(){
        return postService.getAllPost();
    } //게시글 전체 조회

    @RequestMapping(value = "/api/post/{postId}", method = RequestMethod.GET)
    public ResponseDto<?> getPost(@PathVariable Long postId, HttpServletRequest request){
        return postService.getPost(postId, request);
    } //postId의 게시글 상세 조회

    @RequestMapping(value = "/api/auth/post", method = RequestMethod.PUT, consumes = {"multipart/form-data"})
    public ResponseDto<?> updatePost(@ModelAttribute PostRequestDto postRequestDto, HttpServletRequest request) throws IOException{
        return postService.updatePost(postRequestDto, request);
    } //postId의 게시글 수정

    @RequestMapping(value = "/api/auth/post/{postId}", method = RequestMethod.DELETE)
    public ResponseDto<?> deletePost(@PathVariable Long postId, HttpServletRequest request) throws IOException{
        return postService.deletePost(postId, request);
    }

}
