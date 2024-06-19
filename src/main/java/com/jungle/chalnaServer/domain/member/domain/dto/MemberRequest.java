package com.jungle.chalnaServer.domain.member.domain.dto;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

@Getter
@AllArgsConstructor
@Builder
public class MemberRequest {

    private String username;
    private String message;
    private MultipartFile image;

}
