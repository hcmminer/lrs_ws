package com.viettel.base.cms.service;

import com.viettel.base.cms.dto.CommuneDTO;
import com.viettel.base.cms.dto.DataParams;
import com.viettel.base.cms.dto.DistrictDTO;
import com.viettel.base.cms.dto.ProvinceDTO;
import com.viettel.base.cms.model.District;
import com.viettel.base.cms.model.Province;

import java.util.List;

public interface LocationService {
    List<ProvinceDTO> getListProvince() throws Exception;
    List<ProvinceDTO> searchProvince(String paramString) throws Exception;
    Province addProvince(ProvinceDTO paramProvinceDTO, String paramString) throws Exception;
    List<ProvinceDTO> checkProvinceCodeDuplicate(String paramString) throws Exception;
    List<ProvinceDTO> checkProvinceNameDuplicate(String paramString) throws Exception;
    ProvinceDTO checkProvinceStatus(ProvinceDTO provinceDTO) throws Exception;
    int updateProvince(ProvinceDTO paramProvinceDTO, String paramString) throws Exception;
    int deleteProvince(ProvinceDTO paramProvinceDTO, String paramString) throws Exception;
    ProvinceDTO checkProvinceId(ProvinceDTO paramProvinceDTO) throws Exception;
    List<DistrictDTO> getListDistrict(ProvinceDTO provinceDTO) throws Exception;
    List<DistrictDTO> searchDistrict(DistrictDTO districtDTO) throws Exception;
    District addDistrict(DistrictDTO DistrictDTO, String userName) throws Exception;
    List<DistrictDTO> checkDistrictCodeDuplicate(String districtCode) throws Exception;
    List<DistrictDTO> checkDistrictNameDuplicate(String districtName) throws Exception;
    DistrictDTO checkDistrictStatus(DistrictDTO districtDTO) throws Exception;
    int updateDistrict(DistrictDTO districtDTO, String userName) throws Exception;
    DistrictDTO checkDistrictId(DistrictDTO districtDTO) throws Exception;
    int deleteDistrict(DistrictDTO districtDTO, String userName) throws Exception;
    List<CommuneDTO> getListCommune(DataParams dataParams, CommuneDTO communeDTO) throws Exception;
    int totalRecordSearch(DataParams dataParams, CommuneDTO communeDTO) throws Exception;
    List<CommuneDTO> searchCommune(CommuneDTO communeDTO) throws Exception;
    List<Province> getListProvinceByCode(String paramString) throws Exception;
    List<District> getListDistrictByIdAndProCode(String paramString, Long paramLong) throws Exception;
    District getListDistrictById(Long paramLong) throws Exception;
}
