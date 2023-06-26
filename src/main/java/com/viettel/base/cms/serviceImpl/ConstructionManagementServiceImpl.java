package com.viettel.base.cms.serviceImpl;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.*;
import com.viettel.base.cms.model.*;
import com.viettel.base.cms.repo.ActionLogDetailRepo;
import com.viettel.base.cms.repo.ActionLogRepo;
import com.viettel.base.cms.repo.ConstructionDetailRepo;
import com.viettel.base.cms.repo.ConstructionRepo;
import com.viettel.base.cms.service.ConstructionManagementService;
import com.viettel.base.cms.service.ConstructionService;
import com.viettel.base.cms.service.FileUploadService;
import com.viettel.base.cms.service.OptionSetValueService;
import com.viettel.base.cms.utils.FunctionUtils;
import com.viettel.base.cms.utils.SmsUtils;
import com.viettel.vfw5.base.utils.DataUtils;
import com.viettel.vfw5.base.utils.ResourceBundle;
import com.viettel.vfw5.base.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.core.env.Environment;

@Service
public class ConstructionManagementServiceImpl implements ConstructionManagementService {

    @Autowired
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private EntityManager cms;

    @Autowired
    ConstructionRepo constructionRepo;

    @Autowired
    ConstructionService constructionService;

    @Autowired
    OptionSetValueService optionSetValueService;

    @Autowired
    ConstructionDetailRepo constructionDetailRepo;

    @Autowired
    FileUploadService fileUploadService;

    @Autowired
    ActionLogRepo actionLogRepo;

    @Autowired
    ActionLogDetailRepo actionLogDetailRepo;

    @Autowired
    Environment env;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConstructionStartDate(CommonInputDTO commonInputDTO) throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE   construction "
                    + "   SET   start_date = :startDate, "
                    + "         start_by = :userName, "
                    + "         last_modified_date = :updateDate, "
                    + "         last_modified_by = :userName, "
                    + "         status = :statusActive "
                    + " WHERE   construction_id = :constructionId AND status = :statusAssigned ");
            Query query = cms.createNativeQuery(sql.toString());
            query.setParameter("startDate", commonInputDTO.getConstructionDTO().getStartDate());
            query.setParameter("userName", commonInputDTO.getUserName().split("----")[0]);
            query.setParameter("updateDate", commonInputDTO.getConstructionDTO().getLastModifiedDate());
            query.setParameter("statusActive", Constant.CONSTRUCTION.STATUS_ACTIVE);
            query.setParameter("constructionId", commonInputDTO.getConstructionDTO().getConstructionId());
            query.setParameter("statusAssigned", Constant.CONSTRUCTION.STATUS_ASSIGNED);
            query.executeUpdate();
            /*
            ghi log
             */
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConstructionItemStartDate(CommonInputDTO commonInputDTO) throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE   construction_detail "
                    + "   SET   start_date = :startdate, status = :statusUpdate, start_by = :startBy "
                    + " WHERE   construction_detail_id = :constructionDetailId ");
            Query query = cms.createNativeQuery(sql.toString());
            query.setParameter("startdate", commonInputDTO.getConstructionDetailDTO().getStartDate());
            query.setParameter("statusUpdate", commonInputDTO.getConstructionDetailDTO().getStatus());
            query.setParameter("startBy", commonInputDTO.getConstructionDetailDTO().getStartBy());
            query.setParameter("constructionDetailId", commonInputDTO.getConstructionDetailDTO().getConstructionDetailId());

            query.executeUpdate();
            // Gui tin nhan cho KTT khi nhập ngày thi công hoàn tất
            // 1. Lay mau tin nhan
            OptionSetValue template = optionSetValueService.getListByOptionSetValueName(Constant.SMS_CONFIG.SMS, commonInputDTO.getLanguage(), Constant.SMS_CONFIG.KTT_START_MONITOR);
            if (template != null) {
                // 2. Gui tin nhan thong bao cho KTT
                SmsDTO smsDTO = new SmsDTO();
                smsDTO.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                String message = String.format(template.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName());
                smsDTO.setMessage(message);
                smsDTO.setUserRole(Constant.CMS_ROLES.CMS_PROV_TECH_STAFF);
                SmsUtils.sendSMSForUser(smsDTO, cms, env);
            }
            /*
            ghi log
             */

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createApproveRequest(CommonInputDTO commonInputDTO, Staff staff) throws Exception {
        try {
            for (ConstructionDetailDTO cd
                    : commonInputDTO.getListConstructionDetail()) {
                LocalDateTime dateNow = LocalDateTime.now();
                String userName  = commonInputDTO.getUserName().split("----")[0];
                if ("true".equals(cd.getIsRequested())) {
//                cd.setImagePath(fileUploadService.uploadImage(cd));
//                    cd.setStatus(Long.valueOf(Constant.CONSTRUCTION_DETAIL.STATUS_REQUESTED));
                    cd.setAcceptanceBy(userName);
                    cd.setAcceptanceDate(dateNow);

                    ConstructionDetail constructionDetail = getConstructionDetailById(cd.getConstructionDetailId());
                    constructionDetail.setAcceptanceBy(userName);
                    constructionDetail.setAcceptanceDate(dateNow);
                    updateConstructionDetail(constructionDetail);


//                    ConstructionDetail constructionDetail = getConstructionDetailById(cd.getConstructionDetailId());
                    Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
                    ActionLog actionLog = FunctionUtils.saveActionLog(staff, "CREATE_APPROVE_REQUEST", idActionLog, constructionDetail.getConstructionDetailId());
                    actionLogRepo.save(actionLog);

                    if (constructionDetail.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_REJECTED_1) {
                        insertLogForRejectedConstructionDetail(constructionDetail,
                                idActionLog,
                                    "first_reject_date",
                                null,
                                "null");
                        insertLogForRejectedConstructionDetail(constructionDetail,
                                idActionLog,
                                "first_reject_by",
                                constructionDetail.getFirstRejectBy(),
                                "null");
                        insertLogForRejectedConstructionDetail(constructionDetail,
                                idActionLog,
                                "first_reject_reason",
                                constructionDetail.getFirstRejectReason(),
                                "null");
                    }
                    if (constructionDetail.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_REJECTED_2) {
                        insertLogForRejectedConstructionDetail(constructionDetail,
                                idActionLog,
                                "second_reject_date",
                                null,
                                "null");
                        insertLogForRejectedConstructionDetail(constructionDetail,
                                idActionLog,
                                "second_reject_by",
                                constructionDetail.getSecondRejectBy(),
                                "null");
                        insertLogForRejectedConstructionDetail(constructionDetail,
                                idActionLog,
                                "second_reject_reason",
                                constructionDetail.getSecondRejectReason(),
                                "null");
                        insertLogForRejectedConstructionDetail(constructionDetail,
                                idActionLog,
                                "first_approved_date",
                                null,
                                "null");
                        insertLogForRejectedConstructionDetail(constructionDetail,
                                idActionLog,
                                "first_approved_by",
                                constructionDetail.getFirstApprovedBy(),
                                "null");


                    }
                    if (constructionDetail.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_REJECTED_3) {
                        insertLogForRejectedConstructionDetail(constructionDetail,
                                idActionLog,
                                "third_reject_date",
                                null,
                                "null");
                        insertLogForRejectedConstructionDetail(constructionDetail,
                                idActionLog,
                                "third_reject_by",
                                constructionDetail.getThirdRejectBy(),
                                "null");
                        insertLogForRejectedConstructionDetail(constructionDetail,
                                idActionLog,
                                "third_reject_reason",
                                constructionDetail.getThirdRejectReason(),
                                "null");
                        insertLogForRejectedConstructionDetail(constructionDetail,
                                idActionLog,
                                "first_approved_date",
                                null,
                                "null");
                        insertLogForRejectedConstructionDetail(constructionDetail,
                                idActionLog,
                                "first_approved_by",
                                constructionDetail.getFirstApprovedBy(),
                                "null");
                        insertLogForRejectedConstructionDetail(constructionDetail,
                                idActionLog,
                                "second_approved_date",
                                null,
                                "null");
                        insertLogForRejectedConstructionDetail(constructionDetail,
                                idActionLog,
                                "second_approved_by",
                                constructionDetail.getSecondApprovedBy(),
                                "null");
                    }



                    // Gui tin nhan cho HTT khi gửi yêu cầu phê duyệt hạng mục hoàn tất
                    // 1. Lay mau tin nhan
                    OptionSetValue template = optionSetValueService.getListByOptionSetValueName(Constant.SMS_CONFIG.SMS, commonInputDTO.getLanguage(), Constant.SMS_CONFIG.REQUEST_APPROVE);
                    if (template != null) {
                        // 2. Gui tin nhan thong bao cho HTT
                        SmsDTO smsDTO = new SmsDTO();
                        smsDTO.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                        String message = String.format(template.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), cd.getName(), userName, DataUtils.getStringDateTime(dateNow));
                        smsDTO.setMessage(message);
                        smsDTO.setUserRole(Constant.CMS_ROLES.CMS_PROV_INFA_STAFF);
                        SmsUtils.sendSMSForUser(smsDTO, cms, env);
                    }
                }
//                else {
//                    fileUploadService.deactivateOldImageInfo(cd.get);
//                }

            }
            /*
            ghi log
             */

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    void insertLogForRejectedConstructionDetail(ConstructionDetail cd, Long idActionLog, String colName, String oldValue, String newValue){
        Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
        ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                cd.getConstructionDetailId(),
                colName,
                oldValue,
                newValue,
                idActionLog,
                idActionLogDetailUser);
        actionLogDetailRepo.save(actionLogDetail);
    }

    //
