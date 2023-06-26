package com.viettel.base.cms.controller;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.*;
import com.viettel.base.cms.model.*;
import com.viettel.base.cms.repo.BTSStationRepo;
import com.viettel.base.cms.repo.MtHisRepo;
import com.viettel.base.cms.repo.MtRepo;
import com.viettel.base.cms.service.*;
import com.viettel.base.cms.utils.DataUtil;
import com.viettel.base.cms.utils.DateValidator;
import com.viettel.base.cms.utils.FunctionUtils;
import com.viettel.security.PassTranformer;
import com.viettel.vfw5.base.dto.ExecutionResult;
import com.viettel.vfw5.base.utils.DataUtils;
import com.viettel.vfw5.base.utils.ResourceBundle;
import com.viettel.vfw5.base.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import sendunicode.SendUnicode;
import viettel.passport.client.RoleToken;
import viettel.passport.client.UserToken;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class BTSStationCtrl {

    @Autowired
    private BTSStationService btsStationService;

    @Autowired
    private OptionSetValueService optionSetValueService;

    @Autowired
    private Environment env;

    @Autowired
    private LocationService locationService;

    private String linkService = "";

    @Autowired
    private UserService userService;

    @Autowired
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private EntityManager sendSMS;

    @Autowired
    MtHisRepo mtHisRepo;

    @Autowired
    MtRepo mtRepo;

    @Autowired
    BTSStationRepo bTSStationRepo;

    @Autowired
    private MTService mtService;

    @Autowired
    private FileUploadService fileUploadService;

    private int timeout = 5000;

    @PostMapping(value = "/searchBTSStation")
    public ExecutionResult searchBTSStation(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        String userName = commonInputDTO.getUserName().split("----")[0];
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);

        try {
            commonInputDTO.setAppCode("IMT");
            String roleCode = userService.getUserRole(commonInputDTO);
            if (Constant.BTS_ROLES.CMS_BTS_CN_STAFF.equals(roleCode)) {
                String provinceCode = btsStationService.getProvinceCodeOfStaff(userName);
                commonInputDTO.getBtsStationDTO().setProvinceCode(provinceCode);
                List<BTSStationDTO> btsStationDTOS = btsStationService.searchBTSStation(commonInputDTO.getBtsStationDTO(), language);
                res.setData(btsStationDTOS);
            } else {
                List<BTSStationDTO> btsStationDTOS = btsStationService.searchBTSStation(commonInputDTO.getBtsStationDTO(), language);
                res.setData(btsStationDTOS);
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/getListApprovedStatus")
    public ExecutionResult getListApprovedStatus(@RequestHeader("Accept-Language") String language) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        String isdn = null;
        // trace log
//        Long traceID = DataUtils.getSequence(upoint, "trace_logs_seq");
//        String funcitionName = "getListConstructionType";

        try {

            List<OptionSetValueDTO> optionSetValueDTOS = optionSetValueService.getListByOptionSet("APPROVED_STATUS", language, null);

            res.setData(optionSetValueDTOS);
            res.setDescription(Constant.EXECUTION_MESSAGE.OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
            // trace log
//            TraceLogs traceLogEx = new TraceLogs(null, traceID, e.toString(), LocalDateTime.now(), "SYSTEM", "2", funcitionName);
//            DataUtils.traceLogs.add(traceLogEx);
            // end trace log
        }
        return res;
    }

    @PostMapping(value = "/getListStationStatus")
    public ExecutionResult getListStationStatus(@RequestHeader("Accept-Language") String language) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        String isdn = null;
        // trace log
//        Long traceID = DataUtils.getSequence(upoint, "trace_logs_seq");
//        String funcitionName = "getListConstructionType";

        try {

            List<OptionSetValueDTO> optionSetValueDTOS = optionSetValueService.getListByOptionSet("STATION_STATUS", language, null);

            res.setData(optionSetValueDTOS);
            res.setDescription(Constant.EXECUTION_MESSAGE.OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
            // trace log
//            TraceLogs traceLogEx = new TraceLogs(null, traceID, e.toString(), LocalDateTime.now(), "SYSTEM", "2", funcitionName);
//            DataUtils.traceLogs.add(traceLogEx);
            // end trace log
        }
        return res;
    }

    @PostMapping(value = "/createBTSStation")
    public ExecutionResult createBTSStation(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            Staff staff = userService.getStaffByUserName(commonInputDTO.getUserName().split("----")[0]);
            ExecutionResult tempRes = validateInputCreate(commonInputDTO, language, 0);
            if (Constant.EXECUTION_ERROR.ERROR.equals(tempRes.getErrorCode())) {
                return tempRes;
            }
            tempRes = FunctionUtils.validateInputBTSStation(commonInputDTO.getBtsStationDTO(),
                    language,
                    btsStationService,
                    locationService,
                    optionSetValueService);
            if (Constant.EXECUTION_ERROR.ERROR.equals(tempRes.getErrorCode())) {
                return tempRes;
            }
            commonInputDTO.getBtsStationDTO().setCreatedUser(commonInputDTO.getUserName().split("----")[0]);
            commonInputDTO.getBtsStationDTO().setCreatedDate(String.valueOf(LocalDateTime.now()));
            btsStationService.createBTSStation(commonInputDTO.getBtsStationDTO(), staff);
            res.setDescription(r.getResourceMessage("add.bts.station.success"));
            return res;

        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    ExecutionResult validateInputCreate(CommonInputDTO commonInputDTO, String language, int retry) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        VSAValidate vsaValidate = new VSAValidate();
        UserToken userToken = vsaValidate.getUserToken();
//        if (userToken != null) {
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.station.info.null"));
                return res;
            }
            commonInputDTO.setUserName(commonInputDTO.getUserName() + "----" + commonInputDTO.getPassword());
            commonInputDTO.setAppCode("IMT");
            String roleCode = userService.getUserRole(commonInputDTO);
            if (!Constant.BTS_ROLES.CMS_BTS_PNO_STAFF.equals(roleCode)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("not.allowed.create.construction"));
                return res;
            } else {
                if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getSiteOnNims())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.site.of.nims.info.null"));
                    return res;
                }
                if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getLongitude())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.longitude.info.null"));
                    return res;
                }

                if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getLatitude())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.latitude.info.null"));
                    return res;
                }
