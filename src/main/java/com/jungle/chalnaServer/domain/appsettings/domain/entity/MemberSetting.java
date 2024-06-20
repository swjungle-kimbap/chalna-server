package com.jungle.chalnaServer.domain.appsettings.domain.entity;

import com.jungle.chalnaServer.domain.appsettings.domain.dto.SettingRequest;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSetting extends BaseTimestampEntity {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private Member member;

    @Column(nullable = false)
    private Boolean isAlarm = true;

    @Column(nullable = false)
    private Boolean isFriendAlarm = false;

    @Column(nullable = false)
    private Boolean isChatAlarm = true;

    @Column(nullable = false)
    private Boolean isTagAlarm = false;

    @ElementCollection
    @Column(nullable = false)
    private List<String> interestTags;

    @Column(nullable = false)
    private Boolean alarmSound = true;

    @Column(nullable = false)
    private Boolean alarmVibration = true;

    @Column(nullable = false)
    private Boolean bluetooth = true;


    /* isAlarm이 false인 경우, true인 경우 */
    public void update(SettingRequest dto) {
        if (!dto.getIsAlarm()) {
            this.isAlarm = false;
            this.isFriendAlarm = false;
            this.isChatAlarm = false;
            this.isTagAlarm = false;
            this.alarmSound = false;
            this.alarmVibration = false;
            this.bluetooth = dto.getBluetooth();
        } else {
            this.isAlarm = dto.getIsAlarm();
            this.isFriendAlarm = dto.getIsFriendAlarm();
            this.isChatAlarm = dto.getIsChatAlarm();
            this.isTagAlarm = dto.getIsTagAlarm();
            this.alarmSound = dto.getAlarmSound();
            this.alarmVibration = dto.getAlarmVibration();
            this.bluetooth = dto.getBluetooth();
        }
    }


}
