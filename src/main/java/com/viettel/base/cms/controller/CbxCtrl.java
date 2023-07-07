package com.viettel.base.cms.controller;

import com.viettel.base.cms.dto.CommonInputDTO;
import com.viettel.base.cms.model.OptionSetV1;
import com.viettel.base.cms.model.OptionSetValue;
import com.viettel.base.cms.model.OptionSetValueV1;
import com.viettel.base.cms.model.ProvinceV1Model;
import com.viettel.base.cms.repo.CbxAreaRepo;
import com.viettel.base.cms.repo.CbxProvinceRepo;
import com.viettel.vfw5.base.dto.ExecutionResult;
import com.viettel.vfw5.base.utils.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class CbxCtrl {
    @Autowired
    private CbxProvinceRepo cbxProvinceRepo;

    @Autowired
    private CbxAreaRepo cbxAreaRepo;

    @PostMapping(value = "/listProvince")
    public ExecutionResult listProvince(@RequestHeader("Accept-Language") String language,
                                         @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            List<ProvinceV1Model> list = cbxProvinceRepo.findAllByStatus("1");
            res.setData(list);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode("1");
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/listArea")
    public ExecutionResult listArea(@RequestHeader("Accept-Language") String language,
                                        @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            List<OptionSetValueV1> list = cbxAreaRepo.findAllByOptionSetIdAndStatus(commonInputDTO.getSearchV1DTO().getOptionSetId() ,1L);
            res.setData(list);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode("1");
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }


}
