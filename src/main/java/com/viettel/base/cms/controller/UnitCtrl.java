package com.viettel.base.cms.controller;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.CommonInputDTO;
import com.viettel.base.cms.dto.UnitDTO;
import com.viettel.base.cms.interfaces.*;
import com.viettel.base.cms.model.Unit;
import com.viettel.base.cms.repo.UnitRepo;
import com.viettel.vfw5.base.dto.ExecutionResult;
import com.viettel.vfw5.base.utils.DataUtils;
import com.viettel.vfw5.base.utils.ResourceBundle;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api")
public class UnitCtrl {
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private EntityManager cms;

    @Autowired
    UnitRepo unitRepo;

    @PostMapping(value = "/listUnit")
    public ExecutionResult listUnit(@RequestHeader("Accept-Language") String language,
                                    @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            List<IUnit> list = unitRepo.findAllByProvinceIdAndUnitNameAndStatus(
                    commonInputDTO.getSearchV1DTO().getProvinceId(),
                    commonInputDTO.getSearchV1DTO().getUnitName(),
                    1L, language);
            res.setData(list);
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode("1");
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/addUnit")
    public ExecutionResult addUnit(@RequestHeader("Accept-Language") String language,
                                   @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            UnitDTO unitDTO = commonInputDTO.getUnitDTO();
            boolean isExists = unitRepo.existsByUnitCodeOrUnitNameViOrUnitNameEnOrUnitNameLaAndProvinceIdAndStatus(
                    unitDTO.getUnitCode(),
                    unitDTO.getUnitNameVi(),
                    unitDTO.getUnitNameEn(),
                    unitDTO.getUnitNameLa(),
                    unitDTO.getProvinceId(),
                    1L);
            if (isExists) {
                res.setDescription(r.getResourceMessage("cm.codeOrName.exists"));
                res.setErrorCode("1");
                return res;
            }

            Unit unit = new Unit();

            BeanUtils.copyProperties(unitDTO, unit);

            unit.setUnitId(DataUtils.getSequence(cms, "unit_seq"));
            unit.setCreateBy(commonInputDTO.getUserName().split("----")[0]);
            unit.setCreateDatetime(LocalDateTime.now());
            unit.setStatus(1L);
            unitRepo.save(unit);
            res.setDescription(r.getResourceMessage("cm.addSuccess"));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode("1");
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/editUnit")
    public ExecutionResult editUnit(@RequestHeader("Accept-Language") String language,
                                    @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            UnitDTO unitDTO = commonInputDTO.getUnitDTO();


            List<Unit> listRest = unitRepo.findAllByUnitIdNotAndStatus(unitDTO.getUnitId(), 1L);

            List<Unit> listFilter = new ArrayList<>();


            listFilter = listRest.stream().filter(item ->
                    item.getUnitCode().equals(unitDTO.getUnitCode())
                            || item.getUnitNameVi().equals(unitDTO.getUnitNameVi())
                            || item.getUnitNameEn().equals(unitDTO.getUnitNameEn())
                            || item.getUnitNameLa().equals(unitDTO.getUnitNameLa())
                            && item.getProvinceId().equals(unitDTO.getProvinceId())
            ).collect(Collectors.toList());


            if (listFilter.size() > 0) {
                res.setDescription(r.getResourceMessage("cm.codeOrName.exists"));
                res.setErrorCode("1");
                return res;
            }

            Unit unit = new Unit();

            BeanUtils.copyProperties(unitDTO, unit);

            unit.setCreateBy(commonInputDTO.getUserName().split("----")[0]);
            unit.setCreateDatetime(LocalDateTime.now());
            unit.setStatus(1L);

            unitRepo.save(unit);
            res.setDescription(r.getResourceMessage("cm.editSuccess"));
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode("1");
            res.setDescription(r.getResourceMessage("system.error"));
        }
        return res;
    }

    @PostMapping(value = "/delUnit")
    public ExecutionResult delUnit(@RequestHeader("Accept-Language") String language,
                                   @RequestBody CommonInputDTO commonInputDTO) {
        ExecutionResult res = new ExecutionResult();
        ResourceBundle r = new ResourceBundle(language);
        res.setErrorCode("0");
        try {
            UnitDTO unitDTO = commonInputDTO.getUnitDTO();
            boolean isExists = unitRepo.existsByUnitCodeOrUnitNameViOrUnitNameEnOrUnitNameLaAndProvinceIdAndStatus(
                    unitDTO.getUnitCode(),
                    unitDTO.getUnitNameVi(),
                    unitDTO.getUnitNameEn(),
                    unitDTO.getUnitNameLa(),
                    unitDTO.getProvinceId(),
                    1L);
            if (isExists) {
                res.setDescription(r.getResourceMessage("cm.code.exists"));
                res.setErrorCode("1");
                return res;
            }


            Optional<Unit> optional = unitRepo.findById(unitDTO.getUnitId());

            if (!optional.isPresent()) {
                res.setDescription(r.getResourceMessage("cm.code.not.exists"));
                res.setErrorCode("1");
                return res;
            }

            Unit unit = optional.get();

            unit.setCreateBy(commonInputDTO.getUserName().split("----")[0]);
            unit.setCreateDatetime(LocalDateTime.now());
            unit.setStatus(0L);

            unitRepo.save(unit);


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