//                if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getContractNo())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.contract.no.info.null"));
//                    return res;
//                }
//                if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getSiteOnContract())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.site.of.nims.info.null"));
//                    return res;
//                }
                boolean checkLat = new DataUtil.UsernameValidator().validateNumberDouble(commonInputDTO.getBtsStationDTO().getLatitude());
                if (!checkLat){
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.station.lat.long.not.allow.format"));
                    return res;
                }
                boolean checkLong = new DataUtil.UsernameValidator().validateNumberDouble(commonInputDTO.getBtsStationDTO().getLongitude());
                if (!checkLong){
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.station.lat.long.not.allow.format"));
                    return res;
                }
            }

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getSiteOnNims())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.site.of.nims.info.null"));
                return res;
            } else {
                String responseSMS = checkSiteOnNims(commonInputDTO.getBtsStationDTO().getSiteOnNims());
                System.out.println("Response : " + responseSMS);
                boolean checkReturn = responseSMS.contains("<status>0</status>");
                if (checkReturn) {
                    boolean checkRerult = responseSMS.contains("<totalDataJson>0</totalDataJson>");
                    if (!checkRerult) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
                    } else {
                        System.out.println("retry: " + retry);
                        if (retry < Constant.RETRY ){
                            validateInputCreate(commonInputDTO, language, ++retry);
                        } else {
                            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                            res.setDescription(r.getResourceMessage("bts.site.on.nims.not.exits"));
                            return res;
                        }
                    }
                } else {
                    System.out.println("retry: " + retry);
                    if (retry < Constant.RETRY ) {
                        validateInputCreate(commonInputDTO, language, ++retry);
                    } else {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.site.on.nims.not.exits"));
                        return res;
                    }
                }

            }

//        } else {
//            System.out.println("authentication unsuccessful");
//            return null;
//        }
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    ExecutionResult validateInputUpdate(CommonInputDTO commonInputDTO, BTSStationDTO btsStationDTO, String language) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        VSAValidate vsaValidate = new VSAValidate();
        UserToken userToken = vsaValidate.getUserToken();
        DateValidator validator = new DataUtil.DateValidatorUsingDateFormat("dd/MM/yyyy");
//        if (userToken != null) {
        try {
            commonInputDTO.setAppCode("IMT");
            String roleCode = userService.getUserRole(commonInputDTO);
//            String roleCode = Constant.BTS_ROLES.CMS_BTS_TCCN_STAFF;
            List<BTSStation> btsStationList = btsStationService.getListBTSStationById(btsStationDTO);
            if (btsStationList.size() <= 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.station.code.already.not.exist.update", btsStationDTO.getId() + "-" + btsStationDTO.getSiteOnNims()));
                return res;
            }
            String status = btsStationService.getStatusBTSStation(btsStationDTO);
            String approvedStatus = btsStationService.getApprovedStatusBTSStation(btsStationDTO);
            if (Constant.STATUS_VALUE.WORKING.equals(status)) {
                if (Constant.APPROVED_VALUE.APPROVE.equals(approvedStatus)) {
                    if (!(Constant.BTS_ROLES.CMS_BTS_GRAND_TC_STAFF.equals(roleCode) || Constant.BTS_ROLES.CMS_BTS_TCCN_STAFF.equals(roleCode))) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.station.status.not.allow.update.null", btsStationDTO.getId() + "-" + btsStationDTO.getSiteOnNims()));
                        return res;
                    }
                } else {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.station.status.not.allow.update.null", btsStationDTO.getId() + "-" + btsStationDTO.getSiteOnNims()));
                    return res;
                }
//                else if (Constant.APPROVED_VALUE.REJECT.equals(approvedStatus)){
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.station.status.not.allow.update.null", btsStationDTO.getId() + "-" + btsStationDTO.getSiteOnNims()));
//                    return res;
//                }
//                else{
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.station.status.not.allow.update.null", btsStationDTO.getId() + "-" + btsStationDTO.getSiteOnNims()));
//            }
//                return res;
            } else {
                if (Constant.APPROVED_VALUE.APPROVE.equals(approvedStatus)) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.station.status.not.allow.update.null", btsStationDTO.getId() + "-" + btsStationDTO.getSiteOnNims()));
                        return res;
                }
            }



