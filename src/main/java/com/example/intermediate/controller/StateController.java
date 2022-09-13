package com.example.intermediate.controller;

import com.example.intermediate.controller.request.StateRequestDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.service.StateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RequiredArgsConstructor
@Component
@RestController
public class StateController {

    private final StateService stateService;

    @RequestMapping(value = "/api/auth/state/{postId}", method = RequestMethod.PATCH)
    public ResponseDto<?> state_post(@PathVariable Long postId, HttpServletRequest request){
        return stateService.state_post(postId, request);
    } //판매 중 상태 변경

    @RequestMapping(value = "/api/auth/outstate/{postId}", method = RequestMethod.PATCH)
    public ResponseDto<?> outstate_post(@PathVariable Long postId, HttpServletRequest request){
        return stateService.outstate_post(postId, request);
    }//판매 완료 상태로 변경
}
