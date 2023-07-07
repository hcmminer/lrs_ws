package com.viettel.base.cms.controller;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.*;
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
            List<ProvinceDTO> listProvince = locationService.searchProvince(commonInputDTO.getProvinceName());
            if (listProvince != null && !listProvince.isEmpty()) {
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
                if (resultProvinceStatus.getProName().equals(commonInputDTO.getProvinceName())) {
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
                    int resultAddProvince = locationService.updateProvince(commonInputDTO.getProvinceDTO(), userName);
                    if (resultAddProvince != 1) {
                        res.setDescription(r.getResourceMessage("province.update.fail"));
                    } else {
                        res.setDescription(r.getResourceMessage("province.update.success"));
                    }
                    return res;
                }
                if (resultProvinceStatus.getProCode().equals(commonInputDTO.getProvinceCode())) {
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
                    int resultAddProvince = locationService.updateProvince(commonInputDTO.getProvinceDTO(), userName);
                    if (resultAddProvince != 1) {
                        res.setDescription(r.getResourceMessage("province.update.fail"));
                    } else {
                        res.setDescription(r.getResourceMessage("province.update.success"));
                    }
                    return res;
                }
            } else {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.check.status.update"));
                return res;
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
            int resultUpdateProvince = locationService.deleteProvince(commonInputDTO.getProvinceDTO(), userName);
            if (resultUpdateProvince != 1) {
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

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO()) ||
                    StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO().getProId())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.info.null"));
                return res;
            }
            List<DistrictDTO> constructionItemDTOS = locationService.getListDistrict(commonInputDTO.getProvinceDTO());
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
            List<DistrictDTO> listDistrict = locationService.searchDistrict(commonInputDTO.getDistrictDTO());
            if (listDistrict != null && !listDistrict.isEmpty()) {
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
                if (resultDistrictStatus.getDistCode().equals(commonInputDTO.getDistrictDTO().getDistCode())) {
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
                if (resultDistrictStatus.getDistName().equals(commonInputDTO.getDistrictDTO().getDistName())) {
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
            } else {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("district.check.status.update"));
                return res;
            }
            int resultUpdateDistrict = locationService.updateDistrict(commonInputDTO.getDistrictDTO(), userName);
            if (resultUpdateDistrict != 1) {
                res.setDescription(r.getResourceMessage("district.ư.fail"));
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
            int resultUpdateProvince = locationService.deleteDistrict(commonInputDTO.getDistrictDTO(), userName);
            if (resultUpdateProvince != 1) {
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
            int totalRecord = locationService.totalRecordSearch(commonInputDTO.getDataParams(), commonInputDTO.getCommuneDTO());
            DataParams pageInfo = DataUtils.getPageInfo(commonInputDTO.getDataParams(), totalRecord);
            commonInputDTO.setDataParams(pageInfo);
            List<CommuneDTO> constructionItemDTOS = locationService.getListCommune(commonInputDTO.getDataParams(), commonInputDTO.getCommuneDTO());
            res.setPageInfo(pageInfo);
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
            List<CommuneDTO> listCommune = locationService.searchCommune(commonInputDTO.getCommuneDTO());
            if (listCommune != null && !listCommune.isEmpty()) {
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
//    @PostMapping("updateCommune")
//    public ExecutionResult updateCommune(@RequestHeader("Accept-Language") String language, @RequestBody CommonInputDTO commonInputDTO) {
//        ExecutionResult res = new ExecutionResult();
//        ResourceBundle r = new ResourceBundle(language);
//        String userName = commonInputDTO.getUserName().split("----")[0];
//        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
//        try {
//            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getCommuneDTO().getProId())) {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("province.name.null"));
//                return res;
//            }
//            CommuneDTO resultDistrictStatus = locationService.checkDistrictStatus(commonInputDTO.getCommuneDTO());
//            if (resultDistrictStatus != null) {
//                if (resultDistrictStatus.getStatus() == 0) {
//                    res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                    res.setDescription(r.getResourceMessage("district.check.status.update"));
//                    return res;
//                }
//                if (resultDistrictStatus.getDistCode().equals(commonInputDTO.getDistrictDTO().getDistCode())) {
//                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO().getDistCode())) {
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("district.code.null"));
//                        return res;
//                    }
//                    ProvinceDTO province = new ProvinceDTO();
//                    province.setProId(commonInputDTO.getDistrictDTO().getProId());
//                    ProvinceDTO resultProvinceName = locationService.checkProvinceId(province);
//                    if (resultProvinceName.getStatus() == 0) {
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("province.check.id.delete"));
//                        return res;
//                    }
//                    List<DistrictDTO> resultDistrictCode = locationService.checkDistrictCodeDuplicate(commonInputDTO.getDistrictDTO().getDistCode());
//                    if (resultDistrictCode != null && !resultDistrictCode.isEmpty()) {
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("district.code.duplicate"));
//                        return res;
//                    }
//                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO().getDistName())) {
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("district.name.null"));
//                        return res;
//                    }
//                    List<DistrictDTO> resultDistrictName = locationService.checkDistrictNameDuplicate(commonInputDTO.getDistrictDTO().getDistName());
//                    if (resultDistrictName != null && !resultDistrictName.isEmpty()) {
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("district.name.duplicate"));
//                        return res;
//                    }
//                }
//                if (resultDistrictStatus.getDistName().equals(commonInputDTO.getDistrictDTO().getDistName())) {
//                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO().getDistCode())) {
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("district.code.null"));
//                        return res;
//                    }
//                    ProvinceDTO province = new ProvinceDTO();
//                    province.setProId(commonInputDTO.getDistrictDTO().getProId());
//                    ProvinceDTO resultProvinceName = locationService.checkProvinceId(province);
//                    if (resultProvinceName.getStatus() == 0) {
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("province.check.id.delete"));
//                        return res;
//                    }
//                    List<DistrictDTO> resultDistrictCode = locationService.checkDistrictCodeDuplicate(commonInputDTO.getDistrictDTO().getDistCode());
//                    if (resultDistrictCode != null && !resultDistrictCode.isEmpty()) {
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("district.code.duplicate"));
//                        return res;
//                    }
//                    if (StringUtils.isStringNullOrEmpty(commonInputDTO.getDistrictDTO().getDistName())) {
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("district.name.null"));
//                        return res;
//                    }
//                    List<DistrictDTO> resultDistrictName = locationService.checkDistrictNameDuplicate(commonInputDTO.getDistrictDTO().getDistName());
//                    if (resultDistrictName != null && !resultDistrictName.isEmpty()) {
//                        res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                        res.setDescription(r.getResourceMessage("district.name.duplicate"));
//                        return res;
//                    }
//                }
//            } else {
//                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//                res.setDescription(r.getResourceMessage("district.check.status.update"));
//                return res;
//            }
//            int resultUpdateDistrict = locationService.updateDistrict(commonInputDTO.getDistrictDTO(), userName);
//            if (resultUpdateDistrict != 1) {
//                res.setDescription(r.getResourceMessage("district.ư.fail"));
//            } else {
//                res.setDescription(r.getResourceMessage("district.update.success"));
//            }
//            return res;
//        } catch (Exception e) {
//            e.printStackTrace();
//            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
//            res.setDescription(r.getResourceMessage("system.error"));
//            return res;
//        }
//    }
}
