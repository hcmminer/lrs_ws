package com.viettel.base.cms.serviceImpl;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.FileDTO;
import com.viettel.base.cms.dto.ImageDTO;
import com.viettel.base.cms.model.ConstructionDetail;
import com.viettel.base.cms.model.FileModel;
import com.viettel.base.cms.model.ImageModel;
import com.viettel.base.cms.repo.FileRepo;
import com.viettel.base.cms.repo.ImageRepo;
import com.viettel.base.cms.service.FileUploadService;
import com.viettel.vfw5.base.utils.DataUtils;
import com.viettel.vfw5.base.utils.ResourceBundle;
import com.viettel.vfw5.base.utils.StringUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.aspectj.util.FileUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.xml.bind.DatatypeConverter;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Autowired
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private EntityManager cms;

    @Autowired
    Environment env;

    @Autowired
    ImageRepo imageRepo;

    @Autowired
    FileRepo fileRepo;

    //    private String basicDir = "/u01/upoint/tomcat9-8095-WS_GSCT/webapps/photos/";
    //    private String basicDir = "D:/CongViec/UpointProject/photos/";
    static final String[] EXTENSIONS = new String[]{
        "gif", "png", "bmp", "jpg", "jpeg"
    };
    static final FilenameFilter IMAGE_FILTER = new FilenameFilter() {

        @Override
        public boolean accept(final File dir, final String name) {
            for (final String ext : EXTENSIONS) {
                if (name.endsWith("." + ext)) {
                    return (true);
                }
            }
            return (false);
        }
    };

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadImage(List<ImageDTO> listImageDTO, Long constructionDetailId) throws Exception {
        FTPClient client = new FTPClient();
        try {
            //create directory
            String dir = (constructionDetailId + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_dd")));
            //delete old files
//            File[] files = new File(env.getProperty("image.basic.dir") + dir).listFiles();
//            if (files != null) { //some JVMs return null for empty dirs
//                for (File f : files) {
//                    if (!f.isDirectory()) {
//                        f.delete();
//                    }
//                }
//            deactivateOldImageInfo(constructionDetailId);
//            }

            // mo ket noi
            System.out.println("Begin connect ftp");
            client.connect(env.getProperty("image.storage.server"));
            boolean result = client.login(env.getProperty("image.storage.server.username"),
                    env.getProperty("image.storage.server.password"));
            System.out.println("result connect ftp: " + result);
            //thu muc luu file
            client.changeWorkingDirectory(env.getProperty("image.storage.server.directory"));
            if (result) {
                for (ImageDTO imageDTO
                        : listImageDTO) {
                    client.enterLocalPassiveMode(); // important!
                    client.setFileType(FTP.BINARY_FILE_TYPE);
                    client.changeWorkingDirectory("/u01/pms/tomcat9-8888-view-image/webapps/pms/assets/upload");
                    client.enterLocalPassiveMode();
                    System.out.println("client.printWorkingDirectory(): " + client.printWorkingDirectory());
                    if (!client.changeWorkingDirectory(dir)) {
                        client.makeDirectory(dir);
                        System.out.println("client.printWorkingDirectory(): " + client.printWorkingDirectory());
                        client.changeWorkingDirectory(dir);
                        System.out.println("client.printWorkingDirectory(): " + client.printWorkingDirectory());
                    }
                    System.out.println("client.printWorkingDirectory(): " + client.printWorkingDirectory());

                    byte[] image = Base64.getEncoder().encode(imageDTO.getFile().getBytes());
                    String imageBase64 = new String(image);
//                    System.out.println("Anh nhan tu client" + imageBase64);
                    BufferedImage bufferedImage = ImageIO.read(imageDTO.getFile().getInputStream());
//                    BufferedImage bufferedImage = resizeImage(imBuff);
                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    ImageIO.write(bufferedImage, "jpeg", os);                          // Passing: ​(RenderedImage im, String formatName, OutputStream output)
                    InputStream is = new ByteArrayInputStream(os.toByteArray());
//                    if (!StringUtils.isStringNullOrEmpty(is)) {
//                        byte[] byteFileContent = IOUtils.toByteArray(is);
//                        String base64 = Base64.getEncoder().encodeToString(byteFileContent);
//                        System.out.println("File base 64 : " + base64);
//                    } else {
//                        System.out.println("Anh bi null");
//                    }
                    //luu file
                    client.storeFile(imageDTO.getImageName(), is);

                    System.out.println("File is uploaded " + imageDTO.getImageName() + " successfully");
                    client.changeToParentDirectory();

                    //save image
//                Files.createDirectories(Paths.get(env.getProperty("image.basic.dir") + dir));
//
//                //convert base64 string to binary data
//                String path = env.getProperty("image.basic.dir") + dir + imageDTO.getImageName();
//                try {
//                    wait();
//                }
//                catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println(
//                        "Continuing the fire after reloading");
//                // saveImage
//                imageDTO.getFile().transferTo(new File(path));
                    // luu lai vao db
                    imageDTO.setImagePath(client.printWorkingDirectory());
                    if (imageDTO.getImagePath().startsWith(Constant.FTP_CONFIG.PRE)) {
//                        env.getProperty("server.ftp.public") +
                        imageDTO.setImagePath(imageDTO.getImagePath().replace(Constant.FTP_CONFIG.PRE, "")+ "/" + dir);
                    }
//                    imageDTO.setImagePath(dir);
                    saveImageInfo(imageDTO);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (client.isConnected()) {
                client.logout();
                client.disconnect();
            }

        }


        /*
        end_save image
         */
    }

    private void saveImageInfo(ImageDTO imageDTO) throws Exception {
        try {
            ImageModel imageModel = new ImageModel();
            Long imageModelId = DataUtils.getSequence(cms, "image_info_seq");
            imageModel.setImageId(imageModelId);
            imageModel.setConstructionDetailId(imageDTO.getConstructionDetailId());
            imageModel.setImageName(imageDTO.getImageName());
            imageModel.setImagePath(imageDTO.getImagePath());
            imageModel.setStatus(Long.valueOf(Constant.IMAGE_STATUS.ACTIVE));
            imageRepo.save(imageModel);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    private void saveFileInfo(FileDTO fileDTO) throws Exception {
        try {
            FileModel fileModel = new FileModel();
            Long fileModelId = DataUtils.getSequence(cms, "file_info_seq");
            fileModel.setFileId(fileModelId);
            fileModel.setBtsStationId(fileDTO.getBtsStationId());
            fileModel.setFileName(fileDTO.getFileName());
            fileModel.setFilePath(fileDTO.getFilePath());
            fileModel.setStatus(Long.valueOf(Constant.IMAGE_STATUS.ACTIVE));
            fileRepo.save(fileModel);

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveFileInfoToBTSRentPlace(FileDTO fileDTO, Long type) throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(" update bts_rent_place set ");
            if (type == 1L){
                sql.append("file_contract  = :filePath");
            } else if (type == 2L){
                sql.append("file_CR  = :filePath");
            }
            sql.append(" where id = :btsStationId ");
            Query query = cms.createNativeQuery(sql.toString());
            query.setParameter("filePath", fileDTO.getFilePath());
            query.setParameter("btsStationId", fileDTO.getBtsStationId());
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deactivateOldImageInfo(Long constructionDetailId) throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            sql.append(" update image_info set status = 0 where construction_detail_id = :constructionDetailId and status = 1 ");
            Query query = cms.createNativeQuery(sql.toString());
            query.setParameter("constructionDetailId", constructionDetailId);
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public FileDTO uploadFile(MultipartFile fileImage, FileDTO fileDTO, String siteOnNims, Long type) throws Exception {
        FileDTO resultDTO = new FileDTO();
        FTPClient client = new FTPClient();
        try {
            //create directory
            String dir = (siteOnNims + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy_MM_ddHHmmss")));
            //delete old files
//            File[] files = new File(env.getProperty("image.basic.dir") + dir).listFiles();
//            if (files != null) { //some JVMs return null for empty dirs
//                for (File f : files) {
//                    if (!f.isDirectory()) {
//                        f.delete();
//                    }
//                }
            // xoa cac file cu, tam thoi ko can thiet
//            deactivateOldImageInfo(siteOnNims);
//            }

            FileInputStream fis = null;
//            FTPClient ftp = new FTPClient();

            // mo ket noi
            System.out.println("Begin connect ftp");
            client.connect(env.getProperty("image.storage.server"));
            boolean result = client.login(env.getProperty("image.storage.server.username"),
                    env.getProperty("image.storage.server.password"));
            System.out.println("result connect ftp: " + result);
            //thu muc luu file
            client.changeWorkingDirectory(env.getProperty("image.storage.server.directory"));
            if (result) {
//                for (ImageDTO imageDTO
//                        : listImageDTO) {
                    client.enterLocalPassiveMode(); // important!
                    client.setFileType(FTP.BINARY_FILE_TYPE);
                    System.out.println("client.printWorkingDirectory(): " + client.printWorkingDirectory());
                    if (!client.changeWorkingDirectory(dir)) {
                        client.makeDirectory(dir);
                        System.out.println("client.printWorkingDirectory(): " + client.printWorkingDirectory());
                        client.changeWorkingDirectory(dir);
                        System.out.println("client.printWorkingDirectory(): " + client.printWorkingDirectory());
                    }
                    System.out.println("client.printWorkingDirectory(): " + client.printWorkingDirectory());


//                    BufferedImage imBuff = ImageIO.read(fileDTO.getFile().getInputStream());
//                    BufferedImage bufferedImage = resizeImage(imBuff);
//                    ByteArrayOutputStream os = new ByteArrayOutputStream();
//                    ImageIO.write(bufferedImage, "pdf", os);                          // Passing: ​(RenderedImage im, String formatName, OutputStream output)
//                    InputStream is = new ByteArrayInputStream(os.toByteArray());
//                byte[] decodedString = Base64.getDecoder().decode(new String(fileDTO.getFileContent()).getBytes("UTF-8"));

                File convFile = new File(fileImage.getOriginalFilename());
                convFile.createNewFile();
                FileOutputStream fos = new FileOutputStream(convFile);
                fos.write(fileImage.getBytes());
                fos.close();
                fis = new FileInputStream(convFile);

//                ftp.setFileType(2);
                boolean isStore = client.storeFile(fileDTO.getFileName(), fis);
                System.out.println("isStore:" + isStore);
                if (!isStore) {
                    client.enterLocalActiveMode();
                    fis = new FileInputStream(convFile);
                    client.setFileType(FTP.BINARY_FILE_TYPE);
                    boolean isStore1 = client.storeFile(fileDTO.getFileName(), fis);

                    //isSend = isStore1;
                }
//                    FileInputStream fis = null;
//                    fis = new FileInputStream(fileDTO.getFileName());


                    //luu file
//                    client.storeFile(fileDTO.getFileName(), fis);

//                    client.logout();

                    System.out.println("File is uploaded " + fileDTO.getFileName() + " successfully");
                    client.changeToParentDirectory();

                    //save image
//                Files.createDirectories(Paths.get(env.getProperty("image.basic.dir") + dir));
//
//                //convert base64 string to binary data
//                String path = env.getProperty("image.basic.dir") + dir + imageDTO.getImageName();
//                try {
//                    wait();
//                }
//                catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                System.out.println(
//                        "Continuing the fire after reloading");
//                // saveImage
//                imageDTO.getFile().transferTo(new File(path));
                    // luu lai vao db
                    fileDTO.setFilePath(dir);
                    fileDTO.setBtsStationId(siteOnNims);
                    // luu vao bang file_info
                    saveFileInfo(fileDTO);
                    // luu thong tin duong dan vao bang bts_rent_place
//                    saveFileInfoToBTSRentPlace(fileDTO, type);
//                }
                resultDTO.setFilePath(fileDTO.getFilePath());
                resultDTO.setFileName(fileDTO.getFileName());

            }

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (client.isConnected()) {
                client.logout();
                client.disconnect();
            }

        }
        return resultDTO;
    }

    @Override
    public ImageDTO getFullImageList(Long constructionDetailId, ResourceBundle r, String roleCode) throws Exception {
        String status = "";
        String approveStatus = "";
        String rejectStatus = "";
        if (!StringUtils.isStringNullOrEmpty(roleCode)){
            if (Constant.CMS_ROLES.CMS_PROV_TECH_STAFF.equals(roleCode)){
                status = "(1)";
                approveStatus = "(2, 4, 6)";
                rejectStatus = "(3, 5, 7)";
            } else if ((Constant.CMS_ROLES.CMS_PROV_INFA_STAFF.equals(roleCode))){
                status = "(1)";
                approveStatus = "(2)";
                rejectStatus = "(3)";
            }else if ((Constant.CMS_ROLES.CMS_PROV_VICE_PRESIDENT.equals(roleCode))){
                status = "(2)";
                approveStatus = "(4)";
                rejectStatus = "(5)";
            } else if ((Constant.CMS_ROLES.CMS_CORP_STAFF.equals(roleCode))){
                status = "(4)";
                approveStatus = "(6)";
                rejectStatus = "(7)";
            }
        }
        ImageDTO imageDTO = new ImageDTO();
        List<ImageDTO> listImageActive = getImageList(constructionDetailId, r, status);
        imageDTO.setListImageActive(listImageActive);
        List<ImageDTO> listImageApproved = getImageListApproved(constructionDetailId, r, approveStatus);
        imageDTO.setListImageApproved(listImageApproved);
        List<ImageDTO> listImageRejected = getImageListRejected(constructionDetailId, r, rejectStatus);
        imageDTO.setListImageReject(listImageRejected);
        return imageDTO;
    }

    @Override
    public List<ImageDTO> getImageList(Long constructionDetailId, ResourceBundle r, String status) throws Exception {
        try {
            List<ImageDTO> imageDTOList = new ArrayList<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" select * from image_info where construction_detail_id = :constructionDetailId and status in ");
            sql.append(status);
            Query query = cms.createNativeQuery(sql.toString(), ImageModel.class);
            query.setParameter("constructionDetailId", constructionDetailId);
            List<ImageModel> imageModels = query.getResultList();
            List<String> lstFileName = new ArrayList<>();
            for (ImageModel im
                    : imageModels) {
                lstFileName.add(im.getImageName());
                String statusName = "";
                if (im.getStatus() == 0L){
                    statusName = r.getResourceMessage("image.status.delete");
                } else if (im.getStatus() == 1L){
                    statusName = r.getResourceMessage("image.status.active");
                }else if (im.getStatus() == 2L){
                    statusName = r.getResourceMessage("image.status.approved");
                }else if (im.getStatus() == 3L){
                    statusName = r.getResourceMessage("image.status.reject");
                }else if (im.getStatus() == 4L){
                    statusName = r.getResourceMessage("image.status.approved");
                }else if (im.getStatus() == 5L){
                    statusName = r.getResourceMessage("image.status.rejected");
                }else if (im.getStatus() == 6L){
                    statusName = r.getResourceMessage("image.status.approved");
                }else if (im.getStatus() == 7L){
                    statusName = r.getResourceMessage("image.status.rejected");
                }
                String rejectReason = "";
                if (!StringUtils.isStringNullOrEmpty(im.getRejectReason())){
                    if (im.getRejectReason().equals("1")){
                        statusName = r.getResourceMessage("reason.reject.image.1");
                    } else if (im.getRejectReason().equals("2")){
                        statusName = r.getResourceMessage("reason.reject.image.2");
                    }else if (im.getRejectReason().equals("3")){
                        statusName = r.getResourceMessage("reason.reject.image.3");
                    }else if (im.getRejectReason().equals("4")){
                        statusName = r.getResourceMessage("reason.reject.image.4");
                    }
                }
                ImageDTO imageDTO = new ImageDTO(im.getImageId(), im.getConstructionDetailId(), im.getImageName(), im.getImagePath(), im.getStatus(), null, im.getImagePath() + "/" + im.getImageName(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), statusName, rejectReason, im.getRejectDatetime(), im.getRejectBy(), im.getApprovedDatetime(), im.getApprovedBy(), im.getDescription());
                imageDTOList.add(imageDTO);
            }
            if (imageDTOList.size() > 0) {
                imageDTOList.get(0).setListFileName(lstFileName);
            }
            return imageDTOList;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    public List<ImageDTO> getListImage(Long constructionDetailId, ResourceBundle r) throws Exception {
        try {
            List<ImageDTO> imageDTOList = new ArrayList<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" select * from image_info where construction_detail_id = :constructionDetailId and status in (1, 2, 3, 4, 5, 6, 7) ");
            Query query = cms.createNativeQuery(sql.toString(), ImageModel.class);
            query.setParameter("constructionDetailId", constructionDetailId);
            List<ImageModel> imageModels = query.getResultList();
            List<String> lstFileName = new ArrayList<>();
            for (ImageModel im
                    : imageModels) {
                lstFileName.add(im.getImageName());
                String statusName = "";
                if (im.getStatus() == 0L){
                    statusName = r.getResourceMessage("image.status.delete");
                } else if (im.getStatus() == 1L){
                    statusName = r.getResourceMessage("image.status.active");
                }else if (im.getStatus() == 2L){
                    statusName = r.getResourceMessage("image.status.approved");
                }else if (im.getStatus() == 3L){
                    statusName = r.getResourceMessage("image.status.rejected");
                }else if (im.getStatus() == 4L){
                    statusName = r.getResourceMessage("image.status.approved");
                }else if (im.getStatus() == 5L){
                    statusName = r.getResourceMessage("image.status.rejected");
                }else if (im.getStatus() == 6L){
                    statusName = r.getResourceMessage("image.status.approved");
                }else if (im.getStatus() == 7L){
                    statusName = r.getResourceMessage("image.status.rejected");
                }
                String rejectReason = "";
                if (!StringUtils.isStringNullOrEmpty(im.getRejectReason())){
                    if (im.getRejectReason().equals("1")){
                        rejectReason = r.getResourceMessage("reason.reject.image.1");
                    } else if (im.getRejectReason().equals("2")){
                        rejectReason = r.getResourceMessage("reason.reject.image.2");
                    }else if (im.getRejectReason().equals("3")){
                        rejectReason = r.getResourceMessage("reason.reject.image.3");
                    }else if (im.getRejectReason().equals("4")){
                        rejectReason = r.getResourceMessage("reason.reject.image.4");
                    }
                }
                ImageDTO imageDTO = new ImageDTO(im.getImageId(), im.getConstructionDetailId(), im.getImageName(), im.getImagePath(), im.getStatus(), null, im.getImagePath() + "/" + im.getImageName(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), statusName, rejectReason, im.getRejectDatetime(), im.getRejectBy(), im.getApprovedDatetime(), im.getApprovedBy(), im.getDescription());
                imageDTOList.add(imageDTO);
            }
            if (imageDTOList.size() > 0) {
                imageDTOList.get(0).setListFileName(lstFileName);
            }
            return imageDTOList;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public List<ImageDTO> getImageListApproved(Long constructionDetailId, ResourceBundle r, String status ) throws Exception {
        try {
            List<ImageDTO> imageDTOList = new ArrayList<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" select * from image_info where construction_detail_id = :constructionDetailId and status in ");
            sql.append(status);
            Query query = cms.createNativeQuery(sql.toString(), ImageModel.class);
            query.setParameter("constructionDetailId", constructionDetailId);
            List<ImageModel> imageModels = query.getResultList();
            List<String> lstFileName = new ArrayList<>();
            for (ImageModel im
                    : imageModels) {
                lstFileName.add(im.getImageName());
                String statusName = "";
                if (im.getStatus() == 0L){
                    statusName = r.getResourceMessage("image.status.delete");
                } else if (im.getStatus() == 1L){
                    statusName = r.getResourceMessage("image.status.active");
                }else if (im.getStatus() == 2L){
                    statusName = r.getResourceMessage("image.status.approved");
                }else if (im.getStatus() == 3L){
                    statusName = r.getResourceMessage("image.status.rejected");
                }else if (im.getStatus() == 4L){
                    statusName = r.getResourceMessage("image.status.approved");
                }else if (im.getStatus() == 5L){
                    statusName = r.getResourceMessage("image.status.rejected");
                }else if (im.getStatus() == 6L){
                    statusName = r.getResourceMessage("image.status.approved");
                }else if (im.getStatus() == 7L){
                    statusName = r.getResourceMessage("image.status.rejected");
                }
                String rejectReason = "";
                if (!StringUtils.isStringNullOrEmpty(im.getRejectReason())){
                    if (im.getRejectReason().equals("1")){
                        rejectReason = r.getResourceMessage("reason.reject.image.1");
                    } else if (im.getRejectReason().equals("2")){
                        rejectReason = r.getResourceMessage("reason.reject.image.2");
                    }else if (im.getRejectReason().equals("3")){
                        rejectReason = r.getResourceMessage("reason.reject.image.3");
                    }else if (im.getRejectReason().equals("4")){
                        rejectReason = r.getResourceMessage("reason.reject.image.4");
                    }
                }
                ImageDTO imageDTO = new ImageDTO(im.getImageId(), im.getConstructionDetailId(), im.getImageName(), im.getImagePath(), im.getStatus(), null, im.getImagePath() + "/" + im.getImageName(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), statusName, rejectReason, im.getRejectDatetime(), im.getRejectBy(), im.getApprovedDatetime(), im.getApprovedBy(), im.getDescription());
                imageDTOList.add(imageDTO);
            }
            if (imageDTOList.size() > 0) {
                imageDTOList.get(0).setListFileName(lstFileName);
            }
            return imageDTOList;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }


    public List<ImageDTO> getImageListRejected(Long constructionDetailId, ResourceBundle r, String status) throws Exception {
        try {
            List<ImageDTO> imageDTOList = new ArrayList<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" select * from image_info where construction_detail_id = :constructionDetailId and status in ");
            sql.append(status);
            Query query = cms.createNativeQuery(sql.toString(), ImageModel.class);
            query.setParameter("constructionDetailId", constructionDetailId);
            List<ImageModel> imageModels = query.getResultList();
            List<String> lstFileName = new ArrayList<>();
            for (ImageModel im
                    : imageModels) {
                lstFileName.add(im.getImageName());
                String statusName = "";
                if (im.getStatus() == 0L){
                    statusName = r.getResourceMessage("image.status.delete");
                } else if (im.getStatus() == 1L){
                    statusName = r.getResourceMessage("image.status.active");
                }else if (im.getStatus() == 2L){
                    statusName = r.getResourceMessage("image.status.approved");
                }else if (im.getStatus() == 3L){
                    statusName = r.getResourceMessage("image.status.rejected");
                }else if (im.getStatus() == 4L){
                    statusName = r.getResourceMessage("image.status.approved");
                }else if (im.getStatus() == 5L){
                    statusName = r.getResourceMessage("image.status.rejected");
                }else if (im.getStatus() == 6L){
                    statusName = r.getResourceMessage("image.status.approved");
                }else if (im.getStatus() == 7L){
                    statusName = r.getResourceMessage("image.status.rejected");
                }
                String rejectReason = "";
                if (!StringUtils.isStringNullOrEmpty(im.getRejectReason())){
                    if (im.getRejectReason().equals("1")){
                        rejectReason = r.getResourceMessage("reason.reject.image.1");
                    } else if (im.getRejectReason().equals("2")){
                        rejectReason = r.getResourceMessage("reason.reject.image.2");
                    }else if (im.getRejectReason().equals("3")){
                        rejectReason = r.getResourceMessage("reason.reject.image.3");
                    }else if (im.getRejectReason().equals("4")){
                        rejectReason = r.getResourceMessage("reason.reject.image.4");
                    }
                }
                ImageDTO imageDTO = new ImageDTO(im.getImageId(), im.getConstructionDetailId(), im.getImageName(), im.getImagePath(), im.getStatus(), null, im.getImagePath() + "/" + im.getImageName(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), statusName, rejectReason, im.getRejectDatetime(), im.getRejectBy(), im.getApprovedDatetime(), im.getApprovedBy(), im.getDescription());
                imageDTOList.add(imageDTO);
            }
            if (imageDTOList.size() > 0) {
                imageDTOList.get(0).setListFileName(lstFileName);
            }
            return imageDTOList;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }



    public List<FileDTO> getFileListInfo(String filePath) throws Exception {
        try {
            List<FileDTO> imageDTOList = new ArrayList<>();
            StringBuilder sql = new StringBuilder();
            sql.append(" select * from file_info where file_path = :filePath and status = 1 ");
            Query query = cms.createNativeQuery(sql.toString(), FileModel.class);
            query.setParameter("filePath", filePath);
            List<FileModel> fileModels = query.getResultList();
            List<String> lstFileName = new ArrayList<>();
            for (FileModel fm
                    : fileModels) {
                lstFileName.add(fm.getFileName());
                FileDTO fileDTO = new FileDTO(fm.getFileId(), fm.getBtsStationId(), fm.getFileName(), fm.getFilePath(), fm.getStatus(), null, null,null, new ArrayList<>());
                imageDTOList.add(fileDTO);
            }
            if (imageDTOList.size() > 0) {
                imageDTOList.get(0).setListFileName(lstFileName);
            }
            return imageDTOList;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    public List<ImageDTO> getImageListWithContent(Long constructionDetailId, ResourceBundle r) throws Exception {
        List<ImageDTO> imageDTOList = new ArrayList<>();
//        List<ImageDTO> lstFile = new ArrayList<>();
        FTPClient client = new FTPClient();
        try {
            imageDTOList = getImageList(constructionDetailId, r, null);
            if (imageDTOList.size() == 0) {
                return null;
            } else {
//                for (ImageDTO imageDTO
//                        : imageDTOList) {
//                System.out.println("Begin connect ftp");
//                client.connect(env.getProperty("image.storage.server"));
//                boolean result = client.login(env.getProperty("image.storage.server.username"),
//                        env.getProperty("image.storage.server.password"));
//                System.out.println("result connect ftp: " + result);
//                if (result) {
//
//                    client.changeWorkingDirectory(env.getProperty("image.storage.server.directory"));
//                    System.out.println("current dir " + client.printWorkingDirectory());
//                    int reply = client.getReplyCode();
//                    //FTPReply stores a set of constants for FTP reply codes.
//                    if (!FTPReply.isPositiveCompletion(reply)) {
//                        client.disconnect();
//                        return null;
//                    }
//                    client.enterLocalPassiveMode(); // important!
//                    client.setFileType(FTP.BINARY_FILE_TYPE);
//                    System.out.println("filepath " + imageDTOList.get(0).getImagePath());
//                    client.changeWorkingDirectory(imageDTOList.get(0).getImagePath());
//                    System.out.println("current dir " + client.printWorkingDirectory());
//                    for (ImageDTO imageDTO : imageDTOList) {
//                        System.out.println("file.getName(): " + imageDTO.getImageName());
//                        try (InputStream inputStream = client.retrieveFileStream(imageDTO.getImageName())) {
//                            if (inputStream != null) {
//                                byte[] byteFileContent = IOUtils.toByteArray(inputStream);
//                                client.completePendingCommand();
//                                String base64 = Base64.getEncoder().encodeToString(byteFileContent);
//                                String fileContent = "data:image/" + imageDTO.getImageName().split("\\.")[1] + ";base64," + base64;
//                                imageDTO.setFileContent(fileContent);
//                                System.out.println("File is retrieved " + imageDTO.getImageName() + " successfully");
////                                lstFile.add(imageDTO);
//                            }
//                        }
//                    }
//
////                    FTPFile[] ftpFiles = client.listFiles();
////
////                    System.out.println("imageDTOList.get(0).getListFileName " + imageDTOList.get(0).getListFileName());
////                    if (ftpFiles != null && ftpFiles.length > 0) {
////                        for (FTPFile file : ftpFiles) {
////                            if (!file.isFile()) {
////                                continue;
////                            }
////                            System.out.println("current dir " + client.printWorkingDirectory());
////                            System.out.println("file.getName(): " + file.getName());
////                            if (imageDTOList.get(0).getListFileName().contains(file.getName())) {
////                                System.out.println("file.getName() mapping: " + file.getName());
////                                ImageDTO imageDTO = new ImageDTO();
////                                try (InputStream inputStream = client.retrieveFileStream(file.getName())) {
////                                    if (inputStream != null) {
////                                        System.out.println("inputStream: " + inputStream);
////                                        byte[] byteFileContent = IOUtils.toByteArray(inputStream);
////                                        client.completePendingCommand();
////                                        System.out.println("File is retrieved " + file.getName() + " successfully");
////                                        String base64 = Base64.getEncoder().encodeToString(byteFileContent);
////                                        String fileContent = "data:image/" + file.getName().split("\\.")[1] + ";base64," + base64;
////                                        imageDTO.setFileContent(fileContent);
////                                        System.out.println("File is retrieved " + file.getName() + " successfully");
////                                        lstFile.add(imageDTO);
////                                    }
////                                }
////                            }
////                        }
////                    }
////                    System.out.println("current dir after get image path " + client.printWorkingDirectory());
////                    System.out.println("filename " + imageDTO.getImageName());
////                    InputStream inputStream = client.retrieveFileStream(imageDTO.getImageName());
////                    System.out.println("File retrieving " + imageDTO.getImageName());
////                    client.changeToParentDirectory();
////                    System.out.println("current dir after get input stream " + client.printWorkingDirectory());
////                    byte[] byteFileContent = IOUtils.toByteArray(inputStream);
////                    String base64 = Base64.getEncoder().encodeToString(byteFileContent);
////                    String fileContent = "data:image/" + imageDTO.getImageName().split("\\.")[1] + ";base64," + base64;
////                    imageDTO.setFileContent(fileContent);
////
////                    System.out.println("File is retrieved " + imageDTO.getImageName() + " successfully");
////                        client.changeWorkingDirectory("/u01/upoint/cms");
////                        System.out.println("current dir " + client.printWorkingDirectory());
////                        client.changeToParentDirectory();
////                        System.out.println("current dir " + client.printWorkingDirectory());
//                }
//                client.logout();
//                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (client.isConnected()) {
                client.disconnect();
            }
            return imageDTOList;
        }
    }

    @Override
    public ImageDTO getImage(Long imageId) throws Exception {
        ImageModel imageModel = getImageById(imageId);
//        List<ImageDTO> imageDTOList = new ArrayList<>();
//        String path = env.getProperty("image.basic.dir") + imageModel.getImagePath() + imageModel.getImageName();
//        File dir = new File(path);
        ImageDTO imageDTO = new ImageDTO();
//        if (dir.isDirectory() && dir.listFiles(IMAGE_FILTER).length > 0) {
//            for (final File f : dir.listFiles(IMAGE_FILTER)) {
        FTPClient client = new FTPClient();
        try {
            String fileContent = "";
            String base64 = "";
//            if (dir.exists() && !dir.isDirectory()) {
//                byte[] byteFileContent = FileUtil.readAsByteArray(dir);
//                String base64 = Base64.getEncoder().encodeToString(byteFileContent);
//                String extension = FilenameUtils.getExtension(dir.getName());
//                fileContent = "data:image/" + extension + ";base64," + base64;
//
//                imageDTO.setImageName(FilenameUtils.getName(dir.getName()));
//                imageDTO.setFileContent(fileContent);
            imageDTO.setImageId(imageId);
            imageDTO.setConstructionDetailId(imageModel.getConstructionDetailId());
            imageDTO.setImagePath(imageModel.getImagePath());
            imageDTO.setStatus(imageModel.getStatus());
////                        imageDTOList.add(imageDTO);
////                return imageDTO;
//            }
            imageDTO.setImageName(imageModel.getImageName());

//            System.out.println("Begin connect ftp");
//            client.connect(env.getProperty("image.storage.server"));
//            boolean result = client.login(env.getProperty("image.storage.server.username"),
//                    env.getProperty("image.storage.server.password"));
//            System.out.println("result connect ftp: " + result);
//            if (result) {
//                client.enterLocalPassiveMode(); // important!
//                client.setFileType(FTP.BINARY_FILE_TYPE);
//                client.changeWorkingDirectory(env.getProperty("image.storage.server.directory"));
//                client.changeWorkingDirectory(imageDTO.getImagePath());
//                // lay file ra
//                InputStream inputStream = client.retrieveFileStream(imageDTO.getImageName());
//                if (!StringUtils.isStringNullOrEmpty(inputStream)) {
//                    byte[] byteFileContent = IOUtils.toByteArray(inputStream);
//                client.completePendingCommand();
//                base64 = Base64.getEncoder().encodeToString(byteFileContent);
//                fileContent = "data:image/" + "jpg" + ";base64," + base64;
////                System.out.println("File base 64 : " + base64);
//                imageDTO.setFileContent(fileContent);
//                System.out.println("File is retrieved " + imageDTO.getImageName() + " successfully");
//                } else {
//                    System.out.println("Anh bi null");
//                }
//            }
//            client.logout();
//
//            fileContent = "data:image/" + "jpg" + ";base64," + base64;
            imageDTO.setFileContent(imageDTO.getImagePath() + "/" + imageDTO.getImageName());

            return imageDTO;

        } catch (final Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (client.isConnected()) {
                client.disconnect();
            }
        }
    }

    @Override
    public FileDTO getFile(Long fileId) throws Exception {
        FileModel fileModel = getFileById(fileId);
//        List<ImageDTO> imageDTOList = new ArrayList<>();
//        String path = env.getProperty("image.basic.dir") + imageModel.getImagePath() + imageModel.getImageName();
//        File dir = new File(path);
        FileDTO fileDTO = new FileDTO();
//        if (dir.isDirectory() && dir.listFiles(IMAGE_FILTER).length > 0) {
//            for (final File f : dir.listFiles(IMAGE_FILTER)) {
        FTPClient client = new FTPClient();
        try {
            String fileContent = "";
            String base64 = "";
//            if (dir.exists() && !dir.isDirectory()) {
//                byte[] byteFileContent = FileUtil.readAsByteArray(dir);
//                String base64 = Base64.getEncoder().encodeToString(byteFileContent);
//                String extension = FilenameUtils.getExtension(dir.getName());
//                fileContent = "data:image/" + extension + ";base64," + base64;
//
//                imageDTO.setImageName(FilenameUtils.getName(dir.getName()));
//                imageDTO.setFileContent(fileContent);
            fileDTO.setFileId(fileId);
            fileDTO.setBtsStationId(fileModel.getBtsStationId());
            fileDTO.setFilePath(fileModel.getFilePath());
            fileDTO.setStatus(fileModel.getStatus());
////                        imageDTOList.add(imageDTO);
////                return imageDTO;
//            }
            fileDTO.setFileName(fileModel.getFileName());

            System.out.println("Begin connect ftp");
            client.connect(env.getProperty("image.storage.server"));
            boolean result = client.login(env.getProperty("image.storage.server.username"),
                    env.getProperty("image.storage.server.password"));
            System.out.println("result connect ftp: " + result);
            if (result) {
                client.enterLocalPassiveMode(); // important!
                client.setFileType(FTP.BINARY_FILE_TYPE);
                client.changeWorkingDirectory(env.getProperty("image.storage.server.directory"));
                client.changeWorkingDirectory(fileDTO.getFilePath());
                // lay file ra
                InputStream inputStream = client.retrieveFileStream(fileDTO.getFileName());
                byte[] byteFileContent = IOUtils.toByteArray(inputStream);
                client.completePendingCommand();
                base64 = Base64.getEncoder().encodeToString(byteFileContent);
                fileContent = base64;
                fileDTO.setFileContent(fileContent);
                System.out.println("File is retrieved " + fileDTO.getFileName() + " successfully");
            }
            client.logout();

            fileContent = base64;
            fileDTO.setFileContent(fileContent);

            return fileDTO;

        } catch (final Exception e) {
            e.printStackTrace();
            throw e;
        } finally {
            if (client.isConnected()) {
                client.disconnect();
            }
        }
    }

    @Override
    public FileDTO getFileInfo(String filePath) throws Exception {
        FileDTO fileResult = new FileDTO();
        List<FileDTO> fileDTOS = getFileListInfo(filePath);
        if (!fileDTOS.isEmpty() && fileDTOS != null){
            for (FileDTO fileDTO : fileDTOS){
                fileResult = getFile(fileDTO.getFileId());
                if (!StringUtils.isStringNullOrEmpty(fileResult)){
                    return fileResult;
                } else {
                    return new FileDTO();
                }
            }
        }
        return fileResult;
    }

    private ImageModel getImageById(Long imageId) {
        try {
            Optional<ImageModel> result = imageRepo.findById(imageId);
            ImageModel imageModel = result.orElse(null);
            return imageModel;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }


    private FileModel getFileById(Long fileId) {
        try {
            Optional<FileModel> result = fileRepo.findById(fileId);
            FileModel fileModel = result.orElse(null);
            return fileModel;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    BufferedImage resizeImage(BufferedImage originalImage) throws IOException {
        double width = originalImage.getWidth() * Double.parseDouble(env.getProperty("image.resize.scale"));
        double height = originalImage.getHeight() * Double.parseDouble(env.getProperty("image.resize.scale"));
        BufferedImage resizedImage = new BufferedImage((int) width, (int) height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics2D = resizedImage.createGraphics();
        graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics2D.drawImage(originalImage, 0, 0, (int) width, (int) height, null);
        graphics2D.dispose();
        return resizedImage;
    }

}
