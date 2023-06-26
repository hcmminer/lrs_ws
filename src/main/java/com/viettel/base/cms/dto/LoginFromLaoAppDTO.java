/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.base.cms.dto;

/**
 *
 * @author ADMIN
 */
public class LoginFromLaoAppDTO {

    private String userName;
    private String password;
    private String uuid;
    private String msisdn;
    private String token;
    private String userIp;
    private String userBrowser;
    private boolean isExists;

    public boolean isIsExists() {
        return isExists;
    }

    public void setIsExists(boolean isExists) {
        this.isExists = isExists;
    }

    
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public LoginFromLaoAppDTO() {
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
    
    

}
