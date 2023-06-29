package com.viettel.base.cms.controller;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.CommonInputDTO;
import com.viettel.base.cms.dto.OptionSetV1DTO;
import com.viettel.base.cms.dto.SearchV1DTO;
import com.viettel.base.cms.model.OptionSetV1;
import com.viettel.base.cms.model.Staff;
import com.viettel.base.cms.repo.OptionSetV1Repo;
import com.viettel.base.cms.service.UserService;
import com.viettel.vfw5.base.dto.ExecutionResult;
import com.viettel.vfw5.base.utils.DataUtils;
import com.viettel.vfw5.base.utils.ResourceBundle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping(value = "/api")
public class OptionSetV1Ctrl {
    @Autowired
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private EntityManager cms;

    @Autowired
    private OptionSetV1Repo optionSetV1Repo;

    @Autowired
    private UserService userService;

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

    @PostMapping(value = "/addOptionSet")
    public ExecutionResult addOptionSet(@RequestHeader("Accept-Language") String language,
                                        @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {

            OptionSetV1DTO optionSetV1DTO = commonInputDTO.getOptionSetV1DTO();
            OptionSetV1 optionSetV1 = new OptionSetV1();
            optionSetV1.setOptionSetId(DataUtils.getSequence(cms, "option_set_seq"));
            optionSetV1.setOptionSetCode(optionSetV1DTO.getOptionSetCode());
            optionSetV1.setDescription(optionSetV1DTO.getDescription());
            optionSetV1.setCreateBy(commonInputDTO.getUserName().split("----")[0]);
            optionSetV1.setCreateDatetime(LocalDateTime.now());
            optionSetV1.setStatus(1L);
            optionSetV1Repo.save(optionSetV1);
            res.setDescription("cm.addSuccess");
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode("1");
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }


}