//            if (!StringUtils.isStringNullOrEmpty(userToken.getRolesList())){
//                for (RoleToken roleToken: userToken.getRolesList()){
            if (Constant.BTS_ROLES.CMS_BTS_PNO_STAFF.equals(roleCode)) {
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getSiteOnNims())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.site.of.nims.info.null"));
                    return res;
                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getLongitude())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.longitude.info.null"));
                    return res;
                }

                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getLatitude())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.latitude.info.null"));
                    return res;
                }
                boolean checkLat = new DataUtil.UsernameValidator().validateNumberDouble(btsStationDTO.getLatitude());
                if (!checkLat){
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.station.lat.long.not.allow.format"));
                    return res;
                }
                boolean checkLong = new DataUtil.UsernameValidator().validateNumberDouble(btsStationDTO.getLongitude());
                if (!checkLong){
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.station.lat.long.not.allow.format"));
                    return res;
                }
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getContractNo())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.contract.no.info.null"));
//                    return res;
//                }
            } else if (Constant.BTS_ROLES.CMS_BTS_TCCN_STAFF.equals(roleCode)){
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getSiteOnNims())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.site.of.nims.info.null"));
//                    return res;
//                }
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getLongitude())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.longitude.info.null"));
//                    return res;
//                }
//
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getLatitude())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.latitude.info.null"));
//                    return res;
//                }
//
//                boolean checkLat = new DataUtil.UsernameValidator().validateNumberDouble(btsStationDTO.getLatitude());
//                if (!checkLat){
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.station.lat.long.not.allow.format"));
//                    return res;
//                }
//                boolean checkLong = new DataUtil.UsernameValidator().validateNumberDouble(btsStationDTO.getLongitude());
//                if (!checkLong){
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.station.lat.long.not.allow.format"));
//                    return res;
//                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getContractNo())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.contract.no.info.null"));
                    return res;
                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.name.info.null"));
                return res;
                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getProvince())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.provice.info.null"));
                return res;
                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getDistrict())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.district.info.null"));
                return res;
                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getTelephone())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.telephone.info.null"));
                return res;
                } else {
                    boolean checkFormat = new DataUtil.PhoneValidator().validateNumber(btsStationDTO.getTelephone());
                        if (!checkFormat){
                            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                            res.setDescription(r.getResourceMessage("bts.station.telephone.not.allow.format"));
                            return res;
                        }

                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDatePayment())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.started.date.payment.info.null"));
                return res;
                } else {
                    if (!validator.isValid(btsStationDTO.getStartDatePayment())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.date.not.format"));
                        return res;
                    }
                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDatePayment())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.end.date.payment.info.null"));
                return res;
                 }else {
                    if (!validator.isValid(btsStationDTO.getEndDatePayment())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.date.not.format"));
                        return res;
                    }
                }

                if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDatePayment()) && !StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDatePayment())) {
                    if (DataUtil.compareDate(btsStationDTO.getStartDatePayment(), btsStationDTO.getEndDatePayment(), "dd/MM/yyyy")){
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.from.date.not.before.to.date.payment"));
                        return res;
                    }

                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getAmount())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.amount.info.null"));
                    return res;
                }else {
                    boolean checkFormat = new DataUtil.NumberValidator().validateNumber(String.valueOf(btsStationDTO.getAmount()));
                    if (!checkFormat){
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.station.amount.not.allow.format"));
                        return res;
                    }

                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getPaymentTime())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.payment.time.info.null"));
                    return res;
                }else {
                    boolean checkFormat = new DataUtil.NumberValidator().validateNumber(btsStationDTO.getPaymentTime());
                    if (!checkFormat){
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.station.payment.time.not.allow.format"));
                        return res;
                    }

                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getHasElectricity())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.has.electricity.info.null"));
                    return res;
                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getFileContract())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.file.contract.info.null"));
                    return res;
                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getPeriodOfRent())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.period.of.rent.info.null"));
                    return res;
                }else {
                    boolean checkFormat = new DataUtil.NumberValidator().validateNumber(String.valueOf(btsStationDTO.getPeriodOfRent()));
                    if (!checkFormat){
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.station.period.of.rent.not.allow.format"));
                        return res;
                    }

                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDateContract())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.started.date.contract.info.null"));
                    return res;
                }else {
                    if (!validator.isValid(btsStationDTO.getStartDateContract())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.date.not.format"));
                        return res;
                    }
                }
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDateContract())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.end.date.contract.info.null"));
//                    return res;
//                }else {
//                    if (!validator.isValid(btsStationDTO.getEndDateContract())) {
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("bts.date.not.format"));
//                        return res;
//                    }
//                }
                if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDateContract()) && !StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDateContract())) {
                    if (DataUtil.compareDate(btsStationDTO.getStartDateContract(), btsStationDTO.getStartDateContract(), "dd/MM/yyyy")){
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.from.date.not.before.to.date.payment"));
                        return res;
                    }

                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getSignDateContract())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.sign.date.contract.info.null"));
                    return res;
                }else {
                    if (!validator.isValid(btsStationDTO.getSignDateContract())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.date.not.format"));
                        return res;
                    }
                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getRentalFee())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.rental.fee.info.null"));
                    return res;
                }else {
                    boolean checkFormat = new DataUtil.NumberValidator().validateNumber(String.valueOf(btsStationDTO.getRentalFee()));
                    if (!checkFormat){
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.station.rental.fee.not.allow.format"));
                        return res;
                    }

                }

