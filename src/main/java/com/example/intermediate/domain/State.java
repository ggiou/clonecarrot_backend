package com.example.intermediate.domain;

import javax.persistence.*;

public class State {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stateId;

    @JoinColumn(name = "post_id", nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Post post;      //어떤 게시글(상품)의 판매 상태 (내 판매 상태 활용) -> 어처피 POST에 누가 작성잔지 알 수 있음
}
