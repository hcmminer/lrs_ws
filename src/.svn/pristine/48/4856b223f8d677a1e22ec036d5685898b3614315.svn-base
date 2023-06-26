package com.viettel.base.cms.service;

import com.viettel.base.cms.dto.CommonInputDTO;
import com.viettel.base.cms.dto.JwtResponse;
import com.viettel.base.cms.dto.LoginDTO;
import com.viettel.base.cms.model.Staff;

public interface UserService {
    LoginDTO loginAuthentication(CommonInputDTO commonInputDTO, String locate);
    String getUserRole(CommonInputDTO commonInputDTO) throws Exception;
    JwtResponse loadUserByUsername(String userName) throws Exception;
    String getUserProvinceCode(String userName) throws Exception;
    Staff getStaffByUserName(String userName) throws Exception;
}
