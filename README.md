# 📋 당근마켓 Clone Project (backend)

### 🔎 개발 환경
    - Springboot 2.7.2
    - h2 DATABASE
    - Security / JWT
    - AWS s3
    
###  🔗 구현 기능
#### 기능1
        * 회원가입
        * 로그인
        * Oauth2 카카오 로그인
#### 기능2
        * 새 매물 올리기(매물 사진 포함)
        * 전체매물 최신순으로 조회하기
        * 매물 상세조회
        * 매물 수정, 삭제 기능
        * 매물 판매중<->판매완료 변경 기능 (게시글 작성자만 가능)
        * 관심매물(좋아요) 등록, 취소 기능 (게시글 작성자는 불가능, 총 좋아요 수 제공 -> 매너 온도)
        * 조회수 확인(본인이 작성한 게시글은 조회수 + 되지 않음)
#### 기능3
        * 마이페이지 조회
        * 내가 올린 매물들 전체 좋아요 개수(매너 온도 대신) 
        * 마이페이지 상세조회
        * 프로필 사진 업로드 및 내 정보 수정
        * 내 판매내역 조회 및 판매상태 확인
        * 내 관심목록 조회 및 판매상태 확인
        * 관심목록 제거
        
-------
### 💡와이어프레임
![Artboard 13@2x-100](https://user-images.githubusercontent.com/44489399/190330311-336e3e81-41f4-4855-acf9-f26c03d8ae4c.jpeg)
--------

### 💡API 명세서
![스크린샷 2022-09-15 오후 3 23 11](https://user-images.githubusercontent.com/44489399/190330882-e5dbc1ad-a0fc-4e7f-86d1-d4e7b188d754.jpeg)
![스크린샷 2022-09-15 오후 3 24 38](https://user-images.githubusercontent.com/44489399/190330892-96e0dfd8-81dd-4f3a-bab1-4008046605d6.jpeg)


### 💡트러블슈팅
1. oauth2 인증방식 (카카오 로그인)을 사용할 때 ‘’401 unauthorized: (no body)” 에러 발생
    
    → 클라이언트의 정보를 담는 body에 데이터가 없다고 나옴  
    → resttemplate(REST API 호출이후 응답을 받을 때까지 기다리는 동기 방식) 에 ErrorHandler 코드를 추가하여 client_id 값이 잘못 입력된 것을 확인하고 수정

2. 서버 배포 후 카카오 로그인 인가코드가  프론트엔드에서 서버로 넘어오지 않는 문제  
    → 카카오 어플리케이션 도메인을 배포한 서버 주소로 변경  
    → 프론트엔드와 백엔드 API CLIENT KEY, REDIRECT URL 주소 통일. 
    
3. @Transactional, (readonly) 설정.  
   → 변경 사항이 잘 넘어가 수정되 response가 오지만 실질적인 db에서는 저장이 안됨(state, like, 조회 수..).  
   → 알고보니 transactional 설정을 안해 repository에 저장을 해줘야 했으며, readonly라 수정사항이 변경이 안됨.  
   → 코드에 넣어주고 해결 완료
   
4. null pointer
   → 예외처리 검사시 null 데이터에 대해 받을 수 없다 떠 오류가 생김      
   → null 일경우와 아닐 경우로 나누어 예외처리를 해 해결.  
   
### 💡관련 링크
1. 시연 영상
    → https://www.youtube.com/watch?v=tLftqn6eL78
2. 노션 링크
    → https://brindle-weight-a9d.notion.site/Clone-Carrot-Market-S-A-e35d146c17104094bdaef14bf4859f81?pvs=4


