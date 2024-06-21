package com.jungle.chalnaServer.domain.settings.controller;


import com.jungle.chalnaServer.domain.settings.domain.dto.SettingResponse;
import com.jungle.chalnaServer.domain.settings.service.SettingService;
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
    @PutMapping
    public CommonResponse<SettingResponse> updateSettings(HttpServletRequest request, @RequestBody SettingRequest dto) {

        Long id = jwtService.getId(jwtService.resolveToken(request, JwtService.AUTHORIZATION_HEADER));
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

    /* 전체 태그 목록 조회 */
    @GetMapping("/tag")
    public CommonResponse<SettingResponse.TAGLIST> getTags(HttpServletRequest request) {

        Long id = jwtService.getId(jwtService.resolveToken(request, JwtService.AUTHORIZATION_HEADER));
        SettingResponse.TAGLIST response = settingService.getTags(id);

        return CommonResponse.ok(response);
    }

    /* 선호 태그 추가 */
    @PostMapping("/tag")
    public CommonResponse<SettingResponse.TAGLIST> createTags(HttpServletRequest request, @RequestBody SettingRequest.TAG dto) {

        Long id = jwtService.getId(jwtService.resolveToken(request, JwtService.AUTHORIZATION_HEADER));
        SettingResponse.TAGLIST response = settingService.createTags(id, dto);

        return CommonResponse.ok(response);
    }

    /* 전체 선호 태그 삭제 */
    @DeleteMapping("/tag")
    public CommonResponse<SettingResponse.TAGLIST> deleteTags(HttpServletRequest request) {

        Long id = jwtService.getId(jwtService.resolveToken(request, JwtService.AUTHORIZATION_HEADER));
        SettingResponse.TAGLIST response = settingService.deleteTags(id);

        return CommonResponse.ok(response);
    }

    /* 선택 선호 태그 삭제 */
    @DeleteMapping("/tag/{tag}")
    public CommonResponse<SettingResponse.TAGLIST> removeInterestTag(HttpServletRequest request, @PathVariable("tag") String tag) {
        Long id = jwtService.getId(jwtService.resolveToken(request, JwtService.AUTHORIZATION_HEADER));
        SettingResponse.TAGLIST response = settingService.removeInterestTag(id, tag);

        return CommonResponse.ok(response);
    }
}

