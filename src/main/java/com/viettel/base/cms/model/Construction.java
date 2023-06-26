package com.viettel.base.cms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "construction")
// causes Lombok to generate toString(), equals(), hashCode(), getter() & setter(), and Required arguments constructor in one go.
@Data
// causes Lombok to implement the Builder design pattern for the Pojo class.
// usage can be seen in DefaultBeersLoader.java -> createNewBeer() method.
@Builder
// causes Lombok to generate a constructor with no parameters.
@NoArgsConstructor
// causes Lombok to generate a constructor with 1 parameter for each field in your class.
@AllArgsConstructor
@Component
public class Construction {
    @Id
    @Column(name = "construction_id", nullable = false, updatable = false)
    private Long constructionId;

    @Column(name = "construction_code", nullable = true)
    private String constructionCode;

    @Column(name = "construction_name", nullable = true)
    private String constructionName;

    @Column(name = "position_type", nullable = true)
    private Long positionType;

    @Column(name = "station_type", nullable = true)
    private Long stationType;

    @Column(name = "column_type", nullable = true)
    private Long columnType;

    @Column(name = "column_height", nullable = true)
    private String columnHeight;

    @Column(name = "construction_long", nullable = true)
    private String constructionLong;

    @Column(name = "construction_lat", nullable = true)
    private String constructionLat;

    @Column(name = "province_code", nullable = true)
    private String provinceCode;

    @Column(name = "district", nullable = true)
    private Long district;

    @Column(name = "village", nullable = true)
    private String village;

    @Column(name = "construction_type", nullable = true)
    private Long constructionType;

    @Column(name = "network", nullable = true)
    private String network;

    @Column(name = "vendor", nullable = true)
    private String vendor;

    @Column(name = "band", nullable = true)
    private String band;

    @Column(name = "anten_height", nullable = true)
    private String antenHeight;

    @Column(name = "azimuth", nullable = true)
    private String azimuth;

    @Column(name = "tilt", nullable = true)
    private String tilt;

    @Column(name = "sector", nullable = true)
    private Long sector;

    @Column(name = "trx_mode", nullable = true)
    private String trxMode;

    @Column(name = "start_point", nullable = true)
    private String startPoint;

    @Column(name = "end_point", nullable = true)
    private String endPoint;

    @Column(name = "cable_route", nullable = true)
    private String cableRoute;

    @Column(name = "distance_cable", nullable = true)
    private String distanceCable;

    @Column(name = "column_number", nullable = true)
    private Long columnNumber;

    @Column(name = "note", nullable = true)
    private String note;

    @Column(name = "decision_deploy", nullable = true)
    private String decisionDeploy;

//    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "start_date", nullable = true)
    private LocalDateTime startDate;

    @Column(name = "start_by", nullable = true)
    private String startBy;

    @Column(name = "complete_date", nullable = true)
    private LocalDateTime completeDate;

    @Column(name = "complete_by", nullable = true)
    private String completeBy;

    @Column(name = "created_date", nullable = true, updatable = false)
    private LocalDateTime createdDate;

    @Column(name = "created_by", nullable = true, updatable = false)
    private String createdBy;

    @Column(name = "last_modified_date", nullable = true)
    private LocalDateTime lastModifiedDate;

    @Column(name = "last_modified_by", nullable = true)
    private String lastModifiedBy;

    @Column(name = "status", nullable = true)
    private Long status;
}
