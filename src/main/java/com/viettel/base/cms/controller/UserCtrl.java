package com.viettel.base.cms.controller;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.CommonInputDTO;
import com.viettel.base.cms.dto.LoginDTO;
import com.viettel.base.cms.service.OptionSetValueService;
import com.viettel.base.cms.service.UserService;
import com.viettel.vfw5.base.dto.ExecutionResult;
import com.viettel.vfw5.base.utils.ResourceBundle;
import com.viettel.vfw5.base.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class UserCtrl {


    @Autowired
    private UserService userService;

    @Autowired
    private Environment env;


    @Autowired
    @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
    private EntityManager cms;

    @Autowired
    private OptionSetValueService optionSetValueService;

    private String fileExchangeConfig = "exchange_client.cfg";


    // URL - http://localhost:9091/chatbot/api/loginAuthentication
    @PostMapping(value = "/loginAuthentication")
    public ExecutionResult loginAuthentication(@RequestBody CommonInputDTO commonInputDTO, @RequestHeader("Accept-Language") String locate) {
        ResourceBundle r = new ResourceBundle(locate);
        ExecutionResult res = new ExecutionResult();
        res.setErrorCode(Constant.EXECUTION_ERROR.SUCCESS);
        try {
            if (StringUtils.isNullOrEmpty(commonInputDTO.getUserName())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("userName.is.null"));
                return res;
            }
            commonInputDTO.setUserName(commonInputDTO.getUserName().trim());

            if (StringUtils.isNullOrEmpty(commonInputDTO.getPassword())) {
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(r.getResourceMessage("password.is.null"));
                return res;
            }
            commonInputDTO.setPassword(commonInputDTO.getPassword().trim());

            LoginDTO loginDTO = userService.loginAuthentication(commonInputDTO, locate);
            if (!StringUtils.isStringNullOrEmpty(loginDTO.getReasonFail())) {
                String message = "";
                switch (loginDTO.getReasonFail()) {
                    case "u/p wrong" :
                        message = r.getResourceMessage("user.or.pass.invalid");
                        break;
                    case "STATUS_0" :
                        message = r.getResourceMessage("user.disabled");
                        break;
                    case "permission wrong" :
                        message = r.getResourceMessage("userName.not.exits");
                        break;
                    default:
                        message = r.getResourceMessage("system.error");
                }
                res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
                res.setDescription(message);
                return res;
            }
            res.setData(loginDTO);

        } catch (Exception e) {
            e.printStackTrace();
            res.setErrorCode(Constant.EXECUTION_ERROR.ERROR);
            res.setDescription(r.getResourceMessage("sign.in.false"));
        }
        return res;
    }

}
