package com.viettel.base.cms.repo;

import com.viettel.base.cms.interfaces.IPriceRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.viettel.base.cms.model.PriceRange;

import java.util.List;


@Repository
public interface PriceRangeRepo extends JpaRepository<PriceRange, Long> {

    @Query(value = "SELECT " +
            " pr.PRICE_RANGE_ID as priceRangeId," +
            " pr.PROVINCE_ID as provinceId," +
            " pr.PRICE_CODE as priceCode, " +
            " pr.DESCRIPTION as description," +
            " pr.AMOUNT as amount, " +
            " pr.create_datetime as createDatetime," +
            " pr.expired_date as expiredDate, " +
            " pr.start_date as startDate," +
            " pr.create_by as createBy, " +
            " pr.option_set_value_id as optionSetValueId," +
            " pr.status" +
            " FROM price_range pr" +
            " WHERE " +
            " (:priceCode is null or nvl(pr.PRICE_CODE,'') like %:priceCode%)" +
            " and pr.status = :status"
            , nativeQuery = true)
    List<IPriceRange> findAllByPriceCodeAndStatusNavite(String priceCode, Long status);

//    @Query("select a from PriceRange a where (:priceCode is null or a.priceCode like %:priceCode%) and a.status = :status")
//    List<PriceRange> findAllByPriceCodeAndStatus(String priceCode, Long status);

    boolean existsByOptionSetValueIdAndProvinceIdAndStatus(Long optionSetValueId, Long provinceId, Long status);

}
