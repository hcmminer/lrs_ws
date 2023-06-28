package com.viettel.base.cms.service;

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

    int updateProvince(ProvinceDTO paramProvinceDTO, String paramString) throws Exception;

    List<DistrictDTO> getListDistrict(ProvinceDTO paramProvinceDTO) throws Exception;

    List<Province> getListProvinceByCode(String paramString) throws Exception;

    List<District> getListDistrictByIdAndProCode(String paramString, Long paramLong) throws Exception;

    District getListDistrictById(Long paramLong) throws Exception;
}
