# 🗒 당근마켓 Clone Project

## [와이어프레임, API 설계](https://www.notion.so/acho/4-S-A-bb46343e3e5e4f3f944e419e3ebc2705)

### 개발 환경
    - Springboot 2.7.2
    - h2 DATABASE
    - Security / JWT
    - AWS s3
   
   
### Trouble shooting
1. oauth2 인증방식 (카카오 로그인)을 사용할 때 ‘’401 unauthorized: (no body)” 에러 발생
    
    → 클라이언트의 정보를 담는 body에 데이터가 없다고 나옴
    → resttemplate(REST API 호출이후 응답을 받을 때까지 기다리는 동기 방식) 에 ErrorHandler 코드를 추가하여 client_id 값이 잘못 입력된 것을 확인하고 수정

2. 서버 배포 후 카카오 로그인 인가코드가  프론트엔드에서 서버로 넘어오지 않는 문제
    → 카카오 어플리케이션 도메인을 배포한 서버 주소로 변경
    → 프론트엔드와 백엔드 API CLIENT KEY, REDIRECT URL 주소 통일
