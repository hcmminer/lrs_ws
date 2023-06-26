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
public class RequestDTO {
    private String isdn;
    private int pointAmount ;
    private int pointId;
    private String transTypeId;
    private String userSystem;
    private String description;
    
    private String username;
    private String password;
    private String to;
    private String content;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
    
    

    public String getIsdn() {
        return isdn;
    }

    public void setIsdn(String isdn) {
        this.isdn = isdn;
    }

    public int getPointAmount() {
        return pointAmount;
    }

    public void setPointAmount(int pointAmount) {
        this.pointAmount = pointAmount;
    }

    public int getPointId() {
        return pointId;
    }

    public void setPointId(int pointId) {
        this.pointId = pointId;
    }

    public String getTransTypeId() {
        return transTypeId;
    }

    public void setTransTypeId(String transTypeId) {
        this.transTypeId = transTypeId;
    }

    public String getUserSystem() {
        return userSystem;
    }

    public void setUserSystem(String userSystem) {
        this.userSystem = userSystem;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

        public RequestDTO() {
    }
    
        
}
