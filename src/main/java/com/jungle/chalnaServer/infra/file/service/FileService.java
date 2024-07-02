package com.jungle.chalnaServer.infra.file.service;


import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoom;
import com.jungle.chalnaServer.domain.chatRoom.domain.entity.ChatRoomMember;
import com.jungle.chalnaServer.domain.chatRoom.exception.NotFoundChatRoomException;
import com.jungle.chalnaServer.domain.chatRoom.repository.ChatRoomRepository;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.infra.file.domain.dto.FileRequest;
import com.jungle.chalnaServer.infra.file.domain.dto.FileResponse;
import com.jungle.chalnaServer.infra.file.domain.entity.FileInfo;
import com.jungle.chalnaServer.infra.file.exception.MaxFileSizeException;
import com.jungle.chalnaServer.infra.file.exception.NotFoundFileInfoException;
import com.jungle.chalnaServer.infra.file.exception.NotFoundS3ObjectException;
import com.jungle.chalnaServer.infra.file.repository.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.net.URL;
import java.util.Date;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final AmazonS3 amazonS3;
    private final MemberRepository memberRepository;
    private final FileInfoRepository fileInfoRepository;
    private final ChatRoomRepository chatRoomRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private static final long MAX_UPLOAD_SIZE = 10_000_000L; // 10MB

    @Transactional
    public FileResponse.UPLOAD getUploadPreSignedUrl(final long id, FileRequest.UPLOAD fileDto, Long chatId) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        ChatRoom chatRoom = chatRoomRepository.findById(chatId)
                .orElseThrow(NotFoundChatRoomException::new);

        validateUpload(fileDto.fileSize());

        // 파일명 uuid로 변환 (s3 파일명 생성)
        String s3FileName = UUID.randomUUID().toString() + "_" + fileDto.fileName();

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, s3FileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration( createPreSignedUrlExpiration());
        generatePresignedUrlRequest.addRequestParameter("Content-Type", fileDto.contentType());

        URL presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        // s3 파일 url 저장 ( 쿼리 파라미터 제거)
        String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s/%s",bucketName,amazonS3.getRegionName(),s3FileName, chatId);

        // 파일 정보 저장
        FileInfo fileInfo = FileInfo.builder()
                .originalFileName(fileDto.fileName())
                .s3FileName(s3FileName)
                .fileUrl(fileUrl)
                .fileSize(fileDto.fileSize())
                .chatRoomId(chatId)
                .uploadedBy(member)
                .build();

        fileInfoRepository.save(fileInfo);

        return FileResponse.UPLOAD.of( fileInfo.getId(), presignedUrl.toString());

    }

    /* 파일 크기 제한 검사 로직 */
    private void validateUpload(long fileSize) {
        if (fileSize > MAX_UPLOAD_SIZE) {
            throw new MaxFileSizeException();
        }
    }

    /* 프리사인드 url 생성 로직 */

    /* 프리사인드 url 유효기간 생성 로직 */
    private Date createPreSignedUrlExpiration() {
        Date expiration = new Date();
        long expTimeMillis = expiration.getTime();
        expTimeMillis += 1000 * 60 * 60; // 1시간
        expiration.setTime(expTimeMillis);
        return expiration;
    }

    /* 파일 다운로드 로직 */
    @Transactional
    public FileResponse.DOWNLOAD getDownloadPreSignedUrl(final Long fileId) {

        FileInfo fileInfo = fileInfoRepository.findById(fileId)
                .orElseThrow(NotFoundFileInfoException::new);

        // S3 객체 존재 확인
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, fileInfo.getS3FileName());
        } catch (Exception e) {
            throw new NotFoundS3ObjectException();
        }

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, fileInfo.getS3FileName())
                .withMethod(HttpMethod.GET)
                .withExpiration(createPreSignedUrlExpiration());

        URL presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        return FileResponse.DOWNLOAD.of(presignedUrl.toString());
    }

    /* 파일 삭제 로직 */
    @Transactional
    public void deleteFile(final Long fileId) {
        FileInfo fileInfo = fileInfoRepository.findById(fileId)
                .orElseThrow(NotFoundFileInfoException::new);

        // 삭제를 원하는 객체의 경로
        String key = fileInfo.getFileUrl();


        // S3 객체 존재 확인 및 삭제
        try {
            S3Object s3Object = amazonS3.getObject(bucketName, fileInfo.getS3FileName());
            if (s3Object != null) {
                amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
            }
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() != 404) {
                throw e; // 다른 오류가 발생한 경우 예외
            }
        }

        // db 메타데이터 삭제 (삭제 여부 true)
//        fileInfo.softDelete();
//        fileInfoRepository.save(fileInfo);
        fileInfoRepository.delete(fileInfo);

    }

}
