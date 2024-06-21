package com.jungle.chalnaServer.domain.settings.repository;

import com.jungle.chalnaServer.domain.settings.domain.entity.MemberSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberSettingRepository extends JpaRepository<MemberSetting, Long>{
    Optional<MemberSetting> findById(Integer id);
}
