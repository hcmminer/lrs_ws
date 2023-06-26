package com.viettel.base.cms.serviceImpl;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.*;
import com.viettel.base.cms.model.*;
import com.viettel.base.cms.repo.ActionLogDetailRepo;
import com.viettel.base.cms.repo.ActionLogRepo;
import com.viettel.base.cms.repo.BTSStationRepo;
import com.viettel.base.cms.repo.ConstructionRepo;
import com.viettel.base.cms.service.BTSStationService;
import com.viettel.base.cms.service.ConstructionService;
import com.viettel.base.cms.service.LocationService;
import com.viettel.base.cms.service.OptionSetValueService;
import com.viettel.base.cms.utils.DataUtil;
import com.viettel.base.cms.utils.FunctionUtils;
import com.viettel.base.cms.utils.SmsUtils;
import com.viettel.vfw5.base.dto.ExecutionResult;
import com.viettel.vfw5.base.utils.DataUtils;
import com.viettel.vfw5.base.utils.ResourceBundle;
import com.viettel.vfw5.base.utils.StringUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import utils.Param;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.RollbackException;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.apache.poi.ss.usermodel.CellType.NUMERIC;
import static org.apache.poi.ss.usermodel.CellType.STRING;

@Service
public class BTSStationServiceImpl implements BTSStationService {

    private DateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
    private DateFormat formatterString = new SimpleDateFormat("yyyy-MM-dd");

    private DateFormat formatterStringTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    @Autowired
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private EntityManager cms;

    @Autowired
    BTSStationRepo btsStationRepo;

    @Autowired
    BTSStationService btsStationService;

    @Autowired
    LocationService locationService;

    @Autowired
    OptionSetValueService optionSetValueService;

    @Autowired
    ActionLogRepo actionLogRepo;

    @Autowired
    ActionLogDetailRepo actionLogDetailRepo;

    @Autowired
    Environment env;

    private static int timeout = 5000;

    private static String linkService = "";

    @Override
    @Transactional(rollbackFor = Exception.class)
    public List<BTSStationDTO> searchBTSStation(BTSStationDTO btsStationDTO, String lang) throws Exception {
        String errorId = "";
        try {
            List<BTSStationDTO> lstResult = new ArrayList<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT "
                    + "         brp.id,    "
                    + "         brp.site_on_contract siteOnContract, "
                    + "         brp.site_on_nims siteOnNims, "
                    + "         brp.longitude, "
                    + "         brp.latitude, "
                    + "         brp.name, "
                    + "         brp.address_wards address, "
                    + "         brp.dist_id district, "
                    + "         brp.pro_id province, "
                    + "         brp.telephone, "
                    + "         brp.no_of_contract contractNo, "
                    + "         brp.period_of_rent periodOfRent, "
                    + "         date_format(brp.start_date_contract,'%d/%m/%Y') startDateContract, "
                    + "         date_format(brp.end_date_contract,'%d/%m/%Y') endDateContract, "
                    + "         date_format(brp.sign_date_contract,'%d/%m/%Y') signDateContract, "
                    + "         date_format(brp.BTS_aired_date,'%d/%m/%Y') btsAiredDate, "
                    + "         brp.rental_fee rentalFee, "
                    + "         brp.payment_time paymentTime, "
                    + "         brp.file_contract fileContract, "
                    + "         brp.file_CR fileCR, "
                    + "         brp.has_electricity hasElectricity, "
                    + "         brp.approved_status approvedStatus, "
                    + "         brp.status, "
                    + "         brp.amount, "
                    + "         date_format(brp.turn_off_date,'%d/%m/%Y') turnOffDate, "
                    + "         brp.created_user createdUser, "
                    + "         date_format(brp.created_date,'%d/%m/%Y') createdDate, "
                    + "         brp.last_modified_user lastModifiedUser, "
                    + "         date_format(brp.last_modified_date,'%d/%m/%Y') lastModifiedDate, "
                    + "         date_format(brp.start_date_payment,'%d/%m/%Y') startDatePayment, "
                    + "         date_format(brp.end_date_payment,'%d/%m/%Y')  endDatePayment, "
                    + "         brp.notes ,"
                    + "         p.pro_code, "
                    + "         p.pro_name provinceName, "
                    + "         pd.dist_name districtName,"
                    + "         fi.file_name fileContractName,"
                    + "         fi.file_path filePathContract,"
                    + "         fi2.file_name fileCRName,"
                    + "         fi2.file_path filePathCR,"
                    + " (select "
                    + "	osv2.name "
                    + "from "
                    + "	option_set_value osv2, "
                    + "	option_set os "
                    + "where "
                    + "	os.status = 1 "
                    + "	and osv2 .status = 1 "
                    + "	and osv2 .option_set_id = os.option_set_id "
                    + "	and os.option_set_code = :optionSetApproveStatus "
                    + "	and osv2.value = brp.approved_status "
                    + "	and osv2.`language` = :lang) approved_status_name,"
                    + "  (select "
                    + "	osv2.name "
                    + "from "
                    + "	option_set_value osv2, "
                    + "	option_set os "
                    + "where "
                    + "	os.status = 1 "
                    + "	and osv2 .status = 1 "
                    + "	and osv2 .option_set_id = os.option_set_id "
                    + "	and os.option_set_code = :optionSetStationStatus "
                    + "	and osv2.value = brp.status "
                    + "	and osv2.`language` = :lang) station_status_name"
                    + "  FROM   bts_rent_place brp "
                    + " left join province p on  brp.pro_id = p.pro_id "
                    + " left join province_district pd on brp.dist_id = pd.dist_id "
                    + " left join file_info fi on fi.file_path = brp.file_contract "
                    + " left join file_info fi2 on fi2.file_path = brp.file_CR "
                    + " WHERE   1=1  ");
            if (!StringUtils.isNullOrEmpty(btsStationDTO.getProvince())) {
                sql.append(" AND p.pro_code = :provinceCode ");
            }
            if (!StringUtils.isNullOrEmpty(btsStationDTO.getSiteOnNims())) {
                sql.append(" AND brp.site_on_nims like :siteOnNims ");
            }
            if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getName())) {
                sql.append(" AND brp.name like :name ");
            }
            if (!StringUtils.isNullOrEmpty(btsStationDTO.getDistrict())) {
                sql.append(" AND pd.dist_id = :distId  ");
            }
            if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getHasContractFile()) && btsStationDTO.getHasContractFile() == 1) {
                sql.append("   AND ( brp.file_contract IS NOT NULL AND brp.file_contract <> '' ) ");
            } else if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getHasContractFile()) && btsStationDTO.getHasContractFile() == 0) {
                sql.append("   AND ( brp.file_contract IS NULL OR brp.file_contract = '' ) ");
            }
            if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getHasCRFile()) && btsStationDTO.getHasCRFile() == 1) {
                sql.append(" AND ( brp.file_CR IS NOT NULL AND brp.file_CR <> ''  ) ");
            } else if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getHasCRFile()) && btsStationDTO.getHasCRFile() == 0) {
                sql.append("  AND ( brp.file_CR IS NULL OR brp.file_CR = ''  ) ");
            }
            if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getApprovedStatus())) {
                sql.append(" AND ( brp.approved_status = :approvedStatus ) ");
            }
            if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStatus())) {
                sql.append(" AND brp.status = :status ");
            }
            sql.append(" ORDER BY   brp.created_date DESC ");
            Query query = cms.createNativeQuery(sql.toString());
            if (!StringUtils.isStringNullOrEmpty(btsStationDTO)) {
                if (!StringUtils.isNullOrEmpty(btsStationDTO.getProvince())) {
                    query.setParameter("provinceCode", btsStationDTO.getProvince().trim());
                }
                if (!StringUtils.isNullOrEmpty(btsStationDTO.getSiteOnNims())) {
                    query.setParameter("siteOnNims", "%" + btsStationDTO.getSiteOnNims().trim() + "%");
                }
                if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getName())) {
                    query.setParameter("name", "%" + btsStationDTO.getName().trim() + "%");
                }
                if (!StringUtils.isNullOrEmpty(btsStationDTO.getDistrict())) {
                    query.setParameter("distId", DataUtils.getLong(btsStationDTO.getDistrict()));
                }
                if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getApprovedStatus())) {
                    query.setParameter("approvedStatus", DataUtils.getLong(btsStationDTO.getApprovedStatus()));
                }
                if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStatus())) {
                    query.setParameter("status", DataUtils.getLong(btsStationDTO.getStatus()));
                }
                query.setParameter("optionSetApproveStatus", Constant.CMS_OPTION_SET.APPROVED_STATUS);
                query.setParameter("optionSetStationStatus", Constant.CMS_OPTION_SET.STATION_STATUS);
                query.setParameter("lang", lang);
            }

            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    int i = 0;
                    BTSStationDTO btsStationDTO1 = new BTSStationDTO();
                    btsStationDTO1.setId(DataUtils.getLong(obj[0]));
                    btsStationDTO1.setSiteOnContract(DataUtils.getString(obj[1]));
                    btsStationDTO1.setSiteOnNims(DataUtils.getString(obj[2]));
                    btsStationDTO1.setLongitude(DataUtils.getString(obj[3]));
                    btsStationDTO1.setLatitude(DataUtils.getString(obj[4]));
                    btsStationDTO1.setName(DataUtils.getString(obj[5]));
                    btsStationDTO1.setAddress(DataUtils.getString(obj[6]));
                    btsStationDTO1.setDistrict(DataUtils.getString(obj[7]));
                    btsStationDTO1.setProvince(DataUtils.getString(obj[8]));
                    btsStationDTO1.setTelephone(DataUtils.getString(obj[9]));
                    btsStationDTO1.setContractNo(DataUtils.getString(obj[10]));
                    btsStationDTO1.setPeriodOfRent(DataUtils.getLong(obj[11]));
                    btsStationDTO1.setStartDateContract(DataUtils.getString(obj[12]));
                    btsStationDTO1.setEndDateContract(DataUtils.getString(obj[13]));
                    btsStationDTO1.setSignDateContract(DataUtils.getString(obj[14]));
                    btsStationDTO1.setBtsAiredDate(DataUtils.getString(obj[15]));
                    btsStationDTO1.setRentalFee(DataUtils.getLong(obj[16]));
                    btsStationDTO1.setPaymentTime(DataUtils.getString(obj[17]));
//                    if (!StringUtils.isStringNullOrEmpty(DataUtils.getString(obj[18]))) {
//                        String s = Base64.getEncoder().encodeToString(DataUtils.getBytes(obj[18]));
//                        btsStationDTO1.setFileContractBase64(s);
//                    }
                    if (!StringUtils.isStringNullOrEmpty(DataUtils.getBytes(obj[18]))) {
//                        byte[] testContract = Base64.getDecoder().decode(DataUtils.getBytes(obj[18]));
                        btsStationDTO1.setFileContract(DataUtils.getString(obj[18]));
                    } else {
                        btsStationDTO1.setFileContract(null);

                    }
