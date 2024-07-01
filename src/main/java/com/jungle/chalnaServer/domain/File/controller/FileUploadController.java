package com.jungle.chalnaServer.domain.File.controller;

import com.jungle.chalnaServer.domain.File.domain.dto.FileUploadResponse;
import com.jungle.chalnaServer.domain.File.service.S3Service;
import com.jungle.chalnaServer.global.auth.jwt.annotation.AuthUserId;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/files")
public class FileUploadController {

    private final S3Service s3Service;

    @PostMapping("/upload")
    public CommonResponse<FileUploadResponse.URL> uploadFile(@AuthUserId final Long id, @RequestParam("file") MultipartFile file, @RequestParam("chatRoomId") Long chatRoomId){
        return CommonResponse.ok(s3Service.uploadFile(id,file,chatRoomId));
    }
}
