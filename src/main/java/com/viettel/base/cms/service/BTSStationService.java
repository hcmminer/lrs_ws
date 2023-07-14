package com.viettel.base.cms.service;

import com.viettel.base.cms.dto.BTSStationDTO;
import com.viettel.base.cms.dto.ConstructionDTO;
import com.viettel.base.cms.dto.DataParams;
import com.viettel.base.cms.model.BTSStation;
import com.viettel.base.cms.model.Construction;
import com.viettel.base.cms.model.Staff;
import com.viettel.vfw5.base.dto.ExecutionResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface BTSStationService {
    List<BTSStationDTO> searchBTSStation(DataParams dataParams, BTSStationDTO btsStationDTO, String lang) throws Exception;

    int totalRecordSearch(DataParams conditions, BTSStationDTO btsStationDTO, String lang);

    void createBTSStation(BTSStationDTO btsStationDTO, Staff staff) throws Exception;

    ExecutionResult updateBTSStation(List<BTSStationDTO> btsStationDTO,String userName, String roleCode, String language, Staff staff) throws Exception;

    void approvedBTSStation(BTSStationDTO btsStationDTO) throws Exception;

    void turnOffBTSStation(BTSStation btsStation , BTSStationDTO btsStationDTO, Staff staff) throws Exception;

    ExecutionResult getBTSStationFromFile(MultipartFile fileCreateRequest, String userName, String locate, Staff staff) throws Exception;


    List<BTSStation> getListBTSStation(BTSStationDTO btsStationDTO) throws Exception;

    List<BTSStation> getListBTSStationById(BTSStationDTO btsStationDTO) throws Exception;


    String getStatusBTSStation(BTSStationDTO btsStationDTO) throws Exception;

    String getApprovedStatusBTSStation(BTSStationDTO btsStationDTO) throws Exception;

    String getProvinceCodeOfStaff(String staffName) throws Exception;

    Long getProvinceIdByCode(String provinceCode) throws Exception;

    List<String> getListIsdnByDeptCode(String deptCode) throws Exception;

    int totalRecordSearchPNO(DataParams conditions, BTSStationDTO btsStationDTO, String lang);

    List<BTSStationDTO> searchBTSStationPNO(BTSStationDTO btsStationDTO, String lang) throws Exception;

}