//                    btsStationDTO1.setFileContract(DataUtils.getBytes(obj[18]));
                    if (!StringUtils.isStringNullOrEmpty(DataUtils.getBytes(obj[19]))) {
//                        byte[] test = Base64.getDecoder().decode(DataUtils.getBytes(obj[19]));
                        btsStationDTO1.setFileCR(DataUtils.getString(obj[19]));
                    } else {
                        btsStationDTO1.setFileCR(null);
                    }
                    btsStationDTO1.setHasElectricity(DataUtils.getLong(obj[20]));
                    btsStationDTO1.setApprovedStatus(DataUtils.getLong(obj[21]));
                    btsStationDTO1.setStatus(DataUtils.getLong(obj[22]));
                    btsStationDTO1.setAmount(DataUtils.getLong(obj[23]));
                    btsStationDTO1.setTurnOffDate(DataUtils.getString(obj[24]));
                    btsStationDTO1.setCreatedUser(DataUtils.getString(obj[25]));
                    btsStationDTO1.setCreatedDate(DataUtils.getString(obj[26]));
                    btsStationDTO1.setLastModifiedUser(DataUtils.getString(obj[27]));
                    btsStationDTO1.setLastModifiedDate(DataUtils.getString(obj[28]));
                    btsStationDTO1.setStartDatePayment(DataUtils.getString(obj[29]));
                    btsStationDTO1.setEndDatePayment(DataUtils.getString(obj[30]));
                    if (!StringUtils.isStringNullOrEmpty(DataUtils.getString(obj[31]))) {
                        btsStationDTO1.setNotes(DataUtils.getString(obj[31]));
                    }
                    btsStationDTO1.setProvinceCode(DataUtils.getString(obj[32]));
                    btsStationDTO1.setProvinceName(DataUtils.getString(obj[33]));
                    btsStationDTO1.setDistrictName(DataUtils.getString(obj[34]));
                    btsStationDTO1.setFileContractName(DataUtils.getString(obj[35]));
                    btsStationDTO1.setFileContractPath(DataUtils.getString(obj[36]));
                    btsStationDTO1.setFileCRName(DataUtils.getString(obj[37]));
                    btsStationDTO1.setFileCRPath(DataUtils.getString(obj[38]));
                    btsStationDTO1.setApprovedStatusName(DataUtils.getString(obj[39]));
                    btsStationDTO1.setStatusName(DataUtils.getString(obj[40]));
                    errorId = String.valueOf(btsStationDTO1.getId());
                    lstResult.add(btsStationDTO1);
                }
            }
            return lstResult;
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(errorId);
            throw e;
        }
    }

    public String getNameStatus(String param, String optionSet) throws Exception {
        String result = "";
        try {
            List<OptionSetValueDTO> lstResult = new ArrayList<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT   osv.name "
                    + "  FROM   option_set_value osv "
                    + " WHERE   osv.option_set_id = :option_set_id "
                    + " and osv.value = :value ");
            Query query = cms.createNativeQuery(sql.toString());
            if (!StringUtils.isNullOrEmpty(optionSet)) {
                query.setParameter("option_set_id", optionSet);
            }
            if (!StringUtils.isNullOrEmpty(param)) {
                query.setParameter("value", param);
            }
            List<String> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (String obj : lst) {
                    int i = 0;
                    result = DataUtils.getString(obj);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }

    public String getNameProvince(String param) throws Exception {
        String result = "";
        try {
            List<OptionSetValueDTO> lstResult = new ArrayList<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT   p.pro_name "
                    + "  FROM   province p "
                    + " WHERE   p.pro_id = :proId ");
            Query query = cms.createNativeQuery(sql.toString());
            if (!StringUtils.isNullOrEmpty(param)) {
                query.setParameter("proId", param);
            }
            List<String> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (String obj : lst) {
                    int i = 0;
                    if (!StringUtils.isStringNullOrEmpty(obj)) {
                        result = DataUtils.getString(obj);
                    } else {
                        result = "";
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }

    public String getCodeProvince(String param) throws Exception {
        String result = "";
        try {
            List<OptionSetValueDTO> lstResult = new ArrayList<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT   p.pro_code "
                    + "  FROM   province p "
                    + " WHERE   p.pro_id = :proId ");
            Query query = cms.createNativeQuery(sql.toString());
            if (!StringUtils.isNullOrEmpty(param)) {
                query.setParameter("proId", param);
            }
            List<String> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (String obj : lst) {
                    int i = 0;
                    if (!StringUtils.isStringNullOrEmpty(obj)) {
                        result = DataUtils.getString(obj);
                    } else {
                        result = "";
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }

    public String getNameDistrict(String param) throws Exception {
        String result = "";
        try {
            List<OptionSetValueDTO> lstResult = new ArrayList<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT   pd.dist_name "
                    + "  FROM   province_district pd  "
                    + " WHERE   pd.dist_id = :distId ");
            Query query = cms.createNativeQuery(sql.toString());
            if (!StringUtils.isNullOrEmpty(param)) {
                query.setParameter("distId", param);
            }
            List<String> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (String obj : lst) {
                    int i = 0;
                    result = DataUtils.getString(obj);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createBTSStation(BTSStationDTO btsStationDTO, Staff staff) throws Exception {
        try {
            Long btsStationId = DataUtils.getSequence(cms, "bts_rent_place_seq");
            btsStationDTO.setStatus(0L);
            btsStationDTO.setApprovedStatus(0L);
            btsStationDTO.setId(btsStationId);
            BTSStation btsStation = BTSStation.builder()
                    .id(btsStationDTO.getId())
//                    .siteOnContract(btsStationDTO.getSiteOnContract().trim())
                    .siteOnNims(btsStationDTO.getSiteOnNims().trim())
                    .longitude(btsStationDTO.getLongitude().trim())
                    .latitude(btsStationDTO.getLatitude().trim())
                    //                    .name(btsStationDTO.getName())
                    //                    .province(btsStationDTO.getProvince())
                    //                    .district(btsStationDTO.getDistrict())
                    //                    .address(btsStationDTO.getAddress())
                    //                    .telephone(btsStationDTO.getTelephone())
//                    .contractNo(btsStationDTO.getContractNo().trim())
                    //                    .preiodOfRent(btsStationDTO.getPeriodOfRent())
                    //                    .startDateContract(btsStationDTO.getStartDateContract())
                    //                    .endDateContract(btsStationDTO.getEndDateContract())
                    //                    .signDateContract(btsStationDTO.getSignDateContract())
                    //                    .btsAiredDate(btsStationDTO.getBtsAiredDate())
                    //                    .rentalFee(btsStationDTO.getRentalFee())
                    //                    .paymentTime(btsStationDTO.getPaymentTime())
                    //                    .fileContract(btsStationDTO.getFileContract())
                    //                    .hasElectricity(btsStationDTO.getHasElectricity())
                    //                    .approvedStatus(btsStationDTO.getApprovedStatus())
                    //                    .status(btsStationDTO.getStatus())
                    //                    .createdDate(LocalDateTime.now().toString())
                    //                    .cxreatedUser(btsStationDTO.getCreatedUser())
                    //                    .lastModifiedDate(btsStationDTO.getLastModifiedDate())
                    //                    .lastModifiedUser(btsStationDTO.getLastModifiedUser())
                    .status(btsStationDTO.getStatus())
                    .approvedStatus(btsStationDTO.getApprovedStatus())
                    .createdDate(LocalDateTime.now().toString().trim())
                    .createdUser(btsStationDTO.getCreatedUser().trim())
                    .build();
            btsStationRepo.save(btsStation);
            // Them log vao bang action log
            Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
            ActionLog actionLog = FunctionUtils.saveActionLog(staff, "CREATE_BTS_STATION", idActionLog, btsStation.getId());
            actionLogRepo.save(actionLog);

            //Them log vao bang action log detail
            Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("CREATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "site_on_nims",
                    "null",
                    btsStationDTO.getSiteOnNims(),
                    idActionLog,
                    idActionLogDetailUser);
            actionLogDetailRepo.save(actionLogDetail);
            Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("CREATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "latitude",
                    "null",
                    btsStationDTO.getLatitude(),
                    idActionLog,
                    idActionLogDetailUser1);
            actionLogDetailRepo.save(actionLogDetail1);
            Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("CREATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "longitude",
                    "null",
                    btsStationDTO.getLongitude(),
                    idActionLog,
                    idActionLogDetailUser2);
            actionLogDetailRepo.save(actionLogDetail2);
            Long idActionLogDetailUser3 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail3 = FunctionUtils.saveActionLogDetail("CREATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "status",
                    "null",
                    String.valueOf(btsStationDTO.getStatus()),
                    idActionLog,
                    idActionLogDetailUser3);
            actionLogDetailRepo.save(actionLogDetail3);
            Long idActionLogDetailUser4 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail4 = FunctionUtils.saveActionLogDetail("CREATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "approvedStatus",
                    "null",
                    String.valueOf(btsStationDTO.getApprovedStatus()),
                    idActionLog,
                    idActionLogDetailUser4);
            actionLogDetailRepo.save(actionLogDetail4);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ExecutionResult updateBTSStation(List<BTSStationDTO> btsStationDTOList, String userName, String roleCode, String language, Staff staff) throws Exception {
        ExecutionResult executionResult = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        OptionSetValue templateCNUpdate = optionSetValueService.getListByOptionSetValueName(Constant.SMS_CONFIG.SMS, language, Constant.SMS_CONFIG.TCCN_UPDATE_COMPLETE);
        OptionSetValue templateCNDApproved = optionSetValueService.getListByOptionSetValueName(Constant.SMS_CONFIG.SMS, language, Constant.SMS_CONFIG.CND_APPROVED_BTS);
        String provinceCode = "";
        Long quantity = 0L;
        String listBTS = "<";
        int index = 0;
        List<String> provinceCodeList = new ArrayList<>();
        List<BTSStationDTO> btsStationDTOSendSMS = new ArrayList<>();
        byte[] decodeString = new byte[]{};
        ArrayList<String> arr = new ArrayList<String>();
        for (BTSStationDTO btsStationDTO : btsStationDTOList) {
            provinceCode = btsStationDTO.getProvinceCode();
            if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getFileContract())) {
//                byte[] string = Base64.getEncoder().encode(btsStationDTO.getFileContractBase64().trim().getBytes());
//                decodeString = Base64.getDecoder().decode(btsStationDTO.getFileContractBase64().trim().getBytes("UTF-8"));
                btsStationDTO.setFileContract(btsStationDTO.getFileContract());
            } else {
                decodeString = new byte[]{};
            }
            BTSStation btsStationUpdate = getBTSStationById(btsStationDTO, roleCode);
            if (!StringUtils.isStringNullOrEmpty(btsStationUpdate)) {
                Transaction tx = null;
                try {
//            BTSStation btsStation = BTSStation.builder()
//                    .id(btsStationDTO.getId())
//                    .siteOnContract(btsStationDTO.getSiteOnContract().trim())
//                    .siteOnNims(btsStationDTO.getSiteOnNims().trim())
//                    .longitude(btsStationDTO.getLongitude().trim())
//                    .latitude(btsStationDTO.getLatitude().trim())
//                    .name(btsStationDTO.getName().trim())
//                    .province(btsStationDTO.getProvince().trim())
//                    .district(btsStationDTO.getDistrict().trim())
//                    .address(btsStationDTO.getAddress().trim())
//                    .telephone(btsStationDTO.getTelephone().trim())
//                    .contractNo(btsStationDTO.getContractNo().trim())
//                    .preiodOfRent(btsStationDTO.getPeriodOfRent())
//                    .startDateContract(btsStationDTO.getStartDateContract().trim())
//                    .endDateContract(btsStationDTO.getEndDateContract().trim())
//                    .signDateContract(btsStationDTO.getSignDateContract().trim())
//                    .btsAiredDate(btsStationDTO.getBtsAiredDate().trim())
//                    .rentalFee(btsStationDTO.getRentalFee())
//                    .paymentTime(btsStationDTO.getPaymentTime().trim())
//                    .fileContract(decodeString)
//                    .hasElectricity(btsStationDTO.getHasElectricity())
//                    .approvedStatus(btsStationDTO.getApprovedStatus())
//                    .status(btsStationDTO.getStatus())
//                    .createdDate(LocalDateTime.now().toString().trim())
//                    .createdUser(btsStationDTO.getCreatedUser().trim())
//                    .lastModifiedDate(btsStationDTO.getLastModifiedDate().trim())
//                    .lastModifiedUser(btsStationDTO.getLastModifiedUser().trim())
//                    .build();
//            btsStationRepo.save(btsStation);
                    StringBuilder sql = new StringBuilder();
                    sql.append("update bts_rent_place brp "
                            + "set ");
//            sql.append("status = :status, ");
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getLongitude())) {
                        sql.append("brp.longitude = :longitude, ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getLatitude())) {
                        sql.append("brp.latitude = :latitude, ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getContractNo())) {
                        sql.append("brp.site_on_contract = :contractNo, ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getName())) {
                        sql.append("brp.name = :name, ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getAddress())) {
                        sql.append("brp.address_wards = :address, ");
                    }

                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getProvince())) {
                        sql.append("brp.pro_id = :province, ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getDistrict())) {
                        sql.append("brp.dist_id = :district, ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getTelephone())) {
                        sql.append("brp.telephone = :telephone, ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getContractNo())) {
                        sql.append("brp.no_of_contract = :contractNo, ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getPreiodOfRent())) {
                        sql.append("brp.period_of_rent = :periodOfRent, ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getStartDatePayment())) {

                        sql.append("brp.start_date_payment = :startDatePayment, ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getEndDatePayment())) {
                        sql.append("brp.end_date_payment = :endDatePayment, ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getStartDateContract())) {
                        sql.append("brp.start_date_contract = :startDateContract, ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getEndDateContract())) {
                        sql.append("brp.end_date_contract = :endDateContract, ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getSignDateContract())) {
                        sql.append("brp.sign_date_contract = :signDateContract, ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getPaymentTime())) {
                        sql.append("brp.payment_time = :paymentTime, ");
                    }
                    if (btsStationUpdate.getFileContract() != null && btsStationUpdate.getFileContract().length() > 0) {
                        sql.append("brp.file_contract = :fileContract, ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getHasElectricity())) {
                        sql.append("brp.has_electricity = :hasElectricity, ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getRentalFee())) {
                        sql.append("brp.rental_fee = :rentalFee, ");
                    }
                    if (Constant.BTS_ROLES.CMS_BTS_CND_STAFF.equals(roleCode)) {
                        if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getApprovedStatus())) {
                            sql.append("brp.approved_status = :approvedStatus, ");
                        }
                        if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getNotes())) {
                            sql.append("brp.notes = :notes, ");
                        }
                    } else if (Constant.BTS_ROLES.CMS_BTS_TCCN_STAFF.equals(roleCode)){
                        if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getApprovedStatus())) {
                            sql.append("brp.approved_status = :approvedStatus, ");
                        }
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getAmount())) {
                        sql.append("brp.amount = :amount, ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getLastModifiedDate())) {
                        sql.append("brp.last_modified_date = :lastModifiedDate , ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getLastModifiedUser())) {
                        sql.append("brp.last_modified_user = :lastModifiedUser  , ");
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getStatus())) {
                        sql.append("brp.status = :status  ");
                    }

                    sql.append("where ");
                    sql.append("brp.id = :btsStationId ");
                    Query query = cms.createNativeQuery(sql.toString());
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getLongitude())) {
                        query.setParameter("longitude", btsStationUpdate.getLongitude());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getLatitude())) {
                        query.setParameter("latitude", btsStationUpdate.getLatitude());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getName())) {
                        query.setParameter("name", btsStationUpdate.getName());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getAddress())) {
                        query.setParameter("address", btsStationUpdate.getAddress());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getProvince())) {
                        query.setParameter("province", btsStationUpdate.getProvince());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getDistrict())) {
                        query.setParameter("district", btsStationUpdate.getDistrict());
                    }

                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getTelephone())) {
                        query.setParameter("telephone", btsStationUpdate.getTelephone());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getContractNo())) {
                        query.setParameter("contractNo", btsStationUpdate.getContractNo());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getPreiodOfRent())) {
                        query.setParameter("periodOfRent", btsStationUpdate.getPreiodOfRent());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getStartDatePayment())) {
//                        String s = formatterString.format(formatterDate.parse(btsStationUpdate.getStartDatePayment()));
                        query.setParameter("startDatePayment", btsStationUpdate.getStartDatePayment());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getEndDatePayment())) {
                        query.setParameter("endDatePayment", btsStationUpdate.getEndDatePayment());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getStartDateContract())) {
                        query.setParameter("startDateContract", btsStationUpdate.getStartDateContract());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getEndDateContract())) {
                        query.setParameter("endDateContract", btsStationUpdate.getEndDateContract());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getSignDateContract())) {
                        query.setParameter("signDateContract", btsStationUpdate.getSignDateContract());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getPaymentTime())) {
                        query.setParameter("paymentTime", btsStationUpdate.getPaymentTime());
                    }
                    if (btsStationUpdate.getFileContract() != null && btsStationUpdate.getFileContract().length() > 0) {
                        query.setParameter("fileContract", btsStationUpdate.getFileContract());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getHasElectricity())) {
                        query.setParameter("hasElectricity", btsStationUpdate.getHasElectricity());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getRentalFee())) {
                        query.setParameter("rentalFee", btsStationUpdate.getRentalFee());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getAmount())) {
                        query.setParameter("amount", btsStationUpdate.getAmount());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getStatus())) {
                        query.setParameter("status", btsStationUpdate.getStatus());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getLastModifiedDate())) {
                        query.setParameter("lastModifiedDate", btsStationUpdate.getLastModifiedDate());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getLastModifiedUser())) {
                        query.setParameter("lastModifiedUser", btsStationUpdate.getLastModifiedUser());
                    }
                    if (Constant.BTS_ROLES.CMS_BTS_CND_STAFF.equals(roleCode)) {
                        if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getApprovedStatus())) {
                            query.setParameter("approvedStatus", btsStationUpdate.getApprovedStatus());
                        }
                        if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getNotes())) {
                            query.setParameter("notes", btsStationUpdate.getNotes());
                        }
                    }else if (Constant.BTS_ROLES.CMS_BTS_TCCN_STAFF.equals(roleCode)){
                        if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getApprovedStatus())) {
                            query.setParameter("approvedStatus", btsStationUpdate.getApprovedStatus());
                        }
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationUpdate.getId())) {
                        query.setParameter("btsStationId", btsStationUpdate.getId());
                    }
