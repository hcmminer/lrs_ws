package com.viettel.base.cms.controller;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.CommonInputDTO;
import com.viettel.base.cms.dto.OptionSetValueV1DTO;
import com.viettel.base.cms.model.OptionSetValueV1;
import com.viettel.base.cms.repo.OptionSetValueV1Repo;
import com.viettel.base.cms.service.UserService;
import com.viettel.vfw5.base.dto.ExecutionResult;
import com.viettel.vfw5.base.utils.DataUtils;
import com.viettel.vfw5.base.utils.ResourceBundle;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/api")
public class OptionSetValueV1Ctrl {
    @Autowired
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private EntityManager cms;

    @Autowired
    private OptionSetValueV1Repo optionSetValueV1Repo;

    @Autowired
    private UserService userService;

    @PostMapping(value = "/listOptionSetValue")
    public ExecutionResult listOptionSetValue(@RequestHeader("Accept-Language") String language,
                                              @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            List<OptionSetValueV1> optionSetValueV1DTOList = optionSetValueV1Repo.findAllByOptionSetValueIdAndOptionSetIdAndStatus(commonInputDTO.getSearchV1DTO().getOptionSetValueId(), commonInputDTO.getSearchV1DTO().getOptionSetId(), 1L);
            res.setData(optionSetValueV1DTOList);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode("1");
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/addOptionSetValue")
    public ExecutionResult addOptionSetValue(@RequestHeader("Accept-Language") String language,
                                             @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            OptionSetValueV1DTO optionSetValueV1DTO = commonInputDTO.getOptionSetValueV1DTO();
            boolean isExists = optionSetValueV1Repo.existsByValueAndStatus(optionSetValueV1DTO.getValue(), 1L);
            if (isExists) {
                res.setDescription(r.getResourceMessage("cm.code.exists"));
                res.setErrorCode("1");
                return res;
            }

            OptionSetValueV1 optionSetValueV1 = new OptionSetValueV1();

            BeanUtils.copyProperties(optionSetValueV1DTO, optionSetValueV1);

            optionSetValueV1.setOptionSetValueId(DataUtils.getSequence(cms, "option_set_seq"));
            optionSetValueV1.setCreateBy(commonInputDTO.getUserName().split("----")[0]);
            optionSetValueV1.setCreateDatetime(LocalDateTime.now());
            optionSetValueV1.setStatus(1L);
            optionSetValueV1Repo.save(optionSetValueV1);
            res.setDescription(r.getResourceMessage("cm.addSuccess"));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode("1");
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/editOptionSetValue")
    public ExecutionResult editOptionSetValue(@RequestHeader("Accept-Language") String language,
                                              @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            OptionSetValueV1DTO optionSetValueV1DTO = commonInputDTO.getOptionSetValueV1DTO();
            boolean isExists = optionSetValueV1Repo.existsByValueAndStatus(optionSetValueV1DTO.getValue(), 1L);
            if (isExists) {
                res.setDescription(r.getResourceMessage("cm.code.exists"));
                res.setErrorCode("1");
                return res;
            }

            OptionSetValueV1 optionSetValueV1 = new OptionSetValueV1();

            BeanUtils.copyProperties(optionSetValueV1DTO, optionSetValueV1);

            optionSetValueV1.setCreateBy(commonInputDTO.getUserName().split("----")[0]);
            optionSetValueV1.setCreateDatetime(LocalDateTime.now());
            optionSetValueV1.setStatus(1L);

            optionSetValueV1Repo.save(optionSetValueV1);
            res.setDescription(r.getResourceMessage("cm.editSuccess"));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode("1");
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/delOptionSetValue")
    public ExecutionResult delOptionSetValue(@RequestHeader("Accept-Language") String language,
                                             @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            OptionSetValueV1DTO optionSetValueV1DTO = commonInputDTO.getOptionSetValueV1DTO();
            boolean isExists = optionSetValueV1Repo.existsByValueAndStatus(optionSetValueV1DTO.getValue(), 1L);
            if (isExists) {
                res.setDescription(r.getResourceMessage("cm.code.exists"));
                res.setErrorCode("1");
                return res;
            }

            Optional<OptionSetValueV1> optionSetValueV1 = optionSetValueV1Repo.findById(optionSetValueV1DTO.getOptionSetValueId());
            optionSetValueV1.ifPresent(item -> {
                item.setCreateBy(commonInputDTO.getUserName().split("----")[0]);
                item.setCreateDatetime(LocalDateTime.now());
                item.setStatus(0L);

                optionSetValueV1Repo.save(item);
            });


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
