package com.viettel.base.cms.repo;

import com.viettel.base.cms.model.OptionSetValueV1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.viettel.base.cms.interfaces.*;
import com.viettel.base.cms.dto.OptionSetValueV1DTO;

import java.util.List;

@Repository
public interface OptionSetValueV1Repo extends JpaRepository<OptionSetValueV1, Long> {
    @Query(value = "select ops.value as value, ops.option_set_Id as optionSetId, ops.option_set_value_id as optionSetValueId, ops.name_vi as nameVi, ops.name_la as nameLa, ops.name_en as nameEn, ops.create_datetime as createDatetime, ops.create_by as createBy, " +
            "op.option_set_code  as optionSetCode, " +
            "ops.description  as description " +
            "from option_set_value ops left join option_set op " +
            "on ops.option_set_id = op.option_set_id " +
            "where 1=1 " +
            "and :optionSetId is null or ops.option_set_id = :optionSetId " +
            "and ops.status =:status " +
            "and :value is null or trim(ops.value) like %:value% " +
            "order by ops.create_datetime desc ", nativeQuery = true)
    List<IOptionSetValueV1> findAllByValueAndStatus(Long optionSetId, String value, Long status);

    List<OptionSetValueV1> findAllByOptionSetValueIdNotAndOptionSetIdAndStatus(Long optionSetValueId,Long optionSetId ,Long status);

    boolean existsByValueAndOptionSetIdAndStatus(String value,Long optionSetId ,Long status);
}
