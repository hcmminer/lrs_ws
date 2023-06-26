package com.viettel.base.cms.serviceImpl;

import com.viettel.base.cms.common.Constant;

import com.viettel.base.cms.model.*;
import com.viettel.base.cms.repo.*;
import com.viettel.base.cms.service.*;
import com.viettel.vfw5.base.utils.DataUtils;

import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MTServiceImpl implements MTService {

    @Autowired
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private EntityManager upoint;

    @Autowired
    MtRepo repo;
    
    @Autowired
    MtHisRepo mtHisRepo;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendOTP(MTHis mt) throws Exception {
        Long idMt = DataUtils.getSequence(upoint, "MT_HIS_SEQ");
        // insert table user
        mt.setMtHisId(idMt);
        mt.setStatus(1L);
        mt.setAppId("UPOINT APP");
        mt.setSentTime(LocalDateTime.now());
        mt.setRetrySentCount(0L);
        mtHisRepo.save(mt);
    }
    
     @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendOTPFalse(MT mt) throws Exception {
        Long idMt = DataUtils.getSequence(upoint, "MT_SEQ");
        // insert table user
        mt.setMtId(idMt);
        mt.setStatus(1L);
        mt.setAppId("UPOINT APP");
        mt.setRetryNum(0L);
       repo.save(mt);
    }

    @Override
    public MTHis getOTP(String msisdn,String otp) throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            MTHis OTP = null;
            sql.append(" SELECT * FROM mt_his where sent_time >= STR_TO_DATE(DATE_FORMAT(ADDDATE(current_date(),INTERVAL -1 DAY),'%d-%c-%Y'),'%d-%c-%Y') AND sent_time < STR_TO_DATE(DATE_FORMAT(ADDDATE(current_date(),INTERVAL 1 DAY),'%d-%c-%Y'),'%d-%c-%Y') and msisdn=:msisdn and otp=:otp and  type =1 ORDER BY sent_time desc LIMIT 1");
            Query query = upoint.createNativeQuery(sql.toString(),MTHis.class);
            query.setParameter("msisdn", msisdn);
            query.setParameter("otp", otp);
            List<MTHis> lst = query.getResultList();
            if (lst != null && !lst.isEmpty()) {
                return lst.get(0);
            }
            return OTP;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
    
     @Override
    public  MTHis getOTPByIsdn  (String msisdn, String actionIsdn, Long actionType)throws Exception {
        try {
            StringBuilder sql = new StringBuilder();
            MTHis OTP = null;
            sql.append(" SELECT * FROM mt_his where sent_time >= STR_TO_DATE(DATE_FORMAT(ADDDATE(current_date(),INTERVAL -1 DAY),'%d-%c-%Y'),'%d-%c-%Y') AND sent_time < STR_TO_DATE(DATE_FORMAT(ADDDATE(current_date(),INTERVAL 1 DAY),'%d-%c-%Y'),'%d-%c-%Y') "
                    + " and msisdn=:msisdn and  type =1 and action_isdn=:actionIsdn and action_type=:actionType "
                    + " ORDER BY sent_time desc LIMIT 1");
            Query query = upoint.createNativeQuery(sql.toString(),MTHis.class);
            query.setParameter("msisdn", msisdn);
            query.setParameter("actionIsdn", actionIsdn);
            query.setParameter("actionType", actionType);
            List<MTHis> lst = query.getResultList();
            if (lst != null && !lst.isEmpty()) {
                return lst.get(0);
            }
            return OTP;

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
    
}
