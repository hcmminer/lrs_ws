package com.viettel.base.cms.serviceImpl;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.*;
import com.viettel.base.cms.model.*;
import com.viettel.base.cms.repo.ConstructionDetailHisRepo;
import com.viettel.base.cms.repo.ConstructionDetailRepo;
import com.viettel.base.cms.repo.ConstructionItemRepo;
import com.viettel.base.cms.repo.ConstructionRepo;
import com.viettel.base.cms.service.*;
import com.viettel.base.cms.utils.FunctionUtils;
import com.viettel.base.cms.utils.SmsUtils;
import com.viettel.vfw5.base.dto.ExecutionResult;
import com.viettel.vfw5.base.utils.DataUtils;
import com.viettel.vfw5.base.utils.ResourceBundle;
import com.viettel.vfw5.base.utils.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import org.springframework.core.env.Environment;

@Service
public class ConstructionServiceImpl implements ConstructionService {

    @Autowired
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private EntityManager cms;

    @Autowired
    ConstructionRepo constructionRepo;

    @Autowired
    OptionSetValueService optionSetValueService;

    @Autowired
    ConstructionDetailRepo constructionDetailRepo;

    @Autowired
    ConstructionItemRepo constructionItemRepo;

    @Autowired
    LocationService locationService;

    @Autowired
    ConstructionService constructionService;

    @Autowired
    FileUploadService fileUploadService;

    @Autowired
    UserService userService;

    @Autowired
    ConstructionManagementService constructionManagementService;

    @Autowired
    ConstructionDetailHisRepo constructionDetailHisRepo;

    @Autowired
    Environment env;

