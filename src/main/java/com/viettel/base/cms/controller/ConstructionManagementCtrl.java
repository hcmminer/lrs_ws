package com.viettel.base.cms.controller;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.*;
import com.viettel.base.cms.model.*;
import com.viettel.base.cms.repo.ConstructionDetailRepo;
import com.viettel.base.cms.repo.ConstructionItemRepo;
import com.viettel.base.cms.repo.ConstructionRepo;
import com.viettel.base.cms.repo.ImageRepo;
import com.viettel.base.cms.service.*;
import com.viettel.base.cms.utils.DataUtil;
import com.viettel.base.cms.utils.FunctionUtils;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class ConstructionManagementCtrl {

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
    private ConstructionManagementService constructionManagementService;

    @Autowired
    private UserService userService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    ConstructionRepo constructionRepo;

    @Autowired
    ConstructionDetailRepo constructionDetailRepo;

    @Autowired
    ConstructionItemRepo constructionItemRepo;

    @Autowired
    ImageRepo imageRepo;

    @PostMapping(value = "/updateConstructionStartDate")
    public ExecutionResult updateConstructionStartDate(@RequestHeader("Accept-Language") String language,
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
            commonInputDTO.setAppCode("GSCT");
            String roleCode = userService.getUserRole(commonInputDTO);
            System.out.println("Role code updateConstructionStartDate : " + roleCode);
            if (!Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(roleCode)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("not.allowed.update.construction.startdate"));
                return res;
            }

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDTO().getStartDateStr())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.startdate.null"));
                return res;
            }

            Construction tempConstruction = constructionService.getConstructionById(commonInputDTO.getConstructionDTO().getConstructionId());
            if (!(Constant.CONSTRUCTION.STATUS_ASSIGNED == tempConstruction.getStatus()) &&
                    !(Constant.CONSTRUCTION.STATUS_ACTIVE == tempConstruction.getStatus())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.startdate.not.updatable"));
                return res;
            }

            String provinceCode = userService.getUserProvinceCode(commonInputDTO.getUserName().split("----")[0]);
            if (!tempConstruction.getProvinceCode().equals(provinceCode)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.not.in.your.area"));
                return res;
            }

            commonInputDTO.getConstructionDTO().setStartDate(FunctionUtils.stringToLocalDateTme(commonInputDTO.getConstructionDTO().getStartDateStr()));
            if (commonInputDTO.getConstructionDTO().getStartDate().isBefore(LocalDate.now().atTime(0, 0, 0))) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.start.date.before.now"));
                return res;
            }

            List<ConstructionDetailDTO> constructionDetailDTOList = constructionService.getListConstructionDetailDTO(commonInputDTO.getConstructionDTO().getConstructionId(),
                    roleCode, language, r);
            for (ConstructionDetailDTO cdDTO :
                    constructionDetailDTOList) {
                if (!(Constant.CONSTRUCTION_DETAIL.STATUS_CREATED == cdDTO.getStatus())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("construction.item.is.active"));
                    return res;

                }
            }

