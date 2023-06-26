/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.base.cms.service;

import com.viettel.base.cms.model.*;

/**
 *
 * @author ADMIN
 */
public interface MTService {

    void sendOTP(MTHis mt) throws Exception;
    
    void sendOTPFalse(MT mt) throws Exception;
    
    MTHis getOTP (String msisdn,String otp)throws Exception;
    
    MTHis getOTPByIsdn (String msisdn, String actionUser, Long actionType)throws Exception;
    
    
    

}
