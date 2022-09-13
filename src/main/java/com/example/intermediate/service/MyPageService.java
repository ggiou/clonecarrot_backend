package com.example.intermediate.service;

import com.example.intermediate.controller.request.ProfileRequestDto;
import com.example.intermediate.controller.response.LoveListDto;
import com.example.intermediate.controller.response.ProfileDto;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.domain.ImageMapper;
import com.example.intermediate.domain.Like;
import com.example.intermediate.domain.Member;
import com.example.intermediate.controller.response.SalesListDto;
import com.example.intermediate.domain.Post;
import com.example.intermediate.jwt.TokenProvider;
import com.example.intermediate.repository.ImageMapperRepository;
import com.example.intermediate.repository.LikeRepository;
import com.example.intermediate.repository.MemberRepository;
import com.example.intermediate.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class MyPageService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;
    private final ImageMapperRepository imageMapperRepository;
    private final AmazonS3Service amazonS3Service;
    private final LikeRepository likeRepository;
    private final PostRepository postRepository;
    private final TokenProvider tokenProvider;

    public ResponseDto<?> getSalesList(HttpServletRequest request){
        Member member = validateMember(request);
        if(null == member){
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        List<Post> postList = postRepository.findByMember(member);
        List<SalesListDto> dtoList = new ArrayList<>();

        for(Post post : postList){
            dtoList.add(SalesListDto.builder()
                    .imgUrl(post.getPostImgUrl())
                    .title(post.getTitle())
                    .price(post.getPrice())
                    .state(post.getState())
                    .location(post.getLocation())
                    .likeCount(post.getLikeCount())
                    .build()
            );
        }
        return ResponseDto.success(dtoList);
    }

    public ResponseDto<?> getLoveList(HttpServletRequest request){
        Member member = validateMember(request);
        if(null == member){
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        List<Like> likeList = likeRepository.findByMember(member);
        List<Optional<Post>> loveList = new ArrayList<>();
        List<LoveListDto> dtoList= new ArrayList<>();

        for(int i=0; i<likeList.size();i++){
            loveList.add(postRepository.findById(likeList.get(i).getPostId()));
        }

        for(Optional<Post> post : loveList){
            dtoList.add(LoveListDto.builder()
                    .imgUrl(post.get().getPostImgUrl())
                    .title(post.get().getTitle())
                    .price(post.get().getPrice())
                    .state(post.get().getState())
                    .location(post.get().getLocation())
                    .likeCount(post.get().getLikeCount())
                    .build()
            );
        }
        return ResponseDto.success(dtoList);
    }

    public ResponseDto<?> getProfile(HttpServletRequest request){
        Member member = validateMember(request);
        if(null == member){
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        int like=0;
        List<Post> postList = postRepository.findByMember(member);
        for(Post post: postList){
            like += post.getLikeCount();
        }

        return ResponseDto.success(ProfileDto.builder()
                .nickname(member.getNickname())
                .location(member.getLocation())
                .profileImgUrl(member.getProfileImgUrl())
                .total_like(like)
                .build()
        );
    }

    public ResponseDto<?> updateProfile(ProfileRequestDto profileRequestDto, HttpServletRequest request){
        Member member = validateMember(request);
        if(null == member){
            return ResponseDto.fail("INVALID_TOKEN", "Token이 유효하지 않습니다.");
        }

        String profileImgUrl;
        MultipartFile multipartFile = profileRequestDto.getFile();

        if(member.getProfileImgUrl() == null){
            if(!multipartFile.isEmpty()){
                ResponseDto<?> responseDto = amazonS3Service.uploadFile(multipartFile);
                if(!responseDto.isSuccess())
                    return responseDto;
                ImageMapper imageMapper = (ImageMapper) responseDto.getData();
                profileImgUrl = imageMapper.getImageUrl();
            } else{
                profileImgUrl = null;
            }
        }else {
            Optional<ImageMapper> img = imageMapperRepository.findByImageUrl(member.getProfileImgUrl());
            ImageMapper image = img.get();

            if(amazonS3Service.removeFile(image.getName()))
                return ResponseDto.fail("DELETE ERROR","DELETE ERROR");
            if(!multipartFile.isEmpty()){
                ResponseDto<?> responseDto = amazonS3Service.uploadFile(multipartFile);
                if(!responseDto.isSuccess())
                    return responseDto;
                ImageMapper imageMapper = (ImageMapper) responseDto.getData();
                profileImgUrl = imageMapper.getImageUrl();
            } else {
                profileImgUrl = null;
            }
        }
        String newPassword = passwordEncoder.encode(profileRequestDto.getPassword());
        member.update(profileRequestDto, newPassword, profileImgUrl);
        memberRepository.save(member);

        return ResponseDto.success(member);

    }

    public Member validateMember(HttpServletRequest request){
        if(!tokenProvider.validateToken(request.getHeader("RefreshToken"))){
            return null;
        }
        return tokenProvider.getMemberFromAuthentication();
    }
}
