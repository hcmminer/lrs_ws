package com.viettel.base.cms.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import java.util.List;

@Data
// causes Lombok to implement the Builder design pattern for the Pojo class.
// usage can be seen in DefaultBeersLoader.java -> createNewBeer() method.
@Builder
// causes Lombok to generate a constructor with no parameters.
@NoArgsConstructor
// causes Lombok to generate a constructor with 1 parameter for each field in your class.
@AllArgsConstructor
@Component
@Entity
public class BTSStationDTO {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String siteOnNims;
    private String latitude;
    private String longitude;
    private Long uses;
    private String usesName;
    private Long typeRentalArea;
    private String typeRentalAreaName;
    private Long unitPrice;
    private Long stationtype;
    private String stationtypeName;
    private Long stationTypeByService;
    private String stationTypeByServiceName;
    private Long stationLocate;
    private String stationLocateName;
    private Long provinceId;
    private Long districtId;
    private Long communeId;
    private String provinceName;
    private String districtName;
    private String communeName;
    private Long rentPosition;
    private String rentPositionName;
    private String notes;
    private Long constructionPermit;
    private String inforStatus;
    private String contractNo;
    private LocalDateTime signDateContract;
    private LocalDateTime startDateContract;
    private LocalDateTime endDateContract;
    private LocalDateTime startDatePayment;
    private LocalDateTime endDatePayment;
    private Long rentPeriod;
    private Long rentType;
    private String rentTypeName;
    private Long rentArea;
    private String rentAreaName;
    private Long isVat;
    private Long vatRate;
    private Long currencyType;
    private String currencyTypeName;
    private Long exchangeRate;
    private String totalRentValue;
    private Long numMonthPayment1;
    private Long numMonthPayment2;
    private Long numMonthPayment3;
    private Long numMonthPayment4;
    private Long numMonthPayment5;
    private Long numMonthPayment6;
    private Long numMonthPayment7;
    private Long numMonthPayment8;
    private Long numMonthPayment9;
    private Long numMonthPayment10;
    private Long rentContractType;
    private String rentContractTypeName;
    private Long timeContractType;
    private String timeContractTypeName;
    private String rentalUnitName;
    private String taxCode;
    private String benefiEntityName;
    private String benefiEntityNum;
    private String benefiBankCode;
    private Long landownerType;
    private String landownerTypeName;
    private String paidToMaturity;
    private String paidToYear;
    private Long status;
    private String cancelReason;
    private Long approveStatus ;
    private LocalDateTime handoverDate;
    private LocalDateTime constructionStartDate;
    private String constructionNote;
    private LocalDateTime btsAiredDate;
    private Long btsStatus;
    private LocalDateTime turnOffDate;
    private LocalDateTime createDatetime;
    private String createBy;
    private LocalDateTime updateDatetime;
    private String updateBy;



}