//            tempConstructionDTO.setStatus(Long.valueOf(Constant.CONSTRUCTION.STATUS_ASSIGNED));
//            tempConstructionDTO.setProvinceCode(userService.getUserProvinceCode(commonInputDTO.getUserName().split("----")[0]));
//            List<Construction> temp = constructionService.getListConstruction(tempConstructionDTO, Constant.ACTION.CHECK_CONSTRUCTION_STARTDATE);
//            if (temp.size() == 0) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("construction.startdate.not.updatable"));
//                return res;
//            }

            constructionManagementService.updateConstructionStartDate(commonInputDTO);
            res.setDescription(r.getResourceMessage("update.construction.startdate.success"));

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/updateConstructionItemStartDate")
    public ExecutionResult updateConstructionItemStartDate(@RequestHeader("Accept-Language") String language,
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

//            String roleCode = userService.getUserRole(commonInputDTO);
            String roleCode = Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT;
            if (!Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(roleCode)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("not.allowed.update.construction.item.startdate"));
                return res;
            }

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDetailDTO().getStartDateStr())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.startdate.null"));
                return res;
            }

            commonInputDTO.getConstructionDetailDTO().setStartDate(FunctionUtils.stringToLocalDateTme(commonInputDTO.getConstructionDetailDTO().getStartDateStr()));
            if (commonInputDTO.getConstructionDetailDTO().getStartDate().isBefore(LocalDate.now().atTime(0, 0, 0))) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.start.date.before.now"));
                return res;
            }

            ConstructionDetail constructionDetail = constructionDetailRepo.getById(commonInputDTO.getConstructionDetailDTO().getConstructionDetailId());
            if (StringUtils.isStringNullOrEmpty(constructionDetail)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("constructionDetail.not.exists"));
                return res;
            }

            Construction construction = constructionRepo.getById(constructionDetail.getConstructionId());
            if (StringUtils.isStringNullOrEmpty(construction)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.not.exists"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(constructionDetail.getParentId()) && (Constant.CONSTRUCTION_DETAIL.STATUS_ACTIVE == constructionDetail.getStatus())){
                if (!StringUtils.isStringNullOrEmpty(constructionDetail.getStartDate())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("construction.detail.is.active"));
                    return res;
                }
            } else if ((Constant.CONSTRUCTION_DETAIL.STATUS_CREATED == constructionDetail.getStatus()) || (Constant.CONSTRUCTION_DETAIL.STATUS_ACTIVE == constructionDetail.getStatus())) {
                System.out.println("Trạng thái được phép cập nhật");
            } else{
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.detail.is.active"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(construction.getStartDate())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.start.date.null"));
                return res;
            }

            if (construction.getStartDate().isAfter(commonInputDTO.getConstructionDetailDTO().getStartDate())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.startdate.not.valid"));
                return res;
            }

            ConstructionItem constructionItem = constructionItemRepo.getById(constructionDetail.getConstructionItemId());

            if (StringUtils.isStringNullOrEmpty(constructionItem)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("constructionItem.not.exists"));
                return res;
            }


            ConstructionDetailDTO tempCD = commonInputDTO.getConstructionDetailDTO();
            ConstructionDTO tempConstructionDTO = new ConstructionDTO();
            tempConstructionDTO.setConstructionId(construction.getConstructionId());
            tempConstructionDTO.setStatus(Long.valueOf(Constant.CONSTRUCTION.STATUS_ACTIVE));
            tempConstructionDTO.setProvinceCode(userService.getUserProvinceCode(commonInputDTO.getUserName().split("----")[0]));
            List<Construction> temp = constructionService.getListConstruction(tempConstructionDTO, Constant.ACTION.CHECK_CONSTRUCTION_STARTDATE);
            if (temp.size() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.startdate.not.updatable"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(temp.get(0).getStartDate())
                    || temp.get(0).getStartDate().isAfter(commonInputDTO.getConstructionDetailDTO().getStartDate())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.startdate.not.valid"));
                return res;
            }

            if(!StringUtils.isStringNullOrEmpty(constructionDetail.getParentId())){
                ConstructionDetail constructionDetailParent = constructionManagementService.getConstructionDetailParent(constructionDetail.getConstructionId(), constructionDetail.getParentId());
                if (StringUtils.isStringNullOrEmpty(constructionDetailParent)){
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("constructionItem.not.exists"));
                    return res;
                }else {
                    if (StringUtils.isStringNullOrEmpty(constructionDetailParent.getStartDate())){
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("item.parent.startdate.null"));
                        return res;
                    }
                    if (DataUtil.compareDate(DataUtils.getStringDateTime(constructionDetailParent.getStartDate()), DataUtils.getStringDateTime(commonInputDTO.getConstructionDetailDTO().getStartDate()), "dd/MM/yyyy")){
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("item.parent.startdate.invalid"));
                        return res;
                    }
                }
            }
            /*
            validate constructionDetailDTO (startDate > startDate cua construction)
             */
