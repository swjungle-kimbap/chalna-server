package com.jungle.chalnaServer.infra.file.domain.dto;


import com.jungle.chalnaServer.infra.file.domain.entity.FileInfo;

public class FileResponse {
    public record UPLOAD(Long fileId, String presignedUrl) {
        public static UPLOAD of(Long fileId, String presignedUrl) {
            return new UPLOAD(fileId,presignedUrl);
        }
    }

    public record DOWNLOAD(String presignedUrl) {
        public static DOWNLOAD of(String presignedUrl) {
            return new DOWNLOAD(presignedUrl);
        }
    }
}