//    @Override
//    public List<ImageDTO> getImageList(ConstructionDetail constructionDetail) throws Exception {
//        try {
//            return fileUploadService.getImageList(constructionDetail);
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw e;
//        }
//    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approveConstructionItem(CommonInputDTO commonInputDTO, Staff staff) throws Exception {
        try {
            ConstructionDetail constructionDetail = getConstructionDetailById(commonInputDTO.getConstructionDetailDTO().getConstructionDetailId());
            LocalDateTime dateNow = LocalDateTime.now();
            OptionSetValue templateApprove = optionSetValueService.getListByOptionSetValueName(Constant.SMS_CONFIG.SMS, commonInputDTO.getLanguage(), Constant.SMS_CONFIG.APPROVE_SMS);
            OptionSetValue templateRequestApprove = optionSetValueService.getListByOptionSetValueName(Constant.SMS_CONFIG.SMS, commonInputDTO.getLanguage(), Constant.SMS_CONFIG.REQUEST_APPROVE);
            OptionSetValue templateReject = optionSetValueService.getListByOptionSetValueName(Constant.SMS_CONFIG.SMS, commonInputDTO.getLanguage(), Constant.SMS_CONFIG.REJECT_SMS);
            String userName = commonInputDTO.getUserName().split("----")[0];
            if (Constant.APPROVE_TYPE.APPROVE == commonInputDTO.getConstructionDetailDTO().getApproveType()) {
                if (Constant.CONSTRUCTION_DETAIL.STATUS_REQUESTED == constructionDetail.getStatus()) {
                    if (StringUtils.isStringNullOrEmpty(constructionDetail.getParentId())) {
                        Long oldStatus = constructionDetail.getStatus();
                        String oldApprovedDate = "";
                        if (!StringUtils.isStringNullOrEmpty(constructionDetail.getFirstApprovedDate())) {
                            oldApprovedDate = DataUtils.getStringDateTime(constructionDetail.getFirstApprovedDate());
                        }
                        String oldApprovedBy = constructionDetail.getFirstApprovedBy();
                        constructionDetail.setFirstApprovedDate(dateNow);
                        constructionDetail.setFirstApprovedBy(userName);
                        constructionDetail.setStatus(Long.valueOf(Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_1));
                        // Ghi log
                        // Them log vao bang action log
                        Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
                        ActionLog actionLog = FunctionUtils.saveActionLog(staff, "PROV_INFA_APPROVE_CONSTRUCTION_ITEM", idActionLog, constructionDetail.getConstructionDetailId());
                        actionLogRepo.save(actionLog);

                        //Them log vao bang action log detail
                        Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                        ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                                commonInputDTO.getImageId(),
                                "status",
                                String.valueOf(oldStatus),
                                "3",
                                idActionLog,
                                idActionLogDetailUser);
                        actionLogDetailRepo.save(actionLogDetail);
                        Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                        ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                                commonInputDTO.getImageId(),
                                "first_approved_datetime",
                                oldApprovedDate,
                                null,
                                idActionLog,
                                idActionLogDetailUser1);
                        actionLogDetailRepo.save(actionLogDetail1);
                        Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                        ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                                commonInputDTO.getImageId(),
                                "first_approved_by",
                                oldApprovedBy,
                                userName,
                                idActionLog,
                                idActionLogDetailUser2);
                        actionLogDetailRepo.save(actionLogDetail2);

                        // Gui tin nhan khi phê duyệt hạng mục hoàn tất cho nhan vien KTT
                        if (templateApprove != null) {
                            SmsDTO smsDTO = new SmsDTO();
                            smsDTO.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                            String message = String.format(templateApprove.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow));
                            smsDTO.setMessage(message);
                            smsDTO.setUserRole(Constant.CMS_ROLES.CMS_PROV_TECH_STAFF);
                            SmsUtils.sendSMSForUser(smsDTO, cms, env);
                        }

                        // Gui tin nhan gửi yêu cầu phê duyệt hạng mục cho PGĐ CN
                        if (templateRequestApprove != null) {
                            SmsDTO smsDTO = new SmsDTO();
                            smsDTO.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                            String message = String.format(templateRequestApprove.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow));
                            smsDTO.setMessage(message);
                            smsDTO.setUserRole(Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT);
                            SmsUtils.sendSMSForUser(smsDTO, cms, env);
                        }
                    }else {
                        Long oldStatus = constructionDetail.getStatus();
                        String oldApprovedDate = "";
                        if (!StringUtils.isStringNullOrEmpty(constructionDetail.getFirstApprovedDate())) {
                            oldApprovedDate = DataUtils.getStringDateTime(constructionDetail.getFirstApprovedDate());
                        }
                        String oldApprovedBy = constructionDetail.getFirstApprovedBy();
                        constructionDetail.setFirstApprovedDate(dateNow);
                        constructionDetail.setFirstApprovedBy(userName);
                        constructionDetail.setStatus(Long.valueOf(Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_1));
                        // Ghi log
                        // Them log vao bang action log
                        Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
                        ActionLog actionLog = FunctionUtils.saveActionLog(staff, "PROV_INFA_APPROVE_CONSTRUCTION_ITEM", idActionLog, constructionDetail.getConstructionDetailId());
                        actionLogRepo.save(actionLog);

                        //Them log vao bang action log detail
                        Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                        ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                                commonInputDTO.getImageId(),
                                "status",
                                String.valueOf(oldStatus),
                                "3",
                                idActionLog,
                                idActionLogDetailUser);
                        actionLogDetailRepo.save(actionLogDetail);
                        Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                        ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                                commonInputDTO.getImageId(),
                                "first_approved_datetime",
                                oldApprovedDate,
                                null,
                                idActionLog,
                                idActionLogDetailUser1);
                        actionLogDetailRepo.save(actionLogDetail1);
                        Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                        ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                                commonInputDTO.getImageId(),
                                "first_approved_by",
                                oldApprovedBy,
                                userName,
                                idActionLog,
                                idActionLogDetailUser2);
                        actionLogDetailRepo.save(actionLogDetail2);

                        // Gui tin nhan khi phê duyệt hạng mục hoàn tất cho nhan vien KTT
                        if (templateApprove != null) {
                            SmsDTO smsDTO = new SmsDTO();
                            smsDTO.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                            String message = String.format(templateApprove.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow));
                            smsDTO.setMessage(message);
                            smsDTO.setUserRole(Constant.CMS_ROLES.CMS_PROV_TECH_STAFF);
                            SmsUtils.sendSMSForUser(smsDTO, cms, env);
                        }

                        // Gui tin nhan gửi yêu cầu phê duyệt hạng mục cho PGĐ CN
                        if (templateRequestApprove != null) {
                            SmsDTO smsDTO = new SmsDTO();
                            smsDTO.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                            String message = String.format(templateRequestApprove.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow));
                            smsDTO.setMessage(message);
                            smsDTO.setUserRole(Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT);
                            SmsUtils.sendSMSForUser(smsDTO, cms, env);
                        }

                        // Check cac hang muc con khac de update cha
                        // Lay danh sach cac con
                        List<ConstructionDetail> listChildConstructionDetail = constructionService.getListConstructionDetailByParentId(constructionDetail.getConstructionId(), Constant.VALIDATE_CONSTRUCTION_STATUS.VALIDATE, constructionDetail.getParentId());

                        if (listChildConstructionDetail != null && !listChildConstructionDetail.isEmpty()){
                            for (ConstructionDetail child : listChildConstructionDetail){
                                if (!StringUtils.isStringNullOrEmpty(child) && !child.getStatus().equals(3L)){
                                    return;
                                }
                            }
                        }

