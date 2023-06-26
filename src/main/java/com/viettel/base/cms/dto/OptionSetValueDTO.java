/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.base.cms.dto;

import java.util.Date;

/**
 *
 * @author ADMIN
 */
public class OptionSetValueDTO {
    private Long optionSetValueId;
    private Long optionSetId;
    private String name;
    private String value;
    private Long status;
    private byte[] logo;
    private Date createDatetime;
    private String createBy;
    private Date updateDatetime;
    private String updateBy;
    private String description;
    private String strLogo;
    
    private Long rate;

    public Long getOptionSetValueId() {
        return optionSetValueId;
    }

    public void setOptionSetValueId(Long optionSetValueId) {
        this.optionSetValueId = optionSetValueId;
    }

    public Long getOptionSetId() {
        return optionSetId;
    }

    public void setOptionSetId(Long optionSetId) {
        this.optionSetId = optionSetId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Long getStatus() {
        return status;
    }

    public void setStatus(Long status) {
        this.status = status;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public Date getCreateDatetime() {
        return createDatetime;
    }

    public void setCreateDatetime(Date createDatetime) {
        this.createDatetime = createDatetime;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public Date getUpdateDatetime() {
        return updateDatetime;
    }

    public void setUpdateDatetime(Date updateDatetime) {
        this.updateDatetime = updateDatetime;
    }

    public String getUpdateBy() {
        return updateBy;
    }

    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getRate() {
        return rate;
    }

    public void setRate(Long rate) {
        this.rate = rate;
    }
    
    

    public OptionSetValueDTO() {
    }

    public String getStrLogo() {
        return strLogo;
    }

    public void setStrLogo(String strLogo) {
        this.strLogo = strLogo;
    }
    
    
 

}
