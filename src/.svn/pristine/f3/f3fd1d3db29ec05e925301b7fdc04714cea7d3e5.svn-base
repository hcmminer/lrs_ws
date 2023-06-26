/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.base.cms.controller;

import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.dto.JwtResponse;
import com.viettel.base.cms.dto.LoginDTO;
import com.viettel.base.cms.dto.StaffDTO;
import com.viettel.base.cms.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalDateTime;

@RestController
@RequestMapping(value = "/api")
@Slf4j
public class AuthCtrl {

  @Autowired
  private UserService userService;
  @Autowired
  private Environment env;

  @Autowired
  @PersistenceContext(unitName = Constant.UNIT_NAME_ENTITIES_CMS)
  private EntityManager cms;

  private static final Logger logger = LogManager.getLogger(AuthCtrl.class);

  @GetMapping(value = "/loadUserByUsername")
  public JwtResponse loadUserByUsername(@RequestParam String userName) {
    JwtResponse jwtResponse = new JwtResponse();
    // trace log
//    Long traceID = DataUtils.getSequence(upoint, "trace_logs_seq");
//    String functionName = "loadUserByUsername";
//    DataUtils.traceLogs.clear();
    try {
//      TraceLogs traceLog = new TraceLogs(null, traceID, "userName:" + userName, LocalDateTime.now(), "SYSTEM", "1", functionName);
//      DataUtils.traceLogs.add(traceLog);

      jwtResponse = userService.loadUserByUsername(userName);
//      PassTranformer.setInputKey(env.getProperty("key.enscrypt.security"));
//      userDTO.setPassword(PassTranformer.encrypt(PassTranformer.decrypt(userDTO.getPassword()) + env.getProperty("key.public.salt")));
    } catch (Exception e) {
      // trace log
//      TraceLogs traceLogEx = new TraceLogs(null, traceID, e.toString(), LocalDateTime.now(), "SYSTEM", "2", functionName);
//      DataUtils.traceLogs.add(traceLogEx);
      // end trace log
    }
    return jwtResponse;
  }
}
