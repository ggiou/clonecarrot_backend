package com.example.intermediate.service;

import com.example.intermediate.controller.request.KakaoMemberInfoDto;
import com.example.intermediate.controller.response.KakaoOauthTokenDto;
import com.example.intermediate.domain.Member;
import com.example.intermediate.domain.UserDetailsImpl;
import com.example.intermediate.repository.MemberRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class KakaoMemberService {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public KakaoOauthTokenDto kakaoLogin(String code) throws JsonProcessingException {
        // 1. "인가 코드"로 전체 response 요청
        KakaoOauthTokenDto accessToken = getAccessToken(code);

        // 2. response에 access token으로 카카오 api 호출
        KakaoMemberInfoDto kakaoMemberInfo = getkakaoMemberInfo(accessToken.getAccess_token());

        // 3. 필요시에 회원가입
        Member kakaoUser = registerKakaoUserIfNeeded(kakaoMemberInfo);

        // 4. 강제 로그인 처리
        forceLogin(kakaoUser);

        return accessToken;
    }

    private KakaoOauthTokenDto getAccessToken(String code) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP Body 생성
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "authorization_code");
        body.add("client_id", "fdb42734830cbb186c8221bf3acdd6c6");
        body.add("client_secret", "FuvfQecT3uPmfM3wlzF5VxRJU7Iz654F");
        body.add("redirect_uri", "http://localhost:8080/api/member/kakao/callback");
        body.add("code", code);

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest = new HttpEntity<>(body, headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class
        );

        // HTTP 응답 (JSON) -> 액세스 토큰 파싱
        ObjectMapper objectMapper = new ObjectMapper();
        KakaoOauthTokenDto oauthTokenDto = objectMapper.readValue(response.getBody(), KakaoOauthTokenDto.class);
        return oauthTokenDto;
    }

    private KakaoMemberInfoDto getkakaoMemberInfo(String accessToken) throws JsonProcessingException {
        // HTTP Header 생성
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + accessToken);
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        // HTTP 요청 보내기
        HttpEntity<MultiValueMap<String, String>> kakaoMemberInfoRequest = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(
                "https://kapi.kakao.com/v2/user/me",
                HttpMethod.POST,
                kakaoMemberInfoRequest,
                String.class
        );

        String responseBody = response.getBody();
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode = objectMapper.readTree(responseBody);
        Long id = jsonNode.get("id").asLong();
        String nickname = jsonNode.get("properties").get("nickname").asText();
        System.out.println("카카오 사용자 정보: " + id + ", " + nickname);
        return new KakaoMemberInfoDto(id, nickname);
    }

    private Member registerKakaoUserIfNeeded(KakaoMemberInfoDto kakaoMemberInfo) {
        // DB 에 중복된 Kakao Id 가 있는지 확인
        Long kakaoId = kakaoMemberInfo.getId();
        Member kakaoUser = memberRepository.findByKakaoId(kakaoId)
                .orElse(null);
        if (kakaoUser == null) {
            // 회원가입
            // nickname: kakao nickname
            String nickname = kakaoMemberInfo.getNickname();

            // password: random UUID
            String password = UUID.randomUUID().toString();
            String encodedPassword = passwordEncoder.encode(password);

            kakaoUser = Member.builder()
                    .nickname(nickname)
                    .password(encodedPassword)
                    .kakaoId(kakaoId)
                    .build();
            memberRepository.save(kakaoUser);
        }
        return kakaoUser;
    }

    private void forceLogin(Member kakaoUser) {
        UserDetails userDetails = new UserDetailsImpl(kakaoUser);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
// https://kauth.kakao.com/oauth/authorize?client_id=fdb42734830cbb186c8221bf3acdd6c6&redirect_uri=http://localhost:8080/api/member/kakao/callback&response_type=code
