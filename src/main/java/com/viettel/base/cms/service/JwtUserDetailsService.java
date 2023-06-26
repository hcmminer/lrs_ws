/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.base.cms.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
/**
 *
 * @author ADMIN
 */
public interface JwtUserDetailsService extends UserDetailsService{
   public UserDetails loadUserByUsernameAndPassword(String username,String password) throws Exception;
}