    @Override
    public List<ConstructionTypeDTO> getListConstructionType(String language) throws Exception {
        try {
            List<ConstructionTypeDTO> constructionTypeDTOS = new ArrayList<>();
            String sql = " SELECT   * "
                    + "  FROM   construction_type "
                    + " WHERE  status = 1 "
                    + " AND `LANGUAGE` = :language "
                    + " order by name asc";
            Query query = cms.createNativeQuery(sql, ConstructionType.class);
            query.setParameter("language", language);
            List<ConstructionType> constructionTypes = query.getResultList();
            for (ConstructionType constructionType : constructionTypes) {
//                ConstructionTypeDTO constructionItemDTO = new ConstructionTypeDTO(
//                        constructionType.getConstructionTypeId(),
//                        constructionType.getName(),
//                        constructionType.getCreatedDate(),
//                        constructionType.getCreatedBy(),
//                        constructionType.getLastModifiedDate(),
//                        constructionType.getLastModifiedBy(),
//                        constructionType.getStatus());
                ConstructionTypeDTO constructionItemDTO = new ConstructionTypeDTO(
                        constructionType.getConstructionTypeId(),
                        constructionType.getName(),
                        null,
                        null,
                        null,
                        null,
                        null);
                constructionTypeDTOS.add(constructionItemDTO);
            }
            return constructionTypeDTOS;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<ConstructionItemDTO> getListConstructionItem(Long constructionId, ResourceBundle r) throws Exception {
        try {
            List<ConstructionItemDTO> constructionItemDTOS = new ArrayList<>();
            String sql = " SELECT   *"
                    + "  FROM   construction_item"
                    + " WHERE   status = 1 "
                    + " AND type = 1 "
                    + " ORDER BY created_date desc";
            Query query = cms.createNativeQuery(sql, ConstructionItem.class);
            List<ConstructionItem> constructionItems = query.getResultList();
            for (ConstructionItem constructionItem : constructionItems) {
                ConstructionItemDTO constructionItemDTO = new ConstructionItemDTO(
                        constructionItem.getConstructionItemId(),
                        r.getResourceMessage(constructionItem.getName()),
                        constructionItem.getStep(),
                        constructionItem.getStatus(),
                        Long.valueOf(Constant.CONSTRUCTION_ITEM_STATUS.NOT_CHOSEN),
                        constructionItem.getCreatedDate(),
                        constructionItem.getCreatedBy(),
                        constructionItem.getLastModifiedDate(),
                        constructionItem.getLastModifiedBy(),
                        constructionItem.getType(),
                        constructionManagementService.getListConstructionItem(r)
                );
                List<ConstructionDetail> constructionDetailList = getListConstructionDetailByConstructionId(constructionId, Constant.VALIDATE_CONSTRUCTION_STATUS.NOT_VALIDATE);
                if (constructionDetailList.size() > 0) {
                    for (ConstructionDetail cd
                            : constructionDetailList) {
                        if (Objects.equals(cd.getConstructionItemId(), constructionItem.getConstructionItemId())) {
                            constructionItemDTO.setChosen(Long.valueOf(Constant.CONSTRUCTION_ITEM_STATUS.CHOSEN));
                            constructionItemDTO.setLstConstructionItem2(getListConstructionItem2(constructionId, r, constructionItemDTO.getConstructionItemId()));
                            break;
                        } else {
                            constructionItemDTO.setLstConstructionItem2(constructionManagementService.getListConstructionItem(r));
                        }
                    }
                }
                constructionItemDTOS.add(constructionItemDTO);
            }
            return constructionItemDTOS;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<ConstructionItemDTO> getListConstructionItem2(Long constructionId, ResourceBundle r, Long constructionItemId) throws Exception {
        try {
            List<ConstructionItemDTO> constructionItemDTOS = new ArrayList<>();
            String sql = " SELECT   *"
                    + "  FROM   construction_item"
                    + " WHERE   status = 1 "
                    + " AND type = 2 "
                    + " ORDER BY construction_item_id asc";
            Query query = cms.createNativeQuery(sql, ConstructionItem.class);
            List<ConstructionItem> constructionItems = query.getResultList();
            for (ConstructionItem constructionItem : constructionItems) {
                ConstructionItemDTO constructionItemDTO = new ConstructionItemDTO(
                        constructionItem.getConstructionItemId(),
                        r.getResourceMessage(constructionItem.getName()),
                        constructionItem.getStep(),
                        constructionItem.getStatus(),
                        Long.valueOf(Constant.CONSTRUCTION_ITEM_STATUS.NOT_CHOSEN),
                        constructionItem.getCreatedDate(),
                        constructionItem.getCreatedBy(),
                        constructionItem.getLastModifiedDate(),
                        constructionItem.getLastModifiedBy(),
                        constructionItem.getType(),
                        null
                );
                List<ConstructionDetail> constructionDetailList = getListConstructionDetailByConstructionId2(constructionId, Constant.VALIDATE_CONSTRUCTION_STATUS.NOT_VALIDATE, constructionItemId);
                if (constructionDetailList.size() > 0) {
                    for (ConstructionDetail cd
                            : constructionDetailList) {
                        if (Objects.equals(cd.getConstructionItemId(), constructionItem.getConstructionItemId())) {
                            constructionItemDTO.setChosen(Long.valueOf(Constant.CONSTRUCTION_ITEM_STATUS.CHOSEN));
                        }
                    }
                }
                constructionItemDTOS.add(constructionItemDTO);
            }
            return constructionItemDTOS;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<ConstructionDTO> searchConstruction(CommonInputDTO commonInputDTO, String roleCode, String language, ResourceBundle r) throws Exception {
        try {
            ConstructionDTO constructionDTO = commonInputDTO.getConstructionDTO();
            List<ConstructionDTO> lstResult = new ArrayList<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT * FROM (SELECT   c.construction_id, "
                    + "         c.construction_code, "
                    + "         c.construction_name, "
                    + "         c.station_type, "
                    + "         c.column_type, "
                    + "         c.construction_type ,  c.start_date , "
                    + "         status , c.province_code, c.complete_date, c.created_date");
            if (!Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleCode)) {
                sql.append(" , ( SELECT count(*) " +
                        "FROM construction_detail cd " +
                        "WHERE " +
                        " ( status = :sortStatus " +
                        "AND " +
                        "construction_id = c.construction_id  " +
                        " AND parent_id is null )" +
                        " OR construction_id in ( " +
                        " select construction_id " +
                        " FROM construction_detail cdd " +
                        " WHERE cdd.parent_id = cd.construction_item_id" +
                        " and cdd.status = :sortStatus " +
                        " and parent_id is not null " +
                        " and cdd.construction_id = c.construction_id " +
                        " )) count_item "
                );
            }
            sql.append("  FROM   construction c "
                    + " WHERE       status in (1,2,3,4) ");
            if (!StringUtils.isStringNullOrEmpty(constructionDTO)) {
                if (!StringUtils.isNullOrEmpty(constructionDTO.getProvinceCode())) {
                    sql.append("	AND c.province_code = :provinceCode ");
                }
                if (!StringUtils.isStringNullOrEmpty(constructionDTO.getConstructionType())) {
                    sql.append("	AND c.construction_type = :constructionType ");
                }
                if (!StringUtils.isStringNullOrEmpty(constructionDTO.getStatus())) {
                    sql.append("	AND c.status = :status ");
                }
                if (!StringUtils.isNullOrEmpty(constructionDTO.getConstructionCode())) {
                    sql.append("   AND LOWER(c.construction_code) LIKE LOWER(:constructionCode)  ");
                }
                if (!StringUtils.isNullOrEmpty(constructionDTO.getConstructionName())) {
                    sql.append("   AND LOWER(c.construction_name) LIKE LOWER(:constructionName)  ");
                }
            }
            if (Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(roleCode)) {
                sql.append(" AND status in (2,3,4) and c.province_code = :provinceCode ");
            }
            if (Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(roleCode) || Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleCode)) {
                sql.append(" AND status in (3,4) and c.province_code = :provinceCode ");
            }
            sql.append(" ) a ORDER BY   ");
            if (!Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleCode)) {
                sql.append(" a.count_item DESC, ");
            }
            sql.append(" a.created_date DESC, a.start_date, a.construction_code, a.construction_name ");

            Query query = cms.createNativeQuery(sql.toString());
            if (!StringUtils.isStringNullOrEmpty(constructionDTO)) {
                if (!StringUtils.isNullOrEmpty(constructionDTO.getProvinceCode())) {
                    query.setParameter("provinceCode", constructionDTO.getProvinceCode());
                }
                if (!StringUtils.isStringNullOrEmpty(constructionDTO.getConstructionType())) {
                    query.setParameter("constructionType", constructionDTO.getConstructionType());
                }
                if (!StringUtils.isStringNullOrEmpty(constructionDTO.getStatus())) {
                    query.setParameter("status", constructionDTO.getStatus());
                }
                if (!StringUtils.isNullOrEmpty(constructionDTO.getConstructionCode())) {
                    query.setParameter("constructionCode", "%" + constructionDTO.getConstructionCode() + "%");
                }
                if (!StringUtils.isNullOrEmpty(constructionDTO.getConstructionName())) {
                    query.setParameter("constructionName", "%" + constructionDTO.getConstructionName() + "%");
                }
            }
            if (Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(roleCode)) {
                query.setParameter("provinceCode", userService.getUserProvinceCode(commonInputDTO.getUserName().split("----")[0]));
                query.setParameter("sortStatus", Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_1);
            }
            if (Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(roleCode) || Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleCode)) {
                query.setParameter("provinceCode", userService.getUserProvinceCode(commonInputDTO.getUserName().split("----")[0]));
                if (Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(roleCode))
                    query.setParameter("sortStatus", Constant.CONSTRUCTION_DETAIL.STATUS_REQUESTED);
            }
            if (Constant.CMS_ROLES.CMS_CORP_STAFF.equals(roleCode)) {
                query.setParameter("sortStatus", Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_2);
            }
            if (Constant.CMS_ROLES.CMS_CORP_STAFF.equals(roleCode) || Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(roleCode) || Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleCode) || Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(roleCode)) {
                System.out.println("");
            } else {
                query.setParameter("sortStatus", Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_2);
            }
//            query.setParameter("optionSetCode", optionSetCode);
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    int i = 0;
                    ConstructionDTO constructionDTO1 = new ConstructionDTO();
//                    constructionDTO1.setConstructionId(Long.valueOf(obj[i++].toString()));
                    constructionDTO1.setConstructionId(DataUtils.getLong(obj[i++]));
                    constructionDTO1.setConstructionCode(DataUtils.getString(obj[i++]));
                    constructionDTO1.setConstructionName(DataUtils.getString(obj[i++]));
                    constructionDTO1.setStationType(DataUtils.getLong(obj[i++]));
                    constructionDTO1.setColumnType(DataUtils.getLong(obj[i++]));
                    constructionDTO1.setConstructionType(DataUtils.getLong(obj[i++]));
                    if (!StringUtils.isStringNullOrEmpty(obj[i])) {
                        constructionDTO1.setStartDate(LocalDateTime.parse(obj[i++].toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss.S")));
//                        constructionDTO1.setStartDateStr(FunctionUtils.localDateTimeToString(constructionDTO1.getStartDate()));
                    } else {
                        i++;
                    }
                    constructionDTO1.setStatus(DataUtils.getLong(obj[i++]));
                    constructionDTO1.setProvinceCode(DataUtils.getString(obj[i++]));
                    if (!StringUtils.isStringNullOrEmpty(obj[i])) {
                        constructionDTO1.setCompleteDate(LocalDateTime.parse(obj[i++].toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss.S")));
                    } else i++;
                    if (!StringUtils.isStringNullOrEmpty(obj[i])) {
                        constructionDTO1.setCreatedDate(LocalDateTime.parse(obj[i++].toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss.S")));
                    } else i++;
                    if (!Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleCode)) {
                        if (!StringUtils.isStringNullOrEmpty(obj[i])) {
                            constructionDTO1.setItemCount(Long.valueOf(obj[i].toString()));
                        }
                    }
                    List<Province> lisProvince = locationService.getListProvinceByCode(constructionDTO1.getProvinceCode());
                    if (!StringUtils.isStringNullOrEmpty(lisProvince) &&
                            !StringUtils.isStringNullOrEmpty(lisProvince.get(0))) {
                        constructionDTO1.setProvinceName(lisProvince.get(0).getProName());
                    }
                    constructionDTO1.setStatusName(optionSetValueService.getOptionSetValueNameByValue(
                            constructionDTO1.getStatus(),
                            Constant.CMS_OPTION_SET.CONSTRUCTION_STATUS,
                            language));
                    constructionDTO1.setColumnTypeName(optionSetValueService.getOptionSetValueNameByValue(
                            constructionDTO1.getColumnType(),
                            Constant.CMS_OPTION_SET.COLUMN_TYPE,
                            language));
                    constructionDTO1.setStationTypeName(optionSetValueService.getOptionSetValueNameByValue(
                            constructionDTO1.getStationType(),
                            Constant.CMS_OPTION_SET.STATION_TYPE,
                            language));
                    ConstructionType constructionType = getConstructionTypeById(constructionDTO1.getConstructionType());
                    if (!StringUtils.isStringNullOrEmpty(constructionType)) {
                        constructionDTO1.setConstructionTypeName(constructionType.getName());
                    }
                    constructionDTO1.setListConstructionItemName(getListConstructionItemName(constructionDTO1.getConstructionId(), r));
                    lstResult.add(constructionDTO1);
                }
            }
            return lstResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    public List<ConstructionDTO> searchConstructionForCM(CommonInputDTO commonInputDTO, String roleCode, String language, ResourceBundle r) throws Exception {
        try {
            ConstructionDTO constructionDTO = commonInputDTO.getConstructionDTO();
            List<ConstructionDTO> lstResult = new ArrayList<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT * FROM (SELECT   c.construction_id, "
                    + "         c.construction_code, "
                    + "         c.construction_name, "
                    + "         c.station_type, "
                    + "         c.column_type, "
                    + "         c.construction_type ,  c.start_date , "
                    + "         status , c.province_code, c.complete_date, c.created_date, c.last_modified_date");
//            if (!Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleCode)) {
//                sql.append(" , ( SELECT count(*) " +
//                        "FROM construction_detail " +
//                        "WHERE " +
//                        "status = :sortStatus " +
//                        "AND " +
//                        "construction_id = c.construction_id ) count_item ");
//            }
            sql.append("  FROM   construction c "
                    + " WHERE       status in (0,1,2,3,4) ");
            if (!StringUtils.isStringNullOrEmpty(constructionDTO)) {
                if (!StringUtils.isNullOrEmpty(constructionDTO.getProvinceCode())) {
                    sql.append("	AND c.province_code = :provinceCode ");
                }
                if (!StringUtils.isStringNullOrEmpty(constructionDTO.getConstructionType())) {
                    sql.append("	AND c.construction_type = :constructionType ");
                }
                if (!StringUtils.isStringNullOrEmpty(constructionDTO.getStatus())) {
                    sql.append("	AND c.status = :status ");
                }
                if (!StringUtils.isNullOrEmpty(constructionDTO.getConstructionCode())) {
                    sql.append("   AND LOWER(c.construction_code) LIKE LOWER(:constructionCode)  ");
                }
                if (!StringUtils.isNullOrEmpty(constructionDTO.getConstructionName())) {
                    sql.append("   AND LOWER(c.construction_name) LIKE LOWER(:constructionName)  ");
                }
            }
            if (Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(roleCode)) {
                sql.append(" AND status in (2,3,4) and c.province_code = :provinceCode ");
            }
            if (Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(roleCode) || Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleCode)) {
                sql.append(" AND status in (3,4) and c.province_code = :provinceCode ");
            }
            sql.append(" ) a ORDER BY   ");
//            if (!Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleCode)) {
//                sql.append(" a.count_item DESC, ");
//            }
            sql.append(" a.last_modified_date DESC, a.start_date, a.construction_code, a.construction_name ");

            Query query = cms.createNativeQuery(sql.toString());
            if (!StringUtils.isStringNullOrEmpty(constructionDTO)) {
                if (!StringUtils.isNullOrEmpty(constructionDTO.getProvinceCode())) {
                    query.setParameter("provinceCode", constructionDTO.getProvinceCode());
                }
                if (!StringUtils.isStringNullOrEmpty(constructionDTO.getConstructionType())) {
                    query.setParameter("constructionType", constructionDTO.getConstructionType());
                }
                if (!StringUtils.isStringNullOrEmpty(constructionDTO.getStatus())) {
                    query.setParameter("status", constructionDTO.getStatus());
                }
                if (!StringUtils.isNullOrEmpty(constructionDTO.getConstructionCode())) {
                    query.setParameter("constructionCode", "%" + constructionDTO.getConstructionCode() + "%");
                }
                if (!StringUtils.isNullOrEmpty(constructionDTO.getConstructionName())) {
                    query.setParameter("constructionName", "%" + constructionDTO.getConstructionName() + "%");
                }
            }
            if (Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(roleCode)) {
                query.setParameter("provinceCode", userService.getUserProvinceCode(commonInputDTO.getUserName().split("----")[0]));
//                query.setParameter("sortStatus", Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_1);
            }
            if (Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(roleCode) || Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleCode)) {
                query.setParameter("provinceCode", userService.getUserProvinceCode(commonInputDTO.getUserName().split("----")[0]));
//                if (Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(roleCode))
//                    query.setParameter("sortStatus", Constant.CONSTRUCTION_DETAIL.STATUS_REQUESTED);
            }
//            if (Constant.CMS_ROLES.CMS_CORP_STAFF.equals(roleCode)) {
////                query.setParameter("sortStatus", Constant.CONSTRUCTION_DETAIL.STATUS_ACCEPTED_2);
//            }
//            query.setParameter("optionSetCode", optionSetCode);
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    int i = 0;
                    ConstructionDTO constructionDTO1 = new ConstructionDTO();
//                    constructionDTO1.setConstructionId(Long.valueOf(obj[i++].toString()));
                    constructionDTO1.setConstructionId(DataUtils.getLong(obj[i++]));
                    constructionDTO1.setConstructionCode(DataUtils.getString(obj[i++]));
                    constructionDTO1.setConstructionName(DataUtils.getString(obj[i++]));
                    constructionDTO1.setStationType(DataUtils.getLong(obj[i++]));
                    constructionDTO1.setColumnType(DataUtils.getLong(obj[i++]));
                    constructionDTO1.setConstructionType(DataUtils.getLong(obj[i++]));
                    if (!StringUtils.isStringNullOrEmpty(obj[i])) {
                        constructionDTO1.setStartDate(LocalDateTime.parse(obj[i++].toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss.S")));
//                        constructionDTO1.setStartDateStr(FunctionUtils.localDateTimeToString(constructionDTO1.getStartDate()));
                    } else {
                        i++;
                    }
                    constructionDTO1.setStatus(DataUtils.getLong(obj[i++]));
                    constructionDTO1.setProvinceCode(DataUtils.getString(obj[i++]));
                    if (!StringUtils.isStringNullOrEmpty(obj[i])) {
                        constructionDTO1.setCompleteDate(LocalDateTime.parse(obj[i++].toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss.S")));
                    } else i++;
                    if (!StringUtils.isStringNullOrEmpty(obj[i])) {
                        constructionDTO1.setCreatedDate(LocalDateTime.parse(obj[i++].toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss.S")));
                    }
                    if (!StringUtils.isStringNullOrEmpty(obj[i])) {
                        constructionDTO1.setLastModifiedDate(LocalDateTime.parse(obj[i].toString(), DateTimeFormatter.ofPattern("yyyy-MM-dd' 'HH:mm:ss.S")));
                    }
//                    else i++;
//                    if (!Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleCode)) {
//                        if (!StringUtils.isStringNullOrEmpty(obj[i])) {
//                            constructionDTO1.setItemCount(Long.valueOf(obj[i].toString()));
//                        }
//                    }
                    List<Province> lisProvince = locationService.getListProvinceByCode(constructionDTO1.getProvinceCode());
                    if (!StringUtils.isStringNullOrEmpty(lisProvince) &&
                            !StringUtils.isStringNullOrEmpty(lisProvince.get(0))) {
                        constructionDTO1.setProvinceName(lisProvince.get(0).getProName());
                    }
                    constructionDTO1.setStatusName(optionSetValueService.getOptionSetValueNameByValue(
                            constructionDTO1.getStatus(),
                            Constant.CMS_OPTION_SET.CONSTRUCTION_STATUS,
                            language));
                    constructionDTO1.setColumnTypeName(optionSetValueService.getOptionSetValueNameByValue(
                            constructionDTO1.getColumnType(),
                            Constant.CMS_OPTION_SET.COLUMN_TYPE,
                            language));
                    constructionDTO1.setStationTypeName(optionSetValueService.getOptionSetValueNameByValue(
                            constructionDTO1.getStationType(),
                            Constant.CMS_OPTION_SET.STATION_TYPE,
                            language));
                    ConstructionType constructionType = getConstructionTypeById(constructionDTO1.getConstructionType());
                    if (!StringUtils.isStringNullOrEmpty(constructionType)) {
                        constructionDTO1.setConstructionTypeName(constructionType.getName());
                    }
                    constructionDTO1.setListConstructionItemName(getListConstructionItemName(constructionDTO1.getConstructionId(), r));
                    lstResult.add(constructionDTO1);
                }
            }
            return lstResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public ConstructionType getConstructionTypeById(Long constructionTypeId) throws Exception {
        try {
            String sql = " select * from construction_type where status = 1 and construction_type_id = :constructionTypeId ";
            Query query = cms.createNativeQuery(sql, ConstructionType.class);
            query.setParameter("constructionTypeId", constructionTypeId);
            List<ConstructionType> constructionTypeList = query.getResultList();
            if (constructionTypeList.size() > 0) {
                return constructionTypeList.get(0);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<ConstructionDetailDTO> getListItemDetailDTO(Long constructionId, String roleCode, String language, ResourceBundle r, Long constructionItemId) throws Exception {
        try {
            List<ConstructionDetailDTO> constructionDetailDTOList = new ArrayList<>();
            String sql = "SELECT   * "
                    + "  FROM   construction_detail "
                    + " WHERE   construction_id = :constructionId  "
                    + " AND   parent_id = :constructionItemId  ";
            if (Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleCode)
                    || Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(roleCode)) {
                sql += " AND status in (1,2,3,4,5,6,7,8,9) ";
            } else {
                sql += " AND status in (0,1,2,3,4,5,6,7,8,9)";
            }
            sql += " order by start_date desc ";
            Query query = cms.createNativeQuery(sql, ConstructionDetail.class);
            query.setParameter("constructionId", constructionId);
            query.setParameter("constructionItemId", constructionItemId);
            List<ConstructionDetail> constructionDetailList = query.getResultList();
            for (ConstructionDetail cd
                    : constructionDetailList) {
                ConstructionDetailDTO cdDTO = new ConstructionDetailDTO();
                cdDTO.setConstructionDetailId(cd.getConstructionDetailId());
                cdDTO.setConstructionId(cd.getConstructionId());
                cdDTO.setConstructionItemId(cd.getConstructionItemId());
                cdDTO.setStartDate(cd.getStartDate());
                cdDTO.setStartBy(cd.getStartBy());
                cdDTO.setAcceptanceDate(cd.getAcceptanceDate());
                cdDTO.setAcceptanceBy(cd.getAcceptanceBy());
                cdDTO.setFirstApprovedDate(cd.getFirstApprovedDate());
                cdDTO.setFirstApprovedBy(cd.getFirstApprovedBy());
                cdDTO.setFirstRejectDate(cd.getFirstRejectDate());
                cdDTO.setFirstRejectBy(cd.getFirstRejectBy());
                cdDTO.setFirstRejectReason(cd.getFirstRejectReason());
                cdDTO.setSecondApprovedDate(cd.getSecondApprovedDate());
                cdDTO.setSecondApprovedBy(cd.getSecondApprovedBy());
                cdDTO.setSecondRejectDate(cd.getSecondRejectDate());
                cdDTO.setSecondRejectBy(cd.getSecondRejectBy());
                cdDTO.setSecondRejectReason(cd.getSecondRejectReason());
                cdDTO.setThirdApprovedDate(cd.getThirdApprovedDate());
                cdDTO.setThirdApprovedBy(cd.getThirdApprovedBy());
                cdDTO.setThirdRejectDate(cd.getThirdRejectDate());
                cdDTO.setThirdRejectBy(cd.getThirdRejectBy());
                cdDTO.setThirdRejectReason(cd.getThirdRejectReason());
                cdDTO.setCreatedDate(cd.getCreatedDate());
                cdDTO.setCreatedBy(cd.getCreatedBy());
                cdDTO.setImagePath(cd.getImagePath());
                cdDTO.setStatus(cd.getStatus());
                cdDTO.setParentId(cd.getParentId());
                cdDTO.setCompleteDate(cd.getCompleteDate());
                cdDTO.setCompleteBy(cd.getCompleteBy());
                cdDTO.setName(r.getResourceMessage(getConstructionItemNameById(cd.getConstructionItemId())));
                cdDTO.setStatusName(optionSetValueService.getOptionSetValueNameByValue(cd.getStatus(), Constant.CMS_OPTION_SET.CONSTRUCTION_DETAIL_STATUS,
                        language));
                cdDTO.setStatusNameSuper(optionSetValueService.getOptionSetValueNameByValue(cd.getStatus(), Constant.CMS_OPTION_SET.CONSTRUCTION_DETAIL_STATUS,
                        language));

                /*
                set rejectReason
                 */
                if (Constant.CONSTRUCTION_DETAIL.STATUS_REJECTED_1 == cd.getStatus()) {
                    cdDTO.setRejectDate(cd.getFirstRejectDate());
                    cdDTO.setRejectReason(cd.getFirstRejectReason());
                    cdDTO.setRejectBy(cd.getFirstRejectBy());
                }
                if (Constant.CONSTRUCTION_DETAIL.STATUS_REJECTED_2 == cd.getStatus()) {
                    cdDTO.setRejectDate(cd.getSecondRejectDate());
                    cdDTO.setRejectReason(cd.getSecondRejectReason());
                    cdDTO.setRejectBy(cd.getSecondRejectBy());
                }
                if (Constant.CONSTRUCTION_DETAIL.STATUS_REJECTED_3 == cd.getStatus()) {
                    cdDTO.setRejectDate(cd.getThirdRejectDate());
                    cdDTO.setRejectReason(cd.getThirdRejectReason());
                    cdDTO.setRejectBy(cd.getThirdRejectBy());
                }
                List<ImageDTO> imageDTOList = getListImage(cdDTO.getConstructionDetailId(), r);
                if (imageDTOList.size() > 0) {
                    cdDTO.setListImageDTO(imageDTOList);
                }


                constructionDetailDTOList.add(cdDTO);
            }
            return constructionDetailDTOList;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<ConstructionDetail> getListConstructionDetailByParentId(Long constructionId, int actionCode, Long parentId) throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(" select * from construction_detail where construction_id = :constructionId and parent_id = :parentId");
            if (Constant.VALIDATE_CONSTRUCTION_STATUS.VALIDATE == actionCode) {
                sql.append(" and status <> 0 ");
            }
            Query query = cms.createNativeQuery(sql.toString(), ConstructionDetail.class);
            query.setParameter("constructionId", constructionId);
            query.setParameter("parentId", parentId);
            List<ConstructionDetail> constructionDetailList = query.getResultList();
            return constructionDetailList;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createConstruction(ConstructionDTO constructionDTO, int actionCode) throws Exception {
        try {
            Construction construction = new Construction();
            Long constructionId = DataUtils.getSequence(cms, "construction_seq");
            constructionDTO.setConstructionId(constructionId);
            construction.setConstructionId(constructionId);
            construction.setConstructionCode(constructionDTO.getConstructionCode());
            construction.setConstructionName(constructionDTO.getConstructionName());
            construction.setPositionType(constructionDTO.getPositionType());
            construction.setStationType(constructionDTO.getStationType());
            construction.setColumnType(constructionDTO.getColumnType());
            construction.setColumnHeight(constructionDTO.getColumnHeight());
            construction.setConstructionLong(constructionDTO.getConstructionLong());
            construction.setConstructionLat(constructionDTO.getConstructionLat());
            construction.setProvinceCode(constructionDTO.getProvinceCode());
            construction.setDistrict(constructionDTO.getDistrict());
            construction.setVillage(constructionDTO.getVillage());
            construction.setConstructionType(constructionDTO.getConstructionType());
            construction.setNetwork(constructionDTO.getNetwork());
            construction.setVendor(constructionDTO.getVendor());
            construction.setBand(constructionDTO.getBand());
            construction.setAntenHeight(constructionDTO.getAntenHeight());
            construction.setTilt(constructionDTO.getTilt());
            construction.setSector(constructionDTO.getSector());
            construction.setTrxMode(constructionDTO.getTrxMode());
            construction.setStartPoint(constructionDTO.getStartPoint());
            construction.setEndPoint(constructionDTO.getEndPoint());
            construction.setCableRoute(constructionDTO.getCableRoute());
            construction.setDistanceCable(constructionDTO.getDistanceCable());
            construction.setColumnNumber(constructionDTO.getColumnNumber());
            construction.setNote(constructionDTO.getNote());
            construction.setAzimuth(constructionDTO.getAzimuth());
            construction.setDecisionDeploy(constructionDTO.getDecisionDeploy());
            construction.setStartDate(null);
            construction.setStartBy(null);
            construction.setCompleteDate(null);
            construction.setCompleteBy(null);
            construction.setCreatedDate(LocalDateTime.now());
            construction.setCreatedBy(constructionDTO.getCreatedBy());
            construction.setLastModifiedDate(LocalDateTime.now());
            construction.setLastModifiedBy(constructionDTO.getCreatedBy());
            construction.setStatus(Long.valueOf(Constant.CONSTRUCTION.STATUS_CREATED));
            constructionRepo.save(construction);
            saveConstructionDetail(construction, constructionDTO.getCreatedBy());
            /*
            ghi log
             */
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    void saveConstructionDetail(Construction construction, String userName) throws Exception {
        try {
            List<ConstructionDetail> constructionDetailList = createListConstructionDetailByTypeId(construction, userName);
            for (ConstructionDetail cd
                    : constructionDetailList) {
//                Long id = DataUtils.getSequence(cms,"construction_detail_seq");
//                cd.setConstructionDetailId(id);
                constructionDetailRepo.save(cd);
                /*
                ghi log
                 */
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private List<ConstructionDetail> createListConstructionDetailByTypeId(Construction construction, String userName) throws Exception {
        List<ConstructionDetail> constructionDetailList = new ArrayList<>();
        try {
            String sql = "SELECT construction_item_id from construction_type_item where construction_type_id = :constructionTypeId";
            Query query = cms.createNativeQuery(sql);
            query.setParameter("constructionTypeId", construction.getConstructionType());
            List<Object> lst = query.getResultList();
            for (Object obj
                    : lst) {
                ConstructionDetail constructionDetail = new ConstructionDetail();
                Long constructionDetailId = DataUtils.getSequence(cms, "construction_detail_seq");
                constructionDetail.setConstructionDetailId(constructionDetailId);
                constructionDetail.setConstructionId(construction.getConstructionId());
                constructionDetail.setConstructionItemId(Long.valueOf(obj.toString()));
                constructionDetail.setCreatedDate(LocalDateTime.now());
                constructionDetail.setCreatedBy(userName);
                constructionDetail.setStatus(0L);
                constructionDetailList.add(constructionDetail);
            }
            return constructionDetailList;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExecutionResult getConstructionFromFile(MultipartFile fileCreateRequest, String userName, String locate) throws Exception {
        try {
            OutputCreateConstructionDTO outputCreateConstructionDTO = new OutputCreateConstructionDTO();
            com.viettel.vfw5.base.utils.ResourceBundle r = new ResourceBundle(locate);
            ExecutionResult res = new ExecutionResult();
            res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
            List<ConstructionDTO> constructionDTOList = new ArrayList<>();
            XSSFWorkbook workbook = new XSSFWorkbook(fileCreateRequest.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            int lenFile = worksheet.getLastRowNum();
            if (lenFile < 1) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.info.file.null"));
                return res;
            }
            Cell columnHead = worksheet.getRow(0).getCell(28);
            if (StringUtils.isStringNullOrEmpty(columnHead)) {
                columnHead = worksheet.getRow(0).createCell(28);
            }
            columnHead.setCellValue("Lỗi");
            for (int i = 1; i <= lenFile; i++) {
                XSSFRow row = worksheet.getRow(i);
                if (ObjectUtils.isEmpty(row)) {
                    break;
                }
                ConstructionDTO constructionDTO = new ConstructionDTO();
                for (int j = 1; j < row.getLastCellNum(); j++) {
                    switch (row.getCell(j).getCellTypeEnum()) {
                        case STRING:
                            if (j == 1) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue().toUpperCase())) {
                                    constructionDTO.setProvinceCode(row.getCell(j).getStringCellValue().toUpperCase());
                                }
                            }
                            if (j == 2) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue().toUpperCase())) {
                                    constructionDTO.setConstructionCode(row.getCell(j).getStringCellValue());
                                }
                            }
                            if (j == 3) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setConstructionName(row.getCell(j).getStringCellValue());
                                }
                            }
//                            if (j == 4) {
//                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
//                                    constructionDTO.setPositionType(Long.valueOf(row.getCell(j).getStringCellValue()));
//                                }
//                            }
//                            if (j == 5) {
//                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
//                                    constructionDTO.setStationType(Long.valueOf(row.getCell(j).getStringCellValue()));
//                                }
//                            }
//                            if (j == 6) {
//                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
//                                    constructionDTO.setColumnType(Long.valueOf(row.getCell(j).getStringCellValue()));
//                                }
//                            }
                            if (j == 7) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setColumnHeight(row.getCell(j).getStringCellValue());
                                }
                            }
                            if (j == 8) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setConstructionLong(row.getCell(j).getStringCellValue());
                                }
                            }
                            if (j == 9) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setConstructionLat(row.getCell(j).getStringCellValue());
                                }
                            }
//                            if (j == 10) {
//                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue()))
//                                    constructionDTO.setDistrict(Long.valueOf(row.getCell(j).getStringCellValue()));
//                            }
                            if (j == 11) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setVillage(row.getCell(j).getStringCellValue());
                                }
                            }
//                            if (j == 12) {
//                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
//                                    constructionDTO.setConstructionType(Long.valueOf(row.getCell(j).getStringCellValue()));
//                                }
//                            }
                            if (j == 13) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setNetwork(row.getCell(j).getStringCellValue());
                                }
                            }
                            if (j == 14) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setVendor(row.getCell(j).getStringCellValue());
                                }
                            }
                            if (j == 15) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setBand(row.getCell(j).getStringCellValue());
                                }
                            }
                            if (j == 16) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setAntenHeight(row.getCell(j).getStringCellValue());
                                }
                            }
                            if (j == 17) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setAzimuth(row.getCell(j).getStringCellValue());
                                }
                            }
                            if (j == 18) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setTilt(row.getCell(j).getStringCellValue());
                                }
                            }
