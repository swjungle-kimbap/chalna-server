package com.jungle.chalnaServer.domain.member.domain.dto;


import lombok.*;

import java.util.Optional;

@Getter
@ToString
@AllArgsConstructor
@Builder
public class MemberRequest {

    public record PROFILE(Optional<String> username,Optional<String> message, Optional<Long> profileImageId) {}
}
