package com.jungle.chalnaServer.domain.member.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberResponse;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberRequest;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.infra.file.domain.dto.FileRequest;
import com.jungle.chalnaServer.infra.file.domain.entity.FileInfo;
import com.jungle.chalnaServer.infra.file.repository.FileInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;

import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class MemberService {

    private final AmazonS3 amazonS3;
    private final MemberRepository memberRepository;
    private final FileInfoRepository fileInfoRepository;
    private final String uploadDir;

    public MemberService(AmazonS3 amazonS3, MemberRepository memberRepository,FileInfoRepository fileInfoRepository,@Value("${file.upload-dir}") String uploadDir) {
        this.amazonS3 = amazonS3;
        this.memberRepository = memberRepository;
        this.uploadDir = uploadDir;
        this.fileInfoRepository = fileInfoRepository;
    }

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Transactional
    public MemberResponse updateMemberProfile(final Long id, MemberRequest.PROFILE memberDto){

        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        // null 값이 아닌 경우에만 업데이트

        Member finalMember = member;
        memberDto.username().ifPresent(username -> {
            log.info("Updating username to: {}", username);
            finalMember.updateUsername(username);
        });

        memberDto.message().ifPresent(message-> {
            log.info("Updating username to: {}", message);
            finalMember.updateMessage(message);
        });

        member = memberRepository.save(member);
        log.info("Updated member: {}", member);

        return MemberResponse.of(member);
    }


    @Transactional
    public MemberResponse.PROFILE_IMAGE_UPLOAD updateMemberInfo(final Long id, FileRequest.UPLOAD dto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        // 추후 파일 사이즈 검사 로직 추가
        String s3FileName = null;
        URL presignedUrl = null;

        // 파일명 uuid로 변환 (s3 파일명 생성)
        s3FileName =  "profile/" + UUID.randomUUID().toString() + "_" + dto.fileName();

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, s3FileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration(createPreSignedUrlExpiration());
        generatePresignedUrlRequest.addRequestParameter("Content-Type", dto.contentType());

        presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        // s3 파일 url 저장 (쿼리 파라미터 제거)
        String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, amazonS3.getRegionName(), s3FileName);

        // 파일 정보 저장
        FileInfo fileInfo = FileInfo.builder()
                .originalFileName(dto.fileName())
                .s3FileName(s3FileName)
                .fileUrl(fileUrl)
                .fileSize(dto.fileSize())
                .uploadedBy(member)
                .build();

        fileInfoRepository.save(fileInfo);

        return MemberResponse.PROFILE_IMAGE_UPLOAD.of( fileInfoRepository.findById(id).orElseThrow().getId(),  presignedUrl.toString() );
    }

    private Date createPreSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60; // 1시간
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    // 파일 저장 이름 생성
    private String createSaveFileName(String originImageName) {
        String ext = extractExt(originImageName);
        String uuid = UUID.randomUUID().toString();
        return uuid + "." + ext;
    }

    // 확장자 추출
    private String extractExt(String originImageName) {
        int pos = originImageName.lastIndexOf(".");
        return originImageName.substring(pos + 1);
    }

    // 파일 저장 메서드
    private void saveFile(MultipartFile file, String filePath) throws IOException {
        byte[] bytes = file.getBytes();
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.write(path, bytes);
    }


    /* 사용자 정보를 조회하는 메서드 */
    public MemberResponse getMemberInfo(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        log.info("사용자 정보 조회 성공");

        /* 추후 토큰을 검사해서 해당 user의 사용자의 정보만 조회하도록 수정 */

        return MemberResponse.of(member);

    }
}
