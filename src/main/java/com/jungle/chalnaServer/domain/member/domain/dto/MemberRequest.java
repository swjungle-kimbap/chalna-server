package com.jungle.chalnaServer.domain.member.domain.dto;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Optional;

@Getter
@ToString
@AllArgsConstructor
@Builder
public class MemberRequest {

    public record PROFILE(Optional<String> username,Optional<String> message ) {}


    public record UPLOAD(String fileName, String contentType , Long fileSize) {
    }
}
