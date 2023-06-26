package com.viettel.base.cms.service;

import com.viettel.base.cms.dto.FileDTO;
import com.viettel.base.cms.dto.ImageDTO;
import com.viettel.vfw5.base.dto.ExecutionResult;
import com.viettel.vfw5.base.utils.ResourceBundle;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.List;

public interface FileUploadService {

    void uploadImage(List<ImageDTO> imageDTO, Long constructionDetailId) throws Exception;

    List<ImageDTO> getImageList(Long constructionDetailId, ResourceBundle r, String status) throws Exception;

    List<ImageDTO> getListImage(Long constructionDetailId, ResourceBundle r) throws Exception;

    List<ImageDTO> getImageListWithContent(Long constructionDetailId, ResourceBundle r) throws Exception;

    ImageDTO getImage(Long imageId) throws Exception;

    FileDTO getFile(Long fileId) throws Exception;

    FileDTO getFileInfo(String filePath) throws Exception;

    void deactivateOldImageInfo(Long imageId) throws Exception;

    FileDTO uploadFile(MultipartFile fileImage, FileDTO fileDTO, String siteOnNims, Long type) throws Exception;


    ImageDTO getFullImageList(Long constructionDetailId, ResourceBundle r, String roleCode) throws Exception;
}
