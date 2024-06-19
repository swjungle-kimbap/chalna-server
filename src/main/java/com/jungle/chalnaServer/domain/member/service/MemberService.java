package com.jungle.chalnaServer.domain.member.service;

import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberRequest;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberResponse;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@Slf4j
public class MemberService {

    private final MemberRepository memberRepository;
    private final String uploadDir;

    public MemberService(MemberRepository memberRepository, @Value("${file.upload-dir}") String uploadDir) {
        this.memberRepository = memberRepository;
        this.uploadDir = uploadDir;
    }

    public MemberResponse updateMemberInfo(final Integer kakaoId, MemberRequest memberDto,  MultipartFile image) throws IOException {

        Member member = memberRepository.findByKakaoId(kakaoId)
                .orElseThrow(MemberNotFoundException::new);


        // 이미지가 업로드된 경우에만 처리
        if (image != null && !image.isEmpty()) {
            String originalImageName = image.getOriginalFilename();
            String saveFileName = createSaveFileName(originalImageName);

            // 파일을 저장할 경로 설정
            String filePath = uploadDir + File.separator + saveFileName;

            // 파일 저장
            saveFile(image, filePath);
            log.info("filePath ={}",filePath);

            // 파일 URL을 DB에 저장
            String fileUrl = "/uploads/" + saveFileName;
            member.updateProfileImageUrl(fileUrl);

        }

        if (memberDto.getUsername() != null) {
            log.info("username = {}",memberDto.getUsername());
            member.updateUsername(memberDto.getUsername());
        }

        if (memberDto.getMessage() != null) {
            log.info("message = {}", memberDto.getMessage());
            member.updateMessage(memberDto.getMessage());
        }

        member = memberRepository.save(member);
        log.info("Updated member: {}", member);

        return MemberResponse.of(member);

    }

    // 파일 저장 이름
    private String createSaveFileName(String originImageName) {
        String ext = extracExt(originImageName);
        String uuid = UUID.randomUUID().toString();
        return  uuid + "." +ext;
    }

    // 확장자면
    private String extracExt(String originImageName) {
        int pos = originImageName.lastIndexOf(".");
        return  originImageName.substring(pos + 1);
    }

    // 파일 저장 메서드
    private void saveFile(MultipartFile file, String filePath) throws IOException {
        byte[] bytes = file.getBytes();
        Path path = Paths.get(filePath);
        Files.createDirectories(path.getParent());
        Files.write(path, bytes);
    }
}
