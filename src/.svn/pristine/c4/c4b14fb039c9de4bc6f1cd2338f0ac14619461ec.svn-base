package com.viettel.base.cms.controller;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.service.OptionSetValueService;
import com.viettel.vfw5.base.dto.ExecutionResult;
import com.viettel.vfw5.base.utils.DataUtils;
import com.viettel.vfw5.base.utils.ResourceBundle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class OptionSetCtrl {



    @Autowired
    private Environment env;


    @Autowired
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private EntityManager upoint;

    @Autowired
    private OptionSetValueService optionSetValueService;

    private String fileExchangeConfig = "exchange_client.cfg";

}
