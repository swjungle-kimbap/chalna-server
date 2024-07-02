package com.jungle.chalnaServer.infra.file.controller;

import com.jungle.chalnaServer.infra.file.FileService;
import com.jungle.chalnaServer.global.auth.jwt.annotation.AuthUserId;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import com.jungle.chalnaServer.infra.file.domain.dto.FileRequest;
import com.jungle.chalnaServer.infra.file.domain.dto.FileResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/file")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public CommonResponse<FileResponse.UPLOAD> getUploadPreSignedUrl(@AuthUserId final Long id,
                                                        @RequestBody FileRequest.UPLOAD fileDto
                                                        ) {
        return CommonResponse.ok(fileService.getUploadPreSignedUrl(id, fileDto));
    }

    @GetMapping("/download/{fileId}")
    public CommonResponse<FileResponse.DOWNLOAD> getDownloadPreSignedUrl(@PathVariable(value = "fileId") final Long fileId) {
        return CommonResponse.ok(fileService.getDownloadPreSignedUrl(fileId));
    }
}
