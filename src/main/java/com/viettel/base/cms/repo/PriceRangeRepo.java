package com.viettel.base.cms.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.viettel.base.cms.model.PriceRange;

import java.util.List;

@Repository
public interface PriceRangeRepo extends JpaRepository<PriceRange, Long> {
    @Query("select a from PriceRange a where (:priceCode is null or a.priceCode like %:priceCode%) and a.status = :status")
    List<PriceRange> findAllByPriceCodeAndStatus(String priceCode, Long status);

    boolean existsByOptionSetValueIdAndProvinceIdAndStatus(Long optionSetValueId ,Long provinceId, Long status);

}
