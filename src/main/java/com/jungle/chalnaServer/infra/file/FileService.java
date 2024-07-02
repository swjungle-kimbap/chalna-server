package com.jungle.chalnaServer.infra.file;


import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.infra.file.domain.dto.FileRequest;
import com.jungle.chalnaServer.infra.file.domain.dto.FileResponse;
import com.jungle.chalnaServer.infra.file.domain.entity.FileInfo;
import com.jungle.chalnaServer.infra.file.exception.FailToUploadS3Exception;
import com.jungle.chalnaServer.infra.file.exception.MaxFileSizeException;
import com.jungle.chalnaServer.infra.file.exception.NotFoundFileInfoException;
import com.jungle.chalnaServer.infra.file.repository.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.UUID;

import static com.google.common.io.Files.getFileExtension;

@Service
@RequiredArgsConstructor
@Slf4j
public class FileService {

    private final AmazonS3 amazonS3;
    private final MemberRepository memberRepository;
    private final FileInfoRepository fileInfoRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    private static final long MAX_UPLOAD_SIZE = 10_000_000L; // 10MB

    @Transactional
    public FileResponse.UPLOAD getUploadPreSignedUrl(final long id, FileRequest.UPLOAD fileDto) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        validateUpload(fileDto.fileSize());

        // 파일명 uuid로 변환 (s3 파일명 생성)
        String s3FileName = UUID.randomUUID().toString() + "_" + fileDto.fileName();

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, s3FileName)
                .withMethod(HttpMethod.PUT)
                .withExpiration( createPreSignedUrlExpiration());
        generatePresignedUrlRequest.addRequestParameter("Content-Type", fileDto.contentType());

        URL presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);

        // s3 파일 url 저장 ( 쿼리 파라미터 제거)
        String fileUrl = String.format("https://%s.s3.%s.amazonaws.com/%s",bucketName,amazonS3.getRegionName(),s3FileName);

        // 파일 정보 저장
        FileInfo fileInfo = FileInfo.builder()
                .originalFileName(fileDto.fileName())
                .s3FileName(s3FileName)
                .fileUrl(fileUrl)
                .fileSize(fileDto.fileSize())
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

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, fileInfo.getS3FileName())
                .withMethod(HttpMethod.GET)
                .withExpiration(createPreSignedUrlExpiration());

        URL presignedUrl = amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
        return FileResponse.DOWNLOAD.of(presignedUrl.toString());
    }

}
