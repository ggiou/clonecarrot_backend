package com.example.intermediate.controller;

import com.example.intermediate.controller.request.LikeRequestDto;
import com.example.intermediate.controller.request.StateRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.StateService;
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
public class StateController {

    private final StateService stateService;

    @RequestMapping(value = "/api/auth/state", method = RequestMethod.PATCH)
    public ResponseDto<?> state_post(@RequestBody StateRequestDto stateRequestDto, HttpServletRequest request){
        return stateService.state_post(stateRequestDto, request);
    } //판매 중 상태 변경

    @RequestMapping(value = "/api/auth/outstate", method = RequestMethod.PATCH)
    public ResponseDto<?> outstate_post(@RequestBody StateRequestDto stateRequestDto, HttpServletRequest request){
        return stateService.outstate_post(stateRequestDto, request);
    }//판매 완료 상태로 변경
}