//            commonInputDTO.getConstructionDetailDTO().setStartDate(LocalDateTime.now());
            commonInputDTO.getConstructionDetailDTO().setStatus(Long.valueOf(Constant.CONSTRUCTION_DETAIL.STATUS_ACTIVE));
            commonInputDTO.getConstructionDetailDTO().setStartBy(commonInputDTO.getUserName().split("----")[0]);
            ConstructionDTO constructionDTO = new ConstructionDTO();
            constructionDTO.setConstructionId(construction.getConstructionId());
            constructionDTO.setConstructionName(construction.getConstructionName());
            constructionDTO.setConstructionCode(construction.getConstructionCode());
            commonInputDTO.setConstructionDTO(constructionDTO);
            commonInputDTO.getConstructionDetailDTO().setName(r.getResourceMessage(constructionItem.getName()));
            commonInputDTO.setLanguage(language);
            constructionManagementService.updateConstructionItemStartDate(commonInputDTO);
            res.setDescription(r.getResourceMessage("update.construction.item.startdate.success"));

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/createApproveRequest")
    public ExecutionResult createApproveRequest(@RequestHeader("Accept-Language") String language,
                                                @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);

        try {
            Staff staff = userService.getStaffByUserName(commonInputDTO.getUserName().split("----")[0]);
            if (StringUtils.isNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            commonInputDTO.setAppCode("GSCT");
            String roleCode = userService.getUserRole(commonInputDTO);
            if (!Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleCode)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("not.allowed.create.approve.request"));
                return res;
            }

            if (commonInputDTO.getListConstructionDetail().size() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("list.construction.info.null"));
                return res;
            }

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getListConstructionDetail().get(0).getConstructionId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.info.null"));
                return res;
            }

            Long constructionId = commonInputDTO.getListConstructionDetail().get(0).getConstructionId();
            ConstructionDTO tempConstructionDTO = new ConstructionDTO();
            tempConstructionDTO.setConstructionId(constructionId);
            tempConstructionDTO.setStatus(Long.valueOf(Constant.CONSTRUCTION.STATUS_ACTIVE));
            tempConstructionDTO.setProvinceCode(userService.getUserProvinceCode(commonInputDTO.getUserName().split("----")[0]));
            List<Construction> temp = constructionService.getListConstruction(tempConstructionDTO, Constant.ACTION.CHECK_CONSTRUCTION_STARTDATE);
            if (temp.size() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.can.not.be.requested"));
                return res;
            }
            tempConstructionDTO.setConstructionName(temp.get(0).getConstructionName());
            tempConstructionDTO.setConstructionCode(temp.get(0).getConstructionCode());
            commonInputDTO.setLanguage(language);
            commonInputDTO.setConstructionDTO(tempConstructionDTO);
            List<ConstructionDetailDTO> constructionDetailDTOList = new ArrayList<>();
            for (ConstructionDetailDTO cd
                    : commonInputDTO.getListConstructionDetail()) {
                if ("true".equals(cd.getIsRequested())) {
//                    ConstructionDetailDTO tempCD = cd;
//                    ConstructionDTO tempConstructionDTO = new ConstructionDTO();
//                    tempConstructionDTO.setConstructionId(tempCD.getConstructionId());
//                    tempConstructionDTO.setStatus(Long.valueOf(Constant.CONSTRUCTION.STATUS_ACTIVE));
//                    tempConstructionDTO.setProvinceCode(userService.getUserProvinceCode(commonInputDTO.getUserName().split("----")[0]));
//                    List<Construction> temp = constructionService.getListConstruction(tempConstructionDTO, Constant.ACTION.CHECK_CONSTRUCTION_STARTDATE);
//                    if (temp.size() == 0) {
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("construction.item.can.not.be.requested"));
//                        return res;
//                    }
                    ConstructionDetail constructionDetail = constructionDetailRepo.getById(cd.getConstructionDetailId());
                    if (constructionDetail == null) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("construction.item.info.not.exist"));
                        return res;
                    }
                    //bo trang thai 2
