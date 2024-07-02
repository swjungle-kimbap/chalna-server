package com.jungle.chalnaServer.infra.file.repository;

import com.jungle.chalnaServer.infra.file.domain.entity.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo,Long> {

    Optional<FileInfo> findById(Long fileId);

}
