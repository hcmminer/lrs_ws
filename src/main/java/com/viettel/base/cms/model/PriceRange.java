package com.viettel.base.cms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "price_range")


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceRange {
    @Id
    @Column(name = "PRICE_RANGE_ID")
    private Long priceRangeId;

    @Column(name = "PROVINCE_ID")
    private Long provinceId;

    @Column(name = "PRICE_CODE")
    private String priceCode;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "AMOUNT")
    private Long amount;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "expired_date")
    private LocalDateTime expiredDate;

    @Column(name = "create_datetime")
    private LocalDateTime createDatetime;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "update_datetime")
    private LocalDateTime updateDatetime;

    @Column(name = "update_by")
    private String updateBy;


}
