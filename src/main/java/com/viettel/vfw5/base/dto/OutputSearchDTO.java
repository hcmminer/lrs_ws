/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.vfw5.base.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.viettel.vfw5.base.dto.PagingItemDTO;
import java.util.List;

/**
 *
 * @author QuangThieu
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class OutputSearchDTO {
    private List<?> lstObject;
    private PagingItemDTO pagingItem;

    public OutputSearchDTO() {
    }

    public List<?> getLstObject() {
        return lstObject;
    }

    public void setLstObject(List<?> lstObject) {
        this.lstObject = lstObject;
    }

    public PagingItemDTO getPagingItem() {
        return pagingItem;
    }

    public void setPagingItem(PagingItemDTO pagingItem) {
        this.pagingItem = pagingItem;
    }

  

   
    
    
    
}
