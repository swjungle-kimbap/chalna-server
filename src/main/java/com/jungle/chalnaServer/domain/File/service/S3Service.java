package com.jungle.chalnaServer.domain.File.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.jungle.chalnaServer.domain.File.domain.dto.FileUploadResponse;
import com.jungle.chalnaServer.domain.File.domain.entity.File;
import com.jungle.chalnaServer.domain.File.domain.entity.UserQuota;
import com.jungle.chalnaServer.domain.File.exception.FailToUploadS3Exception;
import com.jungle.chalnaServer.domain.File.exception.MaxFileSizeException;
import com.jungle.chalnaServer.domain.File.exception.MaxUploadCountException;
import com.jungle.chalnaServer.domain.File.repository.FileRepository;
import com.jungle.chalnaServer.domain.File.repository.UserQuotaRepository;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chatRoom.exception.NotFoundChatRoomException;
import com.jungle.chalnaServer.domain.chatRoom.repository.ChatRoomRepository;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

import static com.google.common.io.Files.getFileExtension;

@Service
@RequiredArgsConstructor
@Slf4j
public class S3Service {

    private final AmazonS3 amazonS3;
    private final MemberRepository memberRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserQuotaRepository userQuotaRepository;
    private final FileRepository fileRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private static final long MAX_UPLOAD_SIZE = 2_500_000L; // 2.5MB
    private static final int MAX_MONTHLY_UPLOADS = 40;

    @Transactional
    public FileUploadResponse.URL uploadFile(final long id, MultipartFile files, Long chatRoomId) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        validateUpload(member,files.getSize());

        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(files.getSize());
        metadata.setContentType(files.getContentType());

        // 파일명 uuid로 변환
        String fileName = createFileName(files.getOriginalFilename());

        // 파일을 s3에 업로드
        try {
            amazonS3.putObject(new PutObjectRequest(bucketName, fileName, files.getInputStream(), metadata)
                    .withCannedAcl(CannedAccessControlList.PublicRead));
        } catch (IOException e) {
            throw new FailToUploadS3Exception();
        }

        String fileUrl = amazonS3.getUrl(bucketName, fileName).toString();

        // 파일 메타데이터를 데이터베이스에 저장
        saveFileMetadata(files, member, chatRoomId, fileName, fileUrl);

        return new FileUploadResponse.URL(fileUrl);

    }

    /* 파일 크기 제한 검사 로직 */
    private void validateUpload(Member member, long fileSize) {
        if (fileSize > MAX_UPLOAD_SIZE) {
            throw new MaxFileSizeException();
        }

        UserQuota quota = userQuotaRepository.findByMemberAndMonthYear(member, getCurrentMonthYear())
                .orElseGet(() -> createNewQuota(member));

        if (quota.getUsedUploadCount() >= MAX_MONTHLY_UPLOADS) {
            throw new MaxUploadCountException();
        }

        quota.incrementUploadCount();

        userQuotaRepository.save(quota);
    }

    private void saveFileMetadata(MultipartFile file, Member member, Long chatRoomId, String fileName, String fileUrl) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(NotFoundChatRoomException::new);

        File fileRecord = File.builder()
                .originalFileName(file.getOriginalFilename())
                .s3FileName(fileName)
                .fileUrl(fileUrl)
                .fileSize(file.getSize())
                .uploadedBy(member)
                .chatRoom(chatRoom)
                .build();

        fileRepository.save(fileRecord);
    }

    private String getCurrentMonthYear() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM"));
    }

    private UserQuota createNewQuota(Member member) {
        return userQuotaRepository.save(UserQuota.builder()
                .member(member)
                .monthYear(getCurrentMonthYear())
                .monthlyUploadCountLimit(MAX_MONTHLY_UPLOADS)
                .usedUploadCount(0)
                .build());
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(".").concat(getFileExtension(fileName));
    }
}