//            }
//            else if (Constant.BTS_ROLES.CMS_BTS_CN_STAFF.equals(roleCode)){
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getSiteOnNims())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.site.of.nims.info.null"));
//                    return res;
//                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getLongitude())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.longitude.info.null"));
                    return res;
                }

                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getLatitude())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.latitude.info.null"));
                    return res;
                }
                boolean checkLat = new DataUtil.UsernameValidator().validateNumberDouble(btsStationDTO.getLatitude());
                if (!checkLat){
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.station.lat.long.not.allow.format"));
                    return res;
                }
                boolean checkLong = new DataUtil.UsernameValidator().validateNumberDouble(btsStationDTO.getLongitude());
                if (!checkLong){
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.station.lat.long.not.allow.format"));
                    return res;
                }
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getContractNo())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.contract.no.info.null"));
//                }
//                    if (StringUtils.isStringNullOrEmpty(btsStationDTO.getName())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.name.info.null"));
//                    return res;
//                }
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getProvince())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.provice.info.null"));
//                    return res;
//                }
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getDistrict())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.district.info.null"));
//                    return res;
//                }
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getTelephone())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.telephone.info.null"));
//                    return res;
//                }else {
//                    boolean checkFormat = new DataUtil.NumberValidator().validateNumber(btsStationDTO.getTelephone());
//                    if (!checkFormat){
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("bts.station.telephone.not.allow.format"));
//                        return res;
//                    }
//
//                }
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDatePayment())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.started.date.payment.info.null"));
//                    return res;
//                }else {
//                    if (!validator.isValid(btsStationDTO.getStartDatePayment())) {
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("bts.date.not.format"));
//                        return res;
//                    }
//                }
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDatePayment())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.end.date.payment.info.null"));
//                    return res;
//                }else {
//                    if (!validator.isValid(btsStationDTO.getEndDatePayment())) {
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("bts.date.not.format"));
//                        return res;
//                    }
//                }
//                if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDatePayment()) && !StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDatePayment())) {
//                    if (DataUtil.compareDate(btsStationDTO.getStartDatePayment(), btsStationDTO.getEndDatePayment(), "dd/MM/yyyy")){
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("bts.from.date.not.before.to.date.payment"));
//                        return res;
//                    }
//                }
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getAmount())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.amount.info.null"));
//                    return res;
//                }else {
//                    boolean checkFormat = new DataUtil.NumberValidator().validateNumber(String.valueOf(btsStationDTO.getAmount()));
//                    if (!checkFormat){
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("bts.station.amount.not.allow.format"));
//                        return res;
//                    }
//
//                }
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getPaymentTime())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.payment.time.info.null"));
//                    return res;
//                }else {
//                    boolean checkFormat = new DataUtil.NumberValidator().validateNumber(btsStationDTO.getPaymentTime());
//                    if (!checkFormat){
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("bts.station.payment.time.not.allow.format"));
//                        return res;
//                    }
//
//                }
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getHasElectricity())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.has.electricity.info.null"));
//                    return res;
//                }
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getFileContract())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.file.contract.info.null"));
//                    return res;
//                }
//
            }else if (Constant.BTS_ROLES.CMS_BTS_GRAND_TC_STAFF.equals(roleCode)){
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getSiteOnNims())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.site.of.nims.info.null"));
//                    return res;
//                }
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getLongitude())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.longitude.info.null"));
//                    return res;
//                }
//
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getLatitude())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.latitude.info.null"));
//                    return res;
//                }
//                boolean checkLat = new DataUtil.UsernameValidator().validateNumberDouble(btsStationDTO.getLatitude());
//                if (!checkLat){
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.station.lat.long.not.allow.format"));
//                    return res;
//                }
//                boolean checkLong = new DataUtil.UsernameValidator().validateNumberDouble(btsStationDTO.getLongitude());
//                if (!checkLong){
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.station.lat.long.not.allow.format"));
//                    return res;
//                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getContractNo())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.contract.no.info.null"));
                    return res;
                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getName())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.name.info.null"));
                    return res;
                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getProvince())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.provice.info.null"));
                    return res;
                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getDistrict())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.district.info.null"));
                    return res;
                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getTelephone())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.telephone.info.null"));
                    return res;
                }else {
                    boolean checkFormat = new DataUtil.NumberValidator().validateNumber(btsStationDTO.getTelephone());
                    if (!checkFormat){
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.station.telephone.not.allow.format"));
                        return res;
                    }

                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDatePayment())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.started.date.payment.info.null"));
                    return res;
                }else {
                    if (!validator.isValid(btsStationDTO.getStartDatePayment())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.date.not.format"));
                        return res;
                    }
                }
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDatePayment())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.end.date.payment.info.null"));
//                    return res;
//                }else {
//                    if (!validator.isValid(btsStationDTO.getEndDatePayment())) {
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("bts.date.not.format"));
//                        return res;
//                    }
//                }
                if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDatePayment()) && !StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDatePayment())) {
                    if (DataUtil.compareDate(btsStationDTO.getStartDatePayment(), btsStationDTO.getEndDatePayment(), "dd/MM/yyyy")){
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.from.date.not.before.to.date.payment"));
                        return res;
                    }

                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getAmount())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.amount.info.null"));
                    return res;
                }else {
                    boolean checkFormat = new DataUtil.NumberValidator().validateNumber(String.valueOf(btsStationDTO.getAmount()));
                    if (!checkFormat){
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.station.amount.not.allow.format"));
                        return res;
                    }

                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getPaymentTime())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.payment.time.info.null"));
                    return res;
                }else {
                    boolean checkFormat = new DataUtil.NumberValidator().validateNumber(btsStationDTO.getPaymentTime());
                    if (!checkFormat){
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.station.payment.time.not.allow.format"));
                        return res;
                    }

                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getHasElectricity())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.has.electricity.info.null"));
                    return res;
                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getFileContract())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.file.contract.info.null"));
                    return res;
                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getPeriodOfRent())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.period.of.rent.info.null"));
                    return res;
                }else {
                    boolean checkFormat = new DataUtil.NumberValidator().validateNumber(String.valueOf(btsStationDTO.getPeriodOfRent()));
                    if (!checkFormat){
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.station.period.of.rent.not.allow.format"));
                        return res;
                    }

                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDateContract())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.started.date.contract.info.null"));
                    return res;
                }else {
                    if (!validator.isValid(btsStationDTO.getStartDateContract())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.date.not.format"));
                        return res;
                    }
                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDateContract())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.end.date.contract.info.null"));
                    return res;
                }else {
                    if (!validator.isValid(btsStationDTO.getEndDateContract())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.date.not.format"));
                        return res;
                    }
                }
                if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDateContract()) && !StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDateContract())) {
                    if (DataUtil.compareDate(btsStationDTO.getStartDateContract(), btsStationDTO.getStartDateContract(), "dd/MM/yyyy")){
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.from.date.not.before.to.date.payment"));
                        return res;
                    }

                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getSignDateContract())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.sign.date.contract.info.null"));
                    return res;
                }else {
                    if (!validator.isValid(btsStationDTO.getSignDateContract())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.date.not.format"));
                        return res;
                    }
                }
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getRentalFee())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.rental.fee.info.null"));
                    return res;
                }else {
                    boolean checkFormat = new DataUtil.NumberValidator().validateNumber(String.valueOf(btsStationDTO.getRentalFee()));
                    if (!checkFormat){
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.station.rental.fee.not.allow.format"));
                        return res;
                    }

                }

            }else if (Constant.BTS_ROLES.CMS_BTS_CND_STAFF.equals(roleCode)){
                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getApprovedStatus())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.approved.status.info.null"));
                    return res;
                }

            }

            if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getProvinceCode())) {
                Long provinceId = btsStationService.getProvinceIdByCode(btsStationDTO.getProvinceCode());
                btsStationDTO.setProvince(String.valueOf(provinceId));
            }

//                }
//            }
//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getSiteOnNims())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.site.of.nims.info.null"));
//                return res;
//            } else {
//                String responseSMS = checkSiteOnNims(commonInputDTO.getBtsStationDTO().getSiteOnNims());
//                boolean checkReturn = responseSMS.contains("<status>0</status>");
//                if (checkReturn) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
//                } else {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.site.on.nims.not.exits"));
//                    return res;
//                }
//            }
//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getLongitude())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.longitude.info.null"));
//                return res;
//            }
//
//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getLatitude())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.latitude.info.null"));
//                return res;
//            }
//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getContractNo())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.contract.no.info.null"));
//                return res;
//            }
//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getName())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.name.info.null"));
//                return res;
//            }
//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getProvince())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.provice.info.null"));
//                return res;
//            }
//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getDistrict())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.district.info.null"));
//                return res;
//            }
//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getTelephone())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.telephone.info.null"));
//                return res;
//            }
//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getStartDateContract())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.started.date.contract.info.null"));
//                return res;
//            }
//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getEndDateContract())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.end.date.contract.info.null"));
//                return res;
//            }
//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getRentalFee())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.rental.fee.info.null"));
//                return res;
//            }
//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getPaymentTime())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.payment.time.info.null"));
//                return res;
//            }
//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getPeriodOfRent())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.period.of.rent.info.null"));
//                return res;
//            }
//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getFileContract())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.file.contract.info.null"));
//                return res;
//            }
//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getApprovedStatus())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.approved.status.info.null"));
//                return res;
//            }
//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getStatus())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.status.info.null"));
//                return res;
//            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
//        } else {
//            System.out.println("authentication unsuccessful");
//            return null;
//        }
        return res;
    }

    @PostMapping(value = "/updateBTSStation")
    public ExecutionResult updateBTSStation(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
//        String isdn = null;
        // trace log
//        Long traceID = DataUtils.getSequence(upoint, "trace_logs_seq");
//        String funcitionName = "getListConstructionType";

        try {
            Staff staff = userService.getStaffByUserName(commonInputDTO.getUserName().split("----")[0]);
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTOList())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.station.info.null"));
                return res;
            }
            commonInputDTO.setAppCode("IMT");
            String roleCode = userService.getUserRole(commonInputDTO);
