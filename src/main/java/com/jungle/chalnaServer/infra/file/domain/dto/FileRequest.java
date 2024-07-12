package com.jungle.chalnaServer.infra.file.domain.dto;

public class FileRequest {

    public record UPLOAD(String fileName, String contentType, Long fileSize, String fileType) {

    }
}
