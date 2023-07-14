package com.viettel.base.cms.repo;

import com.viettel.base.cms.model.ProvinceV1Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface CbxProvinceRepo extends JpaRepository<ProvinceV1Model, Long> {
    @Query("select p from ProvinceV1Model p where p.status = :status")
    List<ProvinceV1Model> findAllByStatus(String status);
}
