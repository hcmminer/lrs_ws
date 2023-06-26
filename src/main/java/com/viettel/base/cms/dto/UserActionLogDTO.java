/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.base.cms.dto;

import com.viettel.vfw5.base.dto.BaseFWDTO;
import java.util.Date;

/**
 *
 * @author ADMIN
 */
public class UserActionLogDTO {
    
    private Long userActionId;
    private Long userId;
    private String userIp;
    private String userBrowser;
    private String moduleName;
    private String actionName;
    private Date logDate;

    public UserActionLogDTO() {
    }

    public Long getUserActionId() {
        return userActionId;
    }

    public void setUserActionId(Long userActionId) {
        this.userActionId = userActionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserIp() {
        return userIp;
    }

    public void setUserIp(String userIp) {
        this.userIp = userIp;
    }

    public String getUserBrowser() {
        return userBrowser;
    }

    public void setUserBrowser(String userBrowser) {
        this.userBrowser = userBrowser;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getActionName() {
        return actionName;
    }

    public void setActionName(String actionName) {
        this.actionName = actionName;
    }

    public Date getLogDate() {
        return logDate;
    }

    public void setLogDate(Date logDate) {
        this.logDate = logDate;
    }


    
}
