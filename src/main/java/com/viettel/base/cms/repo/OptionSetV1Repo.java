package com.viettel.base.cms.repo;

import com.viettel.base.cms.model.OptionSetV1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionSetV1Repo extends JpaRepository<OptionSetV1, Long> {
    @Query("select o from OptionSetV1 o where  (:optionSetCode is null or trim(o.optionSetCode) like %:optionSetCode%) and o.status = :status order by o.createDatetime desc ")
    List<OptionSetV1> findAllByOptionSetCodeAndStatus(String optionSetCode, Long status);

    List<OptionSetV1> findAllByOptionSetIdNotAndStatus(Long optionSetId, Long status);

    boolean existsByOptionSetCodeAndStatus(String optionSetCode, Long status);
}
