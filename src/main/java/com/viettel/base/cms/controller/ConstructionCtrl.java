package com.viettel.base.cms.controller;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.*;
import com.viettel.base.cms.model.Construction;
import com.viettel.base.cms.model.ConstructionDetail;
import com.viettel.base.cms.model.OptionSetValue;
import com.viettel.base.cms.repo.ConstructionRepo;
import com.viettel.base.cms.service.*;
import com.viettel.base.cms.utils.FunctionUtils;
import com.viettel.base.cms.utils.SmsUtils;
import com.viettel.security.PassTranformer;
import com.viettel.vfw5.base.dto.ExecutionResult;
import com.viettel.vfw5.base.utils.DataUtils;
import com.viettel.vfw5.base.utils.ResourceBundle;
import com.viettel.vfw5.base.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class ConstructionCtrl {

    @Autowired
    private Environment env;

    @Autowired
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private EntityManager cms;

    @Autowired
    private OptionSetValueService optionSetValueService;

    @Autowired
    private ConstructionService constructionService;

    @Autowired
    private UserService userService;

    @Autowired
    private LocationService locationService;

    private String fileExchangeConfig = "exchange_client.cfg";

    @Autowired
    ConstructionRepo constructionRepo;

    @PostMapping(value = "/getListColumnType")
    public ExecutionResult getListColumnType(@RequestHeader("Accept-Language") String language) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        String isdn = null;
        // trace log
//        Long traceID = DataUtils.getSequence(upoint, "trace_logs_seq");
//        String funcitionName = "getListConstructionType";

        try {

            List<OptionSetValueDTO> optionSetValueDTOS = optionSetValueService.getListByOptionSet("COLUMN_TYPE", language, null);

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

    @PostMapping(value = "/getListPositionType")
    public ExecutionResult getListPositionType(@RequestHeader("Accept-Language") String language) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        String isdn = null;
        // trace log
//        Long traceID = DataUtils.getSequence(upoint, "trace_logs_seq");
//        String funcitionName = "getListConstructionType";

        try {

            List<OptionSetValueDTO> optionSetValueDTOS = optionSetValueService.getListByOptionSet("POSITION_TYPE", language, null);

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

    @PostMapping(value = "/getListStationType")
    public ExecutionResult getListStationType(@RequestHeader("Accept-Language") String language) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        String isdn = null;
        // trace log
//        Long traceID = DataUtils.getSequence(upoint, "trace_logs_seq");
//        String funcitionName = "getListConstructionType";

        try {

            List<OptionSetValueDTO> optionSetValueDTOS = optionSetValueService.getListByOptionSet("STATION_TYPE", language, null);

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

    @PostMapping(value = "/getListConstructionStatus")
    public ExecutionResult getListConstructionStatus(@RequestHeader("Accept-Language") String language, @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        String isdn = null;
        // trace log
//        Long traceID = DataUtils.getSequence(upoint, "trace_logs_seq");
//        String funcitionName = "getListConstructionType";

        try {
            commonInputDTO.setAppCode("GSCT");
            String roleCode = userService.getUserRole(commonInputDTO);
            List<OptionSetValueDTO> optionSetValueDTOS = optionSetValueService.getListByOptionSet(Constant.CMS_OPTION_SET.CONSTRUCTION_STATUS, language, roleCode);

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

    @PostMapping(value = "/getListConstructionType")
    public ExecutionResult getListConstructionType(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        String isdn = null;
        // trace log
//        Long traceID = DataUtils.getSequence(upoint, "trace_logs_seq");
//        String funcitionName = "getListConstructionType";

        try {

            List<ConstructionTypeDTO> constructionItemDTOS = constructionService.getListConstructionType(language);

            res.setData(constructionItemDTOS);
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

    @PostMapping(value = "/getListConstructionItem")
    public ExecutionResult getListConstructionItem(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        String isdn = null;
        // trace log
//        Long traceID = DataUtils.getSequence(upoint, "trace_logs_seq");
//        String funcitionName = "getListConstructionType";

        try {

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.null"));
                return res;
            }

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO())
                    && StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getConstructionId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.info.null"));
                return res;
            }
            List<ConstructionItemDTO> constructionItemDTOS = constructionService.getListConstructionItem(commonInputDTO.getConstructionDTO().getConstructionId(), r);

            res.setData(constructionItemDTOS);
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

    @PostMapping(value = "/searchConstruction")
    public ExecutionResult searchConstruction(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO())) {
                if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getConstructionCode())) {
                    commonInputDTO.getConstructionDTO().setConstructionCode(commonInputDTO.getConstructionDTO().getConstructionCode().trim());
                }
                if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getConstructionName())) {
                    commonInputDTO.getConstructionDTO().setConstructionName(commonInputDTO.getConstructionDTO().getConstructionName().trim());
                }
                if (!StringUtils.isNullOrEmpty(commonInputDTO.getConstructionDTO().getProvinceCode())) {
                    commonInputDTO.getConstructionDTO().setProvinceCode(commonInputDTO.getConstructionDTO().getProvinceCode().trim());
                }
            }
            commonInputDTO.setAppCode("GSCT");
            String userRole = "CMS_PROV_VICE_PRESIDENT";
            List<ConstructionDTO> constructionDTOS = constructionService.searchConstruction(commonInputDTO,
                    userRole, language, r);
            res.setData(constructionDTOS);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/searchConstructionForCM")
    public ExecutionResult searchConstructionForCM(@RequestHeader("Accept-Language") String language,
                                              @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO())) {
                if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getConstructionCode())) {
                    commonInputDTO.getConstructionDTO().setConstructionCode(commonInputDTO.getConstructionDTO().getConstructionCode().trim());
                }
                if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getConstructionName())) {
                    commonInputDTO.getConstructionDTO().setConstructionName(commonInputDTO.getConstructionDTO().getConstructionName().trim());
                }
                if (!StringUtils.isNullOrEmpty(commonInputDTO.getConstructionDTO().getProvinceCode())) {
                    commonInputDTO.getConstructionDTO().setProvinceCode(commonInputDTO.getConstructionDTO().getProvinceCode().trim());
                }
            }
            commonInputDTO.setAppCode("GSCT");
            List<ConstructionDTO> constructionDTOS = constructionService.searchConstructionForCM(commonInputDTO,
                    userService.getUserRole(commonInputDTO), language, r);
            res.setData(constructionDTOS);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/createConstruction")
    public ExecutionResult createConstruction(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            ExecutionResult tempRes = FunctionUtils.validateNullInputConstruction(commonInputDTO.getConstructionDTO(), language, Constant.ACTION.CREATE_CONSTRUCTION);
            if (Constant.EXECUTION_ERROR.ERROR.equals(tempRes.getErrorCode())) {
                return tempRes;
            }
            commonInputDTO.setAppCode("GSCT");
            String roleCode = userService.getUserRole(commonInputDTO);
            if (!"CMS_CORP_STAFF".equals(roleCode)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("not.allowed.create.construction"));
                return res;
            }

            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getConstructionCode())) {
                commonInputDTO.getConstructionDTO().setConstructionCode(commonInputDTO.getConstructionDTO().getConstructionCode().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getConstructionName())) {
                commonInputDTO.getConstructionDTO().setConstructionName(commonInputDTO.getConstructionDTO().getConstructionName().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getConstructionLong())) {
                commonInputDTO.getConstructionDTO().setConstructionLong(commonInputDTO.getConstructionDTO().getConstructionLong().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getConstructionLat().trim())) {
                commonInputDTO.getConstructionDTO().setConstructionLat(commonInputDTO.getConstructionDTO().getConstructionLat().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getColumnHeight())) {
                commonInputDTO.getConstructionDTO().setColumnHeight(commonInputDTO.getConstructionDTO().getColumnHeight().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getVillage())) {
                commonInputDTO.getConstructionDTO().setVillage(commonInputDTO.getConstructionDTO().getVillage().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getNetwork())) {
                commonInputDTO.getConstructionDTO().setNetwork(commonInputDTO.getConstructionDTO().getNetwork().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getVendor())) {
                commonInputDTO.getConstructionDTO().setVendor(commonInputDTO.getConstructionDTO().getVendor().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getBand())) {
                commonInputDTO.getConstructionDTO().setBand(commonInputDTO.getConstructionDTO().getBand().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getAntenHeight())) {
                commonInputDTO.getConstructionDTO().setAntenHeight(commonInputDTO.getConstructionDTO().getAntenHeight().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getAzimuth())) {
                commonInputDTO.getConstructionDTO().setAzimuth(commonInputDTO.getConstructionDTO().getAzimuth().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getTilt())) {
                commonInputDTO.getConstructionDTO().setTilt(commonInputDTO.getConstructionDTO().getTilt().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getTrxMode())) {
                commonInputDTO.getConstructionDTO().setTrxMode(commonInputDTO.getConstructionDTO().getTrxMode().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getStartPoint())) {
                commonInputDTO.getConstructionDTO().setStartPoint(commonInputDTO.getConstructionDTO().getStartPoint().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getEndPoint())) {
                commonInputDTO.getConstructionDTO().setEndPoint(commonInputDTO.getConstructionDTO().getEndPoint().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getCableRoute())) {
                commonInputDTO.getConstructionDTO().setCableRoute(commonInputDTO.getConstructionDTO().getCableRoute().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getDistanceCable())) {
                commonInputDTO.getConstructionDTO().setDistanceCable(commonInputDTO.getConstructionDTO().getDistanceCable().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getDecisionDeploy())) {
                commonInputDTO.getConstructionDTO().setDecisionDeploy(commonInputDTO.getConstructionDTO().getDecisionDeploy().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getNote())) {
                commonInputDTO.getConstructionDTO().setNote(commonInputDTO.getConstructionDTO().getNote().trim());
            }

            ConstructionDTO tempConstruction = commonInputDTO.getConstructionDTO();
            tempConstruction.setStatus(Long.valueOf(Constant.CONSTRUCTION.STATUS_CREATED));
            tempRes = FunctionUtils.validateInputConstruction(tempConstruction, Constant.ACTION.CREATE_CONSTRUCTION,
                    language,
                    constructionService,
                    locationService,
                    optionSetValueService);
            if (Constant.EXECUTION_ERROR.ERROR.equals(tempRes.getErrorCode())) {
                return tempRes;
            }
            commonInputDTO.getConstructionDTO().setCreatedBy(commonInputDTO.getUserName().split("----")[0]);
            constructionService.createConstruction(commonInputDTO.getConstructionDTO(), Constant.ACTION.CREATE_CONSTRUCTION);
            res.setDescription(r.getResourceMessage("create.construction.success"));
            return res;

        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/getConstructionDetail")
    public ExecutionResult getConstructionDetail(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            ConstructionDTO constructionDTO = new ConstructionDTO();
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO())
                    || StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getConstructionId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.info.null"));
                return res;
            }
            commonInputDTO.setAppCode("GSCT");
            String roleCode = userService.getUserRole(commonInputDTO);
