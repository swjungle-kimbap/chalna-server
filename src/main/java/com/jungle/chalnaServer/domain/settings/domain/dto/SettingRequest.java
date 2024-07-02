package com.jungle.chalnaServer.domain.settings.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jungle.chalnaServer.domain.settings.domain.entity.MemberSetting;
import lombok.*;

import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SettingRequest {

    private Optional<Boolean> isAlarm;
    private Optional<Boolean>  isFriendAlarm;
    private Optional<Boolean>  isTagAlarm;
    private Optional<Boolean>  isChatAlarm;
    private Optional<Boolean>  alarmSound;
    private Optional<Boolean>  alarmVibration;
    private Optional<Boolean>  isDisturb;

    public record TAGLIST(List<String> interestTags) {}

    public record TAG(String interestTag) {}

}
