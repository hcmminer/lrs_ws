package com.viettel.base.cms.repo;

import com.viettel.base.cms.interfaces.IUnit;
import com.viettel.base.cms.model.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UnitRepo extends JpaRepository<Unit, Long> {

    @Query(value = "SELECT " +
            " un.unit_id as unitId," +
            " un.province_id as provinceId," +
            "(\n" +
            "           select\n" +
            "                  p.pro_Name\n" +
            "           from\n" +
            "                  province p\n" +
            "           where\n" +
            "                  p.pro_id = un.province_id ) as provinceName,\n" +
            " un.create_datetime as createDatetime," +
            " un.unit_name_vi as unitNameVi," +
            " un.unit_name_en as unitNameEn," +
            " un.unit_name_la as unitNameLa," +
            " un.create_by as createBy, " +
            " un.status" +
            " FROM unit un" +
            " WHERE " +
            " (:provinceId is null or nvl(un.PROVINCE_ID,'') = :provinceId)" +
            " (:unitName is null " +
            "or (case " +
            "   when :lang = 'vi' then nvl(un.unit_name_vi,'') " +
            "   when :lang = 'en' then nvl(un.unit_name_en,'') " +
            "   when :lang = 'la' then nvl(un.unit_name_la,'') " +
            " ) like %:unitName%)" +
            " and un.status = :status"
            , nativeQuery = true)
    List<IUnit> findAllByProvinceIdAndUnitNameAndStatus(Long provinceId, String unitName, Long status, String lang);


    boolean existsByUnitCodeOrUnitNameViOrUnitNameEnOrUnitNameLaAndProvinceIdAndStatus(String unitCode,String unitNameVi ,String unitNameEn, String unitNameLa,Long provinceId, Long status);



    List<Unit> findAllByUnitIdNotAndStatus(Long unitId, Long status);

}
