package com.jungle.chalnaServer.infra.file.domain.dto;


public class FileResponse {
    public record UPLOAD(Long fileId, String presignedUrl) {
    }

    public record DOWNLOAD(String presignedUrl) {
    }
}
