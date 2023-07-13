package com.viettel.base.cms.repo;

import com.viettel.base.cms.model.Province;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepo extends JpaRepository<Province, Integer> {}
