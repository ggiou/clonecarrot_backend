package com.example.intermediate.domain;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Post extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long postId;

    @Column(nullable = false)
    private String title;    //게시글 제목

    @Column(nullable = false)
    private String tag;      //상품 카테고리

    @Column(nullable = false)
    private int price;       //상품 가격

    @Column(nullable = false)
    private String content;  //게시글 내용

    @Column(nullable = false)
    private String location; //작성자 위치(작성자가 직접 입력 하거나, 지도 api로 위치 받아오기)

    @Column(nullable = false)
    private int likeCount;   // 좋아요 수(관심 상품으로 등록 된 수)

    @Column(nullable = false)
    private int viewCount;   // 조회 수 (상세 페이지 get 요청 시 ++)

//    @Column(nullable = false)
//    private String postImgUrl; //게시글 이미지(추후 변경 가능)

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;   // 회원(작성자)





}
