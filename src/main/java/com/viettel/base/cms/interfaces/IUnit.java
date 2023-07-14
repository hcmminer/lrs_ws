package com.viettel.base.cms.interfaces;

import java.time.LocalDateTime;

public interface IUnit {
    Long getUnitId();

    String getUnitCode();

    String getUnitNameVi();

    String getUnitNameEn();

    String getUnitNameLa();

    Long getProvinceId();

    String getCreateBy();

    LocalDateTime getCreateDatetime();

    //extend
    String getProvinceName();
}


