package com.viettel.base.cms.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "unit")
public class Unit {
    @Id
    @Column(name = "unit_id")
    private Long unitId;

    @Column(name = "unit_code")
    private String unitCode;

    @Column(name = "unit_name_vi")
    private String unitNameVi;

    @Column(name = "unit_name_en")
    private String unitNameEn;

    @Column(name = "unit_name_la")
    private String unitNameLa;

    @Column(name = "province_id")
    private Long provinceId;

    @Column(name = "create_by")
    private String createBy;

    @Column(name = "create_datetime")
    private LocalDateTime createDatetime;


}
