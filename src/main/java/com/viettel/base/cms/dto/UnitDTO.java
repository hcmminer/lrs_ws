package com.viettel.base.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UnitDTO {
    private Long unitId;
    private String unitCode;
    private String unitName;
    private Long provinceId;
    private String createBy;
    private LocalDateTime createDatetime;

}
