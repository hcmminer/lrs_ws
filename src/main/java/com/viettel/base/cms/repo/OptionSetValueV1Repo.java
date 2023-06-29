package com.viettel.base.cms.repo;

import com.viettel.base.cms.model.OptionSetValueV1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionSetValueV1Repo extends JpaRepository<OptionSetValueV1, Long> {
    @Query("select o from OptionSetValueV1 o where  (:optionSetValueId is null or o.optionSetValueId = :optionSetValueId) and (:optionSetId is null  or  o.optionSetId = :optionSetId ) and o.status = :status")
    List<OptionSetValueV1> findAllByOptionSetValueIdAndOptionSetIdAndStatus(Long optionSetValueId, Long optionSetId, Long status);

    boolean existsByValueAndStatus(String value, Long status);
}
