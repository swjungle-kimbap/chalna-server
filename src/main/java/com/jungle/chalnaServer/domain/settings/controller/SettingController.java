package com.jungle.chalnaServer.domain.settings.controller;


import com.jungle.chalnaServer.domain.settings.domain.dto.SettingResponse;
import com.jungle.chalnaServer.domain.settings.service.SettingService;
import com.jungle.chalnaServer.global.auth.jwt.annotation.AuthUserId;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import com.jungle.chalnaServer.global.util.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.jungle.chalnaServer.domain.settings.domain.dto.SettingRequest;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/setting")
public class SettingController {

    private final SettingService settingService;
    private final JwtService jwtService;

    /* 앱 설정 */
    @PatchMapping
    public CommonResponse<SettingResponse> updateSettings(@AuthUserId final Long id, @RequestBody SettingRequest dto) {
        SettingResponse response = settingService.updateSettings(id, dto);
        return CommonResponse.ok(response);

    }

    /* 앱 설정 조회 */
    @GetMapping
    public CommonResponse<SettingResponse> getSettings(HttpServletRequest request) {

        Long id = jwtService.getId(jwtService.resolveToken(request, JwtService.AUTHORIZATION_HEADER));
        SettingResponse response = settingService.getSettings(id, true);

        return CommonResponse.ok(response);
    }

    /* 방해 금지 모드 */
    @PatchMapping("/disturb")
    public CommonResponse<?> setDoNotDisturb(@AuthUserId final Long id, @RequestBody SettingRequest.DONOTDISTURB dto) {
        settingService.setDoNotDisturb(id,dto);
        return CommonResponse.ok("방해금지 모드 설정이 완료됐습니다.");
    }

    /* 전체 태그 목록 조회 */
    @GetMapping("/keyword")
    public CommonResponse<SettingResponse.KEYWORDLIST> getTags(HttpServletRequest request) {

        Long id = jwtService.getId(jwtService.resolveToken(request, JwtService.AUTHORIZATION_HEADER));
        SettingResponse.KEYWORDLIST response = settingService.getKeywords(id);

        return CommonResponse.ok(response);
    }

    /* 선호 태그 추가 */
    @PostMapping("/keyword")
    public CommonResponse<SettingResponse.KEYWORDLIST> createTags(HttpServletRequest request, @RequestBody SettingRequest.KEYWORD dto) {

        Long id = jwtService.getId(jwtService.resolveToken(request, JwtService.AUTHORIZATION_HEADER));
        SettingResponse.KEYWORDLIST response = settingService.createKeyword(id, dto);

        return CommonResponse.ok(response);
    }

    /* 전체 선호 태그 삭제 */
    @DeleteMapping("/keyword")
    public CommonResponse<SettingResponse.KEYWORDLIST> deleteTags(HttpServletRequest request) {

        Long id = jwtService.getId(jwtService.resolveToken(request, JwtService.AUTHORIZATION_HEADER));
        SettingResponse.KEYWORDLIST response = settingService.deleteKeyword(id);

        return CommonResponse.ok(response);
    }

    /* 선택 선호 태그 삭제 */
    @DeleteMapping("/keyword/{keyword}")
    public CommonResponse<SettingResponse.KEYWORDLIST> removeInterestTag(HttpServletRequest request, @PathVariable("keyword") String keyword) {
        Long id = jwtService.getId(jwtService.resolveToken(request, JwtService.AUTHORIZATION_HEADER));
        SettingResponse.KEYWORDLIST response = settingService.removeInterestKeyword(id, keyword);

        return CommonResponse.ok(response);
    }
}

