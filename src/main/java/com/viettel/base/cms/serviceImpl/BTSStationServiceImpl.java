package com.viettel.base.cms.serviceImpl;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.BTSStationDTO;
import com.viettel.base.cms.dto.OptionSetValueDTO;
import com.viettel.base.cms.dto.OutputCreateConstructionDTO;
import com.viettel.base.cms.model.BTSStation;
import com.viettel.base.cms.model.ConstructionType;
import com.viettel.base.cms.model.Staff;
import com.viettel.base.cms.repo.ActionLogDetailRepo;
import com.viettel.base.cms.repo.ActionLogRepo;
import com.viettel.base.cms.repo.BTSStationRepo;
import com.viettel.base.cms.service.BTSStationService;
import com.viettel.base.cms.service.LocationService;
import com.viettel.base.cms.service.OptionSetValueService;
import com.viettel.base.cms.utils.DataUtil;
import com.viettel.base.cms.utils.FunctionUtils;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class BTSStationServiceImpl implements BTSStationService {

    private static int timeout = 5000;
    private static String linkService = "";
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
    private final DateFormat formatterDate = new SimpleDateFormat("dd/MM/yyyy");
    private final DateFormat formatterString = new SimpleDateFormat("yyyy-MM-dd");
    private final DateFormat formatterStringTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
    @Autowired
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private EntityManager cms;

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
    public List<BTSStationDTO> searchBTSStation(BTSStationDTO btsStationDTO, String lang) throws Exception {
        return null;
    }

    @Override
    public void createBTSStation(BTSStationDTO btsStationDTO, Staff staff) throws Exception {

    }

    @Override
    public ExecutionResult updateBTSStation(List<BTSStationDTO> btsStationDTO, String userName, String roleCode, String language, Staff staff) throws Exception {
        return null;
    }

    @Override
    public void approvedBTSStation(BTSStationDTO btsStationDTO) throws Exception {

    }

    @Override
    public void turnOffBTSStation(BTSStation btsStation, BTSStationDTO btsStationDTO, Staff staff) throws Exception {

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
//                        btsStationDTO.setCreatedUser(userName.split("----")[0]);
//                        btsStationDTO.setCreatedDate(String.valueOf(LocalDateTime.now()));
//                        btsStationDTO.setApprovedStatus(1L);
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

            res.setDescription(r.getResourceMessage("construction.number.gen.success", btsStationDTOList.size(), lenFile));
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
                        if (retry < Constant.RETRY) {
                            validateCheckSiteOnNims(btsStationDTO, language, btsStationService, ++retry);
                        } else {
                            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                            res.setDescription(r.getResourceMessage("bts.site.on.nims.not.exits"));
                            return res;
                        }
                    }
                } else {
                    System.out.println("retry: " + retry);
                    if (retry < Constant.RETRY) {
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

    @Override
    public List<BTSStationDTO> searchBTSStationPNO(BTSStationDTO btsStationDTO, String lang) throws Exception {
        try {
            List<BTSStationDTO> lstResult = new ArrayList<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" SELECT  " +
                    "    brp.id, " +
                    "    brp.site_on_nims, " +
                    "    brp.long_value, " +
                    "    brp.lat_value, " +
                    "    (CASE  " +
                    "        WHEN language_var = 'vi' THEN (SELECT osv.name_vi FROM option_set_value osv WHERE osv.option_set_value_id = brp.uses) " +
                    "        WHEN language_var = 'en' THEN (SELECT osv.name_en FROM option_set_value osv WHERE osv.option_set_value_id = brp.uses) " +
                    "        WHEN language_var = 'la' THEN (SELECT osv.name_la FROM option_set_value osv WHERE osv.option_set_value_id = brp.uses) " +
                    "    END) AS uses, " +
                    "    (CASE  " +
                    "        WHEN language_var = 'vi' THEN (SELECT osv.name_vi FROM option_set_value osv WHERE osv.option_set_value_id = brp.type_rental_area) " +
                    "        WHEN language_var = 'en' THEN (SELECT osv.name_en FROM option_set_value osv WHERE osv.option_set_value_id = brp.type_rental_area) " +
                    "        WHEN language_var = 'la' THEN (SELECT osv.name_la FROM option_set_value osv WHERE osv.option_set_value_id = brp.type_rental_area) " +
                    "    END) AS type_rental_area, " +
                    "    brp.unit_price, " +
                    "    (CASE  " +
                    "        WHEN language_var = 'vi' THEN (SELECT osv.name_vi FROM option_set_value osv WHERE osv.option_set_value_id = brp.station_type) " +
                    "        WHEN language_var = 'en' THEN (SELECT osv.name_en FROM option_set_value osv WHERE osv.option_set_value_id = brp.station_type) " +
                    "        WHEN language_var = 'la' THEN (SELECT osv.name_la FROM option_set_value osv WHERE osv.option_set_value_id = brp.station_type) " +
                    "    END) AS station_type, " +
                    "    (CASE  " +
                    "        WHEN language_var = 'vi' THEN (SELECT osv.name_vi FROM option_set_value osv WHERE osv.option_set_value_id = brp.station_type_by_service) " +
                    "        WHEN language_var = 'en' THEN (SELECT osv.name_en FROM option_set_value osv WHERE osv.option_set_value_id = brp.station_type_by_service) " +
                    "        WHEN language_var = 'la' THEN (SELECT osv.name_la FROM option_set_value osv WHERE osv.option_set_value_id = brp.station_type_by_service) " +
                    "    END) AS station_type_by_service, " +
                    "    (CASE  " +
                    "        WHEN language_var = 'vi' THEN (SELECT osv.name_vi FROM option_set_value osv WHERE osv.option_set_value_id = brp.station_locate) " +
                    "        WHEN language_var = 'en' THEN (SELECT osv.name_en FROM option_set_value osv WHERE osv.option_set_value_id = brp.station_locate) " +
                    "        WHEN language_var = 'la' THEN (SELECT osv.name_la FROM option_set_value osv WHERE osv.option_set_value_id = brp.station_locate) " +
                    "    END) AS station_locate, " +
                    "    brp.province_id , " +
                    "    p.pro_name province_name,  " +
                    "    brp.create_datetime,  " +
                    "    brp.create_by , " +
                    "    brp.update_datetime , " +
                    "    brp.update_by  " +
                    "FROM bts_rent_place brp , province p , district d , commune c  " +
                    "CROSS JOIN (SELECT :lang AS language_var) AS lang " +
                    "WHERE brp.province_id = p.pro_id  " +
                    "and brp.district_id  = d.DISTRICT_ID  " +
                    "and brp.commune_id = c.ID  ");
            if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getSiteOnNims())) {
                sql.append(" and lower(brp.site_on_nims) like lower(:siteOnNims) ");
            }
            if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getProvinceId())) {
                sql.append(" and brp.brp.province_id = :provinceId ");
            }
            if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getDistrictId())) {
                sql.append(" and brp.district_id = :districtId ");
            }
            sql.append(" limit :startRow , :pageLimit ");
            sql.append(" ORDER BY   brp.created_date DESC ");
            Query query = cms.createNativeQuery(sql.toString());
            if (!StringUtils.isStringNullOrEmpty(btsStationDTO)) {
                if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getSiteOnNims())) {
                    query.setParameter("siteOnNims", "%" + btsStationDTO.getSiteOnNims() + "%");
                }
                if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getProvinceId())) {
                    query.setParameter("provinceId", btsStationDTO.getSiteOnNims());
                }
                if (!StringUtils.isStringNullOrEmpty(btsStationDTO.getDistrictId())) {
                    query.setParameter("districtId", btsStationDTO.getDistrictId());
                }
                query.setParameter("lang", lang);
            }

            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    BTSStationDTO btsStationDTO1 = new BTSStationDTO();
                    btsStationDTO1.setId(DataUtils.getLong(obj[0]));
                    btsStationDTO1.setSiteOnNims(DataUtils.getString(obj[1]));
                    btsStationDTO1.setLongitude(DataUtils.getString(obj[2]));
                    btsStationDTO1.setLatitude(DataUtils.getString(obj[3]));
                    btsStationDTO1.setUsesName(DataUtils.getString(obj[4]));
                    btsStationDTO1.setTypeRentalAreaName(DataUtils.getString(obj[5]));
                    btsStationDTO1.setUnitPrice(DataUtils.getLong(obj[6]));
                    btsStationDTO1.setStationtypeName(DataUtils.getString(obj[7]));
                    btsStationDTO1.setStationTypeByServiceName(DataUtils.getString(obj[8]));
                    btsStationDTO1.setStationLocateName(DataUtils.getString(obj[9]));
                    btsStationDTO1.setProvinceId(DataUtils.getLong(obj[10]));
                    btsStationDTO1.setProvinceName(DataUtils.getString(obj[11]));
                    btsStationDTO1.setCreateDatetime(DataUtils.stringToLocalDateTme(DataUtils.getString(obj[12])));
                    btsStationDTO1.setCreateBy(DataUtils.getString(obj[13]));
                    btsStationDTO1.setUpdateDatetime(DataUtils.stringToLocalDateTme(DataUtils.getString(obj[14])));
                    btsStationDTO1.setUpdateBy(DataUtils.getString(obj[15]));
                    lstResult.add(btsStationDTO1);
                }
            }
            return lstResult;
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


}