//            String roleCode = Constant.BTS_ROLES.CMS_BTS_CND_STAFF;
            for (BTSStationDTO btsStationDTO : commonInputDTO.getBtsStationDTOList()) {
                ExecutionResult tempRes = validateInputUpdate(commonInputDTO, btsStationDTO, language);
                if (Constant.EXECUTION_ERROR.ERROR.equals(tempRes.getErrorCode())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(tempRes.getDescription());
                    return tempRes;
                }

//            if (!Constant.BTS_ROLES.CMS_BTS_PNO_STAFF.equals(roleCode) || !Constant.BTS_ROLES.CMS_BTS_TCCN_STAFF.equals(roleCode) || !Constant.BTS_ROLES.CMS_BTS_GRAND_TC_STAFF.equals(roleCode) || !Constant.BTS_ROLES.CMS_BTS_CND_STAFF.equals(roleCode)) {
                if (Constant.BTS_ROLES.CMS_BTS_PNO_STAFF.equals(roleCode) || Constant.BTS_ROLES.CMS_BTS_TCCN_STAFF.equals(roleCode) || Constant.BTS_ROLES.CMS_BTS_GRAND_TC_STAFF.equals(roleCode) || Constant.BTS_ROLES.CMS_BTS_CND_STAFF.equals(roleCode)) {
                    System.out.println("User được phép cập nhật");
                } else {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("not.allowed.update.bts.station"));
                    return res;
                }
//            List<OptionSetValueDTO> optionSetValueDTOS = optionSetValueService.getListByOptionSet("STATION_STATUS", language);
//            res.setData(optionSetValueDTOS);
                btsStationDTO.setCreatedUser(commonInputDTO.getUserName().split("----")[0]);
                btsStationDTO.setCreatedDate(String.valueOf(LocalDateTime.now()));
                btsStationDTO.setLastModifiedUser(commonInputDTO.getUserName().split("----")[0]);
                btsStationDTO.setLastModifiedDate(String.valueOf(LocalDateTime.now()));

//                res.setDescription(Constant.EXECUTION_MESSAGE.OK);
            }
            res = btsStationService.updateBTSStation(commonInputDTO.getBtsStationDTOList(), commonInputDTO.getUserName(), roleCode, language, staff);
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
            // trace log
