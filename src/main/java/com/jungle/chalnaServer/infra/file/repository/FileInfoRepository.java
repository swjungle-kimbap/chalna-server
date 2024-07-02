package com.jungle.chalnaServer.infra.file.repository;

import com.jungle.chalnaServer.infra.file.domain.entity.FileInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FileInfoRepository extends JpaRepository<FileInfo,Long> {

}
