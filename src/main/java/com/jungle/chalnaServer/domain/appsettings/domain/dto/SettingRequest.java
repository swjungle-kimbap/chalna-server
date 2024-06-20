package com.jungle.chalnaServer.domain.appsettings.domain.dto;

import lombok.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SettingRequest {

    private Boolean isAlarm;
    private Boolean isFriendAlarm;
    private Boolean isTagAlarm;
    private Boolean isChatAlarm;
    private Boolean alarmSound;
    private Boolean alarmVibration;
    private Boolean bluetooth;

}
