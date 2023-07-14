package com.viettel.base.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceRangeDTO {
    private Long priceRangeId;
    private Long optionSetValueId;
    private Long provinceId;
    private String priceCode;
    private String description;
    private Long amount;
    private String startDate;
    private String expiredDate;
    private LocalDateTime createDatetime;
    private String createBy;
    private LocalDateTime updateDatetime;
    private String updateBy;

    private Long status;





}
