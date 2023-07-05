/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.vfw5.base.dto;

/**
 *
 * @author QuangThieu
 */
public class ExecutionResult {
    private Object data;
    private Object pageInfo;
    private String errorCode;
    private String description;

    public ExecutionResult() {
    }
    

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getPageInfo() {
        return pageInfo;
    }

    public void setPageInfo(Object pageInfo) {
        this.pageInfo = pageInfo;
    }
    
 
}
