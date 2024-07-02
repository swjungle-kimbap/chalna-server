package com.jungle.chalnaServer.domain.settings.domain.entity;

import com.jungle.chalnaServer.domain.settings.domain.dto.SettingRequest;
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
    private Boolean isDisturb = false;


    /* isAlarm이 false인 경우, true인 경우 */
    public void update(SettingRequest dto) {
        dto.getIsAlarm().ifPresent(value -> {
            this.isAlarm = value;
            if (!value) {
                this.isAlarm = false;
                this.isFriendAlarm = false;
                this.isChatAlarm = false;
                this.isTagAlarm = false;
                this.alarmSound = false;
                this.alarmVibration = false;
            }
        });
        dto.getIsFriendAlarm().ifPresent(value -> this.isFriendAlarm = value);
        dto.getIsChatAlarm().ifPresent(value -> this.isChatAlarm = value);
        dto.getIsTagAlarm().ifPresent(value -> this.isTagAlarm = value);
        dto.getAlarmSound().ifPresent(value -> this.alarmSound = value);
        dto.getAlarmVibration().ifPresent(value -> this.alarmVibration = value);
        dto.getIsDisturb().ifPresent(value -> this.isDisturb = value);
    }

    public void addInterestTag(String tag) {
        if (this.interestTags != null && !this.interestTags.contains(tag)) {
            this.interestTags.add(tag);
        }
    }

    public void removeInterestTag(String tag) {
        if (this.interestTags != null) {
            this.interestTags.remove(tag);
        }
    }

    public void clearInterestTags() {
        if (this.interestTags != null) {
            this.interestTags.clear();
        }
    }

}
