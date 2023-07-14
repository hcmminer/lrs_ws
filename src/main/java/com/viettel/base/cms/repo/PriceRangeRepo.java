package com.viettel.base.cms.repo;

import com.viettel.base.cms.interfaces.IPriceRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import com.viettel.base.cms.model.PriceRange;

import java.util.List;
import java.util.Optional;


@Repository
public interface PriceRangeRepo extends JpaRepository<PriceRange, Long> {

    @Query(value = "SELECT " +
            " pr.PRICE_RANGE_ID as priceRangeId," +
            " pr.PROVINCE_ID as provinceId," +
            "(\n" +
            "           select\n" +
            "                  p.pro_code\n" +
            "           from\n" +
            "                  province p\n" +
            "           where\n" +
            "                  p.pro_id = pr.PROVINCE_ID ) as provinceCode,\n" +
            "       (\n" +
            "           select\n" +
            "                  osv2.value\n" +
            "           from\n" +
            "                  option_set_value osv2\n" +
            "           where\n" +
            "                  osv2.option_set_value_id = pr.option_set_value_id ) as areaCode, " +
            " (\n" +
            "           select\n" +
            "                  concat(p.pro_code , osv.value)\n" +
            "           from\n" +
            "                  province p,\n" +
            "                  option_set_value osv\n" +
            "           where\n" +
            "                  p.pro_id               = pr.PROVINCE_ID\n" +
            "           and    pr.option_set_value_id = osv.option_set_value_id ) as priceCode, " +
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
            " (:provinceId is null or nvl(pr.PROVINCE_ID,'') = :provinceId)" +
            " and (:areaId is null or nvl(pr.option_set_value_id,'') = :areaId)" +
            " and pr.status = :status" +
            " order by pr.create_datetime desc "
            , nativeQuery = true)
    List<IPriceRange> findAllByProvinceIdAndOptionSetValueIdAndStatus(Long provinceId, Long areaId, Long status);


    boolean existsByOptionSetValueIdAndProvinceIdAndStatus(Long optionSetValueId, Long provinceId, Long status);


    Optional<PriceRange> findByPriceRangeIdAndStatus(Long priceRangeId, Long status);


    List<PriceRange> findAllByPriceRangeIdNotAndStatus(Long priceRangeId, Long status);

}