//            TraceLogs traceLogEx = new TraceLogs(null, traceID, e.toString(), LocalDateTime.now(), "SYSTEM", "2", funcitionName);
//            DataUtils.traceLogs.add(traceLogEx);
            // end trace log
        }
        return res;
    }

    @PostMapping(value = "/sendSMS")
    public ExecutionResult sendSMS(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
//            List<BTSStation> btsStationDTOS = btsStationService.searchBTSStation(commonInputDTO.getBtsStationDTO());
            List<String> lstIsdn = btsStationService.getListIsdnByDeptCode(Constant.DEPT_CODE.CND);
            String contentSMS = "You have 02 BTS (VIC001, VIC002) not yet approved on system. Please visit the link http://imt.unitel.com.la/btsmanager to see the details";
            if (!StringUtils.isStringNullOrEmpty(lstIsdn)) {
                for (String isdn : lstIsdn) {
                    String responseSMS = sendSMSNew(isdn);
//            boolean checkReturn = responseSMS.contains("<return>0</return>");
                    boolean checkReturn = responseSMS.contains("0");
                    if (checkReturn) {
                        MTHis mTHis = new MTHis();
                        mTHis.setMsisdn(isdn);
                        mTHis.setMessage(contentSMS);
                        mTHis.setAppId(Constant.APP_ID);
                        mTHis.setChannel(env.getProperty("chanel.sms"));
                        saveMTHis(mTHis);
                        res.setData(Constant.EXECUTION_ERROR.SUCCESS);
                    } else {
                        // insert mt
                        MT mt = new MT();
                        mt.setMsisdn(isdn);
                        mt.setMessage(contentSMS);
                        mt.setAppId(Constant.APP_ID);
                        mt.setChannel(env.getProperty("chanel.sms"));
                        saveMT(mt);
                        res.setData(Constant.EXECUTION_ERROR.ERROR);
                    }
                }
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    public String sendSMS() {
        String responseBody = "";
        String res = "";
        try {
            Date now = Calendar.getInstance().getTime();
//            String content = requestGameVoucherDTO.getContents();
            //userName và password gui tin nhan, password duoc giai ma
            String userName = env.getProperty("username.sms.game.voucher");
            PassTranformer.setInputKey(env.getProperty("key.enscrypt.security.upoint"));
            String password = PassTranformer.decrypt(env.getProperty("password.sms.game.voucher"));
            String time = env.getProperty("time.out.api");
            //dau so gui tin nhan
            String chanel = env.getProperty("chanel.sms.game.voucher");
            timeout = Integer.valueOf(time);
            //link gui tin nhan
            linkService = env.getProperty("url.sms.game.voucher");
            PostMethod post = new PostMethod(linkService);
            HttpClient httpclient = new HttpClient();
            String request = "";
            request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:xsd=\"http://smsws/xsd\">"
                    + "<soapenv:Header/>"
                    + "<soapenv:Body>"
                    + "<xsd:smsRequest>"
                    + "<xsd:username>" + userName + "</xsd:username>"
                    + " <xsd:password>" + password + "</xsd:password>"
                    + "<xsd:msisdn>" + "008562097049700" + "</xsd:msisdn>"
                    + "<xsd:content>" + "You have 02 BTS (VIC001, VIC002) not yet approved on system. Please visit the link http://imt.unitel.com.la/btsmanager to see the details" + "</xsd:content>"
                    + "<xsd:shortcode>" + chanel + "</xsd:shortcode>"
                    + "<xsd:alias>" + chanel + "</xsd:alias>"
                    + "<xsd:params>TEXT</xsd:params>"
                    + " </xsd:smsRequest>"
                    + "</soapenv:Body>"
                    + "</soapenv:Envelope>";
            RequestEntity entity = new StringRequestEntity(request, "text/xml", "UTF-8");
            post.setRequestEntity(entity);
            httpclient.executeMethod(post);
            responseBody = post.getResponseBodyAsString();
        } catch (Exception ex) {
            System.out.println("Loi SOAP call U-Point" + ex.getMessage());
            ex.printStackTrace();
        }
        return responseBody;
    }

    public String sendSMSNew(String isdn) {
        String error = "";
        try {
            MT mt = new MT();
            MTHis mTHis = new MTHis();
            mt.setMessage("You have 02 BTS (VIC001, VIC002) not yet approved on system. Please visit the link http://imt.unitel.com.la/btsmanager to see the details");
            byte[] bytes = mt.getMessage().getBytes("UTF-16BE");
            mt.setMsisdn(isdn);// 2092609748      8562097049700         2097504175      2095294359
            mt.setChannel(env.getProperty("chanel.sms"));
            String url = env.getProperty("url.sms");
            String userName = env.getProperty("username.sms");
//            String userName = "testlab";
//            PassTranformer.setInputKey(env.getProperty("key.security.exchange.client"));
//            String passWord = PassTranformer.decrypt(env.getProperty("password.sms"));
            String passWord = env.getProperty("password.sms");
            SendUnicode su = new SendUnicode(url, userName, passWord);
            int errorCode = su.sendUnicode("0", "warning", mt.getChannel(), mt.getMsisdn(), "1", mt.getMessage().getBytes("UTF-16BE"), "1");
            if (errorCode == 0) {
//                mtService.sendOTP(mTHis);
                System.out.println("----Gưi tin nhắn thành công----");
            } else {
//                mtService.sendOTPFalse(mt);
                System.out.println("----Gưi tin nhắn khong thành công----");
            }
            error = String.valueOf(errorCode);
        } catch (Exception ex) {
            System.out.println("Loi SOAP call U-Point" + ex.getMessage());
            ex.printStackTrace();
            // end trace log
        }
        return error;
    }

    public void saveMTHis(MTHis mTHis) {
        Long idLog = DataUtils.getSequence(sendSMS, "MT_HIS_SEQ");
        mTHis.setMtHisId(idLog);
        mTHis.setType(3L);
        mTHis.setSentTime(LocalDateTime.now());
        mTHis.setChannel(env.getProperty("chanel.sms.game.voucher"));
        mTHis.setAppId("SEND_SMS");
        mTHis.setStatus(1L);
        mTHis.setRetrySentCount(0L);
        mtHisRepo.save(mTHis);
    }

    public void saveMT(MT mt) {
        Long idLog = DataUtils.getSequence(sendSMS, "MT_SEQ");
        mt.setMtId(idLog);
        mt.setType(3L);
        mt.setSentTime(LocalDateTime.now());
        mt.setChannel(env.getProperty("chanel.sms.game.voucher"));
        mt.setAppId("SEND_SMS");
        mt.setStatus(1L);
        mt.setRetryNum(0L);;
        mtRepo.save(mt);
    }

    @PostMapping(value = "/getComboBoxDataBTSStation")
    public ExecutionResult getComboBoxData(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            List<ProvinceDTO> provinceDTOList = locationService.getListProvince();
            List<OptionSetValueDTO> listBTSStationStatus = optionSetValueService.getListByOptionSet(Constant.CMS_OPTION_SET.STATION_STATUS, language, null);
            List<OptionSetValueDTO> listApprovedStationStatus = optionSetValueService.getListByOptionSet(Constant.CMS_OPTION_SET.APPROVED_STATUS, language, null);
            List<OptionSetValueDTO> yesOrNoList = optionSetValueService.getListByOptionSet(Constant.CMS_OPTION_SET.YES_OR_NO, language, null);
//            List<ConstructionTypeDTO> constructionTypeDTOList = constructionService.getListConstructionType();
            ComboBoxData comboBoxData = new ComboBoxData();
            comboBoxData.setProvinceDTOList(provinceDTOList);
            comboBoxData.setListBTSStationStatus(listBTSStationStatus);
//            comboBoxData.setConstructionTypeDTOList(constructionTypeDTOList);
            comboBoxData.setListApprovedStationStatus(listApprovedStationStatus);
            comboBoxData.setListYesOrNo(yesOrNoList);
            res.setData(comboBoxData);
            res.setDescription(Constant.EXECUTION_MESSAGE.OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/checkSiteOnNims")
    public ExecutionResult checkSiteOnNims(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
//            List<BTSStation> btsStationDTOS = btsStationService.searchBTSStation(commonInputDTO.getBtsStationDTO());
            String responseSMS = checkSiteOnNims(commonInputDTO.getStationCode());
            boolean checkReturn = responseSMS.contains("<status>0</status>");
            if (checkReturn) {
                res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
            } else {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    public String checkSiteOnNims(String stationCode) {
        String responseBody = "";
        String res = "";
        try {
            Date now = Calendar.getInstance().getTime();
//            String content = requestGameVoucherDTO.getContents();
            String userName = env.getProperty("username.site.on.nims");
            String password = env.getProperty("password.site.on.nims");
            String code = env.getProperty("code.site.on.nims");
//            String passwordUpoint = PassTranformer.decrypt(env.getProperty("32a08c84c08b02fa5b37cd6f324437e0"));
            String time = env.getProperty("time.out.api");
            timeout = Integer.valueOf(time);
            linkService = env.getProperty("url.site.on.nims");
            PostMethod post = new PostMethod(linkService);
            HttpClient httpclient = new HttpClient();
            String request = "";
            request = "<soapenv:Envelope xmlns:soapenv=\"http://schemas.xmlsoap.org/soap/envelope/\" xmlns:web=\"http://webservice.infra.nims.viettel.com/\">"
                    + "<soapenv:Header/>"
                    + "<soapenv:Body>"
                    + "<web:getDataJson>"
                    + "<username>" + userName + "</username>"
                    + " <password>" + password + "</password>"
                    + "<requestInputBO>"
                    + "<code>" + code + "</code>"
                    + "<compressData>" + "" + "</compressData>"
                    + "<params>"
                    + "<format>" + "" + "</format>"
                    + "<name>" + "stationCodes" + "</name>"
                    + "<separator>" + "," + "</separator>"
                    + "<type>" + "STRING" + "</type>"
                    + "<value>" + stationCode + "</value>"
                    + "</params>"
                    + "</requestInputBO>"
                    + " </web:getDataJson>"
                    + "</soapenv:Body>"
                    + "</soapenv:Envelope>";
            RequestEntity entity = new StringRequestEntity(request, "text/xml", "UTF-8");
            post.setRequestEntity(entity);
            System.out.println("Request : " + request);
            httpclient.executeMethod(post);
            responseBody = post.getResponseBodyAsString();
        } catch (Exception ex) {
            System.out.println("Loi SOAP call U-Point" + ex.getMessage());
            ex.printStackTrace();
        }
        return responseBody;
    }

    @PostMapping(value = "/createBTSStationByFile")
    public ExecutionResult createBTSStationByFile(@RequestHeader("Accept-Language") String locate,
            @RequestBody MultipartFile fileCreateRequest,
            @RequestParam String eun) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        // trace log
//        Long traceID = DataUtils.getSequence(upoint, "trace_logs_seq");
//        String functionName = "uploadFile";
        try {

//            TraceLogs traceLog = TraceLogs.builder().traceId(traceID).content(fileCreateRequest.getOriginalFilename())
//                    .logTime(LocalDateTime.now()).updateBy("SYSTEM").errorCode("1").functionName(functionName).build();
//            DataUtils.traceLogs.add(traceLog);

            PassTranformer.setInputKey(env.getProperty("key.enscrypt.security"));
            String userName = PassTranformer.decrypt(eun);
            Staff staff = userService.getStaffByUserName(userName.split("----")[0]);
            if (StringUtils.isNullOrEmpty(eun)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(fileCreateRequest)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("file.is.null"));
                return res;
            }
            if (StringUtils.isNullOrEmpty(fileCreateRequest.getOriginalFilename())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("file.name.is.empty"));
                return res;
            }

            if (StringUtils.isNullOrEmpty(fileCreateRequest.getContentType())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("file.content.is.empty"));
                return res;
            }
            List<String> fileExtensions = Arrays.asList(".xls", ".xlsx");
            Boolean checkFilename = false;
            for (String fileExtension : fileExtensions) {
                if (fileCreateRequest.getOriginalFilename().contains(fileExtension)) {
                    checkFilename = true;
                    break;
                }
            }
            if (!checkFilename) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("file.type.wrong"));
                return res;
            }
            Long fileSize = fileCreateRequest.getSize();
            if (fileSize > 5L * 1024 * 1024) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("file.size.invalid"));
                return res;
            }
            ExecutionResult tempRes = btsStationService.getBTSStationFromFile(fileCreateRequest, userName, locate, staff);
            return tempRes;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
            // trace log
