package com.viettel.base.cms.serviceImpl;

import com.viettel.base.cms.common.Constant;

import com.viettel.base.cms.model.*;
import com.viettel.base.cms.repo.*;
import com.viettel.base.cms.service.*;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ApplicationLogServiceImpl implements ApplicationLogService {

    @Autowired
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private EntityManager upoint;

    @Autowired
    ApplicationLogRepo repo;

    @Override
    public void applicationLogging(ApplicationLog model) throws Exception {
        try {
           repo.save(model);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

}