//                    if (constructionDetail.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_1
//                            || constructionDetail.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_2
//                            || constructionDetail.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_3) {
//
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("construction.item.status.not.updatable"));
//                        return res;
//                    }
                    if (!StringUtils.isStringNullOrEmpty(constructionDetail.getParentId())) {
                        if (fileUploadService.getListImage(constructionDetail.getConstructionDetailId(), r).size() == 0) {
                            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                            res.setDescription(r.getResourceMessage("construction.item.not.have.image"));
                            return res;
                        }
                    }
                    ConstructionItem constructionItem = constructionItemRepo.getById(constructionDetail.getConstructionItemId());
                    if (constructionItem == null) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("constructionItem.not.exists"));
                        return res;
                    }
                    if (constructionDetail.getStartDate().isAfter(LocalDateTime.now())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("construction.item.startdate.not.valid"));
                        return res;
                    }
                    if (!StringUtils.isStringNullOrEmpty(constructionItem.getStep()) &&
                            constructionItem.getStep() > 1) {
                        List<ConstructionDetail> oldCDWithStep = constructionManagementService.getListCDWithStep(constructionDetail.getConstructionId(),
                                constructionItem.getStep());
                        if (oldCDWithStep.size() > 0) {
                            for (ConstructionDetail oldCD :
                                    oldCDWithStep) {
                                if (Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_3 != oldCD.getStatus()) {
                                    ConstructionItem tempCI = constructionItemRepo.getById(oldCD.getConstructionItemId());
                                    String ciName = "";
                                    if (!StringUtils.isStringNullOrEmpty(tempCI) && !StringUtils.isStringNullOrEmpty(tempCI.getName())){
                                        ciName = r.getResourceMessage(tempCI.getName());
                                    }

                            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                            res.setDescription(r.getResourceMessage("previous.construction.item.not.complete"
                                            , new Object[]{oldCD.getConstructionDetailId()+"-"+ciName}
                            ));
                            return res;
                                }
                            }
                        }
                    }
                    cd.setName(r.getResourceMessage(constructionItem.getName()));
                    constructionDetailDTOList.add(cd);
                }
            }
            if (constructionDetailDTOList.size() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.list.approve.null"));
                return res;
            }

            constructionManagementService.createApproveRequest(commonInputDTO, staff);
            res.setDescription(r.getResourceMessage("create.approve.request.success"));

            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/getImage")
    public ExecutionResult getImage(@RequestHeader("Accept-Language") String language,
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
            ImageDTO imageDTO = fileUploadService.getImage(commonInputDTO.getImageId());
            res.setData(imageDTO);
            res.setDescription(Constant.EXECUTION_MESSAGE.OK);

        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/getImageList")
    public ExecutionResult getImageList(@RequestHeader("Accept-Language") String language,
                                        @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);

        try {

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDetailDTO())
                    && StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDetailDTO().getConstructionDetailId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.info.null"));
                return res;
            }
            commonInputDTO.setAppCode("GSCT");
            String roleCode = userService.getUserRole(commonInputDTO);
            List<ConstructionDetail> constructionDetailList = constructionService.
                    getListConstructionDetailById(commonInputDTO.getConstructionDetailDTO().getConstructionDetailId());
            if (constructionDetailList.size() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.info.not.exist"));
                return res;
            }
            ImageDTO imageDTO = fileUploadService.getFullImageList(commonInputDTO.getConstructionDetailDTO().getConstructionDetailId(), r, roleCode);
            res.setData(imageDTO);
            res.setDescription(Constant.EXECUTION_MESSAGE.OK);

        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/approveConstructionItem")
    public ExecutionResult approveConstructionItem(@RequestHeader("Accept-Language") String language,
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
            Staff staff = userService.getStaffByUserName(commonInputDTO.getUserName().split("----")[0]);
            commonInputDTO.setAppCode("GSCT");
            String userRole = userService.getUserRole(commonInputDTO);
//            String userRole = Constant.CMS_ROLES.CMS_CORP_STAFF;
            if (!userRole.equals(Constant.CMS_ROLES.CMS_PROV_INFA_STAFF) &&
                    !userRole.equals(Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT) &&
                    !userRole.equals(Constant.CMS_ROLES.CMS_CORP_STAFF)){
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("not.allow.approve.request"));
                return res;
            }
            List<ConstructionDetail> constructionDetailList = constructionService.getListConstructionDetailById(
                    commonInputDTO.getConstructionDetailDTO().getConstructionDetailId());
            if (constructionDetailList.size() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("constructionDetail.not.exists"));
                return res;
            }
            ConstructionDetail constructionDetail = constructionDetailList.get(0);
            if (constructionDetail.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_CREATED
                    || constructionDetail.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_ACTIVE) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.not.updatable"));
                return res;
            }
            if (constructionDetail.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_3) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.is.completed"));
                return res;
            }
            if (Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(userRole)
                    && !(constructionDetail.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_REQUESTED)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.status.not.requested"));
                return res;
            }

            if (Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(userRole)
                    && !(constructionDetail.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_1)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.status.not.accepted1"));
                return res;
            }

            if (Constant.CMS_ROLES.CMS_CORP_STAFF.equals(userRole)
                    && !(constructionDetail.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_2)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.status.not.accepted2"));
                return res;
            }
            if (Constant.APPROVE_TYPE.REJECT == commonInputDTO.getConstructionDetailDTO().getApproveType()) {


                if (StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDetailDTO().getRejectReason())) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("reject.reason.null"));
                    return res;
                }
                commonInputDTO.getConstructionDetailDTO().setRejectReason(commonInputDTO.getConstructionDetailDTO().getRejectReason().trim());
                if (commonInputDTO.getConstructionDetailDTO().getRejectReason().length() > 500) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("reject.reason.length.invalid"));
                    return res;
                }
            }
            Construction construction = constructionRepo.getById(constructionDetail.getConstructionId());
            if (construction == null) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.not.exists"));
                return res;
            }
            if (construction.getStatus() != Constant.CONSTRUCTION.STATUS_ACTIVE){
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.status.not.approvable"));
                return res;
            }
            ConstructionItem constructionItem = constructionItemRepo.getById(constructionDetail.getConstructionItemId());
            if (constructionItem == null) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("constructionItem.not.exists"));
                return res;
            }
            ConstructionDTO constructionDTO = new ConstructionDTO();
            constructionDTO.setConstructionId(construction.getConstructionId());
            constructionDTO.setConstructionName(construction.getConstructionName());
            constructionDTO.setConstructionCode(construction.getConstructionCode());
            commonInputDTO.setConstructionDTO(constructionDTO);
            commonInputDTO.getConstructionDetailDTO().setName(r.getResourceMessage(constructionItem.getName()));
            commonInputDTO.setLanguage(language);

            constructionManagementService.approveConstructionItem(commonInputDTO, staff);

            if (Constant.APPROVE_TYPE.REJECT == commonInputDTO.getConstructionDetailDTO().getApproveType()) {
                res.setDescription(r.getResourceMessage("reject.request.success"));
            } else {
                res.setDescription(r.getResourceMessage("approve.request.success"));
            }

        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/uploadImage")
    public ExecutionResult uploadImage(@RequestHeader("Accept-Language") String locate,
                                       @RequestBody List<MultipartFile> listFileImage,
                                       @RequestParam String eun,
                                       @RequestParam String constructionDetailId) {
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
            if (StringUtils.isNullOrEmpty(constructionDetailId)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.info.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(listFileImage) || listFileImage.size() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("list.image.null"));
                return res;
            }
            List<String> fileExtensions = Arrays.asList(".png", ".jpg", ".jpeg", ".svg");
            Boolean checkFilename = false;
            String extension = "";
            List<ImageDTO> listImageDTO = new ArrayList<>();
            int index = 0;
            for (MultipartFile fileImage
                    : listFileImage) {
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
                ImageDTO imageDTO = new ImageDTO();
                imageDTO.setFile(fileImage);
                imageDTO.setConstructionDetailId(Long.valueOf(constructionDetailId));
                imageDTO.setImageName(constructionDetailId + "_"
                        + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd'_'HHmmssSSS")) + "_"
                        + index++ + extension);
                listImageDTO.add(imageDTO);
            }
            fileUploadService.uploadImage(listImageDTO, Long.valueOf(constructionDetailId));
            res.setDescription(r.getResourceMessage("upload.images.success"));
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

    @PostMapping(value = "/getListRejectReasonImage")
    public ExecutionResult getListRejectReasonImage(@RequestHeader("Accept-Language") String language,
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

            List<OptionSetValueDTO> optionSetValueDTOS = optionSetValueService.getListByOptionSetOrderById(Constant.CMS_OPTION_SET.REJECT_REASON_IMAGE, language, null);

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

    @PostMapping(value = "/getListConstructionItem2")
    public ExecutionResult getListConstructionItem2(@RequestHeader("Accept-Language") String language,
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

            List<ConstructionItemDTO> constructionItemDTOS = constructionManagementService.getListConstructionItem(r);

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

    @PostMapping(value = "/approvedImage")
    public ExecutionResult approvedImage(@RequestHeader("Accept-Language") String locate,
                                       @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(locate);
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
            Staff staff = userService.getStaffByUserName(commonInputDTO.getUserName().split("----")[0]);
            commonInputDTO.setAppCode("GSCT");
            String roleCode = userService.getUserRole(commonInputDTO);
            Long status = 1L;
            if (!StringUtils.isStringNullOrEmpty(roleCode)){
                if (Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(roleCode)){
                    status = 2L;
                } else if ((Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(roleCode))){
                    status = 4L;
                } else if ((Constant.CMS_ROLES.CMS_CORP_STAFF.equals(roleCode))){
                    status = 6L;
                }
            }
            Optional<ImageModel> result = imageRepo.findById(commonInputDTO.getImageId());
            ImageModel imageModel = result.orElse(null);
            if (StringUtils.isStringNullOrEmpty(imageModel)){
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("image.not.exist"));
                return res;
            } else {
                if (imageModel.getStatus() == 0L){
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("image.deleted"));
                    return res;
                }
                if (!StringUtils.isStringNullOrEmpty(roleCode)){
                    if (Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(roleCode)){
                        if (imageModel.getStatus() == 2L){
                            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                            res.setDescription(r.getResourceMessage("image.approved"));
                            return res;
                        }
                    } else if ((Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(roleCode))){
                        if (imageModel.getStatus() == 4L){
                            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                            res.setDescription(r.getResourceMessage("image.approved"));
                            return res;
                        }
                    } else if ((Constant.CMS_ROLES.CMS_CORP_STAFF.equals(roleCode))){
                        if (imageModel.getStatus() == 6L){
                            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                            res.setDescription(r.getResourceMessage("image.approved"));
                            return res;
                        }
                    }
                }

            }

            int resu = constructionManagementService.approveImage(commonInputDTO, staff, status);
            if (resu != 1){
                res.setDescription(r.getResourceMessage("system.error"));
            } else {
                res.setDescription(r.getResourceMessage("image.approved.success"));
            }
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

    @PostMapping(value = "/rejectImage")
    public ExecutionResult rejectImage(@RequestHeader("Accept-Language") String locate,
                                         @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(locate);
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
            Staff staff = userService.getStaffByUserName(commonInputDTO.getUserName().split("----")[0]);
            commonInputDTO.setAppCode("GSCT");
            String roleCode = userService.getUserRole(commonInputDTO);
            Long status = 1L;
            if (!StringUtils.isStringNullOrEmpty(roleCode)){
                if (Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(roleCode)){
                    status = 3L;
                } else if ((Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(roleCode))){
                    status = 5L;
                } else if ((Constant.CMS_ROLES.CMS_CORP_STAFF.equals(roleCode))){
                    status = 7L;
                }
            }
            Optional<ImageModel> result = imageRepo.findById(commonInputDTO.getImageId());
            ImageModel imageModel = result.orElse(null);
            if (StringUtils.isStringNullOrEmpty(imageModel)){
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("image.not.exist"));
                return res;
            } else {
                if (imageModel.getStatus() == 0L){
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("image.deleted"));
                    return res;
                }
//                if (imageModel.getStatus() == 2L){
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("image.approved.reject"));
//                    return res;
//                }
                if (!StringUtils.isStringNullOrEmpty(roleCode)){
                    if (Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(roleCode)){
                        if (imageModel.getStatus() == 3L){
                            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                            res.setDescription(r.getResourceMessage("image.rejected"));
                            return res;
                        }
                    } else if ((Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(roleCode))){
                        if (imageModel.getStatus() == 5L){
                            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                            res.setDescription(r.getResourceMessage("image.rejected"));
                            return res;
                        }
                    } else if ((Constant.CMS_ROLES.CMS_CORP_STAFF.equals(roleCode))){
                        if (imageModel.getStatus() == 7L){
                            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                            res.setDescription(r.getResourceMessage("image.rejected"));
                            return res;
                        }
                    }
                }

            }

            int resu = constructionManagementService.rejectImage(commonInputDTO, staff, status);
            if (resu != 1){
                res.setDescription(r.getResourceMessage("system.error"));
            } else {
                res.setDescription(r.getResourceMessage("image.reject.success"));
            }
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

    @PostMapping(value = "/deleteImageListReject")
    public ExecutionResult deleteImageListReject(@RequestHeader("Accept-Language") String locate,
                                       @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(locate);
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
            Staff staff = userService.getStaffByUserName(commonInputDTO.getUserName().split("----")[0]);
            if (commonInputDTO.getImageDTOList() == null && commonInputDTO.getImageDTOList().isEmpty()) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("list.image.null"));
                return res;
            } else {
                for (ImageDTO imageDTO : commonInputDTO.getImageDTOList()) {
                    Optional<ImageModel> result = imageRepo.findById(imageDTO.getImageId());
                    ImageModel imageModel = result.orElse(null);
                    if (StringUtils.isStringNullOrEmpty(imageModel)) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("image.not.exist"));
                        return res;
                    } else {
                        if (imageModel.getStatus() != 3L) {
                            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                            res.setDescription(r.getResourceMessage("image.delete.status") + " Image : " + imageModel.getImageName());
                            return res;
                        }
                    }
                }
            }

            constructionManagementService.deleteImageListReject(commonInputDTO, staff);
            res.setDescription(r.getResourceMessage("delete.image.rejected"));

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

    @PostMapping(value = "/deleteImage")
    public ExecutionResult deleteImage(@RequestHeader("Accept-Language") String locate,
                                                 @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(locate);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        String isdn = null;
        // trace log
//        Long traceID = DataUtils.getSequence(upoint, "trace_logs_seq");
//        String funcitionName = "getListConstructionType";

        try {
            ImageModel image = new ImageModel();
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.null"));
                return res;
            }
            Staff staff = userService.getStaffByUserName(commonInputDTO.getUserName().split("----")[0]);
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getImageId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("image.null"));
                return res;
            } else {
                    Optional<ImageModel> result = imageRepo.findById(commonInputDTO.getImageId());
                    ImageModel imageModel = result.orElse(null);
                    image = imageModel;
                    if (StringUtils.isStringNullOrEmpty(imageModel)) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("image.not.exist"));
                        return res;
                    } else {
                        if (imageModel.getStatus() != 1L) {
                            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                            res.setDescription(r.getResourceMessage("image.delete.not.active"));
                            return res;
                        }
                    }

            }

            constructionManagementService.deleteImage(commonInputDTO,image, staff);
            res.setDescription(r.getResourceMessage("delete.image.success"));

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


    @PostMapping(value = "/getConstructionItemHistory")
    public ExecutionResult getConstructionItemHistory(@RequestHeader("Accept-Language") String language,
                                        @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);

        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.null"));
                return res;
            }

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDetailDTO())
                    && StringUtils.isStringNullOrEmpty(commonInputDTO.getConstructionDetailDTO().getConstructionDetailId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.info.null"));
                return res;
            }

            List<ConstructionDetail> constructionDetailList = constructionService.
                    getListConstructionDetailById(commonInputDTO.getConstructionDetailDTO().getConstructionDetailId());
            if (constructionDetailList.size() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.info.not.exist"));
                return res;
            }
            List<ActionLogDTO> actionLogList = constructionManagementService.getConstructionItemHistory(commonInputDTO.getConstructionDetailDTO().getConstructionDetailId(), r);
            res.setData(actionLogList);
            res.setDescription(Constant.EXECUTION_MESSAGE.OK);

        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/approvedFinishConstructionItem")
    public ExecutionResult approvedFinishConstructionItem(@RequestHeader("Accept-Language") String language,
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
            Staff staff = userService.getStaffByUserName(commonInputDTO.getUserName().split("----")[0]);
            commonInputDTO.setAppCode("GSCT");
            String userRole = userService.getUserRole(commonInputDTO);
            if (!userRole.equals(Constant.CMS_ROLES.CMS_CORP_STAFF)){
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("not.allow.approve.request"));
                return res;
            }
            List<ConstructionDetail> constructionDetailList = constructionService.getListConstructionDetailById(
                    commonInputDTO.getConstructionDetailDTO().getConstructionDetailId());
            if (constructionDetailList.size() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("constructionDetail.not.exists"));
                return res;
            }
            ConstructionDetail constructionDetail = constructionDetailList.get(0);
            if (constructionDetail.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_CREATED
                    || constructionDetail.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_ACTIVE) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.not.updatable"));
                return res;
            }
            if (constructionDetail.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_COMPLETE) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.is.completed"));
                return res;
            }

            if (Constant.CMS_ROLES.CMS_CORP_STAFF.equals(userRole)
                    && !(constructionDetail.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_3)) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.item.status.not.accepted2"));
                return res;
            }

            Construction construction = constructionRepo.getById(constructionDetail.getConstructionId());
            if (construction == null) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.not.exists"));
                return res;
            }
            if (construction.getStatus() != Constant.CONSTRUCTION.STATUS_ACTIVE){
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.status.not.approvable"));
                return res;
            }
            ConstructionItem constructionItem = constructionItemRepo.getById(constructionDetail.getConstructionItemId());
            if (constructionItem == null) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("constructionItem.not.exists"));
                return res;
            }


            ConstructionDTO constructionDTO = new ConstructionDTO();
            constructionDTO.setConstructionId(construction.getConstructionId());
            constructionDTO.setConstructionName(construction.getConstructionName());
            constructionDTO.setConstructionCode(construction.getConstructionCode());
            commonInputDTO.setConstructionDTO(constructionDTO);
            commonInputDTO.getConstructionDetailDTO().setName(r.getResourceMessage(constructionItem.getName()));
            commonInputDTO.setLanguage(language);

            constructionManagementService.approvedFinishConstructionItem(commonInputDTO, staff, commonInputDTO.getUserName().split("----")[0], r);



            res.setDescription(r.getResourceMessage("approve.request.success"));


        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }


}
