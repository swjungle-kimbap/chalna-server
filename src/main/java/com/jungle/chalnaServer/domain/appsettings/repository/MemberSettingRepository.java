package com.jungle.chalnaServer.domain.appsettings.repository;

import com.jungle.chalnaServer.domain.appsettings.domain.entity.MemberSetting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberSettingRepository extends JpaRepository<MemberSetting, Long>{
    Optional<MemberSetting> findById(Integer id);
}
