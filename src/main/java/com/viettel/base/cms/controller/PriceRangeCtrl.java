package com.viettel.base.cms.controller;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.CommonInputDTO;
import com.viettel.base.cms.dto.PriceRangeDTO;
import com.viettel.base.cms.interfaces.*;
import com.viettel.base.cms.model.PriceRange;
import com.viettel.base.cms.repo.PriceRangeRepo;
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
public class PriceRangeCtrl {
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private EntityManager cms;

    @Autowired
    PriceRangeRepo priceRangeRepo;

    @PostMapping(value = "/listPriceRange")
    public ExecutionResult listPriceRange(@RequestHeader("Accept-Language") String language,
                                         @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            List<IPriceRange> list = priceRangeRepo.findAllByProvinceIdAndOptionSetValueIdAndStatus( commonInputDTO.getSearchV1DTO().getProvinceId(), commonInputDTO.getSearchV1DTO().getOptionSetValueId(), 1L);
            res.setData(list);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode("1");
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/addPriceRange")
    public ExecutionResult addPriceRange(@RequestHeader("Accept-Language") String language,
                                        @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            PriceRangeDTO priceRangeDTO = commonInputDTO.getPriceRangeDTO();
            boolean isExists = priceRangeRepo.existsByOptionSetValueIdAndProvinceIdAndStatus(priceRangeDTO.getOptionSetValueId() ,priceRangeDTO.getProvinceId(), 1L);
            if (isExists) {
                res.setDescription(r.getResourceMessage("cm.code.exists"));
                res.setErrorCode("1");
                return res;
            }

            PriceRange priceRange = new PriceRange();

            BeanUtils.copyProperties(priceRangeDTO, priceRange);

            priceRange.setPriceRangeId(DataUtils.getSequence(cms, "price_range_seq"));
            priceRange.setCreateBy(commonInputDTO.getUserName().split("----")[0]);
            priceRange.setCreateDatetime(LocalDateTime.now());
            priceRange.setStatus(1L);
            priceRangeRepo.save(priceRange);
            res.setDescription(r.getResourceMessage("cm.addSuccess"));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode("1");
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/editPriceRange")
    public ExecutionResult editPriceRange(@RequestHeader("Accept-Language") String language,
                                         @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            PriceRangeDTO priceRangeDTO = commonInputDTO.getPriceRangeDTO();


            PriceRange priceRange = new PriceRange();

            BeanUtils.copyProperties(priceRangeDTO, priceRange);

            priceRange.setCreateBy(commonInputDTO.getUserName().split("----")[0]);
            priceRange.setCreateDatetime(LocalDateTime.now());
            priceRange.setStatus(1L);

            priceRangeRepo.save(priceRange);
            res.setDescription(r.getResourceMessage("cm.editSuccess"));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode("1");
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/delPriceRange")
    public ExecutionResult delPriceRange(@RequestHeader("Accept-Language") String language,
                                        @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            PriceRangeDTO priceRangeDTO = commonInputDTO.getPriceRangeDTO();
            boolean isExists = priceRangeRepo.existsByOptionSetValueIdAndProvinceIdAndStatus(priceRangeDTO.getOptionSetValueId() ,priceRangeDTO.getProvinceId(), 1L);
            if (isExists) {
                res.setDescription(r.getResourceMessage("cm.code.exists"));
                res.setErrorCode("1");
                return res;
            }


            Optional<PriceRange> optional = priceRangeRepo.findById(priceRangeDTO.getPriceRangeId());

            if (!optional.isPresent()) {
                res.setDescription(r.getResourceMessage("cm.code.not.exists"));
                res.setErrorCode("1");
                return res;
            }

            PriceRange priceRange = optional.get();

            priceRange.setCreateBy(commonInputDTO.getUserName().split("----")[0]);
            priceRange.setCreateDatetime(LocalDateTime.now());
            priceRange.setStatus(0L);

            priceRangeRepo.save(priceRange);


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
