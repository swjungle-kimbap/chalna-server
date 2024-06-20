package com.jungle.chalnaServer.domain.appsettings.domain.dto;

import com.jungle.chalnaServer.domain.appsettings.domain.entity.MemberSetting;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SettingResponse {

    private Boolean isAlarm;
    private Boolean isTagAlarm;
    private Boolean isFriendAlarm;
    private Boolean isChatAlarm;
    private Boolean alarmSound;
    private Boolean alarmVibration;
    private Boolean bluetooth;

    public static SettingResponse of(MemberSetting memberSetting) {
        return SettingResponse.builder()
                .isAlarm(memberSetting.getIsAlarm())
                .isFriendAlarm(memberSetting.getIsFriendAlarm())
                .isChatAlarm(memberSetting.getIsChatAlarm())
                .isTagAlarm(memberSetting.getIsTagAlarm())
                .alarmSound(memberSetting.getAlarmSound())
                .alarmVibration(memberSetting.getAlarmVibration())
                .bluetooth(memberSetting.getBluetooth())
                .build();
    }
}
