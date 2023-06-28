package com.viettel.base.cms.serviceImpl;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.DistrictDTO;
import com.viettel.base.cms.dto.ProvinceDTO;
import com.viettel.base.cms.model.ConstructionItem;
import com.viettel.base.cms.model.District;
import com.viettel.base.cms.model.Province;
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

    public List<ProvinceDTO> getListProvince() throws Exception {
        try {
            List<ProvinceDTO> lstResult = new ArrayList<>();
            String sql = " SELECT   " +
                    " pro_id, pro_code, pro_name, status, create_datetime, create_by, update_datetime, update_by  " +
                    " FROM   province  " +
                    "order by pro_name ";
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

    public List<ProvinceDTO> searchProvince(String provinceName) throws Exception {
        try {
            List<ProvinceDTO> lstResult = new ArrayList<>();
            String sql = " SELECT   " +
                    " pro_id, pro_name , pro_code  " +
                    " FROM  province  " +
                    " WHERE `status` = 1 ";
            if (!StringUtils.isStringNullOrEmpty(provinceName))
                sql = sql + " AND LOWER(pro_name) LIKE LOWER(:provinceName) ";
            Query query = this.cms.createNativeQuery(sql);
            if (!StringUtils.isStringNullOrEmpty(provinceName))
                query.setParameter("provinceName", "%" + provinceName + "%");
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

    public Province addProvince(ProvinceDTO provinceDTO, String userName) throws Exception {
        Province province = new Province();
        Long provinceId = DataUtils.getSequence(this.cms, "province_seq");
        province.setProId(provinceId);
        province.setProCode(provinceDTO.getProCode());
        province.setProName(provinceDTO.getProName());
        province.setStatus(Long.valueOf(1L));
        province.setCreateDatetime(LocalDateTime.now());
        province.setCreateBy(userName);
        province.setUpdateDatetime(LocalDateTime.now());
        province.setUpdateBy(userName);
        this.provinceRepo.save(province);
        return province;
    }

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

    @Transactional(rollbackFor = {Exception.class})
    public int updateProvince(ProvinceDTO provinceDTO, String userName) throws Exception {
        try {
            List<ProvinceDTO> lstResult = new ArrayList<>();
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

    public List<DistrictDTO> getListDistrict(ProvinceDTO provinceDTO) throws Exception {
        try {
            List<DistrictDTO> lstResult = new ArrayList<>();
            String sql = " SELECT   " +
                    " pd.dist_id,   pd.pro_id,   pd.dist_name,   " +
                    " pd.center_point,   " +
                    " pd.def_zoom   " +
                    " FROM   province_district pd, province p " +
                    " WHERE pd.pro_id = p.pro_id " +
                    " AND LOWER (p.pro_code) = LOWER (:proCode) " +
                    " order by pd.dist_name";
            Query query = this.cms.createNativeQuery(sql);
            query.setParameter("proCode", provinceDTO.getProCode());
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null)
                for (Object[] obj : lst) {
                    int i = 0;
                    DistrictDTO districtDTO = new DistrictDTO();
                    districtDTO.setDistId(Long.valueOf(obj[i++].toString()));
                    districtDTO.setProId(Long.valueOf(obj[i++].toString()));
                    districtDTO.setDistName(obj[i++].toString());
                    districtDTO.setCenterPoint(obj[i++].toString());
                    districtDTO.setDefZoom(Long.valueOf(obj[i++].toString()));
                    lstResult.add(districtDTO);
                }
            return lstResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

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
}