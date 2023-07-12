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
    private String uses;
    private String typerentalarea;
    private String unitprice;
    private String stationtype ;
    private String stationtypebyservice;
    private String stationlocate;
    private String provinceid;
    private String districtid;
    private String communeid;
    private String rentposition;
    private String notes;
    private String constructionpermit;
    private String inforstatus;
    private String contractno;
    private String signdatecontract;
    private String startdatecontract;
    private String enddatecontract;
    private String startdatepayment;
    private String enddatepayment;
    private String rentperiod;
    private String renttype;
    private String rentarea;
    private String isvat;
    private String vatrate;
    private String currencytype;
    private String exchangerate;
    private String totalrentvalue;
    private String nummonthpayment1;
    private String nummonthpayment2;
    private String nummonthpayment3;
    private String nummonthpayment4;
    private String nummonthpayment5;
    private String nummonthpayment6;
    private String nummonthpayment7;
    private String nummonthpayment8;
    private String nummonthpayment9;
    private String nummonthpayment10;
    private String rentcontracttype;
    private String timecontracttype;
    private String rentalunitname;
    private String taxcode;
    private String benefientityname;
    private String benefientitynum;
    private String benefibankcode;
    private String landownertype;
    private String paidtomaturity;
    private String paidtoyear;
    private String status;
    private String cancelreason;
    private String approvestatus ;
    private String handoverdate;
    private String constructionstartdate;
    private String constructionnote;
    private String btsaireddate;
    private String btsstatus;
    private String turnoffdate;
    private String createdatetime;
    private String createby;
    private String updatedatetime;
    private String updateby;



}

