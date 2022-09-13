package com.example.intermediate.controller;

import com.example.intermediate.controller.request.ProfileRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.MyPageService;
import io.jsonwebtoken.io.IOException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@RestController
public class MyPageController {

    private final MyPageService myPageService;

    @RequestMapping(value = "/api/saleslist", method = RequestMethod.GET)
    public ResponseDto<?> getSalesList(HttpServletRequest request){
        return myPageService.getSalesList(request);
    }

    @RequestMapping(value = "/api/lovelist", method = RequestMethod.GET)
    public ResponseDto<?> getLoveList(HttpServletRequest request){
        return myPageService.getLoveList(request);
    }

    @RequestMapping(value = "/api/auth/profile", method = RequestMethod.GET)
    public ResponseDto<?> getProfile(HttpServletRequest request){
        return myPageService.getProfile(request);
    }

    @RequestMapping(value = "/api/auth/profile", method = RequestMethod.PUT, consumes = {"multipart/form-data"})
    public ResponseDto<?> updateProfile(@ModelAttribute ProfileRequestDto profileRequestDto, HttpServletRequest request){
        return myPageService.updateProfile(profileRequestDto, request);
    }

}
