package com.viettel.base.cms.service;

import com.viettel.base.cms.dto.*;
import com.viettel.base.cms.model.Commune;
import com.viettel.base.cms.model.District;
import com.viettel.base.cms.model.Province;

import java.util.List;

public interface LocationService {
    List<ProvinceDTO> getListProvince() throws Exception;
    List<ProvinceDTO> searchProvince(DataParams dataParams, ProvinceDTO provinceDTO) throws Exception;
    int totalRecordSearchProvince(DataParams dataParams, ProvinceDTO provinceDTO ) throws Exception;
    Province addProvince(ProvinceDTO paramProvinceDTO, String paramString) throws Exception;
    List<ProvinceDTO> checkProvinceCodeDuplicate(String paramString) throws Exception;
    List<ProvinceDTO> checkProvinceNameDuplicate(String paramString) throws Exception;
    ProvinceDTO checkProvinceStatus(ProvinceDTO provinceDTO) throws Exception;
    int updateProvince(ProvinceDTO paramProvinceDTO, String paramString) throws Exception;
    int deleteProvince(ProvinceDTO paramProvinceDTO, String paramString) throws Exception;
    ProvinceDTO checkProvinceId(ProvinceDTO paramProvinceDTO) throws Exception;
    List<DistrictDTO> getListDistrict(DistrictDTO districtDTO) throws Exception;
    List<DistrictDTO> searchDistrict(DataParams dataParams, DistrictDTO districtDTO) throws Exception;
    int totalRecordSearchDistrict(DataParams dataParams, DistrictDTO districtDTO) throws Exception;
    District addDistrict(DistrictDTO districtDTO, String userName) throws Exception;
    List<DistrictDTO> checkDistrictCodeDuplicate(String districtCode) throws Exception;
    List<DistrictDTO> checkDistrictNameDuplicate(String districtName) throws Exception;
    DistrictDTO checkDistrictStatus(DistrictDTO districtDTO) throws Exception;
    int updateDistrict(DistrictDTO districtDTO, String userName) throws Exception;
    DistrictDTO checkDistrictId(DistrictDTO districtDTO) throws Exception;
    int deleteDistrict(DistrictDTO districtDTO, String userName) throws Exception;
    List<CommuneDTO> getListCommune(CommuneDTO communeDTO) throws Exception;
    List<CommuneDTO> searchCommune(DataParams dataParams, CommuneDTO communeDTO) throws Exception;
    int totalRecordSearchCommune(DataParams dataParams, CommuneDTO communeDTO) throws Exception;
    Commune addCommune(CommuneDTO communeDTO, String userName) throws Exception;
    int updateCommune(CommuneDTO communeDTO, String userName) throws Exception;
    CommuneDTO checkCommuneStatus(CommuneDTO communeDTO) throws Exception;
    List<CommuneDTO> checkCommuneCodeDuplicate(String communeCode) throws Exception;
    List<CommuneDTO> checkCommuneNameDuplicate(String communeName) throws Exception;
    CommuneDTO checkCommuneId(CommuneDTO communeDTO) throws Exception;
    int deleteCommune(CommuneDTO communeDTO, String userName) throws Exception;
    List<Province> getListProvinceByCode(String paramString) throws Exception;
    List<District> getListDistrictByIdAndProCode(String paramString, Long paramLong) throws Exception;
    District getListDistrictById(Long paramLong) throws Exception;
    int updateConstructionStartDateCND(BTSStationDTO btsStationDTO) throws Exception;
}
