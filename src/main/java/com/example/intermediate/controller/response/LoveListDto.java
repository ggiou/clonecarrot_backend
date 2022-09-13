package com.example.intermediate.controller.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoveListDto {
    private String imgUrl;
    private String title;
    private int price;
    private String state;
    private String location;
    private int likeCount;
}
