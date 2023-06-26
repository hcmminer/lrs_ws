package com.viettel.base.dto.custom;

import java.time.LocalDateTime;


public class SubScriberDTO {

  Long subId;
  String productCode;
  Long subType;
  String staDateTime;
  String actStatus;
  Long status;
  String enDateTime;
  

    public Long getSubId() {
        return subId;
    }

    public void setSubId(Long subId) {
        this.subId = subId;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public Long getSubType() {
        return subType;
    }

    public void setSubType(Long subType) {
        this.subType = subType;
    }

    public String getStaDateTime() {
        return staDateTime;
    }

    public void setStaDateTime(String staDateTime) {
        this.staDateTime = staDateTime;
    }

    public String getActStatus() {
        return actStatus;
    }

    public void setActStatus(String actStatus) {
        this.actStatus = actStatus;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public String getEnDateTime() {
        return enDateTime;
    }

    public void setEnDateTime(String enDateTime) {
        this.enDateTime = enDateTime;
    }
    
    

    
   
    public SubScriberDTO() {
    }

}
