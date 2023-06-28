package com.viettel.base.cms.controller;

import com.viettel.base.cms.dto.CommonInputDTO;
import com.viettel.base.cms.dto.SearchV1DTO;
import com.viettel.base.cms.model.OptionSetV1;
import com.viettel.base.cms.repo.OptionSetV1Repo;
import com.viettel.vfw5.base.dto.ExecutionResult;
import com.viettel.vfw5.base.utils.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class OptionSetV1Ctrl {
    @Autowired
    private OptionSetV1Repo optionSetV1Repo;

    @PostMapping(value = "/listOptionSet")
    public ExecutionResult listOptionSet(@RequestHeader("Accept-Language") String language,
                                         @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            List<OptionSetV1> optionSetV1DTOList = optionSetV1Repo.findAllByOptionSetId((commonInputDTO.getSearchV1DTO().getOptionSetId()));
            res.setData(optionSetV1DTOList);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode("1");
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }


}
