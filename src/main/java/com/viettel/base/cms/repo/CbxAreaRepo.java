package com.viettel.base.cms.repo;

import com.viettel.base.cms.model.OptionSetValueV1;
import com.viettel.base.cms.model.ProvinceV1Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CbxAreaRepo extends JpaRepository<OptionSetValueV1, Long> {
    @Query("select a from OptionSetValueV1 a where (a.optionSetId is null or a.optionSetId = :optionSetId) and  a.status = :status")
    List<OptionSetValueV1> findAllByOptionSetIdAndStatus(Long optionSetId, Long status);
}
