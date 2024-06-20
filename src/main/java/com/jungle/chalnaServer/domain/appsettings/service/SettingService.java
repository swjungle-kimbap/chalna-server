package com.jungle.chalnaServer.domain.appsettings.service;


import com.jungle.chalnaServer.domain.appsettings.domain.dto.SettingRequest;
import com.jungle.chalnaServer.domain.appsettings.domain.dto.SettingResponse;
import com.jungle.chalnaServer.domain.appsettings.domain.entity.MemberSetting;
import com.jungle.chalnaServer.domain.appsettings.repository.MemberSettingRepository;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@AllArgsConstructor
public class SettingService {

    private final MemberRepository memberRepository;
    private final MemberSettingRepository memberSettingRepository;

    public SettingResponse updateSettings(final Integer id, SettingRequest dto) {

        Member member = memberRepository.findByKakaoId(id)
                .orElseThrow(MemberNotFoundException::new);

        MemberSetting memberSetting = memberSettingRepository.findById(member.getId())
                .orElseThrow(MemberNotFoundException::new);

        memberSetting.update(dto);

        memberSetting = memberSettingRepository.save(memberSetting);

        return SettingResponse.of(memberSetting);
    }
}
