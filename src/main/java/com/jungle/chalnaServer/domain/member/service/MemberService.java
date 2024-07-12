package com.jungle.chalnaServer.domain.member.service;

import com.amazonaws.services.s3.AmazonS3;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberResponse;
import com.jungle.chalnaServer.domain.member.domain.dto.MemberRequest;
import com.jungle.chalnaServer.domain.member.domain.entity.Member;
import com.jungle.chalnaServer.domain.member.repository.MemberRepository;
import com.jungle.chalnaServer.infra.file.domain.dto.FileRequest;
import com.jungle.chalnaServer.infra.file.domain.dto.FileResponse;
import com.jungle.chalnaServer.infra.file.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import com.jungle.chalnaServer.domain.member.exception.MemberNotFoundException;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService {

    private final AmazonS3 amazonS3;
    private final MemberRepository memberRepository;
    private final FileService fileService;


    @Transactional
    public MemberResponse.INFO updateMemberProfile(final Long id, MemberRequest.PROFILE memberDto){

        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        // null 값이 아닌 경우에만 업데이트

        Member finalMember = member;
        memberDto.username().ifPresent(username -> {
            finalMember.updateUsername(username);
        });

        memberDto.message().ifPresent(message-> {
            finalMember.updateMessage(message);
        });

        memberDto.profileImageId().ifPresent(profileImageId -> {
            finalMember.updateProfileImageId(profileImageId);
        });


        member = memberRepository.save(member);
        log.info("Updated member: {}", member);

        return MemberResponse.INFO.of(member);
    }


//    @Transactional
//    public MemberResponse.PROFILE_IMAGE_UPLOAD updateMemberInfo(final Long id, FileRequest.UPLOAD fileDto) {
//        Member member = memberRepository.findById(id)
//                .orElseThrow(MemberNotFoundException::new);
//
//        FileResponse.UPLOAD upload = fileService.uploadFile(id, fileDto, FileService.PROFILE_IMAGE_DIRECTORY);
//
//        member.updateProfileImageId(upload.fileId());
//
//        return MemberResponse.PROFILE_IMAGE_UPLOAD.of(upload);
//    }


    /* 사용자 정보를 조회하는 메서드 */
    public MemberResponse.INFO getMemberInfo(Long id) {
        Member member = memberRepository.findById(id)
                .orElseThrow(MemberNotFoundException::new);

        log.info("사용자 정보 조회 성공");

        /* 추후 토큰을 검사해서 해당 user의 사용자의 정보만 조회하도록 수정 */

        return MemberResponse.INFO.of(member);

    }
}
