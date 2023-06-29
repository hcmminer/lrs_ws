package com.viettel.base.cms.repo;

import com.viettel.base.cms.model.OptionSetV1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionSetV1Repo extends JpaRepository<OptionSetV1, Long> {
    @Query("select o from OptionSetV1 o where  (:optionSetId is null or o.optionSetId = :optionSetId) and o.status = :status")
    List<OptionSetV1> findAllByOptionSetIdAndStatus(Long optionSetId, Long status);

    boolean existsByOptionSetCodeAndAndStatus(String optionSetCode, Long status);
}
