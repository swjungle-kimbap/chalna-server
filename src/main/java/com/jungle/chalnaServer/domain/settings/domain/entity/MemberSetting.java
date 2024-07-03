package com.jungle.chalnaServer.domain.settings.domain.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.jungle.chalnaServer.domain.settings.domain.dto.SettingRequest;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.global.common.entity.BaseTimestampEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSetting extends BaseTimestampEntity {

    @Id
    private Long id;

//    @OneToOne(fetch = FetchType.LAZY)
//    @MapsId
//    @JoinColumn(name = "id")
//    private Member member;

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
    @Builder.Default
    private List<String> interestKeyword = new ArrayList<>();

    @Column(nullable = false)
    private Boolean alarmSound = true;

    @Column(nullable = false)
    private Boolean alarmVibration = true;

    @Column(nullable = false)
    private Boolean isDisturb = false;

    @Column(nullable = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "hh:mm", timezone = "Asia/Seoul")
    private LocalTime doNotDisturbStart;

    @Column(nullable = true)
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "hh:mm", timezone = "Asia/Seoul")
    private LocalTime doNotDisturbEnd;


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

    public void updateDoNotDisturb(LocalTime start, LocalTime end) {
        this.doNotDisturbStart = start;
        this.doNotDisturbEnd = end;
    }

    public void addInterestKeyword(String tag) {
        if (this.interestKeyword != null && !this.interestKeyword.contains(tag)) {
            this.interestKeyword.add(tag);
        }
    }

    public void removeInterestKeyword(String tag) {
        if (this.interestKeyword != null) {
            this.interestKeyword.remove(tag);
        }
    }

    public void clearInterestKeyword() {
            this.interestKeyword.clear();
    }

}
