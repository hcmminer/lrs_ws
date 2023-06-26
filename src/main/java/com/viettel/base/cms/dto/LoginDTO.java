/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.base.cms.dto;

import com.viettel.base.cms.model.*;
import com.viettel.vfw5.base.dto.BaseFWDTO;
import com.viettel.vfw5.base.utils.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Table(name = "users")
// causes Lombok to generate toString(), equals(), hashCode(), getter() & setter(), and Required arguments constructor in one go.
@Data
// causes Lombok to implement the Builder design pattern for the Pojo class.
// usage can be seen in DefaultBeersLoader.java -> createNewBeer() method.
@Builder
// causes Lombok to generate a constructor with no parameters.
@NoArgsConstructor
// causes Lombok to generate a constructor with 1 parameter for each field in your class.
@AllArgsConstructor
@Component
public class LoginDTO {
  String token;
  String userName;
  String userFullName;
  String roleCode;
  String roleName;
  String roleDescription;
  String provinceCode;
  String provinceName;
  String reasonFail;
  Staff staff;
  List<MenuParent> lstMenu;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public List<MenuParent> getLstMenu() {
        return lstMenu;
    }

    public void setLstMenu(List<MenuParent> lstMenu) {
        this.lstMenu = lstMenu;
    }
}
