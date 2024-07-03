package com.jungle.chalnaServer.domain.settings.service;


import com.jungle.chalnaServer.domain.settings.domain.dto.SettingRequest;
import com.jungle.chalnaServer.domain.settings.domain.dto.SettingResponse;
import com.jungle.chalnaServer.domain.settings.domain.entity.MemberSetting;
import com.jungle.chalnaServer.domain.settings.exception.TagsNotFoundException;
import com.jungle.chalnaServer.domain.settings.repository.MemberSettingRepository;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@AllArgsConstructor
public class SettingService {

    private final MemberRepository memberRepository;
    private final MemberSettingRepository memberSettingRepository;

    @Transactional
    public SettingResponse updateSettings(final Long id, SettingRequest dto) {

        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        MemberSetting memberSetting = memberSettingRepository.findById(member.getId())
                .orElseThrow(MemberNotFoundException::new);

        memberSetting.update(dto);

        memberSetting = memberSettingRepository.save(memberSetting);

        return SettingResponse.of(memberSetting, false); // interestTag를 포함하지 않는 응답
    }

    public SettingResponse getSettings(final Long id, boolean includeKeyword) {
        log.info("엥??");
        MemberSetting memberSetting = memberSettingRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
        log.info("엥??");

        return SettingResponse.of(memberSetting, includeKeyword);
    }

    @Transactional
    public void setDoNotDisturb(final Long id, SettingRequest.DONOTDISTURB dto) {
        MemberSetting memberSetting = memberSettingRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        memberSetting.updateDoNotDisturb(dto.doNotDisturbStart(),dto.doNotDisturbEnd());
        memberSettingRepository.save(memberSetting);
    }

    public SettingResponse.KEYWORDLIST  getKeywords(final Long id) {
        log.info("엥");
        MemberSetting memberSetting = memberSettingRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);
        log.info("엥");

        return SettingResponse.toKeywordList(memberSetting);
    }

    public SettingResponse.KEYWORDLIST  createKeyword(final Long id, SettingRequest.KEYWORD dto) {

        MemberSetting memberSetting = memberSettingRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        memberSetting.addInterestKeyword(dto.interestKeyword());

        memberSetting = memberSettingRepository.save(memberSetting);

        return SettingResponse.toKeywordList(memberSetting);

    }

    public SettingResponse. KEYWORDLIST  deleteKeyword(final Long id) {

        MemberSetting memberSetting = memberSettingRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        /* 삭제할 태그가 없을 경우 에러 처리 */
        if (memberSetting.getInterestKeyword().isEmpty()) {
            throw new TagsNotFoundException();
        }

        memberSetting.clearInterestKeyword();
        memberSetting = memberSettingRepository.save(memberSetting);

        return SettingResponse.toKeywordList(memberSetting);
    }

    public SettingResponse.KEYWORDLIST removeInterestKeyword(final Long id, final String keyword) {

        MemberSetting memberSetting = memberSettingRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        /* 삭제할 태그가 없을 경우 에러 처리 */
        if (!memberSetting.getInterestKeyword().contains(keyword)) {
            log.error("태그가 존재하지 않습니다: {}", keyword);
            throw new TagsNotFoundException();
        }

        memberSetting.removeInterestKeyword(keyword);
        memberSetting = memberSettingRepository.save(memberSetting);

        return SettingResponse.toKeywordList(memberSetting);

    }
}
