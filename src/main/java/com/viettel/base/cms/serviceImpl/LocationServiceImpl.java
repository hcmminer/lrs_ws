package com.viettel.base.cms.serviceImpl;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.*;
import com.viettel.base.cms.model.Commune;
import com.viettel.base.cms.model.ConstructionItem;
import com.viettel.base.cms.model.District;
import com.viettel.base.cms.model.Province;
import com.viettel.base.cms.repo.CommuneRepo;
import com.viettel.base.cms.repo.DistrictRepo;
import com.viettel.base.cms.repo.ProvinceRepo;
import com.viettel.base.cms.service.LocationService;
import com.viettel.vfw5.base.utils.DataUtils;
import com.viettel.vfw5.base.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {
    @Autowired
    @PersistenceContext(unitName = "UNIT_CMS")
    private EntityManager cms;

    @Autowired
    DistrictRepo districtRepo;

    @Autowired
    ProvinceRepo provinceRepo;

    @Autowired
    CommuneRepo communeRepo;

    @Override
    public List<ProvinceDTO> getListProvince() throws Exception {
        try {
            List<ProvinceDTO> lstResult = new ArrayList<>();
            String sql = " SELECT   " +
                    " pro_id, pro_code, pro_name, status, create_datetime, create_by, update_datetime, update_by  " +
                    " FROM   province  " +
                    " order by pro_name ";
            Query query = this.cms.createNativeQuery(sql);
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null)
                for (Object[] obj : lst) {
                    int i = 0;
                    ProvinceDTO pro = new ProvinceDTO();
                    pro.setProId(Long.valueOf(obj[i++].toString()));
                    pro.setProCode(obj[i++].toString());
                    pro.setProName(obj[i++].toString());
                    pro.setStatus(Long.valueOf(obj[i++].toString()));
                    pro.setCreateDatetime(LocalDateTime.now());
                    pro.setCreateBy(obj[i++].toString());
                    pro.setUpdateDatetime(LocalDateTime.now());
                    pro.setUpdateBy(obj[i++].toString());
                    lstResult.add(pro);
                }
            return lstResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<ProvinceDTO> searchProvince(DataParams dataParams, ProvinceDTO provinceDTO) throws Exception {
        try {
            List<ProvinceDTO> lstResult = new ArrayList<>();
            String sql = " SELECT   " +
                    " pro_id, pro_name , pro_code  " +
                    " FROM  province  " +
                    " WHERE `status` = 1 ";
            if (!StringUtils.isStringNullOrEmpty(provinceDTO.getProName()))
                sql = sql + " AND LOWER(pro_name) LIKE LOWER(:provinceName) ";
            sql = sql + " LIMIT :start_row, :page_limit ";
            Query query = this.cms.createNativeQuery(sql);
            if (!StringUtils.isStringNullOrEmpty(dataParams))
                query.setParameter("start_row", dataParams.getStartRow());
            query.setParameter("page_limit", dataParams.getPageLimit());
            if (!StringUtils.isStringNullOrEmpty(provinceDTO.getProName()))
                query.setParameter("provinceName", "%" + provinceDTO.getProName() + "%");
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null)
                for (Object[] obj : lst) {
                    int i = 0;
                    ProvinceDTO pro = new ProvinceDTO();
                    pro.setProId(Long.valueOf(obj[i++].toString()));
                    pro.setProName(obj[i++].toString());
                    pro.setProCode(obj[i++].toString());
                    lstResult.add(pro);
                }
            return lstResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public int totalRecordSearchProvince(DataParams dataParams, ProvinceDTO provinceDTO) throws Exception {
        try {
            String sql = " SELECT   " +
                    "      count(*)" +
                    " FROM  province  " +
                    " WHERE `status` = 1 ";
            if (!StringUtils.isStringNullOrEmpty(provinceDTO.getProName()))
                sql = sql + " AND LOWER(pro_name) LIKE LOWER(:provinceName) ";
            Query query = this.cms.createNativeQuery(sql);
            if (!StringUtils.isStringNullOrEmpty(provinceDTO.getProName()))
                query.setParameter("provinceName", "%" + provinceDTO.getProName() + "%");
            return ((Number) query.getSingleResult()).intValue();
        } catch (
                Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Province addProvince(ProvinceDTO provinceDTO, String userName) throws Exception {
        Province province = new Province();
        Long provinceId = DataUtils.getSequence(this.cms, "province_seq");
        province.setProId(provinceId);
        province.setProCode(provinceDTO.getProCode());
        province.setProName(provinceDTO.getProName());
        province.setStatus(1L);
        province.setCreateDatetime(LocalDateTime.now());
        province.setCreateBy(userName);
        province.setUpdateDatetime(LocalDateTime.now());
        province.setUpdateBy(userName);
        this.provinceRepo.save(province);
        return province;
    }

    @Override
    public List<ProvinceDTO> checkProvinceCodeDuplicate(String provinceCode) throws Exception {
        try {
            List<ProvinceDTO> lstResult = new ArrayList<>();
            String sql = " SELECT   " +
                    " pro_id, pro_name , pro_code " +
                    " FROM  province  " +
                    " WHERE `status` = 1  " +
                    " AND pro_code = :provinceCode ";
            Query query = this.cms.createNativeQuery(sql);
            if (!StringUtils.isStringNullOrEmpty(provinceCode))
                query.setParameter("provinceCode", provinceCode);
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    int i = 0;
                    ProvinceDTO pro = new ProvinceDTO();
                    pro.setProId(Long.valueOf(obj[i++].toString()));
                    pro.setProName(obj[i++].toString());
                    pro.setProCode(obj[i++].toString());
                    lstResult.add(pro);
                }
            } else {
                lstResult = null;
            }
            return lstResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<ProvinceDTO> checkProvinceNameDuplicate(String provinceName) throws Exception {
        try {
            List<ProvinceDTO> lstResult = new ArrayList<>();
            String sql = " SELECT   " +
                    " pro_id, pro_name , pro_code  " +
                    " FROM  province  " +
                    " WHERE `status` = 1  " +
                    " AND pro_name = :provinceName ";
            Query query = this.cms.createNativeQuery(sql);
            if (!StringUtils.isStringNullOrEmpty(provinceName))
                query.setParameter("provinceName", provinceName);
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    int i = 0;
                    ProvinceDTO pro = new ProvinceDTO();
                    pro.setProId(Long.valueOf(obj[i++].toString()));
                    pro.setProName(obj[i++].toString());
                    pro.setProCode(obj[i++].toString());
                    lstResult.add(pro);
                }
            } else {
                lstResult = null;
            }
            return lstResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public ProvinceDTO checkProvinceStatus(ProvinceDTO provinceDTO) throws Exception {
        try {
            ProvinceDTO pro = new ProvinceDTO();
            String sql = " SELECT   " +
                    " pro_id, pro_code , pro_name, status " +
                    " FROM  province  " +
                    " WHERE pro_id = :provinceId ";
            Query query = this.cms.createNativeQuery(sql);
            query.setParameter("provinceId", provinceDTO.getProId());
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    int i = 0;
                    pro.setProId(Long.valueOf(obj[i++].toString()));
                    pro.setProCode(obj[i++].toString());
                    pro.setProName(obj[i++].toString());
                    pro.setStatus(Long.valueOf(obj[i++].toString()));
                }
            } else {
                pro = null;
            }
            return pro;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateProvince(ProvinceDTO provinceDTO, String userName) throws Exception {
        try {
            String sql = " UPDATE province  " +
                    " SET pro_name = :provinceName, " +
                    " pro_code = :provinceCode, " +
                    " update_by = :updateBy, " +
                    " update_datetime = :updateDatetime  " +
                    " WHERE pro_id = :provinceId ";
            Query query = this.cms.createNativeQuery(sql);
            query.setParameter("provinceId", provinceDTO.getProId());
            query.setParameter("provinceName", provinceDTO.getProName());
            query.setParameter("provinceCode", provinceDTO.getProCode());
            query.setParameter("updateBy", userName);
            query.setParameter("updateDatetime", LocalDateTime.now());
            return query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteProvince(ProvinceDTO provinceDTO, String userName) throws Exception {
        try {
            String sql = " UPDATE province  " +
                    " SET status = 0, " +
                    " update_by = :updateBy, " +
                    " update_datetime = :updateDatetime  " +
                    " WHERE pro_id = :provinceId ";
            Query query = this.cms.createNativeQuery(sql);
            query.setParameter("provinceId", provinceDTO.getProId());
            query.setParameter("updateBy", userName);
            query.setParameter("updateDatetime", LocalDateTime.now());
            return query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public ProvinceDTO checkProvinceId(ProvinceDTO provinceDTO) throws Exception {
        try {
            ProvinceDTO pro = new ProvinceDTO();
            String sql = " SELECT   " +
                    " pro_id, pro_name , pro_code, status " +
                    " FROM  province  " +
                    " WHERE pro_id = :provinceId ";
            Query query = this.cms.createNativeQuery(sql);
            query.setParameter("provinceId", provinceDTO.getProId());
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    int i = 0;
                    pro.setProId(Long.valueOf(obj[i++].toString()));
                    pro.setProCode(obj[i++].toString());
                    pro.setProName(obj[i++].toString());
                    pro.setStatus(Long.valueOf(obj[i++].toString()));
                }
            } else {
                pro = null;
            }
            return pro;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<DistrictDTO> getListDistrict(DistrictDTO districtDTO) throws Exception {
        try {
            List<DistrictDTO> lstResult = new ArrayList<>();
            String sql = " SELECT    " +
                    " d.district_id,   d.province_id,   d.DISTRICT_NAME    " +
                    " FROM   district d, province p  " +
                    " WHERE d.PROVINCE_ID  = p.pro_id  " +
                    " AND p.pro_id  = :proId " +
                    " order by d.DISTRICT_NAME  ";
            Query query = this.cms.createNativeQuery(sql);
            query.setParameter("proId", districtDTO.getProId());
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null)
                for (Object[] obj : lst) {
                    int i = 0;
                    DistrictDTO district = new DistrictDTO();
                    district.setDistId(Long.valueOf(obj[i++].toString()));
                    district.setProId(Long.valueOf(obj[i++].toString()));
                    district.setDistrictName(obj[i++].toString());
                    lstResult.add(district);
                }
            return lstResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<DistrictDTO> searchDistrict(DataParams dataParams, DistrictDTO districtDTO) throws Exception {
        try {
            List<DistrictDTO> lstResult = new ArrayList<>();
            String sql = " SELECT   " +
                    " p.pro_id, d.DISTRICT_ID, d.DISTRICT_NAME, p.pro_code, p.pro_name, d.DISTRICT_CODE " +
                    " FROM  province p, district d " +
                    " WHERE " +
                    " p.pro_id = d.PROVINCE_ID " +
                    " AND d.`status` = 1 " +
                    " AND p.`status` = 1 ";
            if (!StringUtils.isStringNullOrEmpty(districtDTO.getProId()))
                sql = sql + " AND d.PROVINCE_ID = :provinceId";
            if (!StringUtils.isStringNullOrEmpty(districtDTO.getDistName()))
                sql = sql + " AND LOWER(DISTRICT_NAME) LIKE LOWER(:distName) ";
            sql = sql + " LIMIT :start_row, :page_limit ";
            Query query = this.cms.createNativeQuery(sql);
            if (!StringUtils.isStringNullOrEmpty(dataParams))
                query.setParameter("start_row", dataParams.getStartRow());
            query.setParameter("page_limit", dataParams.getPageLimit());
            if (!StringUtils.isStringNullOrEmpty(districtDTO.getDistName()))
                query.setParameter("distName", "%" + districtDTO.getDistName() + "%");
            if (!StringUtils.isStringNullOrEmpty(districtDTO.getProId()))
                query.setParameter("provinceId", districtDTO.getProId());
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null)
                for (Object[] obj : lst) {
                    int i = 0;
                    DistrictDTO dis = new DistrictDTO();
                    dis.setProId(Long.valueOf(obj[i++].toString()));
                    dis.setDistId(Long.valueOf(obj[i++].toString()));
                    dis.setDistName(obj[i++].toString());
                    dis.setProCode(obj[i++].toString());
                    dis.setProName(obj[i++].toString());
                    dis.setDistCode(obj[i++].toString());
                    lstResult.add(dis);
                }
            return lstResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public int totalRecordSearchDistrict(DataParams dataParams, DistrictDTO districtDTO) throws Exception {
        try {
            String sql = " SELECT   " +
                    "      count(*)" +
                    " FROM  province p, district d " +
                    " WHERE " +
                    " p.pro_id = d.PROVINCE_ID " +
                    " AND d.`status` = 1 " +
                    " AND p.`status` = 1 ";
            if (!StringUtils.isStringNullOrEmpty(districtDTO.getProId()))
                sql = sql + " AND d.PROVINCE_ID = :provinceId";
            if (!StringUtils.isStringNullOrEmpty(districtDTO.getDistName()))
                sql = sql + " AND LOWER(DISTRICT_NAME) LIKE LOWER(:distName) ";
            Query query = this.cms.createNativeQuery(sql);
            if (!StringUtils.isStringNullOrEmpty(districtDTO.getDistName()))
                query.setParameter("distName", "%" + districtDTO.getDistName() + "%");
            if (!StringUtils.isStringNullOrEmpty(districtDTO.getProId()))
                query.setParameter("provinceId", districtDTO.getProId());
            return ((Number) query.getSingleResult()).intValue();
        } catch (
                Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public District addDistrict(DistrictDTO districtDTO, String userName) throws Exception {
        District district = new District();
        Long districtId = DataUtils.getSequence(this.cms, "district_seq");
        district.setDistrictId(districtId);
        district.setProvinceId(districtDTO.getProId());
        district.setDistrictName(districtDTO.getDistName());
        district.setDistrictCode(districtDTO.getDistCode());
        district.setCreateDatetime(LocalDateTime.now());
        district.setCreateBy(userName);
        district.setUpdateDatetime(LocalDateTime.now());
        district.setUpdateBy(userName);
        district.setStatus(1L);
        districtRepo.save(district);
        return district;
    }

    @Override
    public List<DistrictDTO> checkDistrictCodeDuplicate(String districtCode) throws Exception {
        try {
            List<DistrictDTO> lstResult = new ArrayList<>();
            String sql = " SELECT   " +
                    " PROVINCE_ID, DISTRICT_CODE , DISTRICT_NAME " +
                    " FROM  district  " +
                    " WHERE `status` = 1  " +
                    " AND DISTRICT_CODE = :districtCode ";
            Query query = this.cms.createNativeQuery(sql);
            if (!StringUtils.isStringNullOrEmpty(districtCode))
                query.setParameter("districtCode", districtCode);
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    int i = 0;
                    DistrictDTO dis = new DistrictDTO();
                    dis.setProId(Long.valueOf(obj[i++].toString()));
                    dis.setDistCode(obj[i++].toString());
                    dis.setDistName(obj[i++].toString());
                    lstResult.add(dis);
                }
            } else {
                lstResult = null;
            }
            return lstResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<DistrictDTO> checkDistrictNameDuplicate(String districtName) throws Exception {
        try {
            List<DistrictDTO> lstResult = new ArrayList<>();
            String sql = " SELECT   " +
                    " PROVINCE_ID, DISTRICT_CODE , DISTRICT_NAME " +
                    " FROM  district  " +
                    " WHERE `status` = 1  " +
                    " AND DISTRICT_NAME = :districtName ";
            Query query = this.cms.createNativeQuery(sql);
            if (!StringUtils.isStringNullOrEmpty(districtName))
                query.setParameter("districtName", districtName);
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    int i = 0;
                    DistrictDTO dis = new DistrictDTO();
                    dis.setProId(Long.valueOf(obj[i++].toString()));
                    dis.setDistCode(obj[i++].toString());
                    dis.setDistName(obj[i++].toString());
                    lstResult.add(dis);
                }
            } else {
                lstResult = null;
            }
            return lstResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public DistrictDTO checkDistrictStatus(DistrictDTO districtDTO) throws Exception {
        try {
            DistrictDTO dis = new DistrictDTO();
            String sql = " SELECT   " +
                    " PROVINCE_ID, DISTRICT_CODE, DISTRICT_NAME , status " +
                    " FROM  district  " +
                    " WHERE DISTRICT_ID = :districtId ";
            Query query = this.cms.createNativeQuery(sql);
            query.setParameter("districtId", districtDTO.getDistId());
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    int i = 0;
                    dis.setProId(Long.valueOf(obj[i++].toString()));
                    dis.setDistCode(obj[i++].toString());
                    dis.setDistName(obj[i++].toString());
                    dis.setStatus(Long.valueOf(obj[i++].toString()));
                }
            } else {
                dis = null;
            }
            return dis;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateDistrict(DistrictDTO districtDTO, String userName) throws Exception {
        try {
            String sql = " UPDATE district  " +
                    " SET PROVINCE_ID = :provinceId, " +
                    " DISTRICT_CODE = :districtCode, " +
                    " DISTRICT_NAME = :districtName, " +
                    " update_by = :updateBy, " +
                    " update_datetime = :updateDatetime  " +
                    " WHERE DISTRICT_ID = :districtId ";
            Query query = this.cms.createNativeQuery(sql);
            query.setParameter("districtId", districtDTO.getDistId());
            query.setParameter("provinceId", districtDTO.getProId());
            query.setParameter("districtCode", districtDTO.getDistCode());
            query.setParameter("districtName", districtDTO.getDistName());
            query.setParameter("updateBy", userName);
            query.setParameter("updateDatetime", LocalDateTime.now());
            return query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public DistrictDTO checkDistrictId(DistrictDTO districtDTO) throws Exception {
        try {
            DistrictDTO dis = new DistrictDTO();
            String sql = " SELECT   " +
                    " PROVINCE_ID, DISTRICT_CODE, DISTRICT_NAME , status " +
                    " FROM  district  " +
                    " WHERE DISTRICT_ID = :districtId ";
            Query query = this.cms.createNativeQuery(sql);
            query.setParameter("districtId", districtDTO.getDistId());
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    int i = 0;
                    dis.setProId(Long.valueOf(obj[i++].toString()));
                    dis.setDistCode(obj[i++].toString());
                    dis.setDistName(obj[i++].toString());
                    dis.setStatus(Long.valueOf(obj[i++].toString()));
                }
            } else {
                dis = null;
            }
            return dis;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteDistrict(DistrictDTO districtDTO, String userName) throws Exception {
        try {
            String sql = " UPDATE district  " +
                    " SET status = 0, " +
                    " update_by = :updateBy, " +
                    " update_datetime = :updateDatetime  " +
                    " WHERE DISTRICT_ID = :districtId ";
            Query query = this.cms.createNativeQuery(sql);
            query.setParameter("districtId", districtDTO.getDistId());
            query.setParameter("updateBy", userName);
            query.setParameter("updateDatetime", LocalDateTime.now());
            return query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<CommuneDTO> getListCommune(CommuneDTO communeDTO) throws Exception {
        try {
            List<CommuneDTO> lstResult = new ArrayList<>();
            String sql = " SELECT   " +
                    "      p.pro_id, d.DISTRICT_ID, c.CODE, c.NAME, c.ID, c.create_datetime, c.create_by, c.update_datetime, c.update_by" +
                    " FROM  " +
                    "      province p, district d, commune c" +
                    " WHERE " +
                    "      p.pro_id = c.PROVINCE_ID" +
                    " AND " +
                    "      d.DISTRICT_ID = c.DISTRICT_ID" +
                    " AND " +
                    "      c.PROVINCE_ID = :provinceId" +
                    " AND " +
                    "      c.DISTRICT_ID = :districtId";
            if (!StringUtils.isStringNullOrEmpty(communeDTO.getCommuneName()))
                sql = sql + " AND c.NAME = :communeName";
            sql = sql + " AND " +
                    "      c.status = 1 " +
                    " ORDER BY " +
                    "      c.NAME " +
                    " LIMIT 500 ";
            Query query = this.cms.createNativeQuery(sql);
            if (!StringUtils.isStringNullOrEmpty(communeDTO.getProId()))
                query.setParameter("provinceId", communeDTO.getProId());
            if (!StringUtils.isStringNullOrEmpty(communeDTO.getDistId()))
                query.setParameter("districtId", communeDTO.getDistId());
            if (!StringUtils.isStringNullOrEmpty(communeDTO.getCommuneName()))
                query.setParameter("communeName", communeDTO.getCommuneName());
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null)
                for (Object[] obj : lst) {
                    int i = 0;
                    CommuneDTO commune = new CommuneDTO();
                    commune.setProId(Long.valueOf(obj[i++].toString()));
                    commune.setDistId(Long.valueOf(obj[i++].toString()));
                    commune.setCommuneCode(obj[i++].toString());
                    commune.setCommuneName(obj[i++].toString());
                    commune.setCommuneId(Long.valueOf(obj[i++].toString()));
                    commune.setCreateDatetime(LocalDateTime.now());
                    commune.setCreateBy(obj[i++].toString());
                    commune.setUpdateDatetime(LocalDateTime.now());
                    commune.setUpdateBy(obj[i++].toString());
                    lstResult.add(commune);
                }
            return lstResult;
        } catch (
                Exception e) {
            e.printStackTrace();
            throw e;
        }

    }

    @Override
    public List<CommuneDTO> searchCommune(DataParams dataParams, CommuneDTO communeDTO) throws Exception {
        try {
            List<CommuneDTO> lstResult = new ArrayList<>();
            String sql = " SELECT   " +
                    "      p.pro_id, d.DISTRICT_ID, e.CODE, e.NAME , p.pro_code , d.district_code, e.id , p.pro_name , d.district_name " +
                    " FROM  " +
                    "      province p, district d, commune e" +
                    " WHERE " +
                    "      p.pro_id = e.PROVINCE_ID" +
                    " AND" +
                    "      d.DISTRICT_ID = e.DISTRICT_ID" +
                    " AND " +
                    "      e.status = 1 " +
                    " AND " +
                    "      p.status = 1 " +
                    " AND " +
                    "      d.status = 1 ";
            if (!StringUtils.isStringNullOrEmpty(communeDTO.getProId()))
                sql = sql + " AND e.PROVINCE_ID = :provinceId";
            if (!StringUtils.isStringNullOrEmpty(communeDTO.getDistId()))
                sql = sql + " AND e.DISTRICT_ID = :districtId";
            if (!StringUtils.isStringNullOrEmpty(communeDTO.getCommuneName()))
                sql = sql + " AND LOWER(NAME) LIKE LOWER(:communeName) ";
            sql = sql + " LIMIT :start_row, :page_limit ";
            Query query = this.cms.createNativeQuery(sql);
            if (!StringUtils.isStringNullOrEmpty(dataParams))
                query.setParameter("start_row", dataParams.getStartRow());
            query.setParameter("page_limit", dataParams.getPageLimit());
            if (!StringUtils.isStringNullOrEmpty(communeDTO.getProId()))
                query.setParameter("provinceId", communeDTO.getProId());
            if (!StringUtils.isStringNullOrEmpty(communeDTO.getDistId()))
                query.setParameter("districtId", communeDTO.getDistId());
            if (!StringUtils.isStringNullOrEmpty(communeDTO.getCommuneName()))
                query.setParameter("communeName", "%" + communeDTO.getCommuneName() + "%");
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null)
                for (Object[] obj : lst) {
                    int i = 0;
                    CommuneDTO com = new CommuneDTO();
                    com.setProId(Long.valueOf(obj[i++].toString()));
                    com.setDistId(Long.valueOf(obj[i++].toString()));
                    com.setCommuneCode(obj[i++].toString());
                    com.setCommuneName(obj[i++].toString());
                    com.setProCode(obj[i++].toString());
                    com.setDistrictCode(obj[i++].toString());
                    com.setCommuneId(Long.valueOf(obj[i++].toString()));
                    com.setProName(obj[i++].toString());
                    com.setDistrictName(obj[i++].toString());
                    lstResult.add(com);
                }
            return lstResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public int totalRecordSearchCommune(DataParams dataParams, CommuneDTO communeDTO) throws Exception {
        try {
            String sql = " SELECT   " +
                    "      count(*)" +
                    " FROM  " +
                    "      province p, district d, commune e" +
                    " WHERE " +
                    "      p.pro_id = e.PROVINCE_ID" +
                    " AND" +
                    "      d.DISTRICT_ID = e.DISTRICT_ID" +
                    " AND " +
                    "      e.status = 1 " +
                    " AND " +
                    "      p.status = 1 " +
                    " AND " +
                    "      d.status = 1 ";
            if (!StringUtils.isStringNullOrEmpty(communeDTO.getProId()))
                sql = sql + " AND e.PROVINCE_ID = :provinceId";
            if (!StringUtils.isStringNullOrEmpty(communeDTO.getDistId()))
                sql = sql + " AND e.DISTRICT_ID = :districtId";
            if (!StringUtils.isStringNullOrEmpty(communeDTO.getCommuneName()))
                sql = sql + " AND LOWER(NAME) LIKE LOWER(:communeName) ";
            Query query = this.cms.createNativeQuery(sql);
            if (!StringUtils.isStringNullOrEmpty(communeDTO.getCommuneName()))
                query.setParameter("communeName", "%" + communeDTO.getCommuneName() + "%");
            if (!StringUtils.isStringNullOrEmpty(communeDTO.getProId()))
                query.setParameter("provinceId", communeDTO.getProId());
            if (!StringUtils.isStringNullOrEmpty(communeDTO.getDistId()))
                query.setParameter("districtId", communeDTO.getDistId());
            return ((Number) query.getSingleResult()).intValue();
        } catch (
                Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Commune addCommune(CommuneDTO communeDTO, String userName) throws Exception {
        Commune commune = new Commune();
        Long communeId = DataUtils.getSequence(this.cms, "commune_seq");
        commune.setCommuneId(communeId);
        commune.setProvinceId(communeDTO.getProId());
        commune.setDistrictId(communeDTO.getDistId());
        commune.setCommuneName(communeDTO.getCommuneName());
        commune.setCommuneCode(communeDTO.getCommuneCode());
        commune.setCreateDatetime(LocalDateTime.now());
        commune.setCreateBy(userName);
        commune.setUpdateDatetime(LocalDateTime.now());
        commune.setUpdateBy(userName);
        commune.setStatus(1L);
        communeRepo.save(commune);
        return commune;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateCommune(CommuneDTO communeDTO, String userName) throws Exception {
        try {
            String sql = " UPDATE commune  " +
                    " SET PROVINCE_ID = :provinceId, " +
                    " DISTRICT_ID = :districtId, " +
                    " CODE = :communeCode, " +
                    " `NAME` = :communeName, " +
                    " update_by = :updateBy, " +
                    " update_datetime = :updateDatetime  " +
                    " WHERE ID = :communeId ";
            Query query = this.cms.createNativeQuery(sql);
            query.setParameter("districtId", communeDTO.getDistId());
            query.setParameter("provinceId", communeDTO.getProId());
            query.setParameter("communeCode", communeDTO.getCommuneCode());
            query.setParameter("communeName", communeDTO.getCommuneName());
            query.setParameter("updateBy", userName);
            query.setParameter("updateDatetime", LocalDateTime.now());
            query.setParameter("communeId", communeDTO.getCommuneId());
            return query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public CommuneDTO checkCommuneStatus(CommuneDTO communeDTO) throws Exception {
        try {
            CommuneDTO com = new CommuneDTO();
            String sql = " SELECT   " +
                    " PROVINCE_ID, DISTRICT_ID, CODE, `NAME` , status " +
                    " FROM  commune  " +
                    " WHERE ID = :communeId ";
            Query query = this.cms.createNativeQuery(sql);
            query.setParameter("communeId", communeDTO.getDistId());
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    int i = 0;
                    com.setProId(Long.valueOf(obj[i++].toString()));
                    com.setDistId(Long.valueOf(obj[i++].toString()));
                    com.setCommuneCode(obj[i++].toString());
                    com.setCommuneName(obj[i++].toString());
                    com.setStatus(Long.valueOf(obj[i++].toString()));
                }
            } else {
                com = null;
            }
            return com;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<CommuneDTO> checkCommuneCodeDuplicate(String communeCode) throws Exception {
        try {
            List<CommuneDTO> lstResult = new ArrayList<>();
            String sql = " SELECT   " +
                    " PROVINCE_ID, DISTRICT_ID, CODE , `NAME` " +
                    " FROM  commune  " +
                    " WHERE `status` = 1  " +
                    " AND CODE = :communeCode ";
            Query query = this.cms.createNativeQuery(sql);
            if (!StringUtils.isStringNullOrEmpty(communeCode))
                query.setParameter("communeCode", communeCode);
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    int i = 0;
                    CommuneDTO com = new CommuneDTO();
                    com.setProId(Long.valueOf(obj[i++].toString()));
                    com.setDistId(Long.valueOf(obj[i++].toString()));
                    com.setCommuneCode(obj[i++].toString());
                    com.setCommuneName(obj[i++].toString());
                    lstResult.add(com);
                }
            } else {
                lstResult = null;
            }
            return lstResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<CommuneDTO> checkCommuneNameDuplicate(String communeName) throws Exception {
        try {
            List<CommuneDTO> lstResult = new ArrayList<>();
            String sql = " SELECT   " +
                    " PROVINCE_ID, DISTRICT_ID, CODE , `NAME` " +
                    " FROM  commune  " +
                    " WHERE `status` = 1  " +
                    " AND `NAME` = :communeName ";
            Query query = this.cms.createNativeQuery(sql);
            if (!StringUtils.isStringNullOrEmpty(communeName))
                query.setParameter("communeName", communeName);
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    int i = 0;
                    CommuneDTO com = new CommuneDTO();
                    com.setProId(Long.valueOf(obj[i++].toString()));
                    com.setDistId(Long.valueOf(obj[i++].toString()));
                    com.setCommuneCode(obj[i++].toString());
                    com.setCommuneName(obj[i++].toString());
                    lstResult.add(com);
                }
            } else {
                lstResult = null;
            }
            return lstResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public CommuneDTO checkCommuneId(CommuneDTO communeDTO) throws Exception {
        try {
            CommuneDTO com = new CommuneDTO();
            String sql = " SELECT   " +
                    " PROVINCE_ID, DISTRICT_ID, CODE, `NAME` , status " +
                    " FROM  commune  " +
                    " WHERE ID = :communeId ";
            Query query = this.cms.createNativeQuery(sql);
            query.setParameter("communeId", communeDTO.getCommuneId());
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    int i = 0;
                    com.setProId(Long.valueOf(obj[i++].toString()));
                    com.setDistId(Long.valueOf(obj[i++].toString()));
                    com.setCommuneCode(obj[i++].toString());
                    com.setCommuneName(obj[i++].toString());
                    com.setStatus(Long.valueOf(obj[i++].toString()));
                }
            } else {
                com = null;
            }
            return com;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteCommune(CommuneDTO communeDTO, String userName) throws Exception {
        try {
            String sql = " UPDATE commune  " +
                    " SET status = 0, " +
                    " update_by = :updateBy, " +
                    " update_datetime = :updateDatetime  " +
                    " WHERE ID = :communeId ";
            Query query = this.cms.createNativeQuery(sql);
            query.setParameter("communeId", communeDTO.getCommuneId());
            query.setParameter("updateBy", userName);
            query.setParameter("updateDatetime", LocalDateTime.now());
            return query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Province> getListProvinceByCode(String provinceCode) throws Exception {
        try {
            String sql = " SELECT   *    " +
                    " FROM   province  " +
                    " where pro_code = :provinceCode ";
            Query query = this.cms.createNativeQuery(sql, Province.class);
            query.setParameter("provinceCode", provinceCode);
            List<Province> lst = query.getResultList();
            return lst;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<District> getListDistrictByIdAndProCode(String provinceCode, Long districtId) throws Exception {
        try {
            String sql = " SELECT   d.*    " +
                    " FROM   province_district d, province p  " +
                    " where  p.pro_code = :provinceCode  " +
                    " and d.pro_id = p.pro_id " +
                    " and d.dist_id = :districtId";
            Query query = this.cms.createNativeQuery(sql, District.class);
            query.setParameter("provinceCode", provinceCode);
            query.setParameter("districtId", districtId);
            List<District> lst = query.getResultList();
            return lst;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public District getListDistrictById(Long districtId) throws Exception {
        try {
            Optional<District> result = this.districtRepo.findById(districtId);
            District district = result.orElse(null);
            if (!StringUtils.isStringNullOrEmpty(district))
                return district;
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public int updateConstructionStartDateCND(BTSStationDTO btsStationDTO) throws Exception {
        try {
            String sql = " UPDATE  " +
                    "      bts_rent_place " +
                    " SET " +
                    "      handover_date = :handoverDate, " +
                    "      construction_start_date = :constructionStartDate, " +
                    "      construction_note = :btsRenPlaceNote  " +
                    " WHERE  " +
                    "      id = :btsRenPlaceId  ";
            Query query = this.cms.createNativeQuery(sql);
            query.setParameter("handoverDate", btsStationDTO.getHandoverDate());
            query.setParameter("constructionStartDate", btsStationDTO.getConstructionStartDate());
            query.setParameter("btsRenPlaceNote", btsStationDTO.getNotes());
            query.setParameter("btsRenPlaceId", btsStationDTO.getId());
            return query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}