/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.base.cms.service;

import com.viettel.base.cms.model.ApplicationLog;
/**
 *
 * @author ADMIN
 */
public interface ApplicationLogService {
    
    void applicationLogging(ApplicationLog model) throws Exception;
    
}