//            String roleCode = Constant.CMS_ROLES.CMS_PROV_INFA_STAFF;
            constructionDTO = constructionService.getConstructionDetail(commonInputDTO.getConstructionDTO(), roleCode, language, r);
            res.setData(constructionDTO);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/getItemListDetail")
    public ExecutionResult getItemListDetail(@RequestHeader("Accept-Language") String language,
                                                 @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            List<ConstructionDetailDTO> constructionDetailDTO = new ArrayList<>();
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.info.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionItemId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.info.null"));
                return res;
            }
            commonInputDTO.setAppCode("GSCT");
            String roleCode = userService.getUserRole(commonInputDTO);

            constructionDetailDTO = constructionService.getListItemDetailDTO(commonInputDTO.getConstructionId(), roleCode, language, r, commonInputDTO.getConstructionItemId());
            res.setData(constructionDetailDTO);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/createConstructionByFile")
    public ExecutionResult createConstructionByFile(@RequestHeader("Accept-Language") String locate,
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

            if (StringUtils.isNullOrEmpty(eun)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }

            CommonInputDTO commonInputDTO = new CommonInputDTO();
            commonInputDTO.setUserName(userName);
            commonInputDTO.setAppCode("GSCT");
            String roleCode = userService.getUserRole(commonInputDTO);

            if (!Constant.CMS_ROLES.CMS_CORP_STAFF.equals(roleCode)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("not.allowed.create.construction"));
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
            ExecutionResult tempRes = constructionService.getConstructionFromFile(fileCreateRequest, userName, locate);
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

    @PostMapping(value = "/updateConstruction")
    public ExecutionResult updateConstruction(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            ExecutionResult tempRes = FunctionUtils.validateNullInputConstruction(commonInputDTO.getConstructionDTO(), language, Constant.ACTION.UPDATE_CONSTRUCTION);
            if (Constant.EXECUTION_ERROR.ERROR.equals(tempRes.getErrorCode())) {
                return tempRes;
            }
            commonInputDTO.setAppCode("GSCT");
            String roleCode = userService.getUserRole(commonInputDTO);
            if (!Constant.CMS_ROLES.CMS_CORP_STAFF.equals(roleCode)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("not.allowed.update.construction"));
                return res;
            }

            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getConstructionCode())) {
                commonInputDTO.getConstructionDTO().setConstructionCode(commonInputDTO.getConstructionDTO().getConstructionCode().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getConstructionName())) {
                commonInputDTO.getConstructionDTO().setConstructionName(commonInputDTO.getConstructionDTO().getConstructionName().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getConstructionLong())) {
                commonInputDTO.getConstructionDTO().setConstructionLong(commonInputDTO.getConstructionDTO().getConstructionLong().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getConstructionLat().trim())) {
                commonInputDTO.getConstructionDTO().setConstructionLat(commonInputDTO.getConstructionDTO().getConstructionLat().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getColumnHeight())) {
                commonInputDTO.getConstructionDTO().setColumnHeight(commonInputDTO.getConstructionDTO().getColumnHeight().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getVillage())) {
                commonInputDTO.getConstructionDTO().setVillage(commonInputDTO.getConstructionDTO().getVillage().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getNetwork())) {
                commonInputDTO.getConstructionDTO().setNetwork(commonInputDTO.getConstructionDTO().getNetwork().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getVendor())) {
                commonInputDTO.getConstructionDTO().setVendor(commonInputDTO.getConstructionDTO().getVendor().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getBand())) {
                commonInputDTO.getConstructionDTO().setBand(commonInputDTO.getConstructionDTO().getBand().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getAntenHeight())) {
                commonInputDTO.getConstructionDTO().setAntenHeight(commonInputDTO.getConstructionDTO().getAntenHeight().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getAzimuth())) {
                commonInputDTO.getConstructionDTO().setAzimuth(commonInputDTO.getConstructionDTO().getAzimuth().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getTilt())) {
                commonInputDTO.getConstructionDTO().setTilt(commonInputDTO.getConstructionDTO().getTilt().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getTrxMode())) {
                commonInputDTO.getConstructionDTO().setTrxMode(commonInputDTO.getConstructionDTO().getTrxMode().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getStartPoint())) {
                commonInputDTO.getConstructionDTO().setStartPoint(commonInputDTO.getConstructionDTO().getStartPoint().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getEndPoint())) {
                commonInputDTO.getConstructionDTO().setEndPoint(commonInputDTO.getConstructionDTO().getEndPoint().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getCableRoute())) {
                commonInputDTO.getConstructionDTO().setCableRoute(commonInputDTO.getConstructionDTO().getCableRoute().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getDistanceCable())) {
                commonInputDTO.getConstructionDTO().setDistanceCable(commonInputDTO.getConstructionDTO().getDistanceCable().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getDecisionDeploy())) {
                commonInputDTO.getConstructionDTO().setDecisionDeploy(commonInputDTO.getConstructionDTO().getDecisionDeploy().trim());
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getNote())) {
                commonInputDTO.getConstructionDTO().setNote(commonInputDTO.getConstructionDTO().getNote().trim());
            }

            ConstructionDTO temCon = commonInputDTO.getConstructionDTO();
            temCon.setStatus(Long.valueOf(Constant.CONSTRUCTION.STATUS_CREATED));
            tempRes = FunctionUtils.validateInputConstruction(temCon, Constant.ACTION.UPDATE_CONSTRUCTION,
                    language,
                    constructionService,
                    locationService,
                    optionSetValueService);
            if (Constant.EXECUTION_ERROR.ERROR.equals(tempRes.getErrorCode())) {
                return tempRes;
            }
            List<ConstructionDetail> constructionDetailList = constructionService.getListConstructionDetailByConstructionId(
                    commonInputDTO.getConstructionDTO().getConstructionId(), Constant.VALIDATE_CONSTRUCTION_STATUS.VALIDATE);
            if (constructionDetailList.size() > 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.is.active.unupdatable"));
                return res;
//                for (ConstructionDetail cd
//                        : constructionDetailList) {
//                    if (!StringUtils.isStringNullOrEmpty(cd.getStartDate())) {
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("construction.is.active.unupdatable"));
//                        return res;
//                    }
//                }
            }
            Construction construction = constructionRepo.getById(commonInputDTO.getConstructionDTO().getConstructionId());
            if (construction == null || !construction.getStatus().equals(1L)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.is.remove.active.unupdatable"));
                return res;
            }
            commonInputDTO.getConstructionDTO().setLastModifiedBy(commonInputDTO.getUserName().split("----")[0]);

            constructionService.updateConstruction(commonInputDTO.getConstructionDTO());
            res.setDescription(r.getResourceMessage("update.construction.success"));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/deleteConstruction")
    public ExecutionResult deleteConstruction(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            commonInputDTO.setAppCode("GSCT");
            String roleCode = userService.getUserRole(commonInputDTO);
            if (!"CMS_CORP_STAFF".equals(roleCode)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("not.allowed.delete.construction"));
                return res;
            }

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO())
                    || StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getConstructionId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.info.null"));
                return res;
            }

            commonInputDTO.getConstructionDTO().setStatus(Long.valueOf(Constant.CONSTRUCTION.STATUS_CREATED));
            List<Construction> temp = constructionService.getListConstruction(commonInputDTO.getConstructionDTO(), Constant.ACTION.CHECK_CONSTRUCTION);
            if (temp.size() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.not.deletable"));
                return res;
            }

            commonInputDTO.getConstructionDTO().setStatus(Long.valueOf(Constant.CONSTRUCTION.STATUS_DELETE));
            commonInputDTO.getConstructionDTO().setLastModifiedBy(commonInputDTO.getUserName().split("----")[0]);
            commonInputDTO.getConstructionDTO().setLastModifiedDate(LocalDateTime.now());
            constructionService.deleteConstruction(commonInputDTO.getConstructionDTO());
            res.setDescription(r.getResourceMessage("delete.construction.success"));
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/addConstructionItem")
    public ExecutionResult addConstructionItem(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            System.out.println(commonInputDTO.getConstructionDTO());
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            commonInputDTO.setAppCode("GSCT");
            String roleCode = userService.getUserRole(commonInputDTO);
            if (!"CMS_CORP_STAFF".equals(roleCode)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("not.allowed.add.construction.item"));
                return res;
            }

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO())
                    && StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getConstructionId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.info.null"));
                return res;
            }
            ExecutionResult tempRes = FunctionUtils.validateNullListConstructionItem(commonInputDTO, language, constructionService);
            if (Constant.EXECUTION_ERROR.ERROR.equals(tempRes.getErrorCode())) {
                return tempRes;
            }
            Construction construction = constructionRepo.getById(commonInputDTO.getConstructionDTO().getConstructionId());
            if (construction == null || !construction.getStatus().equals(1L)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.not.add.item"));
                return res;
            }
            constructionService.addConstructionItem(commonInputDTO);
            res.setDescription(r.getResourceMessage("add.construction.item.success"));

        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/assignConstruction")
    public ExecutionResult assignConstruction(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            commonInputDTO.setAppCode("GSCT");
            String roleCode = userService.getUserRole(commonInputDTO);
            if (!"CMS_CORP_STAFF".equals(roleCode)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("not.allowed.assign.construction"));
                return res;
            }

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO())
                    || StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getConstructionId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.info.null"));
                return res;
            }
            Construction construction = constructionRepo.getById(commonInputDTO.getConstructionDTO().getConstructionId());
            if (construction == null) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.not.exists"));
                return res;
            }

            commonInputDTO.getConstructionDTO().setStatus(Long.valueOf(Constant.CONSTRUCTION.STATUS_CREATED));
            List<Construction> temp = constructionService.getListConstruction(commonInputDTO.getConstructionDTO(), Constant.ACTION.CHECK_CONSTRUCTION);
            if (temp.size() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.not.assignable"));
                return res;
            }
            commonInputDTO.getConstructionDTO().setConstructionName(construction.getConstructionName());
            commonInputDTO.getConstructionDTO().setConstructionCode(construction.getConstructionCode());
            commonInputDTO.setLanguage(language);
            constructionService.assignConstruction(commonInputDTO);

            res.setDescription(r.getResourceMessage("assign.construction.item.success"));

        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/getComboBoxData")
    public ExecutionResult getComboBoxData(@RequestHeader("Accept-Language") String language,
            @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            commonInputDTO.setAppCode("GSCT");
            String roleCode = userService.getUserRole(commonInputDTO);
            List<ProvinceDTO> provinceDTOList = locationService.getListProvince();
            List<OptionSetValueDTO> constructionStatusList = optionSetValueService.getListByOptionSet(Constant.CMS_OPTION_SET.CONSTRUCTION_STATUS, language, roleCode);
            List<OptionSetValueDTO> stationTypeList = optionSetValueService.getListByOptionSet(Constant.CMS_OPTION_SET.STATION_TYPE, language, null);
            List<OptionSetValueDTO> columnTypeList = optionSetValueService.getListByOptionSet(Constant.CMS_OPTION_SET.COLUMN_TYPE, language, null);
            List<OptionSetValueDTO> positionTypeList = optionSetValueService.getListByOptionSet(Constant.CMS_OPTION_SET.POSITION_TYPE, language, null);
            List<ConstructionTypeDTO> constructionTypeDTOList = constructionService.getListConstructionType(language);
            ComboBoxData comboBoxData = new ComboBoxData();
            comboBoxData.setProvinceDTOList(provinceDTOList);
            comboBoxData.setListConstructionStatus(constructionStatusList);
            comboBoxData.setConstructionTypeDTOList(constructionTypeDTOList);
            comboBoxData.setStationTypeList(stationTypeList);
            comboBoxData.setColumnTypeList(columnTypeList);
            comboBoxData.setPositionTypeList(positionTypeList);
            res.setData(comboBoxData);
            res.setDescription(Constant.EXECUTION_MESSAGE.OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }
}
