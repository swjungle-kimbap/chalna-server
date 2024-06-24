package com.jungle.chalnaServer.domain.member.service;

import com.jungle.chalnaServer.domain.member.domain.dto.MemberInfo;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberRequest;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.extern.slf4j.Slf4j;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
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

    public MemberInfo updateMemberInfo(Long id, MemberRequest memberDto, MultipartFile image) throws IOException {

        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        log.info("Updating member with ID: {}", id);
        log.info("Received memberDto: {}", memberDto);

        // 이미지가 업로드된 경우에만 처리
        if (image != null && !image.isEmpty()) {
            String originalImageName = image.getOriginalFilename();
            String saveFileName = createSaveFileName(originalImageName);

            // 파일을 저장할 경로 설정
            String filePath = uploadDir + File.separator + saveFileName;

            // 파일 저장
            saveFile(image, filePath);
            log.info("filePath ={}", filePath);

            // 파일 URL을 DB에 저장
            String fileUrl = "/uploads/" + saveFileName;
            member.updateProfileImageUrl(fileUrl);
        }

        // null 값이 아닌 경우에만 업데이트
        if (memberDto.getUsername() != null) {
            log.info("Updating username to: {}", memberDto.getUsername());
            member.updateUsername(memberDto.getUsername());
        }
        if (memberDto.getMessage() != null) {
            log.info("Updating message to: {}", memberDto.getMessage());
            member.updateMessage(memberDto.getMessage());
        }

        member = memberRepository.save(member);
        log.info("Updated member: {}", member);

        return MemberInfo.of(member);
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
    public MemberInfo getMemberInfo(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        log.info("사용자 정보 조회 성공");

        /* 추후 토큰을 검사해서 해당 user의 사용자의 정보만 조회하도록 수정 */

        return MemberInfo.of(member);

    }
}
