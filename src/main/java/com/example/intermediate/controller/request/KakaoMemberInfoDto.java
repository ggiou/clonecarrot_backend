package com.example.intermediate.controller.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor // 필드에 쓴 모든 생성자만 만들어줌
public class KakaoMemberInfoDto {
    private Long id;
    private String nickname;
}
