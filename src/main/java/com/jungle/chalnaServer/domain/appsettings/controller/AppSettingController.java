package com.jungle.chalnaServer.domain.appsettings.controller;


import com.jungle.chalnaServer.domain.appsettings.domain.dto.SettingResponse;
import com.jungle.chalnaServer.domain.appsettings.service.SettingService;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.jungle.chalnaServer.domain.appsettings.domain.dto.SettingRequest;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/setting")
public class AppSettingController {

    private final SettingService settingService;

    @PutMapping("/{kakaoId}")
    public ResponseEntity<CommonResponse<?>> updateSettings(@PathVariable("kakaoId") final Integer id, @RequestBody SettingRequest dto) {

        SettingResponse response = settingService.updateSettings(id,dto);

        return ResponseEntity.status(HttpStatus.OK).body(CommonResponse.from(HttpStatus.OK,response,"Ok"));
    }


}
