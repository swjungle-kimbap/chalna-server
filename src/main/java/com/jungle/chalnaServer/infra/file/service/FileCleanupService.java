package com.jungle.chalnaServer.infra.file.service;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.jungle.chalnaServer.infra.file.domain.entity.FileInfo;
import com.jungle.chalnaServer.infra.file.repository.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FileCleanupService {
    private final AmazonS3 amazonS3;
    private final FileInfoRepository fileInfoRepository;

    @Value("${cloud.aws.s3.bucket}")
    private String bucketName;

    @Scheduled(cron = "0 0 0 * * ?") // 매일 자정에 실행
    public void cleanupDeletedFiles() {
        List<FileInfo> files = fileInfoRepository.findAll();
        for (FileInfo file : files) {
            try {
                amazonS3.getObjectMetadata(bucketName, file.getS3FileName());
            } catch (AmazonS3Exception e) {
                if (e.getStatusCode() == 404) {
                    fileInfoRepository.delete(file);
                }
            }
        }
    }
}
