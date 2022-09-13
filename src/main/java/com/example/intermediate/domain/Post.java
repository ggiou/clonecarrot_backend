package com.example.intermediate.domain;


import com.example.intermediate.controller.request.PostRequestDto;
import com.example.intermediate.controller.request.StateRequestDto;
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
    private Long id;

    @Column(nullable = false)
    private String title;    //게시글 제목

    @Column(nullable = false)
    private String state;    //판매 상태

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

    @Column(nullable = true)
    private String postImgUrl; //게시글 이미지(추후 변경 가능)

    @JoinColumn(name = "member_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Member member;   // 회원(작성자)


    public void update(PostRequestDto postRequestDto, String postImageUrl) {
        this.title = postRequestDto.getTitle();
        this.postImgUrl = postImageUrl;
        this.tag = postRequestDto.getTag();
        this.price = postRequestDto.getPrice();
        this.content = postRequestDto.getContent();
        this.location = postRequestDto.getLocation();
    }

    public boolean validateMember(Member member) {
        return !this.member.equals(member);
    }
    public  void like(){
        this.likeCount +=1;
    } //총 좋아요 수 +
    public  void dislike(){
        this.likeCount -=1;
    } //총 좋아요 수 -

    public void state(){
        this.state = "판매중";
    } //판매중으로 변경
    public  void outstate(){
        this.state = "판매완료";

    } //판매완료로 변경
    public  void view(){
        this.viewCount +=1;
    } //조회수


}