//            TraceLogs traceLogEx = TraceLogs.builder().traceId(traceID).content(e.toString())
//                    .logTime(LocalDateTime.now()).updateBy("SYSTEM").errorCode("2").functionName(functionName).build();
//            DataUtils.traceLogs.add(traceLogEx);
            // end trace log
        }
        return res;
    }

    @PostMapping(value = "/approvedBTSStation")
    public ExecutionResult approvedBTSStation(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            ExecutionResult tempRes = validateInputApproved(commonInputDTO, language);
            if (Constant.EXECUTION_ERROR.ERROR.equals(tempRes.getErrorCode())) {
                return tempRes;
            }
            ArrayList<String> arr = new ArrayList<String>();
            for (BTSStationDTO btsStationDTO : commonInputDTO.getBtsStationDTOList()) {
//            commonInputDTO.getBtsStationDTO().setApprovedStatus(commonInputDTO.getBtsStationDTO().getApprovedStatus());
                btsStationDTO.setLastModifiedUser(commonInputDTO.getUserName().split("----")[0]);
//                btsStationDTO.setLastModifiedDate(String.valueOf(LocalDateTime.now()));
//                btsStationDTO.setTurnOffDate(String.valueOf(LocalDateTime.now()));
                BTSStation station = bTSStationRepo.findById(btsStationDTO.getId()).orElse(null);

                if (!station.getStatus().equals(1L)) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.station.status.not.allow.turn.off.null", btsStationDTO.getId() + "-" + btsStationDTO.getSiteOnNims()));
                    return res;
                } else {
                    btsStationService.approvedBTSStation(btsStationDTO);
                }
                arr.add(station.getSiteOnNims());
            }
            String listStationSuccess = String.join(",", arr);
            res.setDescription(r.getResourceMessage("turn.off.station.success", listStationSuccess));
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    ExecutionResult validateInputApproved(CommonInputDTO commonInputDTO, String language) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        VSAValidate vsaValidate = new VSAValidate();
        UserToken userToken = vsaValidate.getUserToken();
//        if (userToken != null) {
        try {

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTOList())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.station.info.null"));
                return res;
            }
            for (BTSStationDTO btsStationDTO : commonInputDTO.getBtsStationDTOList()) {
                List<BTSStation> constructionList = btsStationService.getListBTSStation(btsStationDTO);
                if (constructionList.size() < 0) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.station.not.exits"));
                    return res;
                }
                commonInputDTO.setAppCode("IMT");
                String roleCode = userService.getUserRole(commonInputDTO);
//            if (!StringUtils.isStringNullOrEmpty(userToken.getRolesList())){
//                for (RoleToken roleToken: userToken.getRolesList()){
                if (Constant.BTS_ROLES.CMS_BTS_CND_STAFF.equals(roleCode)) {
                    if (StringUtils.isStringNullOrEmpty(btsStationDTO.getId())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.id.info.null"));
                        return res;
                    }
                    if (StringUtils.isStringNullOrEmpty(btsStationDTO.getApprovedStatus())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.approved.status.info.null"));
                        return res;
                    }
                    if (StringUtils.isStringNullOrEmpty(btsStationDTO.getSiteOnNims())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.site.of.nims.info.null"));
                        return res;
                    }
                } else {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("not.allowed.approved.bts.station"));
                    return res;
                }
            }
//                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
//        } else {
//            System.out.println("authentication unsuccessful");
//            return null;
//        }
        return res;
    }

    @PostMapping(value = "/turnOffBTSStation")
    public ExecutionResult turnOffBTSStation(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            Staff staff = userService.getStaffByUserName(commonInputDTO.getUserName().split("----")[0]);
            ExecutionResult tempRes = validateInputTurnOffBTSStation(commonInputDTO, language);
            if (Constant.EXECUTION_ERROR.ERROR.equals(tempRes.getErrorCode())) {
                return tempRes;
            }
            ArrayList<String> arr = new ArrayList<String>();
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTOList())) {
                for (BTSStationDTO btsStationDTO : commonInputDTO.getBtsStationDTOList()) {
                    BTSStation bTSStation = bTSStationRepo.findById(btsStationDTO.getId()).orElse(null);
                    if (bTSStation == null) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.station.not.exits", btsStationDTO.getSiteOnNims()));
                        return res;
                    }

                    if (!(bTSStation.getStatus() == 1L)) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.station.status.cannot.off", btsStationDTO.getSiteOnNims()));
                        return res;
                    }
                    if (!(bTSStation.getApprovedStatus() == 1L)) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.station.approvedstatus.cannot.off", btsStationDTO.getSiteOnNims()));
                        return res;
                    }
                    commonInputDTO.setAppCode("IMT");
                    String roleCode = userService.getUserRole(commonInputDTO);
                    if (Constant.BTS_ROLES.CMS_BTS_NOC_STAFF.equals(roleCode)) {
                        if (StringUtils.isStringNullOrEmpty(btsStationDTO.getId())) {
                            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                            res.setDescription(r.getResourceMessage("bts.id.info.null"));
                            return res;
                        }
                        if (StringUtils.isStringNullOrEmpty(btsStationDTO.getSiteOnNims())) {
                            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                            res.setDescription(r.getResourceMessage("bts.site.of.nims.info.null"));
                            return res;
                        }

                        if (StringUtils.isStringNullOrEmpty(btsStationDTO.getFileCR())) {
                            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                            res.setDescription(r.getResourceMessage("bts.station.file.CR.null"));
                            return res;
                        }

                    } else {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("not.allowed.approved.bts.station"));
                        return res;
                    }
                    btsStationDTO.setLastModifiedUser(commonInputDTO.getUserName().split("----")[0]);
                    btsStationService.turnOffBTSStation(bTSStation, btsStationDTO, staff);
                    arr.add(bTSStation.getSiteOnNims());
                }
                String listStationSuccess = String.join(",", arr);
                res.setDescription(r.getResourceMessage("turn.off.station.success", listStationSuccess));
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    ExecutionResult validateInputTurnOffBTSStation(CommonInputDTO commonInputDTO, String language) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        VSAValidate vsaValidate = new VSAValidate();
        UserToken userToken = vsaValidate.getUserToken();