//                        updateConstructionItemStatus(constructionDetail.getConstructionId(), Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_1, userName, constructionDetail.getParentId());

                    }
                } else if (Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_1 == constructionDetail.getStatus()) {
                    if (StringUtils.isStringNullOrEmpty(constructionDetail.getParentId())) {
                        Long oldStatus = constructionDetail.getStatus();
                        String oldApprovedDate = "";
                        if (!StringUtils.isStringNullOrEmpty(constructionDetail.getSecondApprovedDate())) {
                            oldApprovedDate = DataUtils.getStringDateTime(constructionDetail.getSecondApprovedDate());
                        }
                        String oldApprovedBy = constructionDetail.getSecondApprovedBy();
                        constructionDetail.setSecondApprovedDate(dateNow);
                        constructionDetail.setSecondApprovedBy(userName);
                        constructionDetail.setStatus(Long.valueOf(Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_2));
                        // Ghi log
                        // Them log vao bang action log
                        Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
                        ActionLog actionLog = FunctionUtils.saveActionLog(staff, "PROV_VICE_APPROVE_CONSTRUCTION_ITEM", idActionLog, constructionDetail.getConstructionDetailId());
                        actionLogRepo.save(actionLog);

                        //Them log vao bang action log detail
                        Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                        ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                                commonInputDTO.getImageId(),
                                "status",
                                String.valueOf(oldStatus),
                                "5",
                                idActionLog,
                                idActionLogDetailUser);
                        actionLogDetailRepo.save(actionLogDetail);
                        Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                        ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                                commonInputDTO.getImageId(),
                                "second_approved_datetime",
                                oldApprovedDate,
                                null,
                                idActionLog,
                                idActionLogDetailUser1);
                        actionLogDetailRepo.save(actionLogDetail1);
                        Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                        ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                                commonInputDTO.getImageId(),
                                "second_approved_by",
                                oldApprovedBy,
                                userName,
                                idActionLog,
                                idActionLogDetailUser2);
                        actionLogDetailRepo.save(actionLogDetail2);

                        // Gui tin nhan khi phê duyệt hạng mục hoàn tất cho nhan vien HTT
                        if (templateApprove != null) {
                            SmsDTO smsDTO = new SmsDTO();
                            smsDTO.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                            String message = String.format(templateApprove.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow));
                            smsDTO.setMessage(message);
                            smsDTO.setUserRole(Constant.CMS_ROLES.CMS_PROV_INFA_STAFF);
                            SmsUtils.sendSMSForUser(smsDTO, cms, env);
                        }

                        // Gui tin nhan gửi yêu cầu phê duyệt hạng mục cho NV TCT
                        if (templateRequestApprove != null) {
                            SmsDTO smsDTO = new SmsDTO();
                            smsDTO.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                            String message = String.format(templateRequestApprove.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow));
                            smsDTO.setMessage(message);
                            smsDTO.setUserRole(Constant.CMS_ROLES.CMS_CORP_STAFF);
                            SmsUtils.sendSMSForUser(smsDTO, cms, env);
                        }
                    }else {
                        Long oldStatus = constructionDetail.getStatus();
                        String oldApprovedDate = "";
                        if (!StringUtils.isStringNullOrEmpty(constructionDetail.getSecondApprovedDate())) {
                            oldApprovedDate = DataUtils.getStringDateTime(constructionDetail.getSecondApprovedDate());
                        }
                        String oldApprovedBy = constructionDetail.getSecondApprovedBy();
                        constructionDetail.setSecondApprovedDate(dateNow);
                        constructionDetail.setSecondApprovedBy(userName);
                        constructionDetail.setStatus(Long.valueOf(Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_2));
                        // Ghi log
                        // Them log vao bang action log
                        Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
                        ActionLog actionLog = FunctionUtils.saveActionLog(staff, "PROV_VICE_APPROVE_CONSTRUCTION_ITEM", idActionLog, constructionDetail.getConstructionDetailId());
                        actionLogRepo.save(actionLog);

                        //Them log vao bang action log detail
                        Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                        ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                                commonInputDTO.getImageId(),
                                "status",
                                String.valueOf(oldStatus),
                                "5",
                                idActionLog,
                                idActionLogDetailUser);
                        actionLogDetailRepo.save(actionLogDetail);
                        Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                        ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                                commonInputDTO.getImageId(),
                                "second_approved_datetime",
                                oldApprovedDate,
                                null,
                                idActionLog,
                                idActionLogDetailUser1);
                        actionLogDetailRepo.save(actionLogDetail1);
                        Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                        ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                                commonInputDTO.getImageId(),
                                "second_approved_by",
                                oldApprovedBy,
                                userName,
                                idActionLog,
                                idActionLogDetailUser2);
                        actionLogDetailRepo.save(actionLogDetail2);

                        // Gui tin nhan khi phê duyệt hạng mục hoàn tất cho nhan vien HTT
                        if (templateApprove != null) {
                            SmsDTO smsDTO = new SmsDTO();
                            smsDTO.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                            String message = String.format(templateApprove.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow));
                            smsDTO.setMessage(message);
                            smsDTO.setUserRole(Constant.CMS_ROLES.CMS_PROV_INFA_STAFF);
                            SmsUtils.sendSMSForUser(smsDTO, cms, env);
                        }

                        // Gui tin nhan gửi yêu cầu phê duyệt hạng mục cho NV TCT
                        if (templateRequestApprove != null) {
                            SmsDTO smsDTO = new SmsDTO();
                            smsDTO.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                            String message = String.format(templateRequestApprove.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow));
                            smsDTO.setMessage(message);
                            smsDTO.setUserRole(Constant.CMS_ROLES.CMS_CORP_STAFF);
                            SmsUtils.sendSMSForUser(smsDTO, cms, env);
                        }

                        // Check cac hang muc con khac de update cha
                        // Lay danh sach cac con
                        List<ConstructionDetail> listChildConstructionDetail = constructionService.getListConstructionDetailByParentId(constructionDetail.getConstructionId(), Constant.VALIDATE_CONSTRUCTION_STATUS.VALIDATE, constructionDetail.getParentId());
                        if (listChildConstructionDetail != null && !listChildConstructionDetail.isEmpty()){
                            for (ConstructionDetail child : listChildConstructionDetail){
                                if (!StringUtils.isStringNullOrEmpty(child) && !child.getStatus().equals(5L)){
                                    return;
                                }
                            }
                        }
