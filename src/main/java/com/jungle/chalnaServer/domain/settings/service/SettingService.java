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

@Service
@Slf4j
@AllArgsConstructor
public class SettingService {

    private final MemberRepository memberRepository;
    private final MemberSettingRepository memberSettingRepository;

    public SettingResponse updateSettings(final Long id, SettingRequest dto) {

        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        MemberSetting memberSetting = memberSettingRepository.findById(member.getId())
                .orElseThrow(MemberNotFoundException::new);

        memberSetting.update(dto);

        memberSetting = memberSettingRepository.save(memberSetting);

        return SettingResponse.of(memberSetting, false); // interestTag를 포함하지 않는 응답
    }

    public SettingResponse getSettings(final Long id, boolean includeTags) {
        MemberSetting memberSetting = memberSettingRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        return SettingResponse.of(memberSetting, includeTags);
    }

    public SettingResponse.TAGLIST getTags(final Long id) {
        MemberSetting memberSetting = memberSettingRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        return SettingResponse.toTagList(memberSetting);
    }

    public SettingResponse.TAGLIST createTags(final Long id, SettingRequest.TAG dto) {

        MemberSetting memberSetting = memberSettingRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        memberSetting.addInterestTag(dto.interestTag());

        memberSetting = memberSettingRepository.save(memberSetting);

        return SettingResponse.toTagList(memberSetting);

    }

    public SettingResponse.TAGLIST deleteTags(final Long id) {

        MemberSetting memberSetting = memberSettingRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        /* 삭제할 태그가 없을 경우 에러 처리 */
        if (memberSetting.getInterestTags().isEmpty()) {
            throw new TagsNotFoundException();
        }

        memberSetting.clearInterestTags();
        memberSetting = memberSettingRepository.save(memberSetting);

        return SettingResponse.toTagList(memberSetting);
    }

    public SettingResponse.TAGLIST removeInterestTag(final Long id, final String tag) {

        MemberSetting memberSetting = memberSettingRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        /* 삭제할 태그가 없을 경우 에러 처리 */
        if (!memberSetting.getInterestTags().contains(tag)) {
            log.error("태그가 존재하지 않습니다: {}", tag);
            throw new TagsNotFoundException();
        }

        memberSetting.removeInterestTag(tag);
        memberSetting = memberSettingRepository.save(memberSetting);

        return SettingResponse.toTagList(memberSetting);

    }
}
