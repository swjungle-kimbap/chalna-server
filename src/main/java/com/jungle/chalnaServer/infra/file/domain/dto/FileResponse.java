package com.jungle.chalnaServer.infra.file.domain.dto;


import com.jungle.chalnaServer.infra.file.domain.entity.FileInfo;

public class FileResponse {
    public record INFO(Long id,Long uploadBy,String originalFileName,Long fileSize) {
        public static INFO of(FileInfo fileInfo){
            return  new INFO(fileInfo.getId(),fileInfo.getUploadedBy().getId(),fileInfo.getOriginalFileName(),fileInfo.getFileSize());
        }
    }

    public record UPLOAD(Long fileId, String presignedUrl) {
        public static UPLOAD of(Long fileId, String presignedUrl) {
            return new UPLOAD(fileId,presignedUrl);
        }
    }
}
