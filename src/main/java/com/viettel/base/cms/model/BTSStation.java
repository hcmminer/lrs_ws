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
    @Column(name = "SITE_ON_CONTRACT", nullable = true, updatable = true)
    private String siteOnContract;
    @Column(name = "SITE_ON_NIMS", nullable = false, updatable = true)
    private String siteOnNims;
    @Column(name = "LATITUDE", nullable = false, updatable = true)
    private String latitude;
    @Column(name = "LONGITUDE", nullable = false, updatable = true)
    private String longitude;
    @Column(name = "NAME", updatable = true)
    private String name;
    @Column(name = "ADDRESS_WARDS", updatable = true)
    private String address;
    @Column(name = "PRO_ID",  updatable = true)
    private String province;
    @Column(name = "DIST_ID",  updatable = true)
    private String district;
    @Column(name = "TELEPHONE", updatable = true)
    private String telephone;
    @Column(name = "NO_OF_CONTRACT", nullable = true, updatable = true)
    private String contractNo;
    @Column(name = "PERIOD_OF_RENT", updatable = true)
    private Long preiodOfRent;
    @Column(name = "START_DATE_CONTRACT",  updatable = true)
    private String startDateContract;
    @Column(name = "END_DATE_CONTRACT",  updatable = true)
    private String endDateContract;
    @Column(name = "SIGN_DATE_CONTRACT",  updatable = true)
    private String signDateContract;
    @Column(name = "BTS_AIRED_DATE",  updatable = true)
    private String btsAiredDate;
    @Column(name = "RENTAL_FEE",  updatable = true)
    private Long rentalFee;
    @Column(name = "PAYMENT_TIME", updatable = true)
    private String paymentTime;
    @Column(name = "FILE_CONTRACT", updatable = true)
    private String fileContract;
    @Column(name = "FILE_CR", updatable = true)
    private String fileCR;
    @Column(name = "HAS_ELECTRICITY", updatable = true)
    private Long hasElectricity;
    @Column(name = "APPROVED_STATUS", updatable = true)
    private Long approvedStatus;
    @Column(name = "STATUS", updatable = true)
    private Long status;
    @Column(name = "TURN_OFF_DATE",  updatable = true)
    private String turnOffDate;
    @Column(name = "CREATED_USER", updatable = true)
    private String createdUser;
    @Column(name = "CREATED_DATE", updatable = true)
    private String createdDate;
    @Column(name = "LAST_MODIFIED_USER", updatable = true)
    private String lastModifiedUser;
    @Column(name = "LAST_MODIFIED_DATE", updatable = true)
    private String lastModifiedDate;
    @Column(name = "START_DATE_PAYMENT",  updatable = true)
    private String startDatePayment;
    @Column(name = "END_DATE_PAYMENT",  updatable = true)
    private String endDatePayment;
    @Column(name = "AMOUNT",  updatable = true)
    private Long amount;
    @Column(name = "NOTES",  updatable = true)
    private String notes;



}
