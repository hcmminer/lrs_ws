package com.viettel.base.cms.interfaces;


import java.time.LocalDateTime;

public interface  IOptionSetValueV1{
     Long getOptionSetValueId();
     Long getOptionSetId();
     String getName();
     String getValue();
     Long getStatus();
     LocalDateTime getCreateDatetime();
     String getCreateBy();
     LocalDateTime getUpdateDatetime();
     String getUpdateBy();
     String getDescription();
     String getLanguage();
     String getNameVi();
     String getNameEn();
     String getNameLa();

     String getOptionSetCode();
}
