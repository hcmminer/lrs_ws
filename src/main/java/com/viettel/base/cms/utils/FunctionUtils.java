package com.viettel.base.cms.utils;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.BTSStationDTO;
import com.viettel.base.cms.dto.CommonInputDTO;
import com.viettel.base.cms.dto.ConstructionDTO;
import com.viettel.base.cms.dto.ConstructionItemDTO;
import com.viettel.base.cms.model.*;
import com.viettel.base.cms.service.BTSStationService;
import com.viettel.base.cms.service.ConstructionService;
import com.viettel.base.cms.service.LocationService;
import com.viettel.base.cms.service.OptionSetValueService;
import com.viettel.vfw5.base.dto.ExecutionResult;
import com.viettel.vfw5.base.utils.ResourceBundle;
import com.viettel.vfw5.base.utils.StringUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.viettel.base.cms.common.Constant.*;

@Component
public class FunctionUtils {
    @Autowired
    ConstructionService constructionService;

    @Autowired
    BTSStationService btsStationService;

    @Autowired
    private static Environment env;

    private static int timeout = 5000;

    private static String linkService = "";

    public static ExecutionResult validateNullInputConstruction(ConstructionDTO constructionDTO, String language, int actionCode) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        if (StringUtils.isStringNullOrEmpty(constructionDTO)) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("construction.info.null"));
            return res;
        }
        if (Constant.ACTION.UPDATE_CONSTRUCTION == actionCode){
            if (StringUtils.isStringNullOrEmpty(constructionDTO.getConstructionId())){
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.info.null"));
                return res;
            }
        }
        if (StringUtils.isStringNullOrEmpty(constructionDTO.getProvinceCode())) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("province.code.null"));
            return res;
        }
        if (StringUtils.isStringNullOrEmpty(constructionDTO.getConstructionCode())) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("construction.code.null"));
            return res;
        }
        if (StringUtils.isStringNullOrEmpty(constructionDTO.getConstructionName())) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("construction.name.null"));
            return res;
        }
        if (StringUtils.isStringNullOrEmpty(constructionDTO.getPositionType())) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("position.type.null"));
            return res;
        }
        if (StringUtils.isStringNullOrEmpty(constructionDTO.getStationType())) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("station.type.null"));
            return res;
        }
        if (StringUtils.isStringNullOrEmpty(constructionDTO.getColumnType())) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("column.type.null"));
            return res;
        }
        if (StringUtils.isStringNullOrEmpty(constructionDTO.getColumnHeight())) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("column.height.null"));
            return res;
        }
        if (StringUtils.isStringNullOrEmpty(constructionDTO.getConstructionLong())) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("long.coordinate.null"));
            return res;
        }
        if (StringUtils.isStringNullOrEmpty(constructionDTO.getConstructionLat())) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("lat.coordinate.null"));
            return res;
        }
        if (StringUtils.isStringNullOrEmpty(constructionDTO.getDistrict())) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("district.null"));
            return res;
        }
        if (StringUtils.isStringNullOrEmpty(constructionDTO.getVillage())) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("village.null"));
            return res;
        }
        if (StringUtils.isStringNullOrEmpty(constructionDTO.getConstructionType())) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("construction.type.null"));
            return res;
        }
        if (StringUtils.isStringNullOrEmpty(constructionDTO.getDecisionDeploy())) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("decision.deploy.null"));
            return res;
        }
        return res;
    }

    public static ExecutionResult validateInputConstruction(ConstructionDTO constructionDTO, int actionCode,
                                                            String language,
                                                            ConstructionService constructionService,
                                                            LocationService locationService,
                                                            OptionSetValueService optionSetValueService) throws Exception {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            List<Construction> constructionList = constructionService.getListConstruction(constructionDTO, actionCode);
            if (constructionList.size() > 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.code.already.exist"));
                return res;
            }
            List<Province> provinceList = locationService.getListProvinceByCode(constructionDTO.getProvinceCode());
            if (provinceList.size() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.code.not.exist"));
                return res;
            }
            List<District> districtList = locationService.getListDistrictByIdAndProCode(constructionDTO.getProvinceCode(),
                    constructionDTO.getDistrict());
            if (districtList.size() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("district.code.not.exist"));
                return res;
            }
            String columnType = optionSetValueService.getOptionSetValueNameByValue(constructionDTO.getColumnType(), Constant.CMS_OPTION_SET.COLUMN_TYPE,
                    language);
            if (StringUtils.isNullOrEmpty(columnType)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("column.type.not.exist"));
                return res;
            }
            String positionType = optionSetValueService.getOptionSetValueNameByValue(constructionDTO.getPositionType(), Constant.CMS_OPTION_SET.POSITION_TYPE,
                    language);
            if (StringUtils.isNullOrEmpty(positionType)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("position.type.not.exist"));
                return res;
            }
            String stationType = optionSetValueService.getOptionSetValueNameByValue(constructionDTO.getStationType(), Constant.CMS_OPTION_SET.STATION_TYPE,
                    language);
            if (StringUtils.isNullOrEmpty(stationType)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("station.type.not.exist"));
                return res;
            }
            ConstructionType constructionType = constructionService.getConstructionTypeById(constructionDTO.getConstructionType());
            if (StringUtils.isStringNullOrEmpty(constructionType)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.type.not.exist"));
                return res;
            }
