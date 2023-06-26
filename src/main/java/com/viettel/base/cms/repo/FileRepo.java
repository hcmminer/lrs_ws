package com.viettel.base.cms.repo;

import com.viettel.base.cms.model.FileModel;
import com.viettel.base.cms.model.ImageModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileRepo extends JpaRepository<FileModel, Long> {
}
