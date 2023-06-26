package com.viettel.base.cms.serviceImpl;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.DistrictDTO;
import com.viettel.base.cms.dto.ProvinceDTO;
import com.viettel.base.cms.model.ConstructionItem;
import com.viettel.base.cms.model.District;
import com.viettel.base.cms.model.Province;
import com.viettel.base.cms.repo.DistrictRepo;
import com.viettel.base.cms.service.LocationService;
import com.viettel.vfw5.base.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class LocationServiceImpl implements LocationService {


    @Autowired
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private EntityManager cms;

    @Autowired
    DistrictRepo districtRepo;

    @Override
    public List<ProvinceDTO> getListProvince() throws Exception {
        try {
            List<ProvinceDTO> lstResult = new ArrayList<>();
            String sql = " SELECT  "
                    + "	pro_id,  "
                    + "	pro_code,  "
                    + "	pro_name,  "
                    + "	area,  "
                    + "	area_m,  "
                    + "	pro_group,  "
                    + "	center_point,  "
                    + "	def_zoom   "
                    + " FROM  "
                    + "	province  order by pro_name ";
            Query query = cms.createNativeQuery(sql);
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
                for (Object[] obj : lst) {
                    int i = 0;
                    ProvinceDTO pro = new ProvinceDTO();
                    pro.setProId(Long.valueOf(obj[i++].toString()));
                    pro.setProCode(obj[i++].toString());
                    pro.setProName(obj[i++].toString());
                    pro.setArea(obj[i++].toString());
                    if (!StringUtils.isStringNullOrEmpty(obj[i]))
                        pro.setAreaM(obj[i++].toString());
                    else
                        i++;
                    if (!StringUtils.isStringNullOrEmpty(obj[i]))
                        pro.setProGroup(obj[i++].toString());
                    else
                        i++;
                    if (!StringUtils.isStringNullOrEmpty(obj[i]))
                        pro.setCenterPoint(obj[i++].toString());
                    else
                        i++;
                    if (!StringUtils.isStringNullOrEmpty(obj[i]))
                        pro.setDefZoom(Long.valueOf(obj[i].toString()));
                    lstResult.add(pro);
                }
            }
            return lstResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<DistrictDTO> getListDistrict(ProvinceDTO provinceDTO) throws Exception {
        try {
            List<DistrictDTO> lstResult = new ArrayList<>();
            String sql = " SELECT  "
                    + "	pd.dist_id,  "
                    + "	pd.pro_id,  "
                    + "	pd.dist_name,  "
                    + "	pd.center_point,  "
                    + "	pd.def_zoom  "
                    + " FROM  "
                    + "	province_district pd, province p "
                    + "WHERE pd.pro_id = p.pro_id AND LOWER (p.pro_code) = LOWER (:proCode)   "
                    + " order by pd.dist_name";
            Query query = cms.createNativeQuery(sql);
            query.setParameter("proCode", provinceDTO.getProCode());
            List<Object[]> lst = query.getResultList();
            if (!lst.isEmpty() && lst != null) {
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
            }
            return lstResult;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public List<Province> getListProvinceByCode(String provinceCode) throws Exception {
        try {
            String sql = " SELECT  "
                    + "	*   "
                    + " FROM  "
                    + "	province  where pro_code = :provinceCode ";
            Query query = cms.createNativeQuery(sql, Province.class);
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
            String sql = " SELECT  "
                    + "	d.*   "
                    + " FROM  "
                    + "	province_district d, province p  where  p.pro_code = :provinceCode "
                    + " and d.pro_id = p.pro_id and d.dist_id = :districtId";
            Query query = cms.createNativeQuery(sql, District.class);
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
            Optional<District> result = districtRepo.findById(districtId);
            District district = result.orElse(null);
            if (!StringUtils.isStringNullOrEmpty(district)) {
                return district;
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
}
