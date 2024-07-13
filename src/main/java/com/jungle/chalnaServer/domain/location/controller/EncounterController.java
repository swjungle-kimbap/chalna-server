package com.jungle.chalnaServer.domain.location.controller;

import com.jungle.chalnaServer.domain.location.domain.dto.EncounterResponse;
import com.jungle.chalnaServer.domain.location.service.EncounterService;
import com.jungle.chalnaServer.global.auth.jwt.annotation.AuthUserId;
import com.jungle.chalnaServer.global.common.dto.CommonResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/api/v1/encounter")
@RequiredArgsConstructor
public class EncounterController {

    private final EncounterService locationService;

    @GetMapping("/{otherId}")
    public CommonResponse<List<EncounterResponse.LOCATION>> getLocation(@AuthUserId final Long id, @PathVariable(name = "otherId") final Long otherId) {
        return CommonResponse.ok(locationService.getLocation(id,otherId));
    }

}
