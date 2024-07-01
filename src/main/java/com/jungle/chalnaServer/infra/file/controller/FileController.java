package com.jungle.chalnaServer.infra.file.controller;

import com.jungle.chalnaServer.infra.file.FileService;
import com.jungle.chalnaServer.global.auth.jwt.annotation.AuthUserId;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import com.jungle.chalnaServer.infra.file.domain.dto.FileResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/file")
public class FileController {

    private final FileService fileService;

    @PostMapping("/upload")
    public CommonResponse<FileResponse.INFO> uploadFile(@AuthUserId final Long id,
                                                       @RequestParam("file") MultipartFile file
    ) {
        return CommonResponse.ok(fileService.uploadFile(id, file));
    }
}