//                            if (j == 19) {
//                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
//                                    constructionDTO.setSector(Long.valueOf(row.getCell(j).getStringCellValue()));
//                                }
//                            }
                            if (j == 20) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setTrxMode(row.getCell(j).getStringCellValue());
                                }
                            }
                            if (j == 21) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setStartPoint(row.getCell(j).getStringCellValue());
                                }
                            }
                            if (j == 22) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setEndPoint(row.getCell(j).getStringCellValue());
                                }
                            }
                            if (j == 23) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setCableRoute(row.getCell(j).getStringCellValue());
                                }
                            }
                            if (j == 24) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setDistanceCable(row.getCell(j).getStringCellValue());
                                }
                            }
//                            if (j == 25) {
//                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
//                                    constructionDTO.setColumnNumber(Long.valueOf(row.getCell(j).getStringCellValue()));
//                                }
//                            }
                            if (j == 26) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setNote(row.getCell(j).getStringCellValue());
                                }
                            }
                            if (j == 27) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue())) {
                                    constructionDTO.setDecisionDeploy(row.getCell(j).getStringCellValue());
                                }
                            }
                            break;
                        case NUMERIC:

                            if (j == 4) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getNumericCellValue())) {
                                    constructionDTO.setPositionType((long) row.getCell(j).getNumericCellValue());
                                }
                            }
                            if (j == 5) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getNumericCellValue())) {
                                    constructionDTO.setStationType((long) row.getCell(j).getNumericCellValue());
                                }
                            }
                            if (j == 6) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getNumericCellValue())) {
                                    constructionDTO.setColumnType((long) row.getCell(j).getNumericCellValue());
                                }
                            }
                            if (j == 7) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getNumericCellValue())) {
                                    constructionDTO.setColumnHeight(String.valueOf((long) row.getCell(j).getNumericCellValue()));
                                }
                            }
                            if (j == 8) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getNumericCellValue())) {
                                    constructionDTO.setConstructionLong(String.valueOf(row.getCell(j).getNumericCellValue()));
                                }
                            }
                            if (j == 9) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getNumericCellValue())) {
                                    constructionDTO.setConstructionLat(String.valueOf(row.getCell(j).getNumericCellValue()));
                                }
                            }
                            if (j == 10) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getNumericCellValue())) {
                                    constructionDTO.setDistrict((long) row.getCell(j).getNumericCellValue());
                                }
                            }
                            if (j == 12) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getNumericCellValue())) {
                                    constructionDTO.setConstructionType((long) row.getCell(j).getNumericCellValue());
                                }
                            }
                            if (j == 15) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getNumericCellValue())) {
                                    constructionDTO.setBand(String.valueOf((long) row.getCell(j).getNumericCellValue()));
                                }
                            }
                            if (j == 16) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getNumericCellValue())) {
                                    constructionDTO.setAntenHeight(String.valueOf((long) row.getCell(j).getNumericCellValue()));
                                }
                            }
                            if (j == 17) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getNumericCellValue())) {
                                    constructionDTO.setAzimuth(String.valueOf((long) row.getCell(j).getNumericCellValue()));
                                }
                            }
                            if (j == 19) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getNumericCellValue())) {
                                    constructionDTO.setSector((long) row.getCell(j).getNumericCellValue());
                                }
                            }
                            if (j == 24) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getNumericCellValue())) {
                                    constructionDTO.setDistanceCable(String.valueOf((long) row.getCell(j).getNumericCellValue()));
                                }
                            }
                            if (j == 25) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getNumericCellValue())) {
                                    constructionDTO.setColumnNumber((long) row.getCell(j).getNumericCellValue());
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
                /*
                validate dữ liệu 1 bản ghi construction
                 */
                ExecutionResult tempRes = FunctionUtils.validateNullInputConstruction(constructionDTO, locate, Constant.ACTION.CREATE_CONSTRUCTION);
                Cell lastCell = row.getCell(28);
                if (StringUtils.isStringNullOrEmpty(lastCell)) {
                    lastCell = row.createCell(28);
                }
                if (!Constant.EXECUTION_ERROR.ERROR.equals(tempRes.getErrorCode())) {
                    tempRes = FunctionUtils.validateInputConstruction(constructionDTO,
                            Constant.ACTION.CREATE_CONSTRUCTION,
                            locate,
                            constructionService,
                            locationService,
                            optionSetValueService);
                    if (!Constant.EXECUTION_ERROR.ERROR.equals(tempRes.getErrorCode())) {
                        constructionDTOList.add(constructionDTO);
                        //lưu dữ liệu 1 bản ghi construction
                        constructionDTO.setCreatedBy(userName.split("----")[0]);
                        constructionDTO.setCreatedDate(LocalDateTime.now());
                        createConstruction(constructionDTO, Constant.ACTION.CREATE_CONSTRUCTION_BY_FILE);
                        lastCell.setCellValue("");
                    } else {
                        //ghi loi
                        lastCell.setCellValue(tempRes.getDescription());
                    }
                } else {
                    //ghi loi
                    lastCell.setCellValue(tempRes.getDescription());
                }
            }
            //convert to base64
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            workbook.write(bos);
            byte[] bytes = bos.toByteArray();
