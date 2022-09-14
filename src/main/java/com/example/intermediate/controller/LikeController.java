package com.example.intermediate.controller;

import com.example.intermediate.controller.request.LikeRequestDto;
import com.example.intermediate.controller.response.ResponseDto;

import com.example.intermediate.service.LikeSerivce;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Component
@RestController
public class LikeController {
    private final LikeSerivce likeSerivce;

    @RequestMapping(value = "/api/auth/like", method = RequestMethod.PATCH)
    public ResponseDto<?> like_post(@RequestBody LikeRequestDto likeRequestDto, HttpServletRequest request){
        return likeSerivce.post_like(likeRequestDto, request);
    } //좋아요 하기(관심 상품)

    @RequestMapping(value = "/api/auth/dislike", method = RequestMethod.PATCH)
    public ResponseDto<?> dislike_post(@RequestBody LikeRequestDto likeRequestDto, HttpServletRequest request){
        return likeSerivce.post_dislike(likeRequestDto, request);
    } //좋아요 취소 하기(관심 상품 해제)
}
