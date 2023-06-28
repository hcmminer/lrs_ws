package com.viettel.base.cms.controller;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.CommonInputDTO;
import com.viettel.base.cms.dto.DistrictDTO;
import com.viettel.base.cms.dto.ProvinceDTO;
import com.viettel.base.cms.model.Province;
import com.viettel.base.cms.service.LocationService;
import com.viettel.vfw5.base.dto.ExecutionResult;
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

    @PostMapping({"addProvince"})
    public ExecutionResult addProvice(@RequestHeader("Accept-Language") String language, @RequestBody CommonInputDTO commonInputDTO) throws Exception {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
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
            List<ProvinceDTO> resultProvince = this.locationService.checkProvinceCodeDuplicate(commonInputDTO.getProvinceDTO().getProCode());
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
            List<ProvinceDTO> resultProvinceName = this.locationService.checkProvinceNameDuplicate(commonInputDTO.getProvinceDTO().getProName());
            if (resultProvinceName != null && !resultProvinceName.isEmpty()) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.name.check.duplicate"));
                return res;
            }
            if (!StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO())) {
                Province resultAddProvince = this.locationService.addProvince(commonInputDTO.getProvinceDTO(), commonInputDTO.getUserName());
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

    @PostMapping({"updateProvince"})
    public ExecutionResult updateProvince(@RequestHeader("Accept-Language") String language, @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
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
            List<ProvinceDTO> resultProvince = this.locationService.checkProvinceCodeDuplicate(commonInputDTO.getProvinceDTO().getProCode());
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
            List<ProvinceDTO> resultProvinceName = this.locationService.checkProvinceNameDuplicate(commonInputDTO.getProvinceDTO().getProName());
            if (resultProvinceName != null && !resultProvinceName.isEmpty()) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.name.check.duplicate"));
                return res;
            }
            int resultAddProvince = this.locationService.updateProvince(commonInputDTO.getProvinceDTO(), commonInputDTO.getUserName());
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

    @PostMapping(value = "/getListDistrict")
    public ExecutionResult getListDistrict(@RequestHeader("Accept-Language") String language,
                                           @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        String isdn = null;
        // trace log
//        Long traceID = DataUtils.getSequence(upoint, "trace_logs_seq");
//        String funcitionName = "getListConstructionType";

        try {

            if (StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO()) ||
                    StringUtils.isStringNullOrEmpty(commonInputDTO.getProvinceDTO().getProCode())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("province.info.null"));
                return res;
            }
            List<DistrictDTO> constructionItemDTOS = locationService.getListDistrict(
                    commonInputDTO.getProvinceDTO());
            res.setData(constructionItemDTOS);
            res.setDescription(Constant.EXECUTION_MESSAGE.OK);
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("system.error"));
            // trace log
//            TraceLogs traceLogEx = new TraceLogs(null, traceID, e.toString(), LocalDateTime.now(), "SYSTEM", "2", funcitionName);
//            DataUtils.traceLogs.add(traceLogEx);
            // end trace log
        }
        return res;
    }
}
