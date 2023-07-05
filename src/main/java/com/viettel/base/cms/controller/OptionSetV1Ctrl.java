package com.viettel.base.cms.controller;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.CommonInputDTO;
import com.viettel.base.cms.dto.OptionSetV1DTO;
import com.viettel.base.cms.dto.SearchV1DTO;
import com.viettel.base.cms.model.OptionSetV1;
import com.viettel.base.cms.model.OptionSetValueV1;
import com.viettel.base.cms.model.Staff;
import com.viettel.base.cms.repo.OptionSetV1Repo;
import com.viettel.base.cms.service.UserService;
import com.viettel.vfw5.base.dto.ExecutionResult;
import com.viettel.vfw5.base.utils.DataUtils;
import com.viettel.vfw5.base.utils.ResourceBundle;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api")
public class OptionSetV1Ctrl {
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private final EntityManager cms;

    private final OptionSetV1Repo optionSetV1Repo;

    public OptionSetV1Ctrl(EntityManager cms, OptionSetV1Repo optionSetV1Repo) {
        this.cms = cms;
        this.optionSetV1Repo = optionSetV1Repo;
    }

    @PostMapping(value = "/listOptionSet")
    public ExecutionResult listOptionSet(@RequestHeader("Accept-Language") String language,
                                         @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            List<OptionSetV1> optionSetV1DTOList = optionSetV1Repo.findAllByOptionSetCodeAndStatus(StringUtils.trimWhitespace(commonInputDTO.getSearchV1DTO().getOptionSetCode()), 1L);
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
            boolean isExists = optionSetV1Repo.existsByOptionSetCodeAndStatus(optionSetV1DTO.getOptionSetCode(), 1L);
            if (isExists) {
                res.setDescription(r.getResourceMessage("cm.code.exists"));
                res.setErrorCode("1");
                return res;
            }

            OptionSetV1 optionSetV1 = new OptionSetV1();

            BeanUtils.copyProperties(optionSetV1DTO, optionSetV1);

            optionSetV1.setOptionSetId(DataUtils.getSequence(cms, "option_set_seq"));
            optionSetV1.setCreateBy(commonInputDTO.getUserName().split("----")[0]);
            optionSetV1.setCreateDatetime(LocalDateTime.now());
            optionSetV1.setStatus(1L);
            optionSetV1Repo.save(optionSetV1);
            res.setDescription(r.getResourceMessage("cm.addSuccess"));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode("1");
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/editOptionSet")
    public ExecutionResult editOptionSet(@RequestHeader("Accept-Language") String language,
                                         @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            OptionSetV1DTO optionSetV1DTO = commonInputDTO.getOptionSetV1DTO();
            boolean isExists = optionSetV1Repo.existsByOptionSetCodeAndStatus(optionSetV1DTO.getOptionSetCode(), 1L);
            if (isExists) {
                res.setDescription(r.getResourceMessage("cm.code.exists"));
                res.setErrorCode("1");
                return res;
            }

            OptionSetV1 optionSetV1 = new OptionSetV1();

            BeanUtils.copyProperties(optionSetV1DTO, optionSetV1);

            optionSetV1.setCreateBy(commonInputDTO.getUserName().split("----")[0]);
            optionSetV1.setCreateDatetime(LocalDateTime.now());
            optionSetV1.setStatus(1L);

            optionSetV1Repo.save(optionSetV1);
            res.setDescription(r.getResourceMessage("cm.editSuccess"));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode("1");
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/delOptionSet")
    public ExecutionResult delOptionSet(@RequestHeader("Accept-Language") String language,
                                        @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            OptionSetV1DTO optionSetV1DTO = commonInputDTO.getOptionSetV1DTO();
            boolean isExists = optionSetV1Repo.existsByOptionSetCodeAndStatus(optionSetV1DTO.getOptionSetCode(), 1L);
            if (isExists) {
                res.setDescription(r.getResourceMessage("cm.code.exists"));
                res.setErrorCode("1");
                return res;
            }


            Optional<OptionSetV1> optionalOptionSetV1 = optionSetV1Repo.findById(optionSetV1DTO.getOptionSetId());

            if (!optionalOptionSetV1.isPresent()) {
                res.setDescription(r.getResourceMessage("cm.code.not.exists"));
                res.setErrorCode("1");
                return res;
            }

            OptionSetV1 optionSetV1 = optionalOptionSetV1.get();

            optionSetV1.setCreateBy(commonInputDTO.getUserName().split("----")[0]);
            optionSetV1.setCreateDatetime(LocalDateTime.now());
            optionSetV1.setStatus(0L);

            optionSetV1Repo.save(optionSetV1);


            res.setDescription(r.getResourceMessage("cm.delSuccess"));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode("1");
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }


}
