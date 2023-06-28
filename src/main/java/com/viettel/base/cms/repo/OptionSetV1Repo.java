package com.viettel.base.cms.repo;

import com.viettel.base.cms.model.OptionSetV1;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface OptionSetV1Repo extends JpaRepository<OptionSetV1,Long> {
    @Query("select o from OptionSetV1 o where  :optionSetCode is null or o.optionSetCode like %:optionSetCode% ")
    List<OptionSetV1> findAllByOptionSetCode(String optionSetCode);
}
