package com.viettel.base.cms.service;

import com.viettel.base.cms.dto.*;
import com.viettel.base.cms.model.Construction;
import com.viettel.base.cms.model.ConstructionDetail;
import com.viettel.base.cms.model.ConstructionItem;
import com.viettel.base.cms.model.ConstructionType;
import com.viettel.vfw5.base.dto.ExecutionResult;
import com.viettel.vfw5.base.utils.ResourceBundle;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collection;
import java.util.List;

public interface ConstructionService {
    List<ConstructionTypeDTO> getListConstructionType(String language) throws Exception;
    List<ConstructionItemDTO> getListConstructionItem(Long constructionId, ResourceBundle r) throws Exception;
    List<ConstructionDTO> searchConstruction(CommonInputDTO commonInputDTO, String roleCode, String language, ResourceBundle r) throws Exception;
    List<ConstructionDTO> searchConstructionForCM(CommonInputDTO commonInputDTO, String roleCode, String language, ResourceBundle r) throws Exception;
    void createConstruction(ConstructionDTO constructionDTO, int actionCode) throws Exception;
    ExecutionResult getConstructionFromFile(MultipartFile fileCreateRequest, String userName, String locate) throws Exception;
    ConstructionDTO getConstructionDetail(ConstructionDTO constructionDTO, String roleCode, String language, ResourceBundle r) throws Exception;
    List<String> getListConstructionItemName(Long constructionId, ResourceBundle r) throws Exception;
    List<Construction> getListConstruction(ConstructionDTO constructionDTO, int actionCode) throws Exception;
    void updateConstruction(ConstructionDTO constructionDTO) throws Exception;
    void deleteConstruction(ConstructionDTO constructionDTO) throws Exception;
    List<ConstructionItem> getListConstructionItemById(Long constructionItemId) throws Exception;
    void addConstructionItem(CommonInputDTO commonInputDTO) throws Exception;
    void assignConstruction(CommonInputDTO commonInputDTO) throws Exception;
    List<ConstructionDetail> getListConstructionDetailById(Long constructionDetailId) throws Exception;
    List<ConstructionDetailDTO> getListConstructionDetailDTO(Long constructionId, String roleCode, String language, ResourceBundle r) throws Exception;

    Construction getConstructionById(Long constructionId) throws Exception;
    List<ConstructionDetail> getListConstructionDetailByConstructionId(Long constructionId, int actionCode) throws Exception;
    ConstructionType getConstructionTypeById(Long constructionTypeId) throws Exception;

    List<ConstructionDetailDTO> getListItemDetailDTO(Long constructionId, String roleCode, String language, ResourceBundle r, Long constructionItemId) throws Exception;

    List<ConstructionDetail> getListConstructionDetailByParentId(Long constructionId, int actionCode, Long parentId) throws Exception;
}
