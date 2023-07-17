package com.viettel.base.cms.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "bts_rent_place")
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
public class BTSStation {
    @Id
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;
    @Column(name = "site_on_nims", nullable = true)
    private String siteOnNims;
    @Column(name = "lat_value", nullable = true)
    private String latitude;
    @Column(name = "long_value", nullable = true)
    private String longitude;
    @Column(name = "uses", nullable = true)
    private Long uses;
    @Column(name = "type_rental_area", nullable = true)
    private Long typeRentalArea;
    @Column(name = "unit_price", nullable = true)
    private Long unitPrice;
    @Column(name = "station_type", nullable = true)
    private Long stationtype;
    @Column(name = "station_type_by_service", nullable = true)
    private Long stationTypeByService;
    @Column(name = "station_locate", nullable = true)
    private Long stationLocate;
    @Column(name = "province_id", nullable = true)
    private Long provinceId;
    @Column(name = "district_id", nullable = true)
    private Long districtId;
    @Column(name = "commune_id", nullable = true)
    private Long communeId;
    @Column(name = "rent_position", nullable = true)
    private Long rentPosition;
    @Column(name = "notes", nullable = true)
    private String notes;
    @Column(name = "construction_permit", nullable = true)
    private Long constructionpermit;
    @Column(name = "infor_status", nullable = true)
    private String inforStatus;
    @Column(name = "contract_no", nullable = true)
    private String contractNo;
    @Column(name = "sign_date_contract", nullable = true)
    private LocalDateTime signDateContract;
    @Column(name = "start_date_contract", nullable = true)
    private LocalDateTime startDateContract;
    @Column(name = "end_date_contract", nullable = true)
    private LocalDateTime endDateContract;
    @Column(name = "start_date_payment", nullable = true)
    private LocalDateTime startDatePayment;
    @Column(name = "end_date_payment", nullable = true)
    private LocalDateTime endDatePayment;
    @Column(name = "rent_period", nullable = true)
    private Long rentPeriod;
    @Column(name = "rent_type", nullable = true)
    private Long rentType;
    @Column(name = "rent_area", nullable = true)
    private Long rentArea;
    @Column(name = "is_vat", nullable = true)
    private Long isVat;
    @Column(name = "vat_rate", nullable = true)
    private Long vatRate;
    @Column(name = "currency_type", nullable = true)
    private Long currencyType;
    @Column(name = "exchange_rate", nullable = true)
    private Long exchangeRate;
    @Column(name = "total_rent_value", nullable = true)
    private String totalRentValue;
    @Column(name = "num_month_payment_1", nullable = true)
    private Long numMonthPayment1;
    @Column(name = "num_month_payment_2", nullable = true)
    private Long numMonthPayment2;
    @Column(name = "num_month_payment_3", nullable = true)
    private Long numMonthPayment3;
    @Column(name = "num_month_payment_4", nullable = true)
    private Long numMonthPayment4;
    @Column(name = "num_month_payment_5", nullable = true)
    private Long numMonthPayment5;
    @Column(name = "num_month_payment_6", nullable = true)
    private Long numMonthPayment6;
    @Column(name = "num_month_payment_7", nullable = true)
    private Long numMonthPayment7;
    @Column(name = "num_month_payment_8", nullable = true)
    private Long numMonthPayment8;
    @Column(name = "num_month_payment_9", nullable = true)
    private Long numMonthPayment9;
    @Column(name = "num_month_payment_10", nullable = true)
    private Long numMonthPayment10;
    @Column(name = "rent_contract_type", nullable = true)
    private Long rentContractType;
    @Column(name = "time_contract_type", nullable = true)
    private Long timeContractType;
    @Column(name = "tax_code", nullable = true)
    private String taxCode;
    @Column(name = "benefi_entity_num", nullable = true)
    private String benefiEntityNum;
    @Column(name = "benefi_bank_code", nullable = true)
    private String benefiBankCode;
    @Column(name = "landowner_type", nullable = true)
    private Long landownerType;
    @Column(name = "paid_to_maturity", nullable = true)
    private String paidToMaturity;
    @Column(name = "paid_to_year", nullable = true)
    private String paidToYear;
    @Column(name = "status", nullable = true)
    private Long status;
    @Column(name = "cancel_reason", nullable = true)
    private String cancelReason;
    @Column(name = "approve_status", nullable = true)
    private Long approveStatus ;
    @Column(name = "handover_date", nullable = true)
    private LocalDateTime handoverDate;
    @Column(name = "construction_start_date", nullable = true)
    private LocalDateTime constructionStartDate;
    @Column(name = "construction_note", nullable = true)
    private String constructionNote;
    @Column(name = "bts_aired_date", nullable = true)
    private LocalDateTime btsAiredDate;
    @Column(name = "bts_status", nullable = true)
    private Long btsStatus;
    @Column(name = "turn_off_date", nullable = true)
    private LocalDateTime turnOffDate;
    @Column(name = "creaate_datetime", nullable = true)
    private LocalDateTime createDatetime;
    @Column(name = "create_by", nullable = true)
    private String createBy;
    @Column(name = "update_datetime", nullable = true)
    private LocalDateTime updateDatetime;
    @Column(name = "update_by", nullable = true)
    private String updateBy;




}
