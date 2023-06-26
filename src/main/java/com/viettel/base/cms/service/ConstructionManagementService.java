package com.viettel.base.cms.service;

import com.viettel.base.cms.dto.*;
import com.viettel.base.cms.model.ConstructionDetail;
import com.viettel.base.cms.model.ImageModel;
import com.viettel.base.cms.model.Staff;
import com.viettel.vfw5.base.utils.ResourceBundle;

import java.util.List;

public interface ConstructionManagementService {
    void updateConstructionStartDate(CommonInputDTO commonInputDTO) throws Exception;
    void updateConstructionItemStartDate(CommonInputDTO commonInputDTO) throws Exception;
    void createApproveRequest(CommonInputDTO commonInputDTO, Staff staff) throws Exception;
//    ImageDTO getImage(Long imageId) throws Exception;
    void approveConstructionItem(CommonInputDTO commonInputDTO, Staff staff) throws Exception;
    ConstructionDetail getConstructionDetailById(Long constructionDetailId) throws Exception;
    List<ConstructionDetail> getListCDWithStep(Long constructionId, Long step) throws Exception;

    List<ConstructionItemDTO> getListConstructionItem(ResourceBundle r) throws Exception;

    int approveImage(CommonInputDTO commonInputDTO, Staff staff, Long status) throws Exception;

    int rejectImage(CommonInputDTO commonInputDTO, Staff staff, Long status) throws Exception;

    void deleteImageListReject(CommonInputDTO commonInputDTO, Staff staff) throws Exception;

    void deleteImage(CommonInputDTO commonInputDTO, ImageModel image, Staff staff) throws Exception;

    List<ActionLogDTO> getConstructionItemHistory(Long constructionDetailId, ResourceBundle r) throws Exception;

    void approvedFinishConstructionItem(CommonInputDTO commonInputDTO, Staff staff, String userName, ResourceBundle r) throws Exception;

    ConstructionDetail getConstructionDetailParent(Long constructionId, Long parentId) throws Exception;





}
