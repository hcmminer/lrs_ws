package com.viettel.base.cms.service;

import com.viettel.base.cms.dto.DistrictDTO;
import com.viettel.base.cms.dto.ProvinceDTO;
import com.viettel.base.cms.model.District;
import com.viettel.base.cms.model.Province;

import java.util.List;

public interface LocationService {
    List<ProvinceDTO> getListProvince() throws Exception;
    List<DistrictDTO> getListDistrict(ProvinceDTO provinceDTO) throws Exception;
    List<Province> getListProvinceByCode(String provinceCode) throws Exception;
    List<District> getListDistrictByIdAndProCode(String provinceCode, Long districtId) throws Exception;
    District getListDistrictById(Long districtId) throws Exception;
}