//            byte[] bytes = IOUtils.toByteArray(fileCreateRequest.getInputStream());
            String base64 = "";
            base64 = Base64.getEncoder().encodeToString(bytes);

            if (!StringUtils.isStringNullOrEmpty(fileCreateRequest.getOriginalFilename())) {
                outputCreateConstructionDTO.setName(fileCreateRequest.getOriginalFilename());
            }
            if (!StringUtils.isStringNullOrEmpty(base64)) {
                outputCreateConstructionDTO.setFileContent(base64);
            }

            res.setDescription(r.getResourceMessage("construction.number.gen.success", new Object[]{constructionDTOList.size(), lenFile}));
            if (constructionDTOList.size() != lenFile)
                res.setData(outputCreateConstructionDTO);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private Long getConstructionTypeByName(String constructionTypeName) {
        try {
            String sql = " select * from construction_type where status = 1 and construction_type_name = :constructionTypeName ";
            Query query = cms.createNativeQuery(sql, ConstructionType.class);
            query.setParameter("constructionTypeName", constructionTypeName);
            List<ConstructionType> constructionTypeList = query.getResultList();
            return constructionTypeList.get(0).getConstructionTypeId();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public ConstructionDTO getConstructionDetail(ConstructionDTO constructionDTO, String roleCode, String language, ResourceBundle r) throws Exception {
        try {
            String sql = " SELECT   *"
                    + "  FROM   construction"
                    + " WHERE   construction_id = :constructionId ";
            if (Constant.CMS_ROLES.CMS_CORP_STAFF.equals(roleCode)) {
                sql += " AND status in (0, 1, 2, 3, 4) ";
            }
            if (Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(roleCode)) {
                sql += " AND status in (2,3,4) ";
            }
            if (Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(roleCode) || Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleCode)) {
                sql += " AND status in (3,4) ";
            }
            Query query = cms.createNativeQuery(sql, Construction.class);
            query.setParameter("constructionId", constructionDTO.getConstructionId());
//            if ("CMS_PROV_TECH_STAFF".equals(roleCode))
//                query.setParameter("status",3);

            List<Construction> constructions = query.getResultList();
            constructionDTO = new ConstructionDTO();
            for (Construction c
                    : constructions) {
                constructionDTO.setConstructionId(c.getConstructionId());
                constructionDTO.setConstructionCode(c.getConstructionCode());
                constructionDTO.setConstructionName(c.getConstructionName());
                constructionDTO.setPositionType(c.getPositionType());
                constructionDTO.setStationType(c.getStationType());
                constructionDTO.setColumnType(c.getColumnType());
                constructionDTO.setColumnHeight(c.getColumnHeight());
                constructionDTO.setConstructionLong(c.getConstructionLong());
                constructionDTO.setConstructionLat(c.getConstructionLat());
                constructionDTO.setProvinceCode(c.getProvinceCode());
                constructionDTO.setDistrict(c.getDistrict());
                constructionDTO.setVillage(c.getVillage());
                constructionDTO.setConstructionType(c.getConstructionType());
                constructionDTO.setNetwork(c.getNetwork());
                constructionDTO.setVendor(c.getVendor());
                constructionDTO.setBand(c.getBand());
                constructionDTO.setAntenHeight(c.getAntenHeight());
                constructionDTO.setAzimuth(c.getAzimuth());
                constructionDTO.setTilt(c.getTilt());
                constructionDTO.setSector(c.getSector());
                constructionDTO.setTrxMode(c.getTrxMode());
                constructionDTO.setStartPoint(c.getStartPoint());
                constructionDTO.setEndPoint(c.getEndPoint());
                constructionDTO.setCableRoute(c.getCableRoute());
                constructionDTO.setDistanceCable(c.getDistanceCable());
                constructionDTO.setColumnNumber(c.getColumnNumber());
                constructionDTO.setNote(c.getNote());
                constructionDTO.setDecisionDeploy(c.getDecisionDeploy());
                if (!StringUtils.isStringNullOrEmpty(c.getStartDate())) {
                    constructionDTO.setStartDate(c.getStartDate());
                }
                constructionDTO.setStartBy(c.getStartBy());
                constructionDTO.setCompleteDate(c.getCompleteDate());
//                constructionDTO.setCompleteDateStr(FunctionUtils.localDateTimeToString(c.getCompleteDate()));
                constructionDTO.setCompleteBy(c.getCompleteBy());
                constructionDTO.setCreatedDate(c.getCreatedDate());
                constructionDTO.setCreatedBy(c.getCreatedBy());
                constructionDTO.setLastModifiedDate(c.getLastModifiedDate());
//                constructionDTO.setLastModifiedDateStr(FunctionUtils.localDateTimeToString(c.getLastModifiedDate()));
                constructionDTO.setLastModifiedBy(c.getLastModifiedBy());
                constructionDTO.setStatus(c.getStatus());
                constructionDTO.setListConstructionItemName(getListConstructionItemName(c.getConstructionId(), r));
                constructionDTO.setListConstructionDetailDTO(getListConstructionDetailDTO(constructionDTO.getConstructionId(), roleCode,
                        language, r));
                List<Province> lisProvince = locationService.getListProvinceByCode(constructionDTO.getProvinceCode());
                if (!StringUtils.isStringNullOrEmpty(lisProvince)
                        && !StringUtils.isStringNullOrEmpty(lisProvince.get(0))) {
                    constructionDTO.setProvinceName(lisProvince.get(0).getProName());
                }
                if (!StringUtils.isStringNullOrEmpty(c.getDistrict())) {
                    District district = locationService.getListDistrictById(c.getDistrict());
                    if (!StringUtils.isStringNullOrEmpty(district)) {
                        constructionDTO.setDistrictName(district.getDistName());
                    }
                }
                constructionDTO.setStatusName(optionSetValueService.getOptionSetValueNameByValue(
                        constructionDTO.getStatus(),
                        Constant.CMS_OPTION_SET.CONSTRUCTION_STATUS,
                        language));
                constructionDTO.setColumnTypeName(optionSetValueService.getOptionSetValueNameByValue(
                        constructionDTO.getColumnType(),
                        Constant.CMS_OPTION_SET.COLUMN_TYPE,
                        language));
                constructionDTO.setPositionTypeName(optionSetValueService.getOptionSetValueNameByValue(
                        constructionDTO.getPositionType(),
                        Constant.CMS_OPTION_SET.POSITION_TYPE,
                        language));
                constructionDTO.setStationTypeName(optionSetValueService.getOptionSetValueNameByValue(
                        constructionDTO.getStationType(),
                        Constant.CMS_OPTION_SET.STATION_TYPE,
                        language));
                ConstructionType constructionType = getConstructionTypeById(constructionDTO.getConstructionType());
                if (!StringUtils.isStringNullOrEmpty(constructionType)) {
                    constructionDTO.setConstructionTypeName(constructionType.getName());
                }
//                constructionDTO.setListConstructionItemName(getListConstructionItemName(constructionDTO.getConstructionId()));

            }
            return constructionDTO;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<ConstructionDetailDTO> getListConstructionDetailDTO(Long constructionId, String roleCode, String language, ResourceBundle r) throws Exception {
        try {
            List<ConstructionDetailDTO> constructionDetailDTOList = new ArrayList<>();
            String sql = "SELECT   * "
                    + "  FROM   construction_detail "
                    + " WHERE   construction_id = :constructionId  and parent_id is null ";
            if (Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleCode)
                    || Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(roleCode)) {
                sql += " AND status in (1,2,3,4,5,6,7,8,9) ";
            } else {
                sql += " AND status in (0,1,2,3,4,5,6,7,8,9)";
            }
            sql += " order by start_date desc ";
            Query query = cms.createNativeQuery(sql, ConstructionDetail.class);
            query.setParameter("constructionId", constructionId);
            List<ConstructionDetail> constructionDetailList = query.getResultList();
            for (ConstructionDetail cd
                    : constructionDetailList) {
                ConstructionDetailDTO cdDTO = new ConstructionDetailDTO();
                cdDTO.setConstructionDetailId(cd.getConstructionDetailId());
                cdDTO.setConstructionId(cd.getConstructionId());
                cdDTO.setConstructionItemId(cd.getConstructionItemId());
                cdDTO.setStartDate(cd.getStartDate());
                cdDTO.setStartBy(cd.getStartBy());
                cdDTO.setAcceptanceDate(cd.getAcceptanceDate());
                cdDTO.setAcceptanceBy(cd.getAcceptanceBy());
                cdDTO.setFirstApprovedDate(cd.getFirstApprovedDate());
                cdDTO.setFirstApprovedBy(cd.getFirstApprovedBy());
                cdDTO.setFirstRejectDate(cd.getFirstRejectDate());
                cdDTO.setFirstRejectBy(cd.getFirstRejectBy());
                cdDTO.setFirstRejectReason(cd.getFirstRejectReason());
                cdDTO.setSecondApprovedDate(cd.getSecondApprovedDate());
                cdDTO.setSecondApprovedBy(cd.getSecondApprovedBy());
                cdDTO.setSecondRejectDate(cd.getSecondRejectDate());
                cdDTO.setSecondRejectBy(cd.getSecondRejectBy());
                cdDTO.setSecondRejectReason(cd.getSecondRejectReason());
                cdDTO.setThirdApprovedDate(cd.getThirdApprovedDate());
                cdDTO.setThirdApprovedBy(cd.getThirdApprovedBy());
                cdDTO.setThirdRejectDate(cd.getThirdRejectDate());
                cdDTO.setThirdRejectBy(cd.getThirdRejectBy());
                cdDTO.setThirdRejectReason(cd.getThirdRejectReason());
                cdDTO.setCreatedDate(cd.getCreatedDate());
                cdDTO.setCreatedBy(cd.getCreatedBy());
                cdDTO.setImagePath(cd.getImagePath());
                cdDTO.setStatus(cd.getStatus());
                cdDTO.setParentId(cd.getParentId());
                cdDTO.setCompleteDate(cd.getCompleteDate());
                cdDTO.setCompleteBy(cd.getCompleteBy());
                cdDTO.setName(r.getResourceMessage(getConstructionItemNameById(cd.getConstructionItemId())));
                cdDTO.setStatusName(optionSetValueService.getOptionSetValueNameByValue(cd.getStatus(), Constant.CMS_OPTION_SET.CONSTRUCTION_DETAIL_STATUS,
                        language));

                /*
                set rejectReason
                 */
                if (Constant.CONSTRUCTION_DETAIL.STATUS_REJECTED_1 == cd.getStatus()) {
                    cdDTO.setRejectDate(cd.getFirstRejectDate());
                    cdDTO.setRejectReason(cd.getFirstRejectReason());
                    cdDTO.setRejectBy(cd.getFirstRejectBy());
                }
                if (Constant.CONSTRUCTION_DETAIL.STATUS_REJECTED_2 == cd.getStatus()) {
                    cdDTO.setRejectDate(cd.getSecondRejectDate());
                    cdDTO.setRejectReason(cd.getSecondRejectReason());
                    cdDTO.setRejectBy(cd.getSecondRejectBy());
                }
                if (Constant.CONSTRUCTION_DETAIL.STATUS_REJECTED_3 == cd.getStatus()) {
                    cdDTO.setRejectDate(cd.getThirdRejectDate());
                    cdDTO.setRejectReason(cd.getThirdRejectReason());
                    cdDTO.setRejectBy(cd.getThirdRejectBy());
                }
                List<ImageDTO> imageDTOList = getListImage(cdDTO.getConstructionDetailId(), r);
                if (imageDTOList.size() > 0) {
                    cdDTO.setListImageDTO(imageDTOList);
                }

                // Lay thong tin giai doan
                List<ConstructionDetailDTO> listItemDetailDTO = getListItemDetailDTO(constructionId, roleCode, language, r, cd.getConstructionItemId());
                if (listItemDetailDTO != null && !listItemDetailDTO.isEmpty()){
//                    System.out.println("listItemDetailDTO not null");
                    cdDTO.setListItemDetailDTO(listItemDetailDTO);
                    cdDTO.setListConstructionItemName(getListConstructionItemName(constructionId, cd.getConstructionItemId(), r));
                }
//                else {
//                    System.out.println("listItemDetailDTO null");
//                }
                constructionDetailDTOList.add(cdDTO);
            }
            return constructionDetailDTOList;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Construction getConstructionById(Long constructionId) throws Exception {
        try {
            Optional<Construction> result = constructionRepo.findById(constructionId);
            Construction construction = result.orElse(null);
            return construction;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<ConstructionDetail> getListConstructionDetailByConstructionId(Long constructionId, int actionCode) throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(" select * from construction_detail where construction_id = :constructionId and parent_id is null");
            if (Constant.VALIDATE_CONSTRUCTION_STATUS.VALIDATE == actionCode) {
                sql.append(" and status <> 0 ");
            }
            Query query = cms.createNativeQuery(sql.toString(), ConstructionDetail.class);
            query.setParameter("constructionId", constructionId);
            List<ConstructionDetail> constructionDetailList = query.getResultList();
            return constructionDetailList;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<ConstructionDetail> getListConstructionDetailByConstructionId2(Long constructionId, int actionCode, Long constructionItemId) throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(" select * from construction_detail where construction_id = :constructionId and parent_id = :constructionItemId ");
            if (Constant.VALIDATE_CONSTRUCTION_STATUS.VALIDATE == actionCode) {
                sql.append(" and status <> 0 ");
            }
            Query query = cms.createNativeQuery(sql.toString(), ConstructionDetail.class);
            query.setParameter("constructionId", constructionId);
            query.setParameter("constructionItemId", constructionItemId);
            List<ConstructionDetail> constructionDetailList = query.getResultList();
            return constructionDetailList;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private List<ImageDTO> getListImage(Long constructionDetailId, ResourceBundle r) throws Exception {
        try {
            return fileUploadService.getListImage(constructionDetailId, r);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private String getConstructionItemNameById(Long constructionItemId) throws Exception {
        try {
            Optional<ConstructionItem> result = constructionItemRepo.findById(constructionItemId);
            ConstructionItem constructionItem = result.orElse(null);
            if (!StringUtils.isStringNullOrEmpty(constructionItem)) {
                return constructionItem.getName();
            }
            return "";
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<String> getListConstructionItemName(Long constructionId, ResourceBundle r) throws Exception {
        try {
            List<String> listConstructionItemName = new ArrayList<>();
            String sql = "SELECT  "
                    + "  ci.NAME   "
                    + "FROM  "
                    + "  construction_item ci,  "
                    + "  construction_detail cd   "
                    + "WHERE  "
                    + "  cd.construction_id = :constructionId   "
                    + "  AND cd.construction_item_id = ci.construction_item_id   "
                    + "  AND cd.parent_id is null "
                    + "  AND ci.STATUS = 1";
            Query query = cms.createNativeQuery(sql);
            query.setParameter("constructionId", constructionId);
            List<Object> lst = query.getResultList();
            for (Object obj
                    : lst) {
                if (!StringUtils.isStringNullOrEmpty(obj)) {
                    listConstructionItemName.add(r.getResourceMessage(obj.toString()));
                }
            }
            return listConstructionItemName;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<String> getListConstructionItemName(Long constructionId, Long parentId, ResourceBundle r) throws Exception {
        try {
            List<String> listConstructionItemName = new ArrayList<>();
            String sql = "SELECT  "
                    + "  ci.NAME   "
                    + "FROM  "
                    + "  construction_item ci,  "
                    + "  construction_detail cd   "
                    + "WHERE  "
                    + "  cd.construction_id = :constructionId   "
                    + "  AND cd.construction_item_id = ci.construction_item_id   "
                    + "  AND cd.parent_id = :parentId "
                    + "  AND ci.STATUS = 1";
            Query query = cms.createNativeQuery(sql);
            query.setParameter("constructionId", constructionId);
            query.setParameter("parentId", parentId);
            List<Object> lst = query.getResultList();
            for (Object obj
                    : lst) {
                if (!StringUtils.isStringNullOrEmpty(obj)) {
                    listConstructionItemName.add(r.getResourceMessage(obj.toString()));
                }
            }
            return listConstructionItemName;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Construction> getListConstruction(ConstructionDTO constructionDTO, int actionCode) throws Exception {
        try {
//            List<Construction> listConstructionDTO = new ArrayList<>();
            String sql = "SELECT  "
                    + "  *   "
                    + "FROM  "
                    + "  construction   "
                    + "WHERE  "
                    + "  STATUS in (1,2,3,4) ";
            if (Constant.ACTION.CREATE_CONSTRUCTION == actionCode) {
                sql += "AND construction_code = :constructionCode ";
            }
            if (Constant.ACTION.UPDATE_CONSTRUCTION == actionCode) {
                sql += "AND construction_id != :constructionId AND construction_code = :constructionCode AND status = :status ";
            }
            if (Constant.ACTION.CHECK_CONSTRUCTION == actionCode) {
                sql += "AND construction_id = :constructionId AND status = :status  ";
            }
            if (Constant.ACTION.CHECK_CONSTRUCTION_STARTDATE == actionCode) {
                sql += "AND construction_id = :constructionId AND status = :status AND province_code = :provinceCode";
            }
            Query query = cms.createNativeQuery(sql, Construction.class);

            if (Constant.ACTION.CREATE_CONSTRUCTION == actionCode) {
                query.setParameter("constructionCode", constructionDTO.getConstructionCode());
            }
            if (Constant.ACTION.UPDATE_CONSTRUCTION == actionCode) {
                query.setParameter("constructionId", constructionDTO.getConstructionId());
                query.setParameter("constructionCode", constructionDTO.getConstructionCode());
                query.setParameter("status", constructionDTO.getStatus());
            }
            if (Constant.ACTION.CHECK_CONSTRUCTION == actionCode) {
                query.setParameter("constructionId", constructionDTO.getConstructionId());
                query.setParameter("status", constructionDTO.getStatus());
            }
            if (Constant.ACTION.CHECK_CONSTRUCTION_STARTDATE == actionCode) {
                query.setParameter("constructionId", constructionDTO.getConstructionId());
                query.setParameter("status", constructionDTO.getStatus());
                query.setParameter("provinceCode", constructionDTO.getProvinceCode());
            }
            List<Construction> lst = query.getResultList();
            return lst;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateConstruction(ConstructionDTO constructionDTO) throws Exception {
        try {
            Construction construction = new Construction();
            construction.setConstructionId(constructionDTO.getConstructionId());
            construction.setConstructionCode(constructionDTO.getConstructionCode());
            construction.setConstructionName(constructionDTO.getConstructionName());
            construction.setPositionType(constructionDTO.getPositionType());
            construction.setStationType(constructionDTO.getStationType());
            construction.setColumnType(constructionDTO.getColumnType());
            construction.setColumnHeight(constructionDTO.getColumnHeight());
            construction.setConstructionLong(constructionDTO.getConstructionLong());
            construction.setConstructionLat(constructionDTO.getConstructionLat());
            construction.setProvinceCode(constructionDTO.getProvinceCode());
            construction.setDistrict(constructionDTO.getDistrict());
            construction.setVillage(constructionDTO.getVillage());
            construction.setConstructionType(constructionDTO.getConstructionType());
            construction.setNetwork(constructionDTO.getNetwork());
            construction.setVendor(constructionDTO.getVendor());
            construction.setBand(constructionDTO.getBand());
            construction.setAntenHeight(constructionDTO.getAntenHeight());
            construction.setAzimuth(constructionDTO.getAzimuth());
            construction.setTilt(constructionDTO.getTilt());
            construction.setSector(constructionDTO.getSector());
            construction.setTrxMode(constructionDTO.getTrxMode());
            construction.setStartPoint(constructionDTO.getStartPoint());
            construction.setEndPoint(constructionDTO.getEndPoint());
            construction.setCableRoute(constructionDTO.getCableRoute());
            construction.setDistanceCable(constructionDTO.getDistanceCable());
            construction.setColumnNumber(constructionDTO.getColumnNumber());
            construction.setNote(constructionDTO.getNote());
            construction.setDecisionDeploy(constructionDTO.getDecisionDeploy());
            construction.setStartDate(null);
            construction.setStartBy(null);
            construction.setCompleteDate(null);
            construction.setCompleteBy(null);
            construction.setCreatedDate(null);
            construction.setCreatedBy(null);
            construction.setLastModifiedDate(LocalDateTime.now());
            construction.setLastModifiedBy(constructionDTO.getLastModifiedBy());
            construction.setStatus(1L);
            Construction oldConstruction = getConstructionById(construction.getConstructionId());
            if (oldConstruction.getConstructionType() != construction.getConstructionType()) {
                List<ConstructionDetail> oldConstructionDetailList = getListConstructionDetailByConstructionId(construction.getConstructionId(), Constant.VALIDATE_CONSTRUCTION_STATUS.NOT_VALIDATE);
                if (oldConstructionDetailList.size() > 0) {
                    for (ConstructionDetail cd
                            : oldConstructionDetailList) {
                        deleteConstructionDetail(cd, construction.getLastModifiedBy());
                    }
                }
                saveConstructionDetail(construction, construction.getLastModifiedBy());
            }

            constructionRepo.save(construction);
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
    public void deleteConstruction(ConstructionDTO constructionDTO) throws Exception {
        try {
            int deleteConstruction = updateConstructionStatus(constructionDTO);
            if (deleteConstruction != 1) {
                throw new Exception();
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
    public List<ConstructionItem> getListConstructionItemById(Long constructionItemId) throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("select * from   construction_item "
                    + " WHERE   construction_item_id = :constructionItemId ");
            Query query = cms.createNativeQuery(sql.toString(), ConstructionItem.class);
            query.setParameter("constructionItemId", constructionItemId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void addConstructionItem(CommonInputDTO commonInputDTO) throws Exception {
        try {
            List<ConstructionItemDTO> listConstructionItemDTO = commonInputDTO.getConstructionDTO().getListConstructionItemDTO();
            for (ConstructionItemDTO ci
                    : listConstructionItemDTO) {
                List<ConstructionDetail> listOldConstructionDetail = getListConstructionDetail(commonInputDTO.getConstructionDTO().getConstructionId(),
                        ci.getConstructionItemId());
                if (Constant.CONSTRUCTION_ITEM_STATUS.CHOSEN == ci.getChosen()) {
                    System.out.println(ci);
                    if (listOldConstructionDetail.size() == 0) {
                        addConstructionDetail(commonInputDTO, ci.getConstructionItemId());
                        // Them hang muc cap 2
                        if (ci.getLstConstructionItem2() != null && !ci.getLstConstructionItem2().isEmpty()) {
                            // Xoa cac hang muc cap 2 cu
                            List<ConstructionDetail> listOldConstructionDetail2 = getListConstructionDetail2(commonInputDTO.getConstructionDTO().getConstructionId(),
                                    ci.getConstructionItemId());
                            if (listOldConstructionDetail2 != null && !listOldConstructionDetail2.isEmpty()) {
                                for (ConstructionDetail cd2 : listOldConstructionDetail2 ) {
                                    deleteConstructionDetail(cd2, commonInputDTO.getUserName().split("----")[0]);
                                }
                            } else {
                                for (ConstructionItemDTO ci2 : ci.getLstConstructionItem2()) {
                                    if(ci2.getChosen() == 1L) {
                                        addConstructionDetail2(commonInputDTO, ci.getConstructionItemId(), ci2);
                                    }
                                }
                            }
                        } else {
                            System.out.println("danh sach giai doan null");
                        }
                    } else {
                        if (ci.getLstConstructionItem2() != null && !ci.getLstConstructionItem2().isEmpty()) {
                            System.out.println("danh sach giai doan not null");
                            // Xoa cac hang muc cap 2 cu
                            List<ConstructionDetail> listOldConstructionDetail2 = getListConstructionDetail2(commonInputDTO.getConstructionDTO().getConstructionId(),
                                    ci.getConstructionItemId());
                            if (listOldConstructionDetail2 != null && !listOldConstructionDetail2.isEmpty()) {

                                for (ConstructionDetail cd2 : listOldConstructionDetail2 ) {
                                    deleteConstructionDetail(cd2, commonInputDTO.getUserName().split("----")[0]);
                                }
                                //add lại
                                for (ConstructionItemDTO ci2 : ci.getLstConstructionItem2()) {
                                    if(ci2.getChosen() == 1L) {
                                        addConstructionDetail2(commonInputDTO, ci.getConstructionItemId(), ci2);
                                    }
                                }
                            } else {
                                for (ConstructionItemDTO ci2 : ci.getLstConstructionItem2()) {
                                    if(ci2.getChosen() == 1L) {
                                        addConstructionDetail2(commonInputDTO, ci.getConstructionItemId(), ci2);
                                    }
                                }
                            }
                        } else {
                            System.out.println("danh sach giai doan null");
                        }
                    }
                } else {
                    if (listOldConstructionDetail.size() > 0) {
                        deleteConstructionDetail(listOldConstructionDetail.get(0), commonInputDTO.getUserName().split("----")[0]);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void assignConstruction(CommonInputDTO commonInputDTO) throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("update construction "
                    + "set "
                    + "status = 2, "
                    + "last_modified_date = :lastUpdateDate, "
                    + "last_modified_by = :lastUpdateBy "
                    + "where "
                    + "construction_id = :constructionId "
                    + "and status = 1");
            Query query = cms.createNativeQuery(sql.toString());
            query.setParameter("lastUpdateDate", LocalDateTime.now());
            query.setParameter("lastUpdateBy", commonInputDTO.getUserName().split("----")[0]);
            query.setParameter("constructionId", commonInputDTO.getConstructionDTO().getConstructionId());
            query.executeUpdate();
            // Gui tin nhan cho PGD CN khi giao viec thanh cong
            // 1. Lay mau tin nhan
            OptionSetValue template = optionSetValueService.getListByOptionSetValueName(Constant.SMS_CONFIG.SMS, commonInputDTO.getLanguage(), Constant.SMS_CONFIG.PGD_ASSIGN_COMPLETE);
            if (template != null) {
                // 2. Gui tin nhan thong bao cho PGD
                SmsDTO smsDTO = new SmsDTO();
                smsDTO.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
                String message = String.format(template.getValue(), commonInputDTO.getConstructionDTO().getConstructionCode() + " - " + commonInputDTO.getConstructionDTO().getConstructionName());
                smsDTO.setMessage(message);
                smsDTO.setUserRole(Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT);
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
    public List<ConstructionDetail> getListConstructionDetailById(Long constructionDetailId) throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("select * from construction_detail where construction_detail_id = :constructionDetailId");
            Query query = cms.createNativeQuery(sql.toString(), ConstructionDetail.class);
            query.setParameter("constructionDetailId", constructionDetailId);
            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void deleteConstructionDetail(ConstructionDetail oldConstructionDetail, String userName) throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("DELETE FROM   construction_detail "
                    + " WHERE   construction_item_id = :constructionItemId "
                    + " AND construction_id = :constructionId ");
            Query query = cms.createNativeQuery(sql.toString(), ConstructionItem.class);
            query.setParameter("constructionItemId", oldConstructionDetail.getConstructionItemId());
            query.setParameter("constructionId", oldConstructionDetail.getConstructionId());
            query.executeUpdate();
            saveConstructionDetailHis(oldConstructionDetail, userName);
            /*
            ghi log
             */
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void saveConstructionDetailHis(ConstructionDetail oldConstructionDetail, String userName) throws Exception {
        try {
            ConstructionDetailHis constructionDetailHis = new ConstructionDetailHis();
            Long id = DataUtils.getSequence(cms, "construction_detail_his_seq");
            constructionDetailHis.setId(id);
            constructionDetailHis.setDeletedDate(LocalDateTime.now());
            constructionDetailHis.setDeletedBy(userName);
            constructionDetailHis.setConstructionDetailId(oldConstructionDetail.getConstructionDetailId());
            constructionDetailHis.setConstructionId(oldConstructionDetail.getConstructionId());
            constructionDetailHis.setConstructionItemId(oldConstructionDetail.getConstructionItemId());
            constructionDetailHis.setStartDate(oldConstructionDetail.getStartDate());
            constructionDetailHis.setStartBy(oldConstructionDetail.getStartBy());
            constructionDetailHis.setAcceptanceBy(oldConstructionDetail.getAcceptanceBy());
            constructionDetailHis.setAcceptanceDate(oldConstructionDetail.getAcceptanceDate());
            constructionDetailHis.setFirstApprovedDate(oldConstructionDetail.getFirstApprovedDate());
            constructionDetailHis.setFirstApprovedBy(oldConstructionDetail.getFirstApprovedBy());
            constructionDetailHis.setFirstRejectDate(oldConstructionDetail.getFirstRejectDate());
            constructionDetailHis.setFirstRejectBy(oldConstructionDetail.getFirstRejectBy());
            constructionDetailHis.setFirstRejectReason(oldConstructionDetail.getFirstRejectReason());
            constructionDetailHis.setSecondApprovedDate(oldConstructionDetail.getSecondApprovedDate());
            constructionDetailHis.setSecondApprovedBy(oldConstructionDetail.getSecondApprovedBy());
            constructionDetailHis.setSecondRejectDate(oldConstructionDetail.getSecondRejectDate());
            constructionDetailHis.setSecondRejectBy(oldConstructionDetail.getSecondRejectBy());
            constructionDetailHis.setSecondRejectReason(oldConstructionDetail.getSecondRejectReason());
            constructionDetailHis.setThirdApprovedDate(oldConstructionDetail.getThirdApprovedDate());
            constructionDetailHis.setThirdApprovedBy(oldConstructionDetail.getThirdApprovedBy());
            constructionDetailHis.setThirdRejectDate(oldConstructionDetail.getThirdRejectDate());
            constructionDetailHis.setThirdRejectBy(oldConstructionDetail.getThirdRejectBy());
            constructionDetailHis.setThirdRejectReason(oldConstructionDetail.getThirdRejectReason());
            constructionDetailHis.setCreatedDate(oldConstructionDetail.getCreatedDate());
            constructionDetailHis.setCreatedBy(oldConstructionDetail.getCreatedBy());
            constructionDetailHis.setImagePath(oldConstructionDetail.getImagePath());
            constructionDetailHis.setStatus(oldConstructionDetail.getStatus());

            constructionDetailHisRepo.save(constructionDetailHis);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void addConstructionDetail(CommonInputDTO commonInputDTO, Long constructionItemId) throws Exception {
        try {
            ConstructionDetail cd = new ConstructionDetail();
            Long constructionDetailId = DataUtils.getSequence(cms, "construction_detail_seq");
            cd.setConstructionDetailId(constructionDetailId);
            cd.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
            cd.setConstructionItemId(constructionItemId);
            cd.setCreatedDate(LocalDateTime.now());
            cd.setCreatedBy(commonInputDTO.getUserName().split("----")[0]);
            cd.setStatus(Long.valueOf(Constant.CONSTRUCTION_DETAIL.STATUS_CREATED));
            constructionDetailRepo.save(cd);
            /*
                ghi log
             */
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private void addConstructionDetail2(CommonInputDTO commonInputDTO, Long constructionItemId, ConstructionItemDTO ci2) throws Exception {
        try {
            ConstructionDetail cd = new ConstructionDetail();
            Long constructionDetailId = DataUtils.getSequence(cms, "construction_detail_seq");
            cd.setConstructionDetailId(constructionDetailId);
            cd.setConstructionId(commonInputDTO.getConstructionDTO().getConstructionId());
            cd.setConstructionItemId(ci2.getConstructionItemId());
            cd.setParentId(constructionItemId);
            cd.setCreatedDate(LocalDateTime.now());
            cd.setCreatedBy(commonInputDTO.getUserName().split("----")[0]);
            cd.setStatus(Long.valueOf(Constant.CONSTRUCTION_DETAIL.STATUS_CREATED));
            constructionDetailRepo.save(cd);
            /*
                ghi log
             */
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    private List<ConstructionDetail> getListConstructionDetail(Long constructionId, Long constructionItemId) {
        try {

            StringBuilder sql = new StringBuilder();
            sql.append("select * from   construction_detail "
                    + " WHERE   construction_item_id = :constructionItemId "
                    + " AND construction_id = :constructionId ");
            Query query = cms.createNativeQuery(sql.toString(), ConstructionDetail.class);
            query.setParameter("constructionItemId", constructionItemId);
            query.setParameter("constructionId", constructionId);
            return query.getResultList();
//            String sql = "  SELECT * " +
//                    "  FROM   construction_detail " +
//                    " WHERE   construction_item_id = :constructionItemId " +
//                    " AND construction_id = :constructionId ";
//            Query query = cms.createQuery(sql, ConstructionDetail.class);
//            query.setParameter("constructionItemId", constructionItemId);
//            query.setParameter("constructionId", constructionId);
//            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    private List<ConstructionDetail> getListConstructionDetail2(Long constructionId, Long constructionItemId) {
        try {

            StringBuilder sql = new StringBuilder();
            sql.append("select * from   construction_detail "
                    + " WHERE   parent_id = :constructionItemId "
                    + " AND construction_id = :constructionId ");
            Query query = cms.createNativeQuery(sql.toString(), ConstructionDetail.class);
            query.setParameter("constructionItemId", constructionItemId);
            query.setParameter("constructionId", constructionId);
            return query.getResultList();
//            String sql = "  SELECT * " +
//                    "  FROM   construction_detail " +
//                    " WHERE   construction_item_id = :constructionItemId " +
//                    " AND construction_id = :constructionId ";
//            Query query = cms.createQuery(sql, ConstructionDetail.class);
//            query.setParameter("constructionItemId", constructionItemId);
//            query.setParameter("constructionId", constructionId);
//            return query.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    public int updateConstructionStatus(ConstructionDTO constructionDTO) throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE   construction "
                    + "   SET   status = :status , last_modified_date = :updateDate , last_modified_by = :updateBy "
                    + " WHERE   construction_id = :constructionId and status = 1");
            Query query = cms.createNativeQuery(sql.toString());
            query.setParameter("updateDate", constructionDTO.getLastModifiedDate());
            query.setParameter("updateBy", constructionDTO.getLastModifiedBy());
            query.setParameter("constructionId", constructionDTO.getConstructionId());
            query.setParameter("status", constructionDTO.getStatus());
            return query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
