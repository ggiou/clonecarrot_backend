package com.example.intermediate.service;


import com.amazonaws.Response;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.example.intermediate.controller.response.ResponseDto;
import com.example.intermediate.repository.MemberRepository;
import com.example.intermediate.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.UUID;

@Component
@Service
@RequiredArgsConstructor
@Slf4j //오류 출력
public class AmazonS3Service {

    private final AmazonS3Client amazonS3Client;
    private final PostRepository postRepository;
    private final MemberRepository memberRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Transactional //DB 상태 변화를 위해 수행
    public ResponseDto<?> uploadFile(MultipartFile multipartFile){
        if(validateFileExists(multipartFile)) //유표 파일인지 = 빈 파일인지 확인
            return ResponseDto.fail("NO_EXIST-FILE", "등록된 이미지가 없습니다.");
        String fileName = createFileName(multipartFile.getOriginalFilename()); //난수 파일 이름 생성(난수 이름+ 파일 이름)
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentType((multipartFile.getContentType()));
        objectMetadata.setContentLength(multipartFile.getSize()); //ObjectMetadata에 파일 타입, byte크기 넣어주기. 넣지않으면 IDE상에서 설정하라는 권장로그

        try (InputStream inputStream = multipartFile.getInputStream()) {
            amazonS3Client.putObject(new PutObjectRequest(bucketName, fileName, inputStream, objectMetadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead)); //S3 업로드(외부에 공개하는 이미지이니, 해당 파일에 public read 권한 추가
        }catch (IOException e){
            return ResponseDto.fail("FILE_UPLOAD_FAIL", "파일 업로드를 실패했습니다.");
        }
        String imageURL = amazonS3Client.getUrl(bucketName, fileName).toString(); //PE에 전달하기 위한 업로드한 이미지 URL S3에서 가져오기
        return ResponseDto.success(imageURL);
    } //파일(이미지) 업로드 (post, profile 모두 어처피 url만 받아오니 올리는 건 같이 사용)

    @Transactional
    public boolean removePostImgFile(String fileName) throws UnsupportedEncodingException{
        if (postRepository.findByPostImgUrl(fileName).isEmpty())
                return true; //이미 사라진 이미지

        String urlfileName = URLDecoder.decode(fileName, "UTF-8"); //파일 이름으로 파일 가져오기
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, urlfileName); //삭제 request 생성
        amazonS3Client.deleteObject(request); //s3에서 파일 삭제
        return false;
    } //POST의 이미지 파일 삭제 (따로 따로 담아 관리해서 따로 생성)

    @Transactional
    public boolean removeProfileImgFile(String fileName) throws UnsupportedEncodingException{
        if (memberRepository.findByProfileImgUrl(fileName).isEmpty())
            return true; //이미 사라진 이미지

        String urlfileName = URLDecoder.decode(fileName, "UTF-8"); //파일 이름으로 파일 가져오기
        DeleteObjectRequest request = new DeleteObjectRequest(bucketName, urlfileName); //삭제 request 생성
        amazonS3Client.deleteObject(request); //s3에서 파일 삭제
        return false;
    } //profile의 이미지 파일 삭제

    private String createFileName(String originalFilename) {
        return UUID.randomUUID().toString().concat(originalFilename);
    } //네트워크 상에서 고유성이 보장되는 id를 만들어 줌, 즉 유니크한 파일 이름 생성

    private boolean validateFileExists(MultipartFile multipartFile) {
        return multipartFile.isEmpty();
    } //is Empty로 빈 파일인지 = 실제 있는 파일인지 확인

}
