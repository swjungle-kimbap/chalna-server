package com.jungle.chalnaServer.domain.settings.domain.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.jungle.chalnaServer.domain.settings.domain.entity.MemberSetting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SettingResponse {

    private Boolean isAlarm;
    private Boolean isTagAlarm;
    private List<String> interestTag;
    private Boolean isFriendAlarm;
    private Boolean isChatAlarm;
    private Boolean alarmSound;
    private Boolean alarmVibration;
    private Boolean isDisturb;

    public static SettingResponse of(MemberSetting memberSetting, boolean includeTags) {
        return SettingResponse.builder()
                .isAlarm(memberSetting.getIsAlarm())
                .isFriendAlarm(memberSetting.getIsFriendAlarm())
                .isChatAlarm(memberSetting.getIsChatAlarm())
                .isTagAlarm(memberSetting.getIsTagAlarm())
                .interestTag(memberSetting.getInterestTags())
                .interestTag(includeTags ? memberSetting.getInterestTags() : null)
                .alarmSound(memberSetting.getAlarmSound())
                .alarmVibration(memberSetting.getAlarmVibration())
                .isDisturb(memberSetting.getIsDisturb())
                .build();
    }

    public static TAGLIST toTagList(MemberSetting memberSetting) {
        return new TAGLIST(memberSetting.getInterestTags());
    }

    public record TAGLIST(List<String> interestTag) {}
}
