package com.viettel.base.cms.serviceImpl;

import com.viettel.base.cms.common.Constant;

import com.viettel.base.cms.model.*;
import com.viettel.base.cms.service.SystemsInfoService;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SystemsInfoServiceImpl implements SystemsInfoService {

  @Autowired
  @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
  private EntityManager upoint;

  @Override
  public SystemsInfo findByUserNameAndStatus(String userName, Long status) {
    try {
      String sql = " SELECT * FROM systems_info WHERE status = :status AND user_name = :userName";
      Query query = upoint.createNativeQuery(sql, SystemsInfo.class);
      query.setParameter("status", status);
      query.setParameter("userName", userName);
      List<SystemsInfo> lst = query.getResultList();
      if (lst != null && !lst.isEmpty()) {
        return lst.get(0);
      }
      return null;
    } catch (Exception e) {
      throw e;
    }
  }
  @Override
  public SystemsInfo findByUserNameAndPasswordAndStatus(String userName,String password,Long status) {
    try {
      String sql = " SELECT * FROM systems_info WHERE status = :status AND user_name = :userName and password = :password";
      Query query = upoint.createNativeQuery(sql, SystemsInfo.class);
      query.setParameter("status", status);
      query.setParameter("userName", userName);
      query.setParameter("password", password);
      List<SystemsInfo> lst = query.getResultList();
      if (lst != null && !lst.isEmpty()) {
        return lst.get(0);
      }
      return null;
    } catch (Exception e) {
      throw e;
    }
  }
}
