package com.jungle.chalnaServer.infra.file.service;


import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.jungle.chalnaServer.infra.file.domain.dto.FileRequest;
import com.jungle.chalnaServer.infra.file.domain.dto.FileResponse;
import com.jungle.chalnaServer.infra.file.domain.entity.FileInfo;
import com.jungle.chalnaServer.infra.file.exception.FileInfoNotFoundException;
import com.jungle.chalnaServer.infra.file.exception.MaxFileSizeException;
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


    public static final String FILE_DIRECTORY = "file/";
    public static final String PROFILE_IMAGE_DIRECTORY = "profile/";

    private static final long MAX_UPLOAD_SIZE = 10_000_000L; // 10MB
    private static final long UPLOAD_LINK_EXPIRATION = 1000 * 60 * 15;
    private static final long DOWNLOAD_LINK_EXPIRATION = 1000 * 60 * 60 * 24;

    private final AmazonS3 amazonS3;
    private final FileInfoRepository fileInfoRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Transactional
    public FileResponse.UPLOAD uploadFile(final long memberId, FileRequest.UPLOAD fileDto, String directory) {
        validateFileSize(fileDto.fileSize());

        // 파일명 uuid로 변환 (s3 파일명 생성)
        String s3FileName = directory  + UUID.randomUUID();

        // File Info 생성
        FileInfo fileInfo = FileInfo.builder()
                .originalFileName(fileDto.fileName())
                .s3FileName(s3FileName)
                .fileSize(fileDto.fileSize())
                .contentType(fileDto.contentType())
                .uploadedBy(memberId)
                .build();

        // presigned url 생성
        URL presignedUrl = getUploadPresignedUrl(fileInfo);

        // 파일 정보 저장
        fileInfoRepository.save(fileInfo);

        return new FileResponse.UPLOAD(fileInfo.getId(), presignedUrl.toString());

    }


    /* 파일 다운로드 로직 */
    @Transactional
    public FileResponse.DOWNLOAD downloadFile(final Long fileId) {

        FileInfo fileInfo = fileInfoRepository.findById(fileId).orElseThrow(FileInfoNotFoundException::new);

        validateFileInfo(fileInfo);

        URL presignedUrl = getDownloadPresignedUrl(fileInfo);

        return new FileResponse.DOWNLOAD(presignedUrl.toString());
    }

    /* 파일 삭제 로직 */
    @Transactional
    public void deleteFile(final Long fileId) {
        FileInfo fileInfo = fileInfoRepository.findById(fileId).orElseThrow(FileInfoNotFoundException::new);

        // 삭제를 원하는 객체의 경로
        String key = generateFilePath(fileInfo);

        // S3 객체 존재 확인 및 삭제
        validateFileInfo(fileInfo);
        try {
            amazonS3.deleteObject(new DeleteObjectRequest(bucketName, key));
        } catch (AmazonS3Exception e) {
            if (e.getStatusCode() != 404) {
                throw e; // 다른 오류가 발생한 경우 예외
            }
        }
        fileInfoRepository.delete(fileInfo);
    }
    /* 파일 크기 제한 검사 로직 */
    private void validateFileSize(long fileSize) {
        if (fileSize > MAX_UPLOAD_SIZE) {
            throw new MaxFileSizeException();
        }
    }
    /* upload presigned url 생성 로직 */
    private URL getUploadPresignedUrl(FileInfo fileInfo){
        GeneratePresignedUrlRequest generatePresignedUrlRequest =
                new GeneratePresignedUrlRequest(
                        bucketName,
                        fileInfo.getS3FileName())
                        .withMethod(HttpMethod.PUT)
                        .withExpiration(createUploadPresignedUrlExpiration());
        generatePresignedUrlRequest.addRequestParameter("Content-Type", fileInfo.getContentType());

        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
    }

    /* download presigned url 생성 로직 */
    private URL getDownloadPresignedUrl(FileInfo fileInfo){
        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(
                bucketName,
                fileInfo.getS3FileName()
        ).withMethod(HttpMethod.GET).withExpiration(createDownloadPresignedUrlExpiration());

        return amazonS3.generatePresignedUrl(generatePresignedUrlRequest);
    }

    /* upload presigned url 유효기간 생성 로직 */
    private Date createUploadPresignedUrlExpiration() {
        return new Date(System.currentTimeMillis() + UPLOAD_LINK_EXPIRATION);
    }
    /* download presigned url 유효기간 생성 로직 */
    private Date createDownloadPresignedUrlExpiration() {
        return new Date(System.currentTimeMillis() + DOWNLOAD_LINK_EXPIRATION);
    }
    /* S3 object 검증 */
    private void validateFileInfo(FileInfo fileInfo) throws NotFoundS3ObjectException {
        if (!amazonS3.doesObjectExist(bucketName, fileInfo.getS3FileName())) throw new NotFoundS3ObjectException();
    }
    /* S3 파일 경로 생성 */
    private String generateFilePath(FileInfo fileInfo){
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, amazonS3.getRegionName(), fileInfo.getS3FileName());
    }


}
