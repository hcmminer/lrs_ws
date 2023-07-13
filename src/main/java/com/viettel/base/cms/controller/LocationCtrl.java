package com.viettel.base.cms.controller;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.*;
import com.viettel.base.cms.model.Commune;
import com.viettel.base.cms.model.District;
import com.viettel.base.cms.model.Province;
import com.viettel.base.cms.service.LocationService;
import com.viettel.vfw5.base.dto.ExecutionResult;
import com.viettel.vfw5.base.utils.DataUtils;
import com.viettel.vfw5.base.utils.ResourceBundle;
import com.viettel.vfw5.base.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class LocationCtrl {

    @Autowired
    LocationService locationService;

    @PostMapping(value = "/getListProvince")
    public ExecutionResult getListProvince(@RequestHeader("Accept-Language") String language,
                                           @RequestBody CommonInputDTO commonInputDTO) throws Exception {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            List<ProvinceDTO> listProvince = locationService.getListProvince();
            res.setData(listProvince);
            res.setDescription(Constant.EXECUTION_MESSAGE.OK);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/searchProvince")
    public ExecutionResult searchProvince(@RequestHeader("Accept-Language") String language,
                                          @RequestBody CommonInputDTO commonInputDTO) throws Exception {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDataParams().getCurrentPage()) || commonInputDTO.getDataParams().getCurrentPage() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("current.page.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDataParams().getPageLimit()) || commonInputDTO.getDataParams().getPageLimit() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("page.limit.null"));
                return res;
            }
            int totalRecord = locationService.totalRecordSearchProvince(commonInputDTO.getDataParams(), commonInputDTO.getProvinceDTO());
            DataParams pageInfo = DataUtils.getPageInfo(commonInputDTO.getDataParams(), totalRecord);
            commonInputDTO.setDataParams(pageInfo);
            List<ProvinceDTO> listProvince = locationService.searchProvince(commonInputDTO.getDataParams(), commonInputDTO.getProvinceDTO());
            if (listProvince != null && !listProvince.isEmpty()) {
                res.setPageInfo(pageInfo);
                res.setData(listProvince);
                res.setDescription(Constant.EXECUTION_MESSAGE.OK);
            } else {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("result.is.null"));
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping({"addProvince"})
    public ExecutionResult addProvice(@RequestHeader("Accept-Language") String language, @RequestBody CommonInputDTO commonInputDTO) throws Exception {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        String userName = commonInputDTO.getUserName().split("----")[0];
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO().getProCode())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.code.null"));
                return res;
            }
            List<ProvinceDTO> resultProvince = locationService.checkProvinceCodeDuplicate(commonInputDTO.getProvinceDTO().getProCode());
            if (resultProvince != null && !resultProvince.isEmpty()) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.code.check.duplicate"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO().getProName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.name.null"));
                return res;
            }
            List<ProvinceDTO> resultProvinceName = locationService.checkProvinceNameDuplicate(commonInputDTO.getProvinceDTO().getProName());
            if (resultProvinceName != null && !resultProvinceName.isEmpty()) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.name.check.duplicate"));
                return res;
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO())) {
                Province resultAddProvince = locationService.addProvince(commonInputDTO.getProvinceDTO(), userName);
                if (resultAddProvince == null) {
                    res.setDescription(r.getResourceMessage("province.add.fail"));
                } else {
                    res.setDescription(r.getResourceMessage("province.add.success"));
                }
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping("updateProvince")
    public ExecutionResult updateProvince(@RequestHeader("Accept-Language") String language, @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        String userName = commonInputDTO.getUserName().split("----")[0];
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.null"));
                return res;
            }
            ProvinceDTO resultProvinceStatus = locationService.checkProvinceStatus(commonInputDTO.getProvinceDTO());
            if (resultProvinceStatus != null) {
                if (resultProvinceStatus.getStatus() == 0) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("province.check.status.update"));
                    return res;
                }
                if (!resultProvinceStatus.getProCode().equals(commonInputDTO.getProvinceDTO().getProCode())) {
                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO().getProCode())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("province.code.null"));
                        return res;
                    }
                    List<ProvinceDTO> resultProvince = locationService.checkProvinceCodeDuplicate(commonInputDTO.getProvinceDTO().getProCode());
                    if (resultProvince != null && !resultProvince.isEmpty()) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("province.code.check.duplicate"));
                        return res;
                    }
                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO().getProId())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("province.id.null"));
                        return res;
                    }
                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO().getProName())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("province.name.null"));
                        return res;
                    }

                    List<ProvinceDTO> resultProvinceName = locationService.checkProvinceNameDuplicate(commonInputDTO.getProvinceDTO().getProName());
                    if (resultProvinceName != null && !resultProvinceName.isEmpty()) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("province.name.check.duplicate"));
                        return res;
                    }
                }
                if (!resultProvinceStatus.getProName().equals(commonInputDTO.getProvinceDTO().getProName())) {
                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO().getProName())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("province.name.null"));
                        return res;
                    }
                    List<ProvinceDTO> resultProvince = locationService.checkProvinceCodeDuplicate(commonInputDTO.getProvinceDTO().getProCode());
                    if (resultProvince != null && !resultProvince.isEmpty()) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("province.code.check.duplicate"));
                        return res;
                    }
                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO().getProId())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("province.id.null"));
                        return res;
                    }
                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO().getProCode())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("province.code.null"));
                        return res;
                    }
                    List<ProvinceDTO> resultProvinceName = locationService.checkProvinceNameDuplicate(commonInputDTO.getProvinceDTO().getProName());
                    if (resultProvinceName != null && !resultProvinceName.isEmpty()) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("province.name.check.duplicate"));
                        return res;
                    }
                }
            } else {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.check.status.update"));
                return res;
            }

            int resultAddProvince = locationService.updateProvince(commonInputDTO.getProvinceDTO(), userName);
            if (resultAddProvince != 1) {
                res.setDescription(r.getResourceMessage("province.update.fail"));
            } else {
                res.setDescription(r.getResourceMessage("province.update.success"));
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
            return res;
        }
    }

    @PostMapping("deleteProvince")
    public ExecutionResult deleteProvince(@RequestHeader("Accept-Language") String language, @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        String userName = commonInputDTO.getUserName().split("----")[0];
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO().getProId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.id.null"));
                return res;
            }
            ProvinceDTO resultProvinceName = locationService.checkProvinceId(commonInputDTO.getProvinceDTO());
            if (resultProvinceName.getStatus() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.check.id.delete"));
                return res;
            }
            int resultDeleteProvince = locationService.deleteProvince(commonInputDTO.getProvinceDTO(), userName);
            if (resultDeleteProvince != 1) {
                res.setDescription(r.getResourceMessage("province.delete.fail"));
            } else {
                res.setDescription(r.getResourceMessage("province.delete.success"));
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
            return res;
        }
    }

    @PostMapping(value = "/getListDistrict")
    public ExecutionResult getListDistrict(@RequestHeader("Accept-Language") String language,
                                           @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO()) ||
                    StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO().getProId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.info.null"));
                return res;
            }
            List<DistrictDTO> constructionItemDTOS = locationService.getListDistrict(commonInputDTO.getDistrictDTO());
            res.setData(constructionItemDTOS);
            res.setDescription(Constant.EXECUTION_MESSAGE.OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/searchDistrict")
    public ExecutionResult searchDistrict(@RequestHeader("Accept-Language") String language,
                                          @RequestBody CommonInputDTO commonInputDTO) throws Exception {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDataParams().getCurrentPage()) || commonInputDTO.getDataParams().getCurrentPage() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("current.page.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDataParams().getPageLimit()) || commonInputDTO.getDataParams().getPageLimit() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("page.limit.null"));
                return res;
            }
            int totalRecord = locationService.totalRecordSearchDistrict(commonInputDTO.getDataParams(), commonInputDTO.getDistrictDTO());
            DataParams pageInfo = DataUtils.getPageInfo(commonInputDTO.getDataParams(), totalRecord);
            commonInputDTO.setDataParams(pageInfo);
            List<DistrictDTO> listDistrict = locationService.searchDistrict(commonInputDTO.getDataParams(), commonInputDTO.getDistrictDTO());
            if (listDistrict != null && !listDistrict.isEmpty()) {
                res.setPageInfo(pageInfo);
                res.setData(listDistrict);
                res.setDescription(Constant.EXECUTION_MESSAGE.OK);
            } else {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("result.is.null"));
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping({"addDistrict"})
    public ExecutionResult addDistrict(@RequestHeader("Accept-Language") String language, @RequestBody CommonInputDTO commonInputDTO) throws Exception {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        String userName = commonInputDTO.getUserName().split("----")[0];
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO().getProId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.name.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO().getDistCode())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("district.code.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO().getDistName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("district.name.null"));
                return res;
            }
            ProvinceDTO province = new ProvinceDTO();
            province.setProId(commonInputDTO.getDistrictDTO().getProId());
            ProvinceDTO resultProvinceName = locationService.checkProvinceId(province);
            if (resultProvinceName.getStatus() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.check.id.delete"));
                return res;
            }
            List<DistrictDTO> resultDistrictCode = locationService.checkDistrictCodeDuplicate(commonInputDTO.getDistrictDTO().getDistCode());
            if (resultDistrictCode != null && !resultDistrictCode.isEmpty()) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("district.code.duplicate"));
                return res;
            }
            List<DistrictDTO> resultDistrictName = locationService.checkDistrictNameDuplicate(commonInputDTO.getDistrictDTO().getDistName());
            if (resultDistrictName != null && !resultDistrictName.isEmpty()) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("district.name.duplicate"));
                return res;
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO())) {
                District resultAddDistrict = locationService.addDistrict(commonInputDTO.getDistrictDTO(), userName);
                if (resultAddDistrict == null) {
                    res.setDescription(r.getResourceMessage("district.add.fail"));
                } else {
                    res.setDescription(r.getResourceMessage("district.add.success"));
                }
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping("updateDistrict")
    public ExecutionResult updateDistrict(@RequestHeader("Accept-Language") String language, @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        String userName = commonInputDTO.getUserName().split("----")[0];
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO().getProId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.name.null"));
                return res;
            }
            DistrictDTO resultDistrictStatus = locationService.checkDistrictStatus(commonInputDTO.getDistrictDTO());
            if (resultDistrictStatus != null) {
                if (resultDistrictStatus.getStatus() == 0) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("district.check.status.update"));
                    return res;
                }
                if (!resultDistrictStatus.getDistCode().equals(commonInputDTO.getDistrictDTO().getDistCode())) {
                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO().getDistCode())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("district.code.null"));
                        return res;
                    }
                    ProvinceDTO province = new ProvinceDTO();
                    province.setProId(commonInputDTO.getDistrictDTO().getProId());
                    ProvinceDTO resultProvinceName = locationService.checkProvinceId(province);
                    if (resultProvinceName.getStatus() == 0) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("province.check.id.delete"));
                        return res;
                    }
                    List<DistrictDTO> resultDistrictCode = locationService.checkDistrictCodeDuplicate(commonInputDTO.getDistrictDTO().getDistCode());
                    if (resultDistrictCode != null && !resultDistrictCode.isEmpty()) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("district.code.duplicate"));
                        return res;
                    }
                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO().getDistName())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("district.name.null"));
                        return res;
                    }
                    List<DistrictDTO> resultDistrictName = locationService.checkDistrictNameDuplicate(commonInputDTO.getDistrictDTO().getDistName());
                    if (resultDistrictName != null && !resultDistrictName.isEmpty()) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("district.name.duplicate"));
                        return res;
                    }
                }
                if (!resultDistrictStatus.getDistName().equals(commonInputDTO.getDistrictDTO().getDistName())) {
                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO().getDistName())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("district.name.null"));
                        return res;
                    }
                    ProvinceDTO province = new ProvinceDTO();
                    province.setProId(commonInputDTO.getDistrictDTO().getProId());
                    ProvinceDTO resultProvinceName = locationService.checkProvinceId(province);
                    if (resultProvinceName.getStatus() == 0) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("province.check.id.delete"));
                        return res;
                    }
                    List<DistrictDTO> resultDistrictCode = locationService.checkDistrictCodeDuplicate(commonInputDTO.getDistrictDTO().getDistCode());
                    if (resultDistrictCode != null && !resultDistrictCode.isEmpty()) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("district.code.duplicate"));
                        return res;
                    }
                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO().getDistCode())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("district.code.null"));
                        return res;
                    }
                    List<DistrictDTO> resultDistrictName = locationService.checkDistrictNameDuplicate(commonInputDTO.getDistrictDTO().getDistName());
                    if (resultDistrictName != null && !resultDistrictName.isEmpty()) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("district.name.duplicate"));
                        return res;
                    }
                }
            } else {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("district.check.status.update"));
                return res;
            }
            int resultUpdateDistrict = locationService.updateDistrict(commonInputDTO.getDistrictDTO(), userName);
            if (resultUpdateDistrict != 1) {
                res.setDescription(r.getResourceMessage("district.update.fail"));
            } else {
                res.setDescription(r.getResourceMessage("district.update.success"));
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
            return res;
        }
    }

    @PostMapping("deleteDistrict")
    public ExecutionResult deleteDistrict(@RequestHeader("Accept-Language") String language, @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        String userName = commonInputDTO.getUserName().split("----")[0];
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO().getProId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.id.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("district.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO().getDistId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("district.id.null"));
                return res;
            }
            ProvinceDTO province = new ProvinceDTO();
            province.setProId(commonInputDTO.getDistrictDTO().getProId());
            ProvinceDTO resultProvinceName = locationService.checkProvinceId(province);
            if (resultProvinceName.getStatus() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.check.id.delete"));
                return res;
            }
            DistrictDTO resultDistrictId = locationService.checkDistrictId(commonInputDTO.getDistrictDTO());
            if (resultDistrictId.getStatus() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("district.check.id.delete"));
                return res;
            }
            int resultDeleteDistrict = locationService.deleteDistrict(commonInputDTO.getDistrictDTO(), userName);
            if (resultDeleteDistrict != 1) {
                res.setDescription(r.getResourceMessage("district.delete.fail"));
            } else {
                res.setDescription(r.getResourceMessage("district.delete.success"));
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
            return res;
        }
    }

    @PostMapping(value = "/getListCommune")
    public ExecutionResult getListCommune(@RequestHeader("Accept-Language") String language,
                                           @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO()) ||
                    StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO().getProId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.info.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO()) ||
                    StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO().getDistId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("district.null"));
                return res;
            }
            ProvinceDTO province = new ProvinceDTO();
            province.setProId(commonInputDTO.getCommuneDTO().getProId());
            ProvinceDTO resultProvinceName = locationService.checkProvinceId(province);
            if (resultProvinceName.getStatus() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.check.id.delete"));
                return res;
            }
            DistrictDTO district = new DistrictDTO();
            district.setDistId(commonInputDTO.getCommuneDTO().getDistId());
            DistrictDTO resultDistrictId = locationService.checkDistrictId(district);
            if (resultDistrictId.getStatus() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("district.check.id.delete"));
                return res;
            }
            List<CommuneDTO> constructionItemDTOS = locationService.getListCommune(commonInputDTO.getCommuneDTO());
            res.setData(constructionItemDTOS);
            res.setDescription(Constant.EXECUTION_MESSAGE.OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }
    @PostMapping(value = "/searchCommune")
    public ExecutionResult searchCommune(@RequestHeader("Accept-Language") String language,
                                          @RequestBody CommonInputDTO commonInputDTO) throws Exception {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDataParams().getCurrentPage()) || commonInputDTO.getDataParams().getCurrentPage() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("current.page.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDataParams().getPageLimit()) || commonInputDTO.getDataParams().getPageLimit() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("page.limit.null"));
                return res;
            }
            int totalRecord = locationService.totalRecordSearchCommune(commonInputDTO.getDataParams(), commonInputDTO.getCommuneDTO());
            DataParams pageInfo = DataUtils.getPageInfo(commonInputDTO.getDataParams(), totalRecord);
            commonInputDTO.setDataParams(pageInfo);
            List<CommuneDTO> listCommune = locationService.searchCommune(commonInputDTO.getDataParams(), commonInputDTO.getCommuneDTO());
            if (listCommune != null && !listCommune.isEmpty()) {
                res.setPageInfo(pageInfo);
                res.setData(listCommune);
                res.setDescription(Constant.EXECUTION_MESSAGE.OK);
            } else {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("result.is.null"));
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping({"addCommune"})
    public ExecutionResult addCommune(@RequestHeader("Accept-Language") String language, @RequestBody CommonInputDTO commonInputDTO) throws Exception {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        String userName = commonInputDTO.getUserName().split("----")[0];
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO().getProId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.name.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO().getDistId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("district.name.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO().getCommuneCode())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("commune.code.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO().getCommuneName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("commune.name.null"));
                return res;
            }
            ProvinceDTO province = new ProvinceDTO();
            province.setProId(commonInputDTO.getCommuneDTO().getProId());
            ProvinceDTO resultProvinceName = locationService.checkProvinceId(province);
            if (resultProvinceName.getStatus() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.check.id.delete"));
                return res;
            }
            DistrictDTO district = new DistrictDTO();
            district.setDistId(commonInputDTO.getCommuneDTO().getDistId());
            DistrictDTO resultDistName = locationService.checkDistrictId(district);
            if (resultDistName.getStatus() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("district.check.id.delete"));
                return res;
            }
            List<CommuneDTO> resultCommuneCode = locationService.checkCommuneCodeDuplicate(commonInputDTO.getCommuneDTO().getCommuneCode());
            if (resultCommuneCode != null && !resultCommuneCode.isEmpty()) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("commune.code.duplicate"));
                return res;
            }
            List<CommuneDTO> resultCommuneName = locationService.checkCommuneNameDuplicate(commonInputDTO.getCommuneDTO().getCommuneName());
            if (resultCommuneName != null && !resultCommuneName.isEmpty()) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("commune.name.duplicate"));
                return res;
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO())) {
                Commune resultAddCommune = locationService.addCommune(commonInputDTO.getCommuneDTO(), userName);
                if (resultAddCommune == null) {
                    res.setDescription(r.getResourceMessage("commune.add.fail"));
                } else {
                    res.setDescription(r.getResourceMessage("commune.add.success"));
                }
                return res;
            }
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping("updateCommune")
    public ExecutionResult updateCommune(@RequestHeader("Accept-Language") String language, @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        String userName = commonInputDTO.getUserName().split("----")[0];
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO().getProId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.name.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO().getDistId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("district.name.null"));
                return res;
            }
            CommuneDTO resultCommuneStatus = locationService.checkCommuneStatus(commonInputDTO.getCommuneDTO());
            if (resultCommuneStatus != null) {
                if (resultCommuneStatus.getStatus() == 0) {
                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                    res.setDescription(r.getResourceMessage("commune.check.status.update"));
                    return res;
                }
                if (!resultCommuneStatus.getCommuneCode().equals(commonInputDTO.getCommuneDTO().getCommuneCode())) {
                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO().getCommuneCode())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("commune.code.null"));
                        return res;
                    }
                    ProvinceDTO province = new ProvinceDTO();
                    province.setProId(commonInputDTO.getCommuneDTO().getProId());
                    ProvinceDTO resultProvinceName = locationService.checkProvinceId(province);
                    if (resultProvinceName.getStatus() == 0) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("province.check.id.delete"));
                        return res;
                    }
                    DistrictDTO district = new DistrictDTO();
                    district.setDistId(commonInputDTO.getCommuneDTO().getDistId());
                    DistrictDTO resultDistName = locationService.checkDistrictId(district);
                    if (resultDistName.getStatus() == 0) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("district.check.id.delete"));
                        return res;
                    }
                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO().getCommuneName())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("commune.name.null"));
                        return res;
                    }
                    List<CommuneDTO> resultCommuneCode = locationService.checkCommuneCodeDuplicate(commonInputDTO.getCommuneDTO().getCommuneCode());
                    if (resultCommuneCode != null && !resultCommuneCode.isEmpty()) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("commune.code.duplicate"));
                        return res;
                    }
                    List<CommuneDTO> resultCommuneName = locationService.checkCommuneNameDuplicate(commonInputDTO.getCommuneDTO().getCommuneName());
                    if (resultCommuneName != null && !resultCommuneName.isEmpty()) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("commune.name.duplicate"));
                        return res;
                    }
                }
                if (!resultCommuneStatus.getCommuneName().equals(commonInputDTO.getCommuneDTO().getCommuneName())) {
                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO().getCommuneName())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("commune.name.null"));
                        return res;
                    }
                    ProvinceDTO province = new ProvinceDTO();
                    province.setProId(commonInputDTO.getCommuneDTO().getProId());
                    ProvinceDTO resultProvinceName = locationService.checkProvinceId(province);
                    if (resultProvinceName.getStatus() == 0) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("province.check.id.delete"));
                        return res;
                    }
                    DistrictDTO district = new DistrictDTO();
                    district.setDistId(commonInputDTO.getCommuneDTO().getDistId());
                    DistrictDTO resultDistName = locationService.checkDistrictId(district);
                    if (resultDistName.getStatus() == 0) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("district.check.id.delete"));
                        return res;
                    }
                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO().getCommuneCode())) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("commune.code.null"));
                        return res;
                    }
                    List<CommuneDTO> resultCommuneCode = locationService.checkCommuneCodeDuplicate(commonInputDTO.getCommuneDTO().getCommuneCode());
                    if (resultCommuneCode != null && !resultCommuneCode.isEmpty()) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("commune.code.duplicate"));
                        return res;
                    }
                    List<CommuneDTO> resultCommuneName = locationService.checkCommuneNameDuplicate(commonInputDTO.getCommuneDTO().getCommuneName());
                    if (resultCommuneName != null && !resultCommuneName.isEmpty()) {
                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                        res.setDescription(r.getResourceMessage("commune.name.duplicate"));
                        return res;
                    }
                }
            } else {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("commune.check.status.update"));
                return res;
            }
            int resultCommuneUpdate = locationService.updateCommune(commonInputDTO.getCommuneDTO(), userName);
            if (resultCommuneUpdate != 1) {
                res.setDescription(r.getResourceMessage("commune.update.fail"));
            } else {
                res.setDescription(r.getResourceMessage("commune.update.success"));
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
            return res;
        }
    }
    @PostMapping("deleteCommune")
    public ExecutionResult deleteCommune(@RequestHeader("Accept-Language") String language, @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        String userName = commonInputDTO.getUserName().split("----")[0];
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO().getProId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.id.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO().getDistId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("district.id.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("commune.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO().getCommuneId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("commune.id.null"));
                return res;
            }
            ProvinceDTO province = new ProvinceDTO();
            province.setProId(commonInputDTO.getCommuneDTO().getProId());
            ProvinceDTO resultProvinceName = locationService.checkProvinceId(province);
            if (resultProvinceName.getStatus() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.check.id.delete"));
                return res;
            }
            DistrictDTO district = new DistrictDTO();
            district.setDistId(commonInputDTO.getCommuneDTO().getDistId());
            DistrictDTO resultDistName = locationService.checkDistrictId(district);
            if (resultDistName.getStatus() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("district.check.id.delete"));
                return res;
            }
            CommuneDTO resultCommuneId = locationService.checkCommuneId(commonInputDTO.getCommuneDTO());
            if (resultCommuneId.getStatus() == 0) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("commune.check.id.delete"));
                return res;
            }
            int resultUpdateCommune = locationService.deleteCommune(commonInputDTO.getCommuneDTO(), userName);
            if (resultUpdateCommune != 1) {
                res.setDescription(r.getResourceMessage("commune.delete.fail"));
            } else {
                res.setDescription(r.getResourceMessage("commune.delete.success"));
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
            return res;
        }
    }
    @PostMapping("updateConstructionStartDateCND")
    public ExecutionResult updateConstructionStartDateCND(@RequestHeader("Accept-Language") String language, @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        String userName = commonInputDTO.getUserName().split("----")[0];
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.station.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.station.id.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getHandoverdate())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.hand.over.date.null"));
                return res;
            }
            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getBtsStationDTO().getConstructionstartdate())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("bts.construction.start.date.null"));
                return res;
            }
            int resultCNDUpdate = locationService.updateConstructionStartDateCND(commonInputDTO.getBtsStationDTO());
            if (resultCNDUpdate != 1) {
                res.setDescription(r.getResourceMessage("update.construction.start.date.CND.fail"));
            } else {
                res.setDescription(r.getResourceMessage("update.construction.start.date.CND.success"));
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
            return res;
        }
    }
}
