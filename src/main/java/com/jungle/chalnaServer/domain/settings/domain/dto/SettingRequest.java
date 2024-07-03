package com.jungle.chalnaServer.domain.settings.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.jungle.chalnaServer.domain.settings.domain.entity.MemberSetting;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SettingRequest {

    private Optional<Boolean> isAlarm = Optional.empty();
    private Optional<Boolean>  isFriendAlarm = Optional.empty();
    private Optional<Boolean>  isTagAlarm = Optional.empty();
    private Optional<Boolean>  isChatAlarm = Optional.empty();
    private Optional<Boolean>  alarmSound = Optional.empty();
    private Optional<Boolean>  alarmVibration = Optional.empty();
    private Optional<Boolean>  isDisturb = Optional.empty();

    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "hh:mm", timezone = "Asia/Seoul")
    private LocalTime doNotDisturbStart;
    @JsonFormat(shape = JsonFormat.Shape.STRING,pattern = "hh:mm", timezone = "Asia/Seoul")
    private LocalTime doNotDisturbEnd;

    public record KEYWORDLIST(List<String> interestKeyword) {}

    public record KEYWORD(String interestKeyword) {}

    public record DONOTDISTURB(LocalTime doNotDisturbStart, LocalTime doNotDisturbEnd) {}

}