//        if (userToken != null) {
        try {

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            if (DataUtil.isNullOrEmpty(commonInputDTO.getBtsStationDTOList())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.station.info.null"));
                return res;
            }
//            for (BTSStationDTO btsStationDTO : commonInputDTO.getBtsStationDTOList()) {
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getId())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.id.info.null"));
//                    return res;
//                }
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getApprovedStatus())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.approved.status.info.null"));
//                    return res;
//                }
//                if (StringUtils.isStringNullOrEmpty(btsStationDTO.getSiteOnNims())) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("bts.site.of.nims.info.null"));
//                    return res;
//                }
//            }
//                }
//            }

        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
//        } else {
//            System.out.println("authentication unsuccessful");
//            return null;
//        }
        return res;
    }

    @PostMapping(value = "/uploadFile")
    public ExecutionResult uploadFile(@RequestHeader("Accept-Language") String locate,
                                       @RequestBody List<MultipartFile> listFileImage,
                                       @RequestParam String eun,
                                       @RequestParam String btsStationId,
                                        @RequestParam Long type) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        // trace log
//        Long traceID = DataUtils.getSequence(upoint, "trace_logs_seq");
        String functionName = "uploadImg";
        try {
//            TraceLogs traceLog = TraceLogs.builder().traceId(traceID).content(fileCreateRequest.getOriginalFilename())
//                    .logTime(LocalDateTime.now()).updateBy("SYSTEM").errorCode("1").functionName(functionName).build();
//            DataUtils.traceLogs.add(traceLog);

            PassTranformer.setInputKey(env.getProperty("key.enscrypt.security"));
            String userName = PassTranformer.decrypt(eun);

            if (StringUtils.isNullOrEmpty(eun)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            if (StringUtils.isNullOrEmpty(btsStationId)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.info.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(listFileImage) || listFileImage.size() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("list.image.null"));
                return res;
            }
            List<String> fileExtensions = Arrays.asList(".pdf");
            Boolean checkFilename = false;
            String extension = "";
            List<FileDTO> listImageDTO = new ArrayList<>();
            int index = 0;
            MultipartFile file = new MultipartFile() {
                @Override
                public String getName() {
                    return null;
                }

                @Override
                public String getOriginalFilename() {
                    return null;
                }

                @Override
                public String getContentType() {
                    return null;
                }

                @Override
                public boolean isEmpty() {
                    return false;
                }

                @Override
                public long getSize() {
                    return 0;
                }

                @Override
                public byte[] getBytes() throws IOException {
                    return new byte[0];
                }

                @Override
                public InputStream getInputStream() throws IOException {
                    return null;
                }

                @Override
                public void transferTo(File file) throws IOException, IllegalStateException {

                }
            };
            for (MultipartFile fileImage
                    : listFileImage) {
                file = fileImage;
                if (StringUtils.isNullOrEmpty(fileImage.getOriginalFilename())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("file.name.is.empty"));
                    return res;
                }

                if (StringUtils.isNullOrEmpty(fileImage.getContentType())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("file.content.is.empty"));
                    return res;
                }
                for (String fileExtension : fileExtensions) {
                    if (fileImage.getOriginalFilename().toLowerCase().contains(fileExtension)) {
                        checkFilename = true;
                        extension = fileExtension;
                        break;
                    }
                }
                if (!checkFilename) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("file.type.wrong"));
                    return res;
                }
                Long fileSize = fileImage.getSize();
                if (fileSize > 50L * 1024 * 1024) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("file.size.invalid"));
                    return res;
                }
                FileDTO fileDTO = new FileDTO();
                fileDTO.setFile(fileImage);
                fileDTO.setBtsStationId(btsStationId);
                fileDTO.setFileName(btsStationId + "_"
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'_'HHmmssSSS")) + "_"
                        + index++ + extension);
                listImageDTO.add(fileDTO);
            }
            FileDTO result = fileUploadService.uploadFile(file,listImageDTO.get(0), btsStationId, type);
//            System.out.println(result);
            if (!StringUtils.isStringNullOrEmpty(result)) {
                res.setData(result);
            }
            res.setDescription(r.getResourceMessage("upload.file.success"));
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
            // trace log
//            TraceLogs traceLogEx = TraceLogs.builder().traceId(traceID).content(e.toString())
//                    .logTime(LocalDateTime.now()).updateBy("SYSTEM").errorCode("2").functionName(functionName).build();
//            DataUtils.traceLogs.add(traceLogEx);
            // end trace log
        }
        return res;
    }

    @PostMapping(value = "/getFile")
    public ExecutionResult getFile(@RequestHeader("Accept-Language") String language,
                                    @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);

        try {

//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDetailDTO()) ||
//                    StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDetailDTO().getConstructionDetailId())){
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("construction.item.info.null"));
//                return res;
//            }
//            List<ConstructionDetail> constructionDetailList = constructionService.
//                    getListConstructionDetailById(commonInputDTO.getConstructionDetailDTO().getConstructionDetailId());
//            if (constructionDetailList.size() == 0) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("construction.item.info.not.exist"));
//                return res;
//            }
            FileDTO fileDTO = fileUploadService.getFileInfo(commonInputDTO.getFilePath());
            res.setData(fileDTO);
            res.setDescription(Constant.EXECUTION_MESSAGE.OK);

        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

}