//                    System.out.println("Log querrrrrrrry---------------------------------" + sql.toString());
//                    System.out.println("Log parameter---------------------------------" + query.getParameter(9));
//                    System.out.println("Log parameter---------------------------------" + query.getParameter("startDatePayment").toString());

                    query.executeUpdate();
                    arr.add(btsStationUpdate.getSiteOnNims());
                    executionResult.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);

                } catch (Exception e) {
//            log.error(e.getMessage(), e);
                    executionResult.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    executionResult.setDescription(e.getMessage());
                    e.printStackTrace();
                    throw e;
                }
            } else {
                executionResult.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                executionResult.setDescription("Lỗi hệ thống");
            }
            if (executionResult.getErrorCode().equals(Constant.EXECUTION_ERROR.SUCCESS)) {
                insertLog(btsStationDTO, btsStationUpdate, staff, roleCode);
                quantity += 1;
                btsStationDTOSendSMS.add(btsStationDTO);
                if (index == (btsStationDTOList.size() - 1)) {
                    listBTS = listBTS + btsStationDTO.getSiteOnNims();
                } else {
                    listBTS = listBTS + btsStationDTO.getSiteOnNims() + ", ";
                }
                index += 1;

                if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getProvinceCode())) {
                    if (!StringUtils.isStringNullOrEmpty(provinceCodeList)) {
                        for (String s : provinceCodeList) {
                            if (s.equals(btsStationDTO.getProvinceCode())) {
                                provinceCodeList.add(s);
                            }
                        }
                    }
                }
            }
        }
        String listStationSuccess = String.join(",", arr);
        executionResult.setDescription(r.getResourceMessage("update.station.success", listStationSuccess));
        listBTS = listBTS + ">";
        if (executionResult.getErrorCode().equals(Constant.EXECUTION_ERROR.SUCCESS) && (Constant.BTS_ROLES.CMS_BTS_CN_STAFF.equals(roleCode) || Constant.BTS_ROLES.CMS_BTS_TCCN_STAFF.equals(roleCode)) ) {
            SmsDTO smsDTO = new SmsDTO();
            smsDTO.setProvinceCode(provinceCode);
            String message = String.format(templateCNUpdate.getValue(), quantity, listBTS);
            smsDTO.setMessage(message);
            smsDTO.setUserRole(Constant.BTS_ROLES.CMS_BTS_CND_STAFF);
            SmsUtils.sendSMSForUserBTS(smsDTO, cms, env);
        } else if (executionResult.getErrorCode().equals(Constant.EXECUTION_ERROR.SUCCESS) && Constant.BTS_ROLES.CMS_BTS_CND_STAFF.equals(roleCode)) {
            String listBTSSendAprroved = "";
            String listBTSSendReject = "";
            int i = 0;
            int proIndexApproved = 0;
            int proIndeReject = 0;
            if (!StringUtils.isStringNullOrEmpty(provinceCodeList)) {
                listBTSSendAprroved = "";
                listBTSSendReject = "";
                for (String s : provinceCodeList) {
                    for (BTSStationDTO btsStationDTOSend : btsStationDTOSendSMS) {
                        listBTSSendAprroved = listBTSSendAprroved + "<";
                        listBTSSendReject = listBTSSendReject + "<";
                        if (!StringUtils.isStringNullOrEmpty(btsStationDTOSend.getProvinceCode())) {
                            if (1 == btsStationDTOSend.getApprovedStatus()) {
                                if (s.equals(btsStationDTOSend.getProvinceCode())) {
                                    if (i == (btsStationDTOList.size() - 1)) {
                                        listBTSSendAprroved = listBTSSendAprroved + btsStationDTOSend.getSiteOnNims();
                                    } else {
                                        listBTSSendAprroved = listBTSSendAprroved + btsStationDTOSend.getSiteOnNims() + ", ";
                                    }
                                    proIndexApproved++;
                                }
                            } else if (2 == btsStationDTOSend.getApprovedStatus()) {
                                if (s.equals(btsStationDTOSend.getProvinceCode())) {
                                    if (i == (btsStationDTOList.size() - 1)) {
                                        listBTSSendReject = listBTSSendReject + btsStationDTOSend.getSiteOnNims();
                                    } else {
                                        listBTSSendReject = listBTSSendReject + btsStationDTOSend.getSiteOnNims() + ", ";
                                    }
                                    proIndeReject++;
                                }
                                i++;
                            }
                        }
                        listBTSSendAprroved = listBTSSendAprroved + ">";
                        listBTSSendReject = listBTSSendReject + ">";
                        if (!StringUtils.isStringNullOrEmpty(listBTSSendAprroved) || !StringUtils.isStringNullOrEmpty(listBTSSendReject)) {
                            SmsDTO smsDTO = new SmsDTO();
                            smsDTO.setProvinceCode(s);
                            String message = String.format(templateCNDApproved.getValue(), proIndexApproved, listBTSSendAprroved, proIndeReject, listBTSSendReject);
                            smsDTO.setMessage(message);
                            smsDTO.setUserRole(Constant.BTS_ROLES.CMS_BTS_CN_STAFF);
                            SmsUtils.sendSMSForUserBTS(smsDTO, cms, env);
                        }
                    }
                }
            }
        }
        return executionResult;
    }

    public void insertLog(BTSStationDTO btsStationDTO, BTSStation btsUpdateStation, Staff staff, String roleCode){
        // Them log vao bang action log
        Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
        ActionLog actionLog = FunctionUtils.saveActionLog(staff, "UPDATE_BTS_STATION", idActionLog, btsStationDTO.getId());
        actionLogRepo.save(actionLog);

        if (Constant.BTS_ROLES.CMS_BTS_TCCN_STAFF.equals(roleCode)) {
            //Them log vao bang action log detail
            Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "no_of_contract",
                    btsStationDTO.getContractNo(),
                    btsUpdateStation.getContractNo(),
                    idActionLog,
                    idActionLogDetailUser);
            actionLogDetailRepo.save(actionLogDetail);
            Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "name",
                    btsStationDTO.getName(),
                    btsUpdateStation.getName(),
                    idActionLog,
                    idActionLogDetailUser1);
            actionLogDetailRepo.save(actionLogDetail1);
            Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "pro_id",
                    btsStationDTO.getProvince(),
                    btsUpdateStation.getLongitude(),
                    idActionLog,
                    idActionLogDetailUser2);
            actionLogDetailRepo.save(actionLogDetail2);
            Long idActionLogDetailUser3 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail3 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "dist_id",
                    btsStationDTO.getDistrict(),
                    btsUpdateStation.getDistrict(),
                    idActionLog,
                    idActionLogDetailUser3);
            actionLogDetailRepo.save(actionLogDetail3);
            Long idActionLogDetailUser4 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail4 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "address_wards",
                    btsStationDTO.getAddress(),
                    btsUpdateStation.getAddress(),
                    idActionLog,
                    idActionLogDetailUser4);
            actionLogDetailRepo.save(actionLogDetail4);
            Long idActionLogDetailUser5 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail5 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "telephone",
                    btsStationDTO.getTelephone(),
                    btsUpdateStation.getTelephone(),
                    idActionLog,
                    idActionLogDetailUser5);
            actionLogDetailRepo.save(actionLogDetail5);
            Long idActionLogDetailUser6 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail6 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "start_date_payment",
                    btsStationDTO.getStartDatePayment(),
                    btsUpdateStation.getStartDatePayment(),
                    idActionLog,
                    idActionLogDetailUser6);
            actionLogDetailRepo.save(actionLogDetail6);
            Long idActionLogDetailUser7 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail7 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "end_date_payment",
                    btsStationDTO.getEndDatePayment(),
                    btsUpdateStation.getEndDatePayment(),
                    idActionLog,
                    idActionLogDetailUser7);
            actionLogDetailRepo.save(actionLogDetail7);
            Long idActionLogDetailUser8 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail8 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "amount",
                    String.valueOf(btsStationDTO.getAmount()),
                    String.valueOf(btsUpdateStation.getAmount()),
                    idActionLog,
                    idActionLogDetailUser8);
            actionLogDetailRepo.save(actionLogDetail8);
            Long idActionLogDetailUser9 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail9 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "payment_time",
                    btsStationDTO.getPaymentTime(),
                    btsUpdateStation.getPaymentTime(),
                    idActionLog,
                    idActionLogDetailUser9);
            actionLogDetailRepo.save(actionLogDetail9);
            Long idActionLogDetailUser10 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail10 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "file_contract",
                    btsStationDTO.getFileContract(),
                    btsUpdateStation.getFileContract(),
                    idActionLog,
                    idActionLogDetailUser10);
            actionLogDetailRepo.save(actionLogDetail10);
            Long idActionLogDetailUser11 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail11 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "has_electricity",
                    String.valueOf(btsStationDTO.getHasElectricity()),
                    String.valueOf(btsUpdateStation.getHasElectricity()),
                    idActionLog,
                    idActionLogDetailUser11);
            actionLogDetailRepo.save(actionLogDetail11);
            Long idActionLogDetailUser12 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail12 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "period_of_rent",
                    String.valueOf(btsStationDTO.getPeriodOfRent()),
                    String.valueOf(btsUpdateStation.getPreiodOfRent()),
                    idActionLog,
                    idActionLogDetailUser12);
            actionLogDetailRepo.save(actionLogDetail12);
            Long idActionLogDetailUser13 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail13 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "start_date_contract",
                    btsStationDTO.getStartDateContract(),
                    btsUpdateStation.getStartDateContract(),
                    idActionLog,
                    idActionLogDetailUser13);
            actionLogDetailRepo.save(actionLogDetail13);
            Long idActionLogDetailUser14 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail14 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "end_date_contract",
                    btsStationDTO.getEndDateContract(),
                    btsUpdateStation.getEndDateContract(),
                    idActionLog,
                    idActionLogDetailUser14);
            actionLogDetailRepo.save(actionLogDetail14);
            Long idActionLogDetailUser15 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail15 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "sign_date_contract",
                    btsStationDTO.getSignDateContract(),
                    btsUpdateStation.getSignDateContract(),
                    idActionLog,
                    idActionLogDetailUser15);
            actionLogDetailRepo.save(actionLogDetail15);
            Long idActionLogDetailUser16 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail16 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "rental_fee",
                    String.valueOf(btsStationDTO.getRentalFee()),
                    String.valueOf(btsUpdateStation.getRentalFee()),
                    idActionLog,
                    idActionLogDetailUser16);
            actionLogDetailRepo.save(actionLogDetail16);
        } else if (Constant.BTS_ROLES.CMS_BTS_GRAND_TC_STAFF.equals(roleCode)){
            //Them log vao bang action log detail
            Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "no_of_contract",
                    btsStationDTO.getContractNo(),
                    btsUpdateStation.getContractNo(),
                    idActionLog,
                    idActionLogDetailUser);
            actionLogDetailRepo.save(actionLogDetail);
            Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "name",
                    btsStationDTO.getName(),
                    btsUpdateStation.getName(),
                    idActionLog,
                    idActionLogDetailUser1);
            actionLogDetailRepo.save(actionLogDetail1);
            Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "pro_id",
                    btsStationDTO.getProvince(),
                    btsUpdateStation.getLongitude(),
                    idActionLog,
                    idActionLogDetailUser2);
            actionLogDetailRepo.save(actionLogDetail2);
            Long idActionLogDetailUser3 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail3 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "dist_id",
                    btsStationDTO.getDistrict(),
                    btsUpdateStation.getDistrict(),
                    idActionLog,
                    idActionLogDetailUser3);
            actionLogDetailRepo.save(actionLogDetail3);
            Long idActionLogDetailUser4 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail4 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "address_wards",
                    btsStationDTO.getAddress(),
                    btsUpdateStation.getAddress(),
                    idActionLog,
                    idActionLogDetailUser4);
            actionLogDetailRepo.save(actionLogDetail4);
            Long idActionLogDetailUser5 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail5 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "telephone",
                    btsStationDTO.getTelephone(),
                    btsUpdateStation.getTelephone(),
                    idActionLog,
                    idActionLogDetailUser5);
            actionLogDetailRepo.save(actionLogDetail5);
            Long idActionLogDetailUser6 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail6 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "start_date_payment",
                    btsStationDTO.getStartDatePayment(),
                    btsUpdateStation.getStartDatePayment(),
                    idActionLog,
                    idActionLogDetailUser6);
            actionLogDetailRepo.save(actionLogDetail6);
            Long idActionLogDetailUser7 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail7 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "end_date_payment",
                    btsStationDTO.getEndDatePayment(),
                    btsUpdateStation.getEndDatePayment(),
                    idActionLog,
                    idActionLogDetailUser7);
            actionLogDetailRepo.save(actionLogDetail7);
            Long idActionLogDetailUser8 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail8 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "amount",
                    String.valueOf(btsStationDTO.getAmount()),
                    String.valueOf(btsUpdateStation.getAmount()),
                    idActionLog,
                    idActionLogDetailUser8);
            actionLogDetailRepo.save(actionLogDetail8);
            Long idActionLogDetailUser9 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail9 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "payment_time",
                    btsStationDTO.getPaymentTime(),
                    btsUpdateStation.getPaymentTime(),
                    idActionLog,
                    idActionLogDetailUser9);
            actionLogDetailRepo.save(actionLogDetail9);
            Long idActionLogDetailUser10 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail10 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "file_contract",
                    btsStationDTO.getFileContract(),
                    btsUpdateStation.getFileContract(),
                    idActionLog,
                    idActionLogDetailUser10);
            actionLogDetailRepo.save(actionLogDetail10);
            Long idActionLogDetailUser11 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail11 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "has_electricity",
                    String.valueOf(btsStationDTO.getHasElectricity()),
                    String.valueOf(btsUpdateStation.getHasElectricity()),
                    idActionLog,
                    idActionLogDetailUser11);
            actionLogDetailRepo.save(actionLogDetail11);
            Long idActionLogDetailUser12 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail12 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "period_of_rent",
                    String.valueOf(btsStationDTO.getPeriodOfRent()),
                    String.valueOf(btsUpdateStation.getPreiodOfRent()),
                    idActionLog,
                    idActionLogDetailUser12);
            actionLogDetailRepo.save(actionLogDetail12);
            Long idActionLogDetailUser13 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail13 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "start_date_contract",
                    btsStationDTO.getStartDateContract(),
                    btsUpdateStation.getStartDateContract(),
                    idActionLog,
                    idActionLogDetailUser13);
            actionLogDetailRepo.save(actionLogDetail13);
            Long idActionLogDetailUser14 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail14 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "end_date_contract",
                    btsStationDTO.getEndDateContract(),
                    btsUpdateStation.getEndDateContract(),
                    idActionLog,
                    idActionLogDetailUser14);
            actionLogDetailRepo.save(actionLogDetail14);
            Long idActionLogDetailUser15 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail15 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "sign_date_contract",
                    btsStationDTO.getSignDateContract(),
                    btsUpdateStation.getSignDateContract(),
                    idActionLog,
                    idActionLogDetailUser15);
            actionLogDetailRepo.save(actionLogDetail15);
            Long idActionLogDetailUser16 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail16 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "rental_fee",
                    String.valueOf(btsStationDTO.getRentalFee()),
                    String.valueOf(btsUpdateStation.getRentalFee()),
                    idActionLog,
                    idActionLogDetailUser16);
            actionLogDetailRepo.save(actionLogDetail16);
            Long idActionLogDetailUser17 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail17 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "latitude",
                    btsStationDTO.getLatitude(),
                    btsUpdateStation.getLatitude(),
                    idActionLog,
                    idActionLogDetailUser17);
            actionLogDetailRepo.save(actionLogDetail17);
            Long idActionLogDetailUser18 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail18 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "longitude",
                    btsStationDTO.getLongitude(),
                    btsUpdateStation.getLongitude(),
                    idActionLog,
                    idActionLogDetailUser18);
            actionLogDetailRepo.save(actionLogDetail18);
        } else if (Constant.BTS_ROLES.CMS_BTS_CND_STAFF.equals(roleCode)){
            Long idActionLogDetailUser17 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail17 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "notes",
                    btsStationDTO.getNotes(),
                    btsUpdateStation.getNotes(),
                    idActionLog,
                    idActionLogDetailUser17);
            actionLogDetailRepo.save(actionLogDetail17);
            Long idActionLogDetailUser18 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail18 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "aprroved_status",
                    String.valueOf(btsStationDTO.getApprovedStatus()),
                    String.valueOf(btsUpdateStation.getApprovedStatus()),
                    idActionLog,
                    idActionLogDetailUser18);
            actionLogDetailRepo.save(actionLogDetail18);
        } else if (Constant.BTS_ROLES.CMS_BTS_PNO_STAFF.equals(roleCode)){
            Long idActionLogDetailUser17 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail17 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "latitude",
                    btsStationDTO.getLatitude(),
                    btsUpdateStation.getLatitude(),
                    idActionLog,
                    idActionLogDetailUser17);
            actionLogDetailRepo.save(actionLogDetail17);
            Long idActionLogDetailUser18 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail18 = FunctionUtils.saveActionLogDetail("UPDATE_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "longitude",
                    btsStationDTO.getLongitude(),
                    btsUpdateStation.getLongitude(),
                    idActionLog,
                    idActionLogDetailUser18);
            actionLogDetailRepo.save(actionLogDetail18);
        }


    }

    public BTSStation getBTSStationById(BTSStationDTO btsStationDTO, String roleCode) throws UnsupportedEncodingException, ParseException {
        try {
            Optional<BTSStation> result = btsStationRepo.findById(btsStationDTO.getId());
            BTSStation btsStation = result.orElse(null);
            if (!StringUtils.isStringNullOrEmpty(btsStation)) {
//                if (Constant.BTS_ROLES.CMS_BTS_CN_STAFF.equals(roleCode)) {
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getLongitude()) && !(btsStation.getLongitude().equals(btsStationDTO.getLongitude()))) {
//                        btsStation.setLongitude(btsStationDTO.getLongitude());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getLatitude()) && !(btsStation.getLatitude().equals(btsStationDTO.getLatitude()))) {
//                        btsStation.setLatitude(btsStationDTO.getLatitude());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getContractNo()) && !StringUtils.isStringNullOrEmpty(btsStation.getContractNo()) && !(btsStation.getContractNo().equals(btsStationDTO.getContractNo()))) {
//                        btsStation.setContractNo(btsStationDTO.getContractNo());
//                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getContractNo())){
//                        btsStation.setContractNo(btsStationDTO.getContractNo());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getName()) && !StringUtils.isStringNullOrEmpty(btsStation.getName()) && !(btsStation.getName().equals(btsStationDTO.getName()))) {
//                        btsStation.setName(btsStationDTO.getName());
//                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getName())){
//                        btsStation.setName(btsStationDTO.getName());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getProvince()) && !StringUtils.isStringNullOrEmpty(btsStation.getProvince()) && !(btsStation.getProvince().equals(btsStationDTO.getProvince()))) {
//                        btsStation.setProvince(btsStationDTO.getProvince());
//                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getProvince())){
//                        btsStation.setProvince(btsStationDTO.getProvince());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getDistrict()) && StringUtils.isStringNullOrEmpty(btsStation.getDistrict()) && !(btsStation.getDistrict().equals(btsStationDTO.getDistrict()))) {
//                        btsStation.setDistrict(btsStationDTO.getDistrict());
//                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getDistrict())){
//                        btsStation.setDistrict(btsStationDTO.getDistrict());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getAddress()) && !StringUtils.isStringNullOrEmpty(btsStation.getAddress()) && !(btsStation.getAddress().equals(btsStationDTO.getAddress()))) {
//                        btsStation.setAddress(btsStationDTO.getAddress());
//                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getAddress())){
//                        btsStation.setAddress(btsStationDTO.getAddress());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getTelephone()) && !StringUtils.isStringNullOrEmpty(btsStation.getTelephone()) && !(btsStation.getTelephone().equals(btsStationDTO.getTelephone()))) {
//                        btsStation.setTelephone(btsStationDTO.getTelephone());
//                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getTelephone())){
//                        btsStation.setTelephone(btsStationDTO.getTelephone());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDatePayment()) && !StringUtils.isStringNullOrEmpty(btsStation.getStartDatePayment())) {
//                        if(!btsStation.getStartDatePayment().equals(btsStationDTO.getStartDatePayment())) {
//                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDatePayment()));
//                            btsStation.setStartDatePayment(s);
//                        }
//                    } else if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDatePayment())){
//                        String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDatePayment()));
//                        btsStation.setStartDatePayment(s);
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDatePayment()) && !StringUtils.isStringNullOrEmpty(btsStation.getEndDatePayment())) {
//                        if (!btsStation.getEndDatePayment().equals(btsStationDTO.getEndDatePayment())) {
//                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDatePayment()));
//                            btsStation.setEndDatePayment(s);
//                        }
//                    }else if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDatePayment())){
//                        String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDatePayment()));
//                        btsStation.setEndDatePayment(s);
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getAmount()) && !(btsStation.getAmount() == btsStationDTO.getAmount())) {
//                        btsStation.setAmount(btsStationDTO.getAmount());
//                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getAmount())){
//                        btsStation.setAmount(btsStationDTO.getAmount());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getPaymentTime()) && !(btsStation.getPaymentTime() == btsStationDTO.getPaymentTime())) {
//                        btsStation.setPaymentTime(btsStationDTO.getPaymentTime());
//                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getPaymentTime())){
//                        btsStation.setPaymentTime(btsStationDTO.getPaymentTime());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getFileContract()) && !StringUtils.isStringNullOrEmpty(btsStation.getFileContract()) && !(btsStation.getFileContract() == (btsStationDTO.getFileContract()))) {
////                        byte[] string = Base64.getEncoder().encode(btsStationDTO.getFileContract().getBytes());
////                        byte[] decodeString = Base64.getDecoder().decode(new String(string).getBytes("UTF-8"));
//                        btsStation.setFileContract(btsStationDTO.getFileContract());
//                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getFileContract())){
//                        btsStation.setFileContract(btsStationDTO.getFileContract());
//                    } else {
////                        byte[] decodeString = new byte[]{};
//                        btsStation.setFileContract(null);
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getHasElectricity()) && !(btsStation.getHasElectricity() == btsStationDTO.getHasElectricity())) {
//                        btsStation.setHasElectricity(btsStationDTO.getHasElectricity());
//                    }
//
//                } else
                if (Constant.BTS_ROLES.CMS_BTS_TCCN_STAFF.equals(roleCode)) {
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getLongitude()) && !(btsStation.getLongitude().equals(btsStationDTO.getLongitude()))) {
                        btsStation.setLongitude(btsStationDTO.getLongitude());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getLatitude()) && !(btsStation.getLatitude().equals(btsStationDTO.getLatitude()))) {
                        btsStation.setLatitude(btsStationDTO.getLatitude());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getContractNo()) && !StringUtils.isStringNullOrEmpty(btsStation.getContractNo()) && !(btsStation.getContractNo().equals(btsStationDTO.getContractNo()))) {
                        btsStation.setContractNo(btsStationDTO.getContractNo());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getContractNo())) {
                        btsStation.setContractNo(btsStationDTO.getContractNo());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getName()) && !StringUtils.isStringNullOrEmpty(btsStation.getName()) && !(btsStation.getName().equals(btsStationDTO.getName()))) {
                        btsStation.setName(btsStationDTO.getName());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getName())) {
                        btsStation.setName(btsStationDTO.getName());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getProvince()) && !StringUtils.isStringNullOrEmpty(btsStation.getProvince()) && !(btsStation.getProvince().equals(btsStationDTO.getProvince()))) {
                        btsStation.setProvince(btsStationDTO.getProvince());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getProvince())) {
                        btsStation.setProvince(btsStationDTO.getProvince());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getDistrict()) && !StringUtils.isStringNullOrEmpty(btsStation.getDistrict()) && !(btsStation.getDistrict().equals(btsStationDTO.getDistrict()))) {
                        btsStation.setDistrict(btsStationDTO.getDistrict());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getDistrict())) {
                        btsStation.setDistrict(btsStationDTO.getDistrict());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getAddress()) && !StringUtils.isStringNullOrEmpty(btsStation.getAddress()) && !(btsStation.getAddress().equals(btsStationDTO.getAddress()))) {
                        btsStation.setAddress(btsStationDTO.getAddress());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getAddress())) {
                        btsStation.setAddress(btsStationDTO.getAddress());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getTelephone()) && !StringUtils.isStringNullOrEmpty(btsStation.getTelephone()) && !(btsStation.getTelephone().equals(btsStationDTO.getTelephone()))) {
                        btsStation.setTelephone(btsStationDTO.getTelephone());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getTelephone())) {
                        btsStation.setTelephone(btsStationDTO.getTelephone());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDatePayment()) && !StringUtils.isStringNullOrEmpty(btsStation.getStartDatePayment())) {
                        if (!btsStation.getStartDatePayment().equals(btsStationDTO.getStartDatePayment())) {
                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDatePayment()));
                            btsStation.setStartDatePayment(s);
                        }
                    } else if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDatePayment())) {
                        String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDatePayment()));
                        btsStation.setStartDatePayment(s);
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDatePayment()) && !StringUtils.isStringNullOrEmpty(btsStation.getEndDatePayment())) {
                        if (!btsStation.getEndDatePayment().equals(btsStationDTO.getEndDatePayment())) {
                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDatePayment()));
                            btsStation.setEndDatePayment(s);
                        }
                    } else if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDatePayment())) {
                        String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDatePayment()));
                        btsStation.setEndDatePayment(s);
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getAmount()) && !StringUtils.isStringNullOrEmpty(btsStation.getAmount())) {
                        if (!btsStation.getAmount().equals(btsStationDTO.getAmount())) {
                            btsStation.setAmount(btsStationDTO.getAmount());
                        }
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getAmount())) {
                        btsStation.setAmount(btsStationDTO.getAmount());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getPaymentTime()) && !StringUtils.isStringNullOrEmpty(btsStation.getPaymentTime()) && !(btsStation.getPaymentTime().equals(btsStationDTO.getPaymentTime()))) {
                        btsStation.setPaymentTime(btsStationDTO.getPaymentTime());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getPaymentTime())) {
                        btsStation.setPaymentTime(btsStationDTO.getPaymentTime());
                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getPaymentTime()) && !StringUtils.isStringNullOrEmpty(btsStation.getPaymentTime()) &&  !(btsStation.getPaymentTime() == btsStationDTO.getPaymentTime())) {
//                        btsStation.setPaymentTime(btsStationDTO.getPaymentTime());
//                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getFileContract()) && !StringUtils.isStringNullOrEmpty(btsStation.getFileContract()) && !(btsStation.getFileContract() == btsStationDTO.getFileContract())) {
//                        byte[] string = Base64.getEncoder().encode(btsStationDTO.getFileContract().getBytes());
//                        byte[] decodeString = Base64.getDecoder().decode(new String(string).getBytes("UTF-8"));
                        btsStation.setFileContract(btsStationDTO.getFileContract());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getFileContract())) {
                        btsStation.setFileContract(btsStationDTO.getFileContract());
                    } else {
//                        byte[] decodeString = new byte[]{};
                        btsStation.setFileContract(null);
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getHasElectricity()) && !(btsStation.getHasElectricity() == btsStationDTO.getHasElectricity())) {
                        btsStation.setHasElectricity(btsStationDTO.getHasElectricity());
                    }

                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getPeriodOfRent()) && !StringUtils.isStringNullOrEmpty(btsStation.getPreiodOfRent()) && !(btsStation.getPreiodOfRent() == btsStationDTO.getPeriodOfRent())) {
                        btsStation.setPreiodOfRent(btsStationDTO.getPeriodOfRent());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getPreiodOfRent())) {
                        btsStation.setPreiodOfRent(btsStationDTO.getPeriodOfRent());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStation.getStartDateContract())) {
                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDateContract()) && !(btsStation.getStartDateContract().equals(btsStationDTO.getStartDateContract()))) {
                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDateContract()));
                            btsStation.setStartDateContract(s);
                        }
                    } else {
                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDateContract())) {
                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDateContract()));
                            btsStation.setStartDateContract(s);
                        }
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStation.getEndDateContract())) {
                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDateContract()) && !(btsStation.getEndDateContract().equals(btsStationDTO.getEndDateContract()))) {
                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDateContract()));
                            btsStation.setEndDateContract(s);
                        }
                    } else {
                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDateContract())) {
                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDateContract()));
                            btsStation.setEndDateContract(s);
                        }
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStation.getSignDateContract())) {
                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getSignDateContract()) && !(btsStation.getSignDateContract().equals(btsStationDTO.getSignDateContract()))) {
                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getSignDateContract()));
                            btsStation.setSignDateContract(s);
                        }
                    } else {
                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getSignDateContract())) {
                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getSignDateContract()));
                            btsStation.setSignDateContract(s);
                        }
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getRentalFee()) && !StringUtils.isStringNullOrEmpty(btsStation.getRentalFee()) && !(btsStation.getRentalFee() == btsStationDTO.getRentalFee())) {
                        btsStation.setRentalFee(btsStationDTO.getRentalFee());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getRentalFee())){
                        btsStation.setRentalFee(btsStationDTO.getRentalFee());
                    }
                    btsStation.setApprovedStatus(0L);
                    btsStation.setStatus(0L);
                } else if (Constant.BTS_ROLES.CMS_BTS_CND_STAFF.equals(roleCode)) {
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getLongitude()) && !(btsStation.getLongitude().equals(btsStationDTO.getLongitude()))) {
//                        btsStation.setLongitude(btsStationDTO.getLongitude());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getLatitude()) && !(btsStation.getLatitude().equals(btsStationDTO.getLatitude()))) {
//                        btsStation.setLatitude(btsStationDTO.getLatitude());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getContractNo()) && !(btsStation.getContractNo().equals(btsStationDTO.getContractNo()))) {
//                        btsStation.setContractNo(btsStationDTO.getContractNo());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getName()) && !(btsStation.getName().equals(btsStationDTO.getName()))) {
//                        btsStation.setName(btsStationDTO.getName());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getProvince()) && !(btsStation.getProvince().equals(btsStationDTO.getProvince()))) {
//                        btsStation.setProvince(btsStationDTO.getProvince());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getDistrict()) && !(btsStation.getDistrict().equals(btsStationDTO.getDistrict()))) {
//                        btsStation.setDistrict(btsStationDTO.getDistrict());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getAddress()) && !(btsStation.getAddress().equals(btsStationDTO.getAddress()))) {
//                        btsStation.setAddress(btsStationDTO.getAddress());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getPeriodOfRent()) && !(btsStation.getPreiodOfRent() == btsStationDTO.getPeriodOfRent())) {
//                        btsStation.setPreiodOfRent(btsStationDTO.getPeriodOfRent());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDatePayment()) && !StringUtils.isStringNullOrEmpty(btsStation.getStartDatePayment())) {
//                        if (!btsStation.getStartDatePayment().equals(btsStationDTO.getStartDatePayment())) {
//                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDatePayment()));
//                            btsStation.setStartDatePayment(s);
//                        }
//                    } else if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDatePayment())) {
//                        String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDatePayment()));
//                        btsStation.setStartDatePayment(s);
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDatePayment()) && !StringUtils.isStringNullOrEmpty(btsStation.getEndDatePayment())) {
//                        if (!btsStation.getEndDatePayment().equals(btsStationDTO.getEndDatePayment())) {
//                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDatePayment()));
//                            btsStation.setEndDatePayment(s);
//                        }
//                    } else if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDatePayment())) {
//                        String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDatePayment()));
//                        btsStation.setEndDatePayment(s);
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getRentalFee()) && !(btsStation.getRentalFee() == btsStationDTO.getRentalFee())) {
//                        btsStation.setRentalFee(btsStationDTO.getRentalFee());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getPaymentTime()) && !(btsStation.getPaymentTime() == btsStationDTO.getPaymentTime())) {
//                        btsStation.setPaymentTime(btsStationDTO.getPaymentTime());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getFileContract()) && !(btsStation.getFileContract() == btsStationDTO.getFileContract())) {
////                        byte[] string = Base64.getEncoder().encode(btsStationDTO.getFileContract().getBytes());
////                        byte[] decodeString = Base64.getDecoder().decode(new String(string).getBytes("UTF-8"));
//                        btsStation.setFileContract(btsStationDTO.getFileContract());
//                    } else {
//                        byte[] decodeString = new byte[]{};
//                        btsStation.setFileContract(decodeString);
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getHasElectricity()) && !StringUtils.isStringNullOrEmpty(btsStation.getHasElectricity()) && !(btsStation.getHasElectricity() == btsStationDTO.getHasElectricity())) {
//                        btsStation.setHasElectricity(btsStationDTO.getHasElectricity());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getPeriodOfRent()) && !StringUtils.isStringNullOrEmpty(btsStation.getPreiodOfRent()) && !(btsStation.getPreiodOfRent() == btsStationDTO.getPeriodOfRent())) {
//                        btsStation.setPreiodOfRent(btsStationDTO.getPeriodOfRent());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStation.getStartDateContract())) {
//                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDateContract()) && !(btsStation.getStartDateContract().equals(btsStationDTO.getStartDateContract()))) {
//                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDateContract()));
//                            btsStation.setStartDateContract(s);
//                        }
//                    } else {
//                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDateContract())) {
//                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDateContract()));
//                            btsStation.setStartDateContract(s);
//                        }
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStation.getEndDateContract())) {
//                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDateContract()) && !(btsStation.getEndDateContract().equals(btsStationDTO.getEndDateContract()))) {
//                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDateContract()));
//                            btsStation.setEndDateContract(s);
//                        }
//                    } else {
//                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDateContract())) {
//                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDateContract()));
//                            btsStation.setEndDateContract(s);
//                        }
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStation.getSignDateContract())) {
//                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getSignDateContract()) && !(btsStation.getSignDateContract().equals(btsStationDTO.getSignDateContract()))) {
//                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getSignDateContract()));
//                            btsStation.setSignDateContract(s);
//                        }
//                    } else {
//                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getSignDateContract())) {
//                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getSignDateContract()));
//                            btsStation.setSignDateContract(s);
//                        }
//                    }


                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getApprovedStatus()) && !StringUtils.isStringNullOrEmpty(btsStation.getApprovedStatus()) && !(btsStation.getApprovedStatus() == btsStationDTO.getApprovedStatus())) {
                        btsStation.setApprovedStatus(btsStationDTO.getApprovedStatus());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStation.getNotes())) {
                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getNotes()) && !(btsStation.getNotes().equals(btsStationDTO.getNotes()))) {
                            btsStation.setNotes(btsStationDTO.getNotes());
                        }
                    } else {
                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getNotes())) {
                            btsStation.setNotes(btsStationDTO.getNotes());
                        }
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getApprovedStatus()) && (btsStationDTO.getApprovedStatus() == Long.valueOf(Constant.APPROVED_VALUE.APPROVE))) {
                        btsStation.setStatus(Long.valueOf(Constant.STATUS_VALUE.WORKING));
                    } else if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getApprovedStatus()) && (btsStationDTO.getApprovedStatus() == Long.valueOf(Constant.APPROVED_VALUE.REJECT))) {
                        btsStation.setStatus(Long.valueOf(Constant.STATUS_VALUE.TURN_OFF));
                    } else {
                        btsStation.setStatus(Long.valueOf(Constant.STATUS_VALUE.TURN_OFF));
                    }

                } else if (Constant.BTS_ROLES.CMS_BTS_GRAND_TC_STAFF.equals(roleCode)) {
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getLongitude()) && !(btsStation.getLongitude().equals(btsStationDTO.getLongitude()))) {
                        btsStation.setLongitude(btsStationDTO.getLongitude());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getLatitude()) && !(btsStation.getLatitude().equals(btsStationDTO.getLatitude()))) {
                        btsStation.setLatitude(btsStationDTO.getLatitude());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getContractNo()) && !StringUtils.isStringNullOrEmpty(btsStation.getContractNo()) && !(btsStation.getContractNo().equals(btsStationDTO.getContractNo()))) {
                        btsStation.setContractNo(btsStationDTO.getContractNo());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getContractNo())) {
                        btsStation.setContractNo(btsStationDTO.getContractNo());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getName()) && !StringUtils.isStringNullOrEmpty(btsStation.getName()) && !(btsStation.getName().equals(btsStationDTO.getName()))) {
                        btsStation.setName(btsStationDTO.getName());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getName())) {
                        btsStation.setName(btsStationDTO.getName());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getProvince()) && !StringUtils.isStringNullOrEmpty(btsStation.getProvince()) && !(btsStation.getProvince().equals(btsStationDTO.getProvince()))) {
                        btsStation.setProvince(btsStationDTO.getProvince());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getProvince())) {
                        btsStation.setProvince(btsStationDTO.getProvince());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getDistrict()) && !StringUtils.isStringNullOrEmpty(btsStation.getDistrict()) && !(btsStation.getDistrict().equals(btsStationDTO.getDistrict()))) {
                        btsStation.setDistrict(btsStationDTO.getDistrict());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getDistrict())) {
                        btsStation.setDistrict(btsStationDTO.getDistrict());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getAddress()) && !StringUtils.isStringNullOrEmpty(btsStation.getAddress()) && !(btsStation.getAddress().equals(btsStationDTO.getAddress()))) {
                        btsStation.setAddress(btsStationDTO.getAddress());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getAddress())) {
                        btsStation.setAddress(btsStationDTO.getAddress());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getTelephone()) && !StringUtils.isStringNullOrEmpty(btsStation.getTelephone()) && !(btsStation.getTelephone().equals(btsStationDTO.getTelephone()))) {
                        btsStation.setTelephone(btsStationDTO.getTelephone());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getTelephone())) {
                        btsStation.setTelephone(btsStationDTO.getTelephone());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDatePayment()) && !StringUtils.isStringNullOrEmpty(btsStation.getStartDatePayment())) {
                        if (!btsStation.getStartDatePayment().equals(btsStationDTO.getStartDatePayment())) {
                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDatePayment()));
                            btsStation.setStartDatePayment(s);
                        }
                    } else if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDatePayment())) {
                        String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDatePayment()));
                        btsStation.setStartDatePayment(s);
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDatePayment()) && !StringUtils.isStringNullOrEmpty(btsStation.getEndDatePayment())) {
                        if (!btsStation.getEndDatePayment().equals(btsStationDTO.getEndDatePayment())) {
                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDatePayment()));
                            btsStation.setEndDatePayment(s);
                        }
                    } else if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDatePayment())) {
                        String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDatePayment()));
                        btsStation.setEndDatePayment(s);
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getAmount()) && !StringUtils.isStringNullOrEmpty(btsStation.getAmount()) && !(btsStation.getAmount() == btsStationDTO.getAmount())) {
                        btsStation.setAmount(btsStationDTO.getAmount());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getAmount())) {
                        btsStation.setAmount(btsStationDTO.getAmount());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getPaymentTime()) && !StringUtils.isStringNullOrEmpty(btsStation.getPaymentTime()) && !(btsStation.getPaymentTime() == btsStationDTO.getPaymentTime())) {
                        btsStation.setPaymentTime(btsStationDTO.getPaymentTime());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getPaymentTime())) {
                        btsStation.setPaymentTime(btsStationDTO.getPaymentTime());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getFileContract()) && !StringUtils.isStringNullOrEmpty(btsStation.getFileContract()) && !(btsStation.getFileContract() == btsStationDTO.getFileContract())) {
//                        byte[] string = Base64.getEncoder().encode(btsStationDTO.getFileContract().getBytes());
//                        byte[] decodeString = Base64.getDecoder().decode(new String(string).getBytes("UTF-8"));
                        btsStation.setFileContract(btsStationDTO.getFileContract());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getFileContract())) {
                        btsStation.setFileContract(btsStationDTO.getFileContract());
                    } else {
//                        byte[] decodeString = new byte[]{};
                        btsStation.setFileContract(null);
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getHasElectricity()) && !StringUtils.isStringNullOrEmpty(btsStation.getHasElectricity()) && !(btsStation.getHasElectricity() == btsStationDTO.getHasElectricity())) {
                        btsStation.setHasElectricity(btsStationDTO.getHasElectricity());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getHasElectricity())) {
                        btsStation.setHasElectricity(btsStationDTO.getHasElectricity());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getPeriodOfRent()) && !StringUtils.isStringNullOrEmpty(btsStation.getPreiodOfRent()) && !(btsStation.getPreiodOfRent() == btsStationDTO.getPeriodOfRent())) {
                        btsStation.setPreiodOfRent(btsStationDTO.getPeriodOfRent());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getPreiodOfRent())) {
                        btsStation.setPreiodOfRent(btsStationDTO.getPeriodOfRent());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStation.getStartDateContract())) {
                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDateContract()) && !(btsStation.getStartDateContract().equals(btsStationDTO.getStartDateContract()))) {
                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDateContract()));
                            btsStation.setStartDateContract(s);
                        }
                    } else {
                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDateContract())) {
                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDateContract()));
                            btsStation.setStartDateContract(s);
                        }
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStation.getEndDateContract())) {
                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDateContract()) && !(btsStation.getEndDateContract().equals(btsStationDTO.getEndDateContract()))) {
                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDateContract()));
                            btsStation.setEndDateContract(s);
                        }
                    } else {
                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDateContract())) {
                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDateContract()));
                            btsStation.setEndDateContract(s);
                        }
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getRentalFee()) && !StringUtils.isStringNullOrEmpty(btsStation.getRentalFee()) && !(btsStation.getRentalFee() == btsStationDTO.getRentalFee())) {
                        btsStation.setRentalFee(btsStationDTO.getRentalFee());
                    } else if (StringUtils.isStringNullOrEmpty(btsStation.getRentalFee())) {
                        btsStation.setRentalFee(btsStationDTO.getRentalFee());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStation.getSignDateContract())) {
                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getSignDateContract()) && !(btsStation.getSignDateContract().equals(btsStationDTO.getSignDateContract()))) {
                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getSignDateContract()));
                            btsStation.setSignDateContract(s);
                        }
                    } else {
                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getSignDateContract())) {
                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getSignDateContract()));
                            btsStation.setSignDateContract(s);
                        }
                    }
                } else if (Constant.BTS_ROLES.CMS_BTS_PNO_STAFF.equals(roleCode)) {
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getLongitude()) && !(btsStation.getLongitude().equals(btsStationDTO.getLongitude()))) {
                        btsStation.setLongitude(btsStationDTO.getLongitude());
                    }
                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getLatitude()) && !(btsStation.getLatitude().equals(btsStationDTO.getLatitude()))) {
                        btsStation.setLatitude(btsStationDTO.getLatitude());
                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getContractNo()) && !(btsStation.getContractNo().equals(btsStationDTO.getContractNo()))) {
//                        btsStation.setContractNo(btsStationDTO.getContractNo());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getName()) && !(btsStation.getName().equals(btsStationDTO.getName()))) {
//                        btsStation.setName(btsStationDTO.getName());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getProvince()) && !(btsStation.getProvince().equals(btsStationDTO.getProvince()))) {
//                        btsStation.setProvince(btsStationDTO.getProvince());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getDistrict()) && !(btsStation.getDistrict().equals(btsStationDTO.getDistrict()))) {
//                        btsStation.setDistrict(btsStationDTO.getDistrict());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getAddress()) && !(btsStation.getAddress().equals(btsStationDTO.getAddress()))) {
//                        btsStation.setAddress(btsStationDTO.getAddress());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getTelephone()) && !(btsStation.getTelephone().equals(btsStationDTO.getTelephone()))) {
//                        btsStation.setTelephone(btsStationDTO.getTelephone());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDatePayment()) && !StringUtils.isStringNullOrEmpty(btsStation.getStartDatePayment())) {
//                        if(!btsStation.getStartDatePayment().equals(btsStationDTO.getStartDatePayment())) {
//                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDatePayment()));
//                            btsStation.setStartDatePayment(s);
//                        }
//                    } else if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDatePayment())){
//                        String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDatePayment()));
//                        btsStation.setStartDatePayment(s);
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDatePayment()) && !StringUtils.isStringNullOrEmpty(btsStation.getEndDatePayment())) {
//                        if (!btsStation.getEndDatePayment().equals(btsStationDTO.getEndDatePayment())) {
//                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDatePayment()));
//                            btsStation.setEndDatePayment(s);
//                        }
//                    }else if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDatePayment())){
//                        String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDatePayment()));
//                        btsStation.setEndDatePayment(s);
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getAmount()) && !(btsStation.getAmount() == btsStationDTO.getAmount())) {
//                        btsStation.setAmount(btsStationDTO.getAmount());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getPaymentTime()) && !(btsStation.getPaymentTime() == btsStationDTO.getPaymentTime())) {
//                        btsStation.setPaymentTime(btsStationDTO.getPaymentTime());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getFileContract()) && !(btsStation.getFileContract() == btsStationDTO.getFileContract())) {
////                        byte[] string = Base64.getEncoder().encode(btsStationDTO.getFileContract().getBytes());
////                        byte[] decodeString = Base64.getDecoder().decode(new String(string).getBytes("UTF-8"));
//                        btsStation.setFileContract(btsStationDTO.getFileContract());
//                    } else {
//                        byte[] decodeString = new byte[]{};
//                        btsStation.setFileContract(decodeString);
//                    }
//
//                    if (StringUtils.isStringNullOrEmpty(btsStation.getFileCR())) {
//                        byte[] decodeString = new byte[]{};
//                        btsStation.setFileContract(decodeString);
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getHasElectricity()) && !(btsStation.getHasElectricity().equals(btsStationDTO.getHasElectricity()))) {
//                        btsStation.setHasElectricity(btsStationDTO.getHasElectricity());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getPeriodOfRent()) && !(btsStation.getPreiodOfRent() == btsStationDTO.getPeriodOfRent())) {
//                        btsStation.setPreiodOfRent(btsStationDTO.getPeriodOfRent());
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStation.getStartDateContract())) {
//                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDateContract()) && !(btsStation.getStartDateContract().equals(btsStationDTO.getStartDateContract()))) {
//                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDateContract()));
//                            btsStation.setStartDateContract(s);
//                        }
//                    } else {
//                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getStartDateContract())) {
//                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getStartDateContract()));
//                            btsStation.setStartDateContract(s);
//                        }
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStation.getEndDateContract())) {
//                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDateContract()) && !(btsStation.getEndDateContract().equals(btsStationDTO.getEndDateContract()))) {
//                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDateContract()));
//                            btsStation.setEndDateContract(s);
//                        }
//                    } else {
//                        if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getEndDateContract())) {
//                            String s = formatterString.format(formatterDate.parse(btsStationDTO.getEndDateContract()));
//                            btsStation.setEndDateContract(s);
//                        }
//                    }
//                    if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getRentalFee()) && !(btsStation.getRentalFee() == btsStationDTO.getRentalFee())) {
//                        btsStation.setRentalFee(btsStationDTO.getRentalFee());
//                    }
                }
                return btsStation;
            } else {
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void approvedBTSStation(BTSStationDTO btsStationDTO) throws Exception {
        try {
            int updateApprovedStatusConstruction = updateBTSStationStatus(btsStationDTO);
            if (updateApprovedStatusConstruction != 1) {
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
    @Transactional(rollbackFor = Exception.class)
    public void turnOffBTSStation(BTSStation btsStation, BTSStationDTO btsStationDTO, Staff staff) throws Exception {
        try {

            int turnOffBTSStation = updateStatusBTSStation(btsStationDTO);
            if (turnOffBTSStation != 1) {
                throw new Exception();
            }

            /*
            ghi log
             */
            // Them log vao bang action log
            Long idActionLog = DataUtils.getSequence(cms, "ACTION_LOG_SEQ");
            ActionLog actionLog = FunctionUtils.saveActionLog(staff, "TURNOFF_BTS_STATION", idActionLog, btsStationDTO.getId());
            actionLogRepo.save(actionLog);

            //Them log vao bang action log detail
            Long idActionLogDetailUser = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail = FunctionUtils.saveActionLogDetail("TURNOFF_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "status",
                    String.valueOf(btsStationDTO.getStatus()),
                    btsStationDTO.getSiteOnNims(),
                    idActionLog,
                    idActionLogDetailUser);
            actionLogDetailRepo.save(actionLogDetail);
            Long idActionLogDetailUser1 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail1 = FunctionUtils.saveActionLogDetail("TURNOFF_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "file_CR",
                    btsStation.getFileCR(),
                    btsStationDTO.getFileCR(),
                    idActionLog,
                    idActionLogDetailUser1);
            actionLogDetailRepo.save(actionLogDetail1);
            Long idActionLogDetailUser2 = DataUtils.getSequence(cms, "ACTION_LOG_DETAIL_SEQ");
            ActionLogDetail actionLogDetail2 = FunctionUtils.saveActionLogDetail("TURNOFF_BTS_STATION_DETAIL",
                    btsStationDTO.getId(),
                    "turn_off_date",
                    btsStation.getTurnOffDate(),
                    DataUtils.getSystemDate(),
                    idActionLog,
                    idActionLogDetailUser2);
            actionLogDetailRepo.save(actionLogDetail2);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public ExecutionResult getBTSStationFromFile(MultipartFile fileCreateRequest, String userName, String locate, Staff staff) throws Exception {
        try {
            OutputCreateConstructionDTO outputCreateConstructionDTO = new OutputCreateConstructionDTO();
            com.viettel.vfw5.base.utils.ResourceBundle r = new ResourceBundle(locate);
            ExecutionResult res = new ExecutionResult();
            res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
            List<BTSStationDTO> btsStationDTOList = new ArrayList<>();
            XSSFWorkbook workbook = new XSSFWorkbook(fileCreateRequest.getInputStream());
            XSSFSheet worksheet = workbook.getSheetAt(0);
            int lenFile = worksheet.getLastRowNum();
            if (lenFile < 1) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("construction.info.file.null"));
                return res;
            }
            Cell columnHead = worksheet.getRow(0).getCell(5);
            if (StringUtils.isStringNullOrEmpty(columnHead)) {
                columnHead = worksheet.getRow(0).createCell(5);
            }
            columnHead.setCellValue("Lỗi");
            for (int i = 1; i <= lenFile; i++) {
                XSSFRow row = worksheet.getRow(i);
                if (ObjectUtils.isEmpty(row)) {
                    break;
                }
                BTSStationDTO btsStationDTO = new BTSStationDTO();
                for (int j = 1; j < row.getLastCellNum(); j++) {
                    switch (row.getCell(j).getCellTypeEnum()) {
                        case STRING:
                            if (j == 1) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue().toUpperCase())) {
                                    btsStationDTO.setSiteOnNims(row.getCell(j).getStringCellValue().toUpperCase());
                                }
                            }
//                            if (j == 2) {
//                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getStringCellValue().toUpperCase())) {
//                                    btsStationDTO.setSiteOnContract(row.getCell(j).getStringCellValue());
//                                }
//                                btsStationDTO.setContractNo(row.getCell(j).getStringCellValue());
//                            }
                            break;
                        case NUMERIC:
                            if (j == 2) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getNumericCellValue())) {
                                    btsStationDTO.setLongitude(String.valueOf(row.getCell(j).getNumericCellValue()));
                                }
                            }
                            if (j == 3) {
                                if (!StringUtils.isStringNullOrEmpty(row.getCell(j).getNumericCellValue())) {
                                    btsStationDTO.setLatitude(String.valueOf(row.getCell(j).getNumericCellValue()));
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
                ExecutionResult tempRes = validateCheckSiteOnNims(btsStationDTO, locate, btsStationService, 0);
                Cell lastCell = row.getCell(4);
                if (StringUtils.isStringNullOrEmpty(lastCell)) {
                    lastCell = row.createCell(4);
                }
                if (!Constant.EXECUTION_ERROR.ERROR.equals(tempRes.getErrorCode())) {
                    tempRes = FunctionUtils.validateInputBTSStation(btsStationDTO,
                            locate,
                            btsStationService,
                            locationService,
                            optionSetValueService);
                    if (!Constant.EXECUTION_ERROR.ERROR.equals(tempRes.getErrorCode())) {
                        btsStationDTOList.add(btsStationDTO);
                        /*
                    lưu dữ liệu 1 bản ghi construction
                         */
                        btsStationDTO.setCreatedUser(userName.split("----")[0]);
                        btsStationDTO.setCreatedDate(String.valueOf(LocalDateTime.now()));
                        btsStationDTO.setApprovedStatus(1L);
                        createBTSStation(btsStationDTO, staff);
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

            res.setDescription(r.getResourceMessage("construction.number.gen.success", new Object[]{btsStationDTOList.size(), lenFile}));
            if (btsStationDTOList.size() != lenFile) {
                res.setData(outputCreateConstructionDTO);
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public ExecutionResult validateCheckSiteOnNims(BTSStationDTO btsStationDTO, String language, BTSStationService btsStationService, int retry) throws Exception {
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
//            if (StringUtils.isStringNullOrEmpty(btsStationDTO.getSiteOnContract())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("bts.contract.no.info.null"));
//                return res;
//            }
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
            if (!checkLat) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.station.lat.long.not.allow.format"));
                return res;
            }
            boolean checkLong = new DataUtil.UsernameValidator().validateNumberDouble(btsStationDTO.getLongitude());
            if (!checkLong) {
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
                        System.out.println("retry: " + retry);
                        if (retry < Constant.RETRY ){
                            validateCheckSiteOnNims(btsStationDTO, language, btsStationService, ++retry);
                        } else {
                            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                            res.setDescription(r.getResourceMessage("bts.site.on.nims.not.exits"));
                            return res;
                        }
                    }
                } else {
                    System.out.println("retry: " + retry);
                    if (retry < Constant.RETRY ){
                        validateCheckSiteOnNims(btsStationDTO, language, btsStationService, ++retry);
                    } else {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("bts.site.on.nims.not.exits"));
                        return res;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
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
            httpclient.executeMethod(post);
            responseBody = post.getResponseBodyAsString();
            System.out.println(responseBody);
        } catch (Exception ex) {
            System.out.println("Loi SOAP call U-Point" + ex.getMessage());
            ex.printStackTrace();
        }
        return responseBody;
    }

    @Override
    public List<BTSStation> getListBTSStation(BTSStationDTO btsStationDTO) throws Exception {
        try {
//            List<Construction> listConstructionDTO = new ArrayList<>();
            String sql = "SELECT  "
                    + "  *   "
                    + "FROM  "
                    + "  bts_rent_place   "
                    + "WHERE  "
                    + "  STATUS in (0,1,2,3,4) ";
            if (!DataUtil.isNullOrEmpty(btsStationDTO.getSiteOnNims())) {
                sql += "AND site_on_nims = :siteOnNims ";
            }
//            if (!DataUtil.isNullOrEmpty(btsStationDTO.getSiteOnContract())) {
//                sql += "AND site_on_contract = :siteOnContract ";
//            }
            Query query = cms.createNativeQuery(sql, BTSStation.class);

            if (!DataUtil.isNullOrEmpty(btsStationDTO.getSiteOnNims())) {
                query.setParameter("siteOnNims", btsStationDTO.getSiteOnNims().trim());
            }
//            if (!DataUtil.isNullOrEmpty(btsStationDTO.getSiteOnContract())) {
//                query.setParameter("siteOnContract", btsStationDTO.getSiteOnContract().trim());
//            }
            List<BTSStation> lst = query.getResultList();
            return lst;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<BTSStation> getListBTSStationById(BTSStationDTO btsStationDTO) throws Exception {
        try {
//            List<Construction> listConstructionDTO = new ArrayList<>();
            String sql = "SELECT  "
                    + "  *   "
                    + "FROM  "
                    + "  bts_rent_place brp  "
                    + " WHERE   brp.id = :id ";
            Query query = cms.createNativeQuery(sql, BTSStation.class);

            if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getId())) {
                query.setParameter("id", btsStationDTO.getId());
            }
            List<BTSStation> lst = query.getResultList();
            return lst;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public String getStatusBTSStation(BTSStationDTO btsStationDTO) throws Exception {
        String result = "";
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT   brp.status "
                    + "  FROM   bts_rent_place brp "
                    + " WHERE   brp.id = :id ");
            Query query = cms.createNativeQuery(sql.toString());
            if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getId())) {
                query.setParameter("id", btsStationDTO.getId());
            }
            List<Integer> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Integer obj : lst) {
                    int i = 0;
                    if (!StringUtils.isStringNullOrEmpty(obj)) {
                        result = String.valueOf(DataUtils.getString(obj));
                    } else {
                        result = "";
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }

    @Override
    public String getApprovedStatusBTSStation(BTSStationDTO btsStationDTO) throws Exception {
        String result = "";
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT   brp.approved_status "
                    + "  FROM   bts_rent_place brp "
                    + " WHERE   brp.id = :id ");
            Query query = cms.createNativeQuery(sql.toString());
            if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getId())) {
                query.setParameter("id", btsStationDTO.getId());
            }
            List<Integer> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Integer obj : lst) {
                    int i = 0;
                    if (!StringUtils.isStringNullOrEmpty(obj)) {
                        result = String.valueOf(DataUtils.getString(obj));
                    } else {
                        result = "";
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }

    @Override
    public String getProvinceCodeOfStaff(String staffName) throws Exception {
        String result = "";
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT   s.province_code "
                    + "  FROM   staff s "
                    + " WHERE   s.staff_name = :staffName ");
            Query query = cms.createNativeQuery(sql.toString());
            if (!StringUtils.isStringNullOrEmpty(staffName)) {
                query.setParameter("staffName", staffName);
            }
            List<String> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (String obj : lst) {
                    int i = 0;
                    result = obj;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return result;
    }

    @Override
    public Long getProvinceIdByCode(String provinceCode) throws Exception {
        Long provinceId = null;
        try {
//            List<Construction> listConstructionDTO = new ArrayList<>();
            String sql = "SELECT  "
                    + "  p.pro_id   "
                    + "FROM  "
                    + "  province p  "
                    + " WHERE   p.pro_code = :provinceCode ";
            Query query = cms.createNativeQuery(sql);

            if (!StringUtils.isStringNullOrEmpty(provinceCode)) {
                query.setParameter("provinceCode", provinceCode);
            }
            List<Object> lst = query.getResultList();
            if (!DataUtil.isNullOrEmpty(lst)) {
                provinceId = DataUtils.getLong(lst.get(0));
            }
            return provinceId;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<String> getListIsdnByDeptCode(String deptCode) throws Exception {
        try {
//            List<Construction> listConstructionDTO = new ArrayList<>();
            String sql = "SELECT  "
                    + "  ss.staff_mobile  "
                    + "FROM  "
                    + "  sms_staff ss  "
                    + " WHERE   ss.pro_code = :deptCode ";
            Query query = cms.createNativeQuery(sql, BTSStation.class);

            if (!StringUtils.isStringNullOrEmpty(deptCode)) {
                query.setParameter("deptCode", deptCode);
            }
            List<String> lst = query.getResultList();
            return lst;
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

    public int updateBTSStationStatus(BTSStationDTO btsStationDTO) throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append("UPDATE   bts_rent_place brp"
                    + "   SET   brp.approved_status = :approvedStatus , brp.notes = :notes , last_modified_date = :updateDate , last_modified_user = :updateBy "
                    + " WHERE   brp.id = :btsStationId ");
            Query query = cms.createNativeQuery(sql.toString());
            query.setParameter("updateDate", btsStationDTO.getLastModifiedDate());
            query.setParameter("updateBy", btsStationDTO.getLastModifiedUser());
            query.setParameter("btsStationId", btsStationDTO.getId());
            query.setParameter("approvedStatus", btsStationDTO.getApprovedStatus());
            query.setParameter("notes", btsStationDTO.getNotes());
            return query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public int updateStatusBTSStation(BTSStationDTO btsStationDTO) throws Exception {
        try {
//            byte[] string = Base64.getEncoder().encode(btsStationDTO.getFileCRBase64().getBytes());
//            byte[] decodeString = Base64.getDecoder().decode(new String(string).getBytes("UTF-8"));
            StringBuilder sql = new StringBuilder();
            LocalDateTime now = LocalDateTime.now();
            sql.append("UPDATE   bts_rent_place brp"
                    + "   SET   brp.status = :status , "
                    + " brp.file_CR = :fileCR , last_modified_date = :updateDate , "
                    + " last_modified_user = :updateBy, "
                    + " turn_off_date = :turnOffDate"
                    + " WHERE   brp.id = :btsStationId and status = 1 and approved_status = 1 ");
            Query query = cms.createNativeQuery(sql.toString());
            query.setParameter("updateDate", now);
            query.setParameter("updateBy", btsStationDTO.getLastModifiedUser());
            query.setParameter("btsStationId", btsStationDTO.getId());
            query.setParameter("status", btsStationDTO.getStatus());
            query.setParameter("turnOffDate", now);
//            query.setParameter("fileCR", btsStationDTO.getFileCRBase64());
            query.setParameter("fileCR", btsStationDTO.getFileCR());

            return query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