//                        updateConstructionItemStatus(constructionDetail.getConstructionId(), Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_2, userName, constructionDetail.getParentId());

                    }
                } else if (Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_2 == constructionDetail.getStatus()) {
                    if (StringUtils.isStringNullOrEmpty(constructionDetail.getParentId())) {
                        Long oldStatus = constructionDetail.getStatus();
                        String oldApprovedDate = "";
                        if (!StringUtils.isStringNullOrEmpty(constructionDetail.getThirdApprovedDate())) {
                            oldApprovedDate = DataUtils.getStringDateTime(constructionDetail.getThirdApprovedDate());
                        }
                        String oldApprovedBy = constructionDetail.getThirdApprovedBy();
                        constructionDetail.setThirdApprovedDate(dateNow);
                        constructionDetail.setThirdApprovedBy(userName);
                        constructionDetail.setStatus(Long.valueOf(Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_3));
                        // Ghi log
                        // Them log vao bang action log
                        Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
                        ActionLog actionLog = FunctionUtils.saveActionLog(staff, "CORP_APPROVE_CONSTRUCTION_ITEM", idActionLog, constructionDetail.getConstructionDetailId());
                        actionLogRepo.save(actionLog);

                        //Them log vao bang action log detail
                        Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                        ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                                commonInputDTO.getImageId(),
                                "status",
                                String.valueOf(oldStatus),
                                "7",
                                idActionLog,
                                idActionLogDetailUser);
                        actionLogDetailRepo.save(actionLogDetail);
                        Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                        ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                                commonInputDTO.getImageId(),
                                "third_approved_datetime",
                                oldApprovedDate,
                                null,
                                idActionLog,
                                idActionLogDetailUser1);
                        actionLogDetailRepo.save(actionLogDetail1);
                        Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                        ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                                commonInputDTO.getImageId(),
                                "third_approved_by",
                                oldApprovedBy,
                                userName,
                                idActionLog,
                                idActionLogDetailUser2);
                        actionLogDetailRepo.save(actionLogDetail2);

                        OptionSetValue templateFinish = optionSetValueService.getListByOptionSetValueName(Constant.SMS_CONFIG.SMS, commonInputDTO.getLanguage(), Constant.SMS_CONFIG.ACCEPTANCE_SMS);
                        // Gui tin nhan khi phê duyệt hạng mục hoàn tất cho nhan vien HTT
                        if (templateFinish != null) {
                            // Gui tin nhan cho PGD
                            SmsDTO smsDTO = new SmsDTO();
                            smsDTO.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                            String message = String.format(templateFinish.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow));
                            smsDTO.setMessage(message);
                            smsDTO.setUserRole(Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT);
                            SmsUtils.sendSMSForUser(smsDTO, cms, env);

                            // Gui tin nhan cho HTT
                            SmsDTO smsDTOHtt = new SmsDTO();
                            smsDTOHtt.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                            message = String.format(templateFinish.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow));
                            smsDTOHtt.setMessage(message);
                            smsDTOHtt.setUserRole(Constant.CMS_ROLES.CMS_PROV_INFA_STAFF);
                            SmsUtils.sendSMSForUser(smsDTOHtt, cms, env);

                            // Gui tin nhan cho KTT
                            SmsDTO smsDTOKtt = new SmsDTO();
                            smsDTOKtt.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                            message = String.format(templateApprove.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow));
                            smsDTOKtt.setMessage(message);
                            smsDTOKtt.setUserRole(Constant.CMS_ROLES.CMS_PROV_TECH_STAFF);
                            SmsUtils.sendSMSForUser(smsDTOKtt, cms, env);
                        }

                    } else {
                    Long oldStatus = constructionDetail.getStatus();
                    String oldApprovedDate = "";
                    if (!StringUtils.isStringNullOrEmpty(constructionDetail.getThirdApprovedDate())) {
                        oldApprovedDate = DataUtils.getStringDateTime(constructionDetail.getThirdApprovedDate());
                    }
                    String oldApprovedBy = constructionDetail.getThirdApprovedBy();
                    constructionDetail.setThirdApprovedDate(dateNow);
                    constructionDetail.setThirdApprovedBy(userName);
                    constructionDetail.setStatus(Long.valueOf(Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_3));
                    // Ghi log
                    // Them log vao bang action log
                    Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
                    ActionLog actionLog = FunctionUtils.saveActionLog(staff, "CORP_APPROVE_CONSTRUCTION_ITEM", idActionLog, constructionDetail.getConstructionDetailId());
                    actionLogRepo.save(actionLog);

                    //Them log vao bang action log detail
                    Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                    ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                            commonInputDTO.getImageId(),
                            "status",
                            String.valueOf(oldStatus),
                            "7",
                            idActionLog,
                            idActionLogDetailUser);
                    actionLogDetailRepo.save(actionLogDetail);
                    Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                    ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                            commonInputDTO.getImageId(),
                            "third_approved_datetime",
                            oldApprovedDate,
                            null,
                            idActionLog,
                            idActionLogDetailUser1);
                    actionLogDetailRepo.save(actionLogDetail1);
                    Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                    ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                            commonInputDTO.getImageId(),
                            "third_approved_by",
                            oldApprovedBy,
                            userName,
                            idActionLog,
                            idActionLogDetailUser2);
                    actionLogDetailRepo.save(actionLogDetail2);

                    OptionSetValue templateFinish = optionSetValueService.getListByOptionSetValueName(Constant.SMS_CONFIG.SMS, commonInputDTO.getLanguage(), Constant.SMS_CONFIG.ACCEPTANCE_SMS);
                    // Gui tin nhan khi phê duyệt hạng mục hoàn tất cho nhan vien HTT
                    if (templateFinish != null) {
                        // Gui tin nhan cho PGD
                        SmsDTO smsDTO = new SmsDTO();
                        smsDTO.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                        String message = String.format(templateFinish.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow));
                        smsDTO.setMessage(message);
                        smsDTO.setUserRole(Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT);
                        SmsUtils.sendSMSForUser(smsDTO, cms, env);

                        // Gui tin nhan cho HTT
                        SmsDTO smsDTOHtt = new SmsDTO();
                        smsDTOHtt.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                        message = String.format(templateFinish.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow));
                        smsDTOHtt.setMessage(message);
                        smsDTOHtt.setUserRole(Constant.CMS_ROLES.CMS_PROV_INFA_STAFF);
                        SmsUtils.sendSMSForUser(smsDTOHtt, cms, env);

                        // Gui tin nhan cho KTT
                        SmsDTO smsDTOKtt = new SmsDTO();
                        smsDTOKtt.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                        message = String.format(templateApprove.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow));
                        smsDTOKtt.setMessage(message);
                        smsDTOKtt.setUserRole(Constant.CMS_ROLES.CMS_PROV_TECH_STAFF);
                        SmsUtils.sendSMSForUser(smsDTOKtt, cms, env);
                    }

                    // Check cac hang muc con khac de update cha
                    // Lay danh sach cac con
                    List<ConstructionDetail> listChildConstructionDetail = constructionService.getListConstructionDetailByParentId(constructionDetail.getConstructionId(), Constant.VALIDATE_CONSTRUCTION_STATUS.VALIDATE, constructionDetail.getParentId());

                    if (listChildConstructionDetail != null && !listChildConstructionDetail.isEmpty()){
                        for (ConstructionDetail child : listChildConstructionDetail){
                            if (!StringUtils.isStringNullOrEmpty(child) && !child.getStatus().equals(7L)){
                                return;
                            }
                        }
                    }

                    updateConstructionItemStatus(constructionDetail.getConstructionId(), Constant.CONSTRUCTION_DETAIL.STATUS_COMPLETE, userName, constructionDetail.getParentId());

                }
                }
            }
            if (Constant.APPROVE_TYPE.REJECT == commonInputDTO.getConstructionDetailDTO().getApproveType()) {
                if (Constant.CONSTRUCTION_DETAIL.STATUS_REQUESTED == constructionDetail.getStatus()) {
                    Long oldStatus = constructionDetail.getStatus();
                    String oldApprovedDate = "";
                    if (!StringUtils.isStringNullOrEmpty(constructionDetail.getFirstRejectDate())) {
                        oldApprovedDate = DataUtils.getStringDateTime(constructionDetail.getFirstRejectDate());
                    }
                    String oldApprovedBy = constructionDetail.getFirstRejectBy();
                    String oldRejectReason = constructionDetail.getFirstRejectReason();
                    constructionDetail.setFirstRejectDate(dateNow);
                    constructionDetail.setFirstRejectBy(userName);
                    constructionDetail.setStatus(Long.valueOf(Constant.CONSTRUCTION_DETAIL.STATUS_REJECTED_1));
                    constructionDetail.setFirstRejectReason(commonInputDTO.getConstructionDetailDTO().getRejectReason());

                    // Ghi log
                    // Them log vao bang action log
                    Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
                    ActionLog actionLog = FunctionUtils.saveActionLog(staff, "PROV_INFA_REJECT_CONSTRUCTION_ITEM", idActionLog, constructionDetail.getConstructionDetailId());
                    actionLogRepo.save(actionLog);

                    //Them log vao bang action log detail
                    Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                    ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                            commonInputDTO.getImageId(),
                            "status",
                            String.valueOf(oldStatus),
                            "4",
                            idActionLog,
                            idActionLogDetailUser);
                    actionLogDetailRepo.save(actionLogDetail);
                    Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                    ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                            commonInputDTO.getImageId(),
                            "first_reject_datetime",
                            oldApprovedDate,
                            null,
                            idActionLog,
                            idActionLogDetailUser1);
                    actionLogDetailRepo.save(actionLogDetail1);
                    Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                    ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                            commonInputDTO.getImageId(),
                            "first_reject_by",
                            oldApprovedBy,
                            userName,
                            idActionLog,
                            idActionLogDetailUser2);
                    actionLogDetailRepo.save(actionLogDetail2);
                    Long idActionLogDetailUser3 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                    ActionLogDetail actionLogDetail3 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                            commonInputDTO.getImageId(),
                            "first_reject_reason",
                            oldRejectReason,
                            constructionDetail.getFirstRejectReason(),
                            idActionLog,
                            idActionLogDetailUser3);
                    actionLogDetailRepo.save(actionLogDetail3);

                    // Gui tin nhan khi từ chối hạng mục cho nhan vien KTT
                    if (templateReject != null) {
                        SmsDTO smsDTO = new SmsDTO();
                        smsDTO.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                        String message = String.format(templateReject.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow), commonInputDTO.getConstructionDetailDTO().getRejectReason());
                        smsDTO.setMessage(message);
                        smsDTO.setUserRole(Constant.CMS_ROLES.CMS_PROV_TECH_STAFF);
                        SmsUtils.sendSMSForUser(smsDTO, cms, env);
                    }

                } else if (Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_1 == constructionDetail.getStatus()) {
                    Long oldStatus = constructionDetail.getStatus();
                    String oldApprovedDate = "";
                    if (!StringUtils.isStringNullOrEmpty(constructionDetail.getSecondRejectDate())) {
                        oldApprovedDate = DataUtils.getStringDateTime(constructionDetail.getSecondRejectDate());
                    }
                    String oldApprovedBy = constructionDetail.getSecondRejectBy();
                    String oldRejectReason = constructionDetail.getSecondRejectReason();
                    constructionDetail.setSecondRejectDate(dateNow);
                    constructionDetail.setSecondRejectBy(userName);
                    constructionDetail.setStatus(Long.valueOf(Constant.CONSTRUCTION_DETAIL.STATUS_REJECTED_2));
                    constructionDetail.setSecondRejectReason(commonInputDTO.getConstructionDetailDTO().getRejectReason());

                    // Ghi log
                    // Them log vao bang action log
                    Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
                    ActionLog actionLog = FunctionUtils.saveActionLog(staff, "PROV_VICE_REJECT_CONSTRUCTION_ITEM", idActionLog, constructionDetail.getConstructionDetailId());
                    actionLogRepo.save(actionLog);

                    //Them log vao bang action log detail
                    Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                    ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                            commonInputDTO.getImageId(),
                            "status",
                            String.valueOf(oldStatus),
                            "6",
                            idActionLog,
                            idActionLogDetailUser);
                    actionLogDetailRepo.save(actionLogDetail);
                    Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                    ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                            commonInputDTO.getImageId(),
                            "second_reject_datetime",
                            oldApprovedDate,
                            null,
                            idActionLog,
                            idActionLogDetailUser1);
                    actionLogDetailRepo.save(actionLogDetail1);
                    Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                    ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                            commonInputDTO.getImageId(),
                            "second_reject_by",
                            oldApprovedBy,
                            userName,
                            idActionLog,
                            idActionLogDetailUser2);
                    actionLogDetailRepo.save(actionLogDetail2);
                    Long idActionLogDetailUser3 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                    ActionLogDetail actionLogDetail3 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                            commonInputDTO.getImageId(),
                            "second_reject_reason",
                            oldRejectReason,
                            constructionDetail.getSecondRejectReason(),
                            idActionLog,
                            idActionLogDetailUser3);
                    actionLogDetailRepo.save(actionLogDetail3);

                    if (templateReject != null) {
                        // Gui tin nhan khi từ chối hạng mục cho nhan vien HTT
                        SmsDTO smsDTO = new SmsDTO();
                        smsDTO.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                        String message = String.format(templateReject.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow), commonInputDTO.getConstructionDetailDTO().getRejectReason());
                        smsDTO.setMessage(message);
                        smsDTO.setUserRole(Constant.CMS_ROLES.CMS_PROV_INFA_STAFF);
                        SmsUtils.sendSMSForUser(smsDTO, cms, env);

                        // Gui tin nhan khi từ chối hạng mục cho nhan vien KTT
                        SmsDTO smsDTOKTT = new SmsDTO();
                        smsDTOKTT.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                        message = String.format(templateReject.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow), commonInputDTO.getConstructionDetailDTO().getRejectReason());
                        smsDTOKTT.setMessage(message);
                        smsDTOKTT.setUserRole(Constant.CMS_ROLES.CMS_PROV_TECH_STAFF);
                        SmsUtils.sendSMSForUser(smsDTOKTT, cms, env);
                    }

                } else if (Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_2 == constructionDetail.getStatus()) {
                    Long oldStatus = constructionDetail.getStatus();
                    String oldApprovedDate = "";
                    if (!StringUtils.isStringNullOrEmpty(constructionDetail.getThirdRejectDate())) {
                        oldApprovedDate = DataUtils.getStringDateTime(constructionDetail.getThirdRejectDate());
                    }
                    String oldApprovedBy = constructionDetail.getThirdRejectBy();
                    String oldRejectReason = constructionDetail.getThirdRejectReason();
                    constructionDetail.setThirdRejectDate(dateNow);
                    constructionDetail.setThirdRejectBy(userName);
                    constructionDetail.setStatus(Long.valueOf(Constant.CONSTRUCTION_DETAIL.STATUS_REJECTED_3));
                    constructionDetail.setThirdRejectReason(commonInputDTO.getConstructionDetailDTO().getRejectReason());

                    // Ghi log
                    // Them log vao bang action log
                    Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
                    ActionLog actionLog = FunctionUtils.saveActionLog(staff, "CORP_REJECT_CONSTRUCTION_ITEM", idActionLog, constructionDetail.getConstructionDetailId());
                    actionLogRepo.save(actionLog);

                    //Them log vao bang action log detail
                    Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                    ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                            commonInputDTO.getImageId(),
                            "status",
                            String.valueOf(oldStatus),
                            "8",
                            idActionLog,
                            idActionLogDetailUser);
                    actionLogDetailRepo.save(actionLogDetail);
                    Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                    ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                            commonInputDTO.getImageId(),
                            "third_reject_datetime",
                            oldApprovedDate,
                            null,
                            idActionLog,
                            idActionLogDetailUser1);
                    actionLogDetailRepo.save(actionLogDetail1);
                    Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                    ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                            commonInputDTO.getImageId(),
                            "third_reject_by",
                            oldApprovedBy,
                            userName,
                            idActionLog,
                            idActionLogDetailUser2);
                    actionLogDetailRepo.save(actionLogDetail2);
                    Long idActionLogDetailUser3 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                    ActionLogDetail actionLogDetail3 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                            commonInputDTO.getImageId(),
                            "third_reject_reason",
                            oldRejectReason,
                            constructionDetail.getThirdRejectReason(),
                            idActionLog,
                            idActionLogDetailUser3);
                    actionLogDetailRepo.save(actionLogDetail3);

                    if (templateReject != null) {
                        // Gui tin nhan khi từ chối hạng mục cho nhan vien HTT
                        SmsDTO smsDTO = new SmsDTO();
                        smsDTO.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                        String message = String.format(templateReject.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow), commonInputDTO.getConstructionDetailDTO().getRejectReason());
                        smsDTO.setMessage(message);
                        smsDTO.setUserRole(Constant.CMS_ROLES.CMS_PROV_INFA_STAFF);
                        SmsUtils.sendSMSForUser(smsDTO, cms, env);

                        // Gui tin nhan khi từ chối hạng mục cho nhan vien KTT
                        SmsDTO smsDTOKTT = new SmsDTO();
                        smsDTOKTT.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                        message = String.format(templateReject.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow), commonInputDTO.getConstructionDetailDTO().getRejectReason());
                        smsDTOKTT.setMessage(message);
                        smsDTOKTT.setUserRole(Constant.CMS_ROLES.CMS_PROV_TECH_STAFF);
                        SmsUtils.sendSMSForUser(smsDTOKTT, cms, env);

                        // Gui tin nhan khi từ chối hạng mục cho PGD CN
                        SmsDTO smsDTOPGD = new SmsDTO();
                        smsDTOPGD.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                        message = String.format(templateReject.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow), commonInputDTO.getConstructionDetailDTO().getRejectReason());
                        smsDTOPGD.setMessage(message);
                        smsDTOPGD.setUserRole(Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT);
                        SmsUtils.sendSMSForUser(smsDTOPGD, cms, env);
                    }
                }
            }

            constructionDetailRepo.save(constructionDetail);

            /*
            ghi log
             */
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public int updateConstructionItemStatus(Long constructionId, int status, String userName, Long parentId) throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE   construction_detail "
                    + "   SET   status = :status , third_approved_date = :updateDate, third_approved_by = :updateBy, complete_date = :updateDate , complete_by = :updateBy "
                    + " WHERE   construction_id = :constructionId and construction_item_id = :parentId ");
            Query query = cms.createNativeQuery(sql.toString());
            query.setParameter("updateDate", LocalDateTime.now());
            query.setParameter("updateBy", userName);
            query.setParameter("constructionId", constructionId);
            query.setParameter("parentId", parentId);
            query.setParameter("status", status);
            return query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    @Override
    public ConstructionDetail getConstructionDetailById(Long constructionDetailId) throws Exception {
        try {
            Optional<ConstructionDetail> result = constructionDetailRepo.findById(constructionDetailId);
            ConstructionDetail constructionDetail = result.orElse(null);
            return constructionDetail;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<ConstructionDetail> getListCDWithStep(Long constructionId, Long step) throws Exception {
        try {
            List<ConstructionDetail> result = new ArrayList<>();
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT " +
                    "  cd.*  " +
                    " FROM " +
                    "  construction_detail cd, " +
                    "  construction_item ci  " +
                    "WHERE " +
                    "  cd.construction_item_id = ci.construction_item_id  " +
                    "  AND cd.construction_id = :constructionId  " +
                    "  AND ci.step < :step " +
                    "  AND cd.status <> 7 ");
            Query query = cms.createNativeQuery(sql.toString(), ConstructionDetail.class);
            query.setParameter("constructionId", constructionId);
            query.setParameter("step", step);
//            query.setParameter("status", Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_3);
            result = query.getResultList();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<ConstructionItemDTO> getListConstructionItem(ResourceBundle r) throws Exception {
        try{
            List<ConstructionItemDTO> constructionItemDTOS = new ArrayList<>();
            String sql = " SELECT   "
                    + " ci.construction_item_id , "
                    + " ci.name , "
                    + " ci.step , "
                    + " ci.created_date , "
                    + " ci.created_by , "
                    + " ci.last_modified_date , "
                    + " ci.last_modified_by , "
                    + " ci.status , "
                    + " ci.`type` "
                    + "  FROM   construction_item ci "
                    + " WHERE   status = 1 "
                    + " AND type = 2 "
                    + " ORDER BY construction_item_id asc";
            Query query = cms.createNativeQuery(sql);
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    ConstructionItemDTO option = new ConstructionItemDTO();
                    option.setConstructionItemId(DataUtils.getLong(obj[0].toString()));
                    option.setConstructionItemName(r.getResourceMessage(DataUtils.getString(obj[1])));
                    if (obj[2] != null) {
                        option.setStep(DataUtils.getLong(obj[2]));
                    }
                    if (obj[3] != null) {
                        option.setCreateDatetime(DataUtils.stringToLocalDateTme(DataUtils.getString(obj[3])));
                    }
                    option.setCreateBy(DataUtils.getString(obj[4]));
                    if (obj[5] != null) {
                        option.setLastModifiedDatetime(DataUtils.stringToLocalDateTme(DataUtils.getString(obj[5])));
                    }
                    option.setLastModifiedBy(DataUtils.getString(obj[6]));
                    option.setStatus(Long.valueOf(obj[7].toString()));
                    option.setType(DataUtils.getLong(obj[8]));
                    option.setChosen(0L);
                    constructionItemDTOS.add(option);
                }
            }
            return constructionItemDTOS;
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int approveImage(CommonInputDTO commonInputDTO, Staff staff, Long status) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append(" update image_info ii "
                + "  set "
                + "  ii.status = :status , approved_datetime =:approvedDatetime, "
                + "  ii.approved_by = :approvedBy "
                + "  where ii.image_id = :imageId ");
        Query query = cms.createNativeQuery(sql.toString());
        query.setParameter("status", status);
        query.setParameter("approvedDatetime", LocalDateTime.now());
        query.setParameter("approvedBy", commonInputDTO.getUserName().split("----")[0]);
        query.setParameter("imageId", commonInputDTO.getImageId());

        // Ghi log
        // Them log vao bang action log
        Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
        ActionLog actionLog = FunctionUtils.saveActionLog(staff, "APPROVED_IMAGE", idActionLog, commonInputDTO.getImageId());
        actionLogRepo.save(actionLog);

        //Them log vao bang action log detail
        Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
        ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("IMAGE_INFO",
                commonInputDTO.getImageId(),
                "status",
                "1",
                "2",
                idActionLog,
                idActionLogDetailUser);
        actionLogDetailRepo.save(actionLogDetail);
        Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
        ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("IMAGE_INFO",
                commonInputDTO.getImageId(),
                "approved_datetime",
                null,
                DataUtils.getStringDateTime(LocalDateTime.now()),
                idActionLog,
                idActionLogDetailUser1);
        actionLogDetailRepo.save(actionLogDetail1);
        Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
        ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("IMAGE_INFO",
                commonInputDTO.getImageId(),
                "approved_by",
                null,
                commonInputDTO.getUserName().split("----")[0],
                idActionLog,
                idActionLogDetailUser2);
        actionLogDetailRepo.save(actionLogDetail2);

        return query.executeUpdate();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int rejectImage(CommonInputDTO commonInputDTO, Staff staff, Long status) throws Exception {
        StringBuilder sql = new StringBuilder();
        sql.append(" update image_info ii "
                + "  set "
                + "  ii.status = :status , reject_datetime =:rejectDatetime, "
                + "  ii.reject_by = :rejectBy , "
                + "  ii.reject_reason = :rejectReason , "
                + "  ii.description = :description "
                + "  where ii.image_id = :imageId ");
        Query query = cms.createNativeQuery(sql.toString());
        query.setParameter("status", status);
        query.setParameter("rejectDatetime", LocalDateTime.now());
        query.setParameter("rejectBy", commonInputDTO.getUserName().split("----")[0]);
        query.setParameter("rejectReason", commonInputDTO.getRejectReason());
        query.setParameter("description", commonInputDTO.getDescription());
        query.setParameter("imageId", commonInputDTO.getImageId());

        // Ghi log
        // Them log vao bang action log
        Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
        ActionLog actionLog = FunctionUtils.saveActionLog(staff, "REJECT_IMAGE", idActionLog, commonInputDTO.getImageId());
        actionLogRepo.save(actionLog);

        //Them log vao bang action log detail
        Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
        ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("IMAGE_INFO",
                commonInputDTO.getImageId(),
                "status",
                "1",
                "2",
                idActionLog,
                idActionLogDetailUser);
        actionLogDetailRepo.save(actionLogDetail);
        Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
        ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("IMAGE_INFO",
                commonInputDTO.getImageId(),
                "reject_datetime",
                null,
                DataUtils.getStringDateTime(LocalDateTime.now()),
                idActionLog,
                idActionLogDetailUser1);
        actionLogDetailRepo.save(actionLogDetail1);
        Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
        ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("IMAGE_INFO",
                commonInputDTO.getImageId(),
                "reject_by",
                null,
                commonInputDTO.getUserName().split("----")[0],
                idActionLog,
                idActionLogDetailUser2);
        actionLogDetailRepo.save(actionLogDetail2);

        Long idActionLogDetailUser3 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
        ActionLogDetail actionLogDetail3 = FunctionUtils.saveActionLogDetail("IMAGE_INFO",
                commonInputDTO.getImageId(),
                "reject_reason",
                null,
                commonInputDTO.getRejectReason(),
                idActionLog,
                idActionLogDetailUser3);
        actionLogDetailRepo.save(actionLogDetail3);

        Long idActionLogDetailUser4 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
        ActionLogDetail actionLogDetail4 = FunctionUtils.saveActionLogDetail("IMAGE_INFO",
                commonInputDTO.getImageId(),
                "description",
                null,
                commonInputDTO.getDescription(),
                idActionLog,
                idActionLogDetailUser4);
        actionLogDetailRepo.save(actionLogDetail4);

        return query.executeUpdate();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteImageListReject(CommonInputDTO commonInputDTO, Staff staff) throws Exception {
        for (ImageDTO imageDTO : commonInputDTO.getImageDTOList()) {
            StringBuilder sql = new StringBuilder();
            sql.append(" update image_info ii "
                    + "  set "
                    + "  ii.status = 0 "
                    + "  where ii.image_id = :imageId ");
            Query query = cms.createNativeQuery(sql.toString());
            query.setParameter("imageId", imageDTO.getImageId());
            query.executeUpdate();
            // Ghi log
            // Them log vao bang action log
            Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
            ActionLog actionLog = FunctionUtils.saveActionLog(staff, "DELETE_IMAGE_REJECT", idActionLog, imageDTO.getImageId());
            actionLogRepo.save(actionLog);

            //Them log vao bang action log detail
            Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("IMAGE_INFO",
                    commonInputDTO.getImageId(),
                    "status",
                    "2",
                    "0",
                    idActionLog,
                    idActionLogDetailUser);
            actionLogDetailRepo.save(actionLogDetail);
            Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("IMAGE_INFO",
                    commonInputDTO.getImageId(),
                    "last_update_datetime",
                    null,
                    DataUtils.getStringDateTime(LocalDateTime.now()),
                    idActionLog,
                    idActionLogDetailUser1);
            actionLogDetailRepo.save(actionLogDetail1);

            Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("IMAGE_INFO",
                    commonInputDTO.getImageId(),
                    "last_update_by",
                    null,
                    commonInputDTO.getUserName().split("----")[0],
                    idActionLog,
                    idActionLogDetailUser2);
            actionLogDetailRepo.save(actionLogDetail2);

        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteImage(CommonInputDTO commonInputDTO, ImageModel image, Staff staff) throws Exception {
            StringBuilder sql = new StringBuilder();
            sql.append(" update image_info ii "
                    + "  set "
                    + "  ii.status = 0 "
                    + "  where ii.image_id = :imageId ");
            Query query = cms.createNativeQuery(sql.toString());
            query.setParameter("imageId", commonInputDTO.getImageId());
            query.executeUpdate();
            // Ghi log
            // Them log vao bang action log
            Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
            ActionLog actionLog = FunctionUtils.saveActionLog(staff, "DELETE_IMAGE_REJECT", idActionLog, commonInputDTO.getImageId());
            actionLogRepo.save(actionLog);

            //Them log vao bang action log detail
            Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("IMAGE_INFO",
                    commonInputDTO.getImageId(),
                    "status",
                    String.valueOf(image.getStatus()),
                    "0",
                    idActionLog,
                    idActionLogDetailUser);
            actionLogDetailRepo.save(actionLogDetail);
            Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("IMAGE_INFO",
                    commonInputDTO.getImageId(),
                    "last_update_datetime",
                    null,
                    DataUtils.getStringDateTime(LocalDateTime.now()),
                    idActionLog,
                    idActionLogDetailUser1);
            actionLogDetailRepo.save(actionLogDetail1);

            Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("IMAGE_INFO",
                    commonInputDTO.getImageId(),
                    "last_update_by",
                    null,
                    commonInputDTO.getUserName().split("----")[0],
                    idActionLog,
                    idActionLogDetailUser2);
            actionLogDetailRepo.save(actionLogDetail2);
    }

    @Override
    public List<ActionLogDTO> getConstructionItemHistory(Long constructionDetailId, ResourceBundle r) throws Exception {
        try{
            List<ActionLogDTO> actionLogDTOList = new ArrayList<>();
            String sql = " SELECT   "
                    + " al.action_log_id , "
                    + " ( select "
                    + " s.staff_name "
                    + " from  "
                    + " staff s "
                    + " where "
                    + " s.staff_id = al.staff_id) create_by, "
                    + " date_format(al.create_datetime,'%d/%m/%Y %H:%i:%s') create_datetime, "
                    + " al.description "
                    + "  FROM  "
                    + " action_log al "
                    + " WHERE "
                    + " al.row_id = :constructionDetailId "
                    + " and al.description not in ('DELETE_IMAGE_REJECT', 'REJECT_IMAGE', 'APPROVED_IMAGE' ) "
                    + "  order by al.create_datetime desc ";
            Query query = cms.createNativeQuery(sql);
            query.setParameter("constructionDetailId", constructionDetailId);
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    ActionLogDTO option = new ActionLogDTO();
                    option.setActionLogId(DataUtils.getLong(obj[0].toString()));
                    option.setCreateBy(DataUtils.getString(obj[1]));
                    option.setCreateDatetimeStr(DataUtils.getString(obj[2]));
                    option.setDescription(DataUtils.getString(obj[3]));
                    if (!StringUtils.isStringNullOrEmpty(option.getDescription())){
                        if ("CREATE_APPROVE_REQUEST".equals(option.getDescription())){
                            option.setStatusName(r.getResourceMessage("create.approve.request"));
                        } else if ("PROV_INFA_APPROVE_CONSTRUCTION_ITEM".equals(option.getDescription())){
                            option.setStatusName(r.getResourceMessage("prov.infa.approve.construction_item"));
                        }else if ("PROV_VICE_APPROVE_CONSTRUCTION_ITEM".equals(option.getDescription())){
                            option.setStatusName(r.getResourceMessage("prov.vice.approve.construction_item"));
                        }else if ("CORP_APPROVE_CONSTRUCTION_ITEM".equals(option.getDescription())){
                            option.setStatusName(r.getResourceMessage("corp.approve.construction_item"));
                        }else if ("PROV_INFA_REJECT_CONSTRUCTION_ITEM".equals(option.getDescription())){
                            option.setStatusName(r.getResourceMessage("prov.infa.reject.construction_item"));
                        }else if ("PROV_VICE_REJECT_CONSTRUCTION_ITEM".equals(option.getDescription())){
                            option.setStatusName(r.getResourceMessage("prov.vice.reject.construction_item"));
                        }else if ("CORP_REJECT_CONSTRUCTION_ITEM".equals(option.getDescription())){
                            option.setStatusName(r.getResourceMessage("corp.reject.construction_item"));
                        }else if ("CORP_COMPLETE_CONSTRUCTION_ITEM".equals(option.getDescription())){
                            option.setStatusName(r.getResourceMessage("corp.reject.construction_item"));
                        }
                    }
                    actionLogDTOList.add(option);
                }
            }
            return actionLogDTOList;
        } catch (Exception e){
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvedFinishConstructionItem(CommonInputDTO commonInputDTO, Staff staff,String userName, ResourceBundle r) throws Exception {
        try {
            ConstructionDetail constructionDetail = getConstructionDetailById(commonInputDTO.getConstructionDetailDTO().getConstructionDetailId());
            LocalDateTime dateNow = LocalDateTime.now();
            OptionSetValue templateApprove = optionSetValueService.getListByOptionSetValueName(Constant.SMS_CONFIG.SMS, commonInputDTO.getLanguage(), Constant.SMS_CONFIG.APPROVE_SMS);
            OptionSetValue templateRequestApprove = optionSetValueService.getListByOptionSetValueName(Constant.SMS_CONFIG.SMS, commonInputDTO.getLanguage(), Constant.SMS_CONFIG.REQUEST_APPROVE);
            OptionSetValue templateReject = optionSetValueService.getListByOptionSetValueName(Constant.SMS_CONFIG.SMS, commonInputDTO.getLanguage(), Constant.SMS_CONFIG.REJECT_SMS);
            if (Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_3 == constructionDetail.getStatus()) {
                Long oldStatus = constructionDetail.getStatus();
                String oldApprovedDate = "";
                if (!StringUtils.isStringNullOrEmpty(constructionDetail.getCompleteDate())) {
                    oldApprovedDate = DataUtils.getStringDateTime(constructionDetail.getCompleteDate());
                }
                String oldApprovedBy = constructionDetail.getCompleteBy();
                constructionDetail.setCompleteDate(dateNow);
                constructionDetail.setCompleteBy(userName);
                constructionDetail.setStatus(Long.valueOf(Constant.CONSTRUCTION_DETAIL.STATUS_COMPLETE));
                // Ghi log
                // Them log vao bang action log
                Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
                ActionLog actionLog = FunctionUtils.saveActionLog(staff, "CORP_COMPLETE_CONSTRUCTION_ITEM", idActionLog, constructionDetail.getConstructionDetailId());
                actionLogRepo.save(actionLog);

                //Them log vao bang action log detail
                Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                        commonInputDTO.getImageId(),
                        "status",
                        String.valueOf(oldStatus),
                        "9",
                        idActionLog,
                        idActionLogDetailUser);
                actionLogDetailRepo.save(actionLogDetail);
                Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                        commonInputDTO.getImageId(),
                        "complete_date",
                        oldApprovedDate,
                        DataUtils.getStringDateTime(constructionDetail.getCompleteDate()),
                        idActionLog,
                        idActionLogDetailUser1);
                actionLogDetailRepo.save(actionLogDetail1);
                Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
                ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("CONSTRUCTION_DETAIL",
                        commonInputDTO.getImageId(),
                        "complete_by",
                        oldApprovedBy,
                        userName,
                        idActionLog,
                        idActionLogDetailUser2);
                actionLogDetailRepo.save(actionLogDetail2);

                OptionSetValue templateFinish = optionSetValueService.getListByOptionSetValueName(Constant.SMS_CONFIG.SMS, commonInputDTO.getLanguage(), Constant.SMS_CONFIG.ACCEPTANCE_SMS);
                // Gui tin nhan khi phê duyệt hạng mục hoàn tất cho nhan vien HTT
//                if (templateFinish != null) {
//                    // Gui tin nhan cho PGD
//                    SmsDTO smsDTO = new SmsDTO();
//                    smsDTO.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
//                    String message = String.format(templateFinish.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow));
//                    smsDTO.setMessage(message);
//                    smsDTO.setUserRole(Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT);
//                    SmsUtils.sendSMSForUser(smsDTO, cms, env);
//
//                    // Gui tin nhan cho HTT
//                    SmsDTO smsDTOHtt = new SmsDTO();
//                    smsDTOHtt.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
//                    message = String.format(templateFinish.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow));
//                    smsDTOHtt.setMessage(message);
//                    smsDTOHtt.setUserRole(Constant.CMS_ROLES.CMS_PROV_INFA_STAFF);
//                    SmsUtils.sendSMSForUser(smsDTOHtt, cms, env);
//
//                    // Gui tin nhan cho KTT
//                    SmsDTO smsDTOKtt = new SmsDTO();
//                    smsDTOKtt.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
//                    message = String.format(templateApprove.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName(), commonInputDTO.getConstructionDetailDTO().getName(), userName, DataUtils.getStringDateTime(dateNow));
//                    smsDTOKtt.setMessage(message);
//                    smsDTOKtt.setUserRole(Constant.CMS_ROLES.CMS_PROV_TECH_STAFF);
//                    SmsUtils.sendSMSForUser(smsDTOKtt, cms, env);
//                }

            }
            constructionDetailRepo.save(constructionDetail);

            /*
            ghi log
             */
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public ConstructionDetail getConstructionDetailParent(Long constructionId, Long parentId) throws Exception {
        try {
            ConstructionDetail constructionDetail = new ConstructionDetail();
            StringBuilder sql = new StringBuilder();
            sql.append(" select * from construction_detail where construction_id = :constructionId and parent_id is null AND construction_item_id = :parentId and status <> 0 ");
            Query query = cms.createNativeQuery(sql.toString(), ConstructionDetail.class);
            query.setParameter("constructionId", constructionId);
            query.setParameter("parentId", parentId);
            List<ConstructionDetail> constructionDetailList = query.getResultList();
            if (constructionDetailList != null && !constructionDetailList.isEmpty()){
                constructionDetail = constructionDetailList.get(0);
            }
            return constructionDetail;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    void updateConstructionDetail(ConstructionDetail constructionDetailDTO) {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("update construction_detail "
                    + "set "
                    + "status = :status, acceptance_date =:acceptanceDate, "
                    + "acceptance_by = :acceptanceBy ");
            if (constructionDetailDTO.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_REJECTED_1) {
                sql.append(", first_reject_date = NULL, first_reject_by = NULL, first_reject_reason = NULL ");
            }
            if (constructionDetailDTO.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_REJECTED_2) {
                sql.append(", first_approved_date = NULL, first_approved_by = NULL, second_reject_date = NULL, second_reject_by = NULL, second_reject_reason = NULL ");
            }
            if (constructionDetailDTO.getStatus() == Constant.CONSTRUCTION_DETAIL.STATUS_REJECTED_3) {
                sql.append(", first_approved_date = NULL, first_approved_by = NULL, second_approved_date = NULL, second_approved_by = NULL, third_reject_date = NULL, third_reject_by = NULL, third_reject_reason = NULL ");
            }
            sql.append(" where "
                    + "construction_detail_id = :constructionDetailId and status = :oldStatus ");
            Query query = cms.createNativeQuery(sql.toString());
            query.setParameter("acceptanceDate", constructionDetailDTO.getAcceptanceDate());
            query.setParameter("acceptanceBy", constructionDetailDTO.getAcceptanceBy());
//            query.setParameter("imagePath", constructionDetailDTO.getImagePath());
            query.setParameter("constructionDetailId", constructionDetailDTO.getConstructionDetailId());
            query.setParameter("status", Constant.CONSTRUCTION_DETAIL.STATUS_REQUESTED);
            query.setParameter("oldStatus", constructionDetailDTO.getStatus());
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
