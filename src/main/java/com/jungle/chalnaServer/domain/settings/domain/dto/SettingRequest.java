package com.jungle.chalnaServer.domain.settings.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SettingRequest {

    private Boolean isAlarm;
    private Boolean isFriendAlarm;
    private Boolean isTagAlarm;
    private Boolean isChatAlarm;
    private Boolean alarmSound;
    private Boolean alarmVibration;
    private Boolean bluetooth;

    public record TAGLIST(List<String> interestTags) {}

    public record TAG(String interestTag) {}

}
