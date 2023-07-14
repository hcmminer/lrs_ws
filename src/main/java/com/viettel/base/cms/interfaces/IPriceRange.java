package com.viettel.base.cms.interfaces;

import java.time.LocalDateTime;

public interface IPriceRange {
     Long getPriceRangeId();
     Long getOptionSetValueId();
     Long getProvinceId();
     String getPriceCode();
     String getDescription();
     Long getAmount();
     String getStartDate();
     String getExpiredDate();
     LocalDateTime getCreateDatetime();
     String getCreateBy();
     LocalDateTime getUpdateDatetime();
     String getUpdateBy();

     Long getstatus();

     //extend
     String getProvinceCode();
     String getAreaCode();
}
