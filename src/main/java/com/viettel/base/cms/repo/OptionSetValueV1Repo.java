package com.viettel.base.cms.repo;

import com.viettel.base.cms.model.OptionSetValueV1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OptionSetValueV1Repo extends JpaRepository<OptionSetValueV1, Long> {

    @Query("select o from OptionSetValueV1 o where  (:value is null or trim(o.value) like %:value%)  and o.status = :status order by o.createDatetime desc ")
    List<OptionSetValueV1> findAllByValueAndStatus(String value, Long status);

    boolean existsByValueAndStatus(String value, Long status);
}
