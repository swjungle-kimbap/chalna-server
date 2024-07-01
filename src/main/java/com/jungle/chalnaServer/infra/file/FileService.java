package com.jungle.chalnaServer.infra.file;


import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.infra.file.domain.dto.FileResponse;
import com.jungle.chalnaServer.infra.file.domain.entity.FileInfo;
import com.jungle.chalnaServer.infra.file.exception.FailToUploadS3Exception;
import com.jungle.chalnaServer.infra.file.exception.MaxFileSizeException;
import com.jungle.chalnaServer.infra.file.repository.FileInfoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    public FileResponse.INFO uploadFile(final long id, MultipartFile files) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        validateUpload(files.getSize());

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

        return FileResponse.INFO.of(saveFileMetadata(files, member, fileName, fileUrl));

    }

    /* 파일 크기 제한 검사 로직 */
    private void validateUpload(long fileSize) {
        if (fileSize > MAX_UPLOAD_SIZE) {
            throw new MaxFileSizeException();
        }
    }

    private FileInfo saveFileMetadata(MultipartFile file, Member member, String fileName, String fileUrl) {

        FileInfo fileInfo = FileInfo.builder()
                .originalFileName(file.getOriginalFilename())
                .s3FileName(fileName)
                .fileUrl(fileUrl)
                .fileSize(file.getSize())
                .uploadedBy(member)
                .build();

        return fileInfoRepository.save(fileInfo);
    }

    private String createFileName(String fileName) {
        return UUID.randomUUID().toString().concat(".").concat(getFileExtension(fileName));
    }
}