//            String constructionStatus = optionSetValueService.getOptionSetValueNameByValue(constructionDTO.getStatus(), "CONSTRUCTION_STATUS");
//            if (StringUtils.isNullOrEmpty(constructionStatus)) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("construction.status.not.exist"));
//                return res;
//            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return res;
    }

    public static ExecutionResult validateNullInputBTSStation(BTSStationDTO btsStationDTO, String language, BTSStationService btsStationService) throws Exception {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
        if (StringUtils.isStringNullOrEmpty(btsStationDTO)) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("bts.station.info.null"));
            return res;
        }
        List<BTSStation> btsStationList = btsStationService.getListBTSStation(btsStationDTO);
        if (btsStationList.size() > 0) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("bts.station.code.already.exist"));
            return res;
        }
        if (StringUtils.isStringNullOrEmpty(btsStationDTO.getSiteOnNims())) {
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("bts.site.of.nims.info.null"));
            return res;
        }
//        if (StringUtils.isStringNullOrEmpty(btsStationDTO.getSiteOnContract())) {
//            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//            res.setDescription(r.getResourceMessage("bts.contract.no.info.null"));
//            return res;
//        }
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
            if (StringUtils.isStringNullOrEmpty(btsStationDTO.getSiteOnNims())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.site.of.nims.info.null"));
                return res;
            } else {
                String responseSMS = checkSiteOnNims(btsStationDTO.getSiteOnNims());
                boolean checkReturn = responseSMS.contains("<status>0</status>");
                if (checkReturn) {
                    boolean checkRerult = responseSMS.contains("<totalDataJson>0</totalDataJson>");
                    if (!checkRerult) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
                    } else {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.site.on.nims.not.exits"));
                        return res;
                    }
                } else {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("bts.site.on.nims.not.exits"));
                    return res;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return res;
    }

    public static ExecutionResult validateInputBTSStation(BTSStationDTO btsStationDTO,
                                                          String language,
                                                          BTSStationService btsStationService,
                                                          LocationService locationService,
                                                          OptionSetValueService optionSetValueService) throws Exception {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            List<BTSStation> constructionList = btsStationService.getListBTSStation(btsStationDTO);
            if (constructionList.size() > 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.station.code.already.exist"));
                return res;
            }
            List<BTSStation> constructionList1 = btsStationService.getListBTSStation(btsStationDTO);
            if (constructionList1.size() > 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.code.not.exist"));
                return res;
            }
//            String constructionStatus = optionSetValueService.getOptionSetValueNameByValue(constructionDTO.getStatus(), "CONSTRUCTION_STATUS");
//            if (StringUtils.isNullOrEmpty(constructionStatus)) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("construction.status.not.exist"));
//                return res;
//            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return res;
    }

    public static ExecutionResult validateNullListConstructionItem(CommonInputDTO commonInputDTO, String language, ConstructionService constructionService) throws Exception {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getListConstructionItemDTO())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("list.construction.info.null"));
                return res;
            }
            List<ConstructionItemDTO> list = commonInputDTO.getConstructionDTO().getListConstructionItemDTO();
            List<ConstructionItemDTO> listChosen = new ArrayList<>();
            for (ConstructionItemDTO ci :
                    list) {
                if (StringUtils.isStringNullOrEmpty(ci.getConstructionItemId()) ||
                        StringUtils.isStringNullOrEmpty(ci.getChosen())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("construction.item.info.null"));
                    return res;
                }
                if (constructionService.getListConstructionItemById(ci.getConstructionItemId()).size() == 0){
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("construction.item.info.not.exist"));
                    return res;
                }
                if (Constant.CONSTRUCTION_ITEM_STATUS.CHOSEN == ci.getChosen())
                    listChosen.add(ci);
            }
            if (listChosen.size() == 0){
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("list.construction.item.info.null"));
                return res;
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public static String localDateTimeToString(LocalDateTime date, String format) {
        String formattedDate = date.format(DateTimeFormatter.ofPattern(format));
        return formattedDate;
    }


    public static LocalDateTime stringToLocalDateTme(String date) {
        date += " 00:00:00";
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(DATETIME_PATTERN_DMY);
        LocalDateTime dateTime = LocalDateTime.parse(date, formatter);
        return dateTime;
    }

    public static ActionLog saveActionLog(Staff staff, String description, Long idActionLog, Long rowId) {
        return ActionLog.builder().actionLogId(idActionLog)
                .staffId(staff.getStaffId())
                .isdn(staff.getTelNumber())
                .createDatetime(LocalDateTime.now())
                .ip(StringUtils.getIp())
                .description(description)
                .rowId(rowId)
                .build();
    }

    public static ActionLogDetail saveActionLogDetail(String tableName, Long rowId, String colName, String oldValue, String newValue, Long idActionLog, Long idActionLogDetailUser) {
        return ActionLogDetail.builder().actionLogDetalId(idActionLogDetailUser)
                .actionLogId(idActionLog)
                .tableName(tableName)
                .rowId(rowId)
                .colName(colName)
                .oldValue(oldValue)
                .newValue(newValue)
                .createDatetime(LocalDateTime.now())
                .build();
    }

    public static String checkSiteOnNims(String stationCode) {
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
            httpclient.executeMethod(post);
            responseBody = post.getResponseBodyAsString();
            System.out.println(responseBody);
        } catch (Exception ex) {
            System.out.println("Loi SOAP call U-Point" + ex.getMessage());
            ex.printStackTrace();
        }
        return responseBody;
    }



}
