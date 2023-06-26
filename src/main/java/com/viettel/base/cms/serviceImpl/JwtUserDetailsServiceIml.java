/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.viettel.base.cms.serviceImpl;


import com.viettel.base.cms.common.Constant;
import com.viettel.base.cms.model.SystemsInfo;
import com.viettel.base.cms.service.JwtUserDetailsService;
import com.viettel.base.cms.service.SystemsInfoService;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsServiceIml implements JwtUserDetailsService {

    @Autowired
    private SystemsInfoService repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SystemsInfo system = repo.findByUserNameAndStatus(username,Constant.ACTIVE);
        if (system != null) {
            return new User(system.getUserName(), system.getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
    @Override
    public UserDetails loadUserByUsernameAndPassword(String username,String password) throws Exception {
        SystemsInfo system = repo.findByUserNameAndPasswordAndStatus(username,password,Constant.ACTIVE);
        if (system != null) {
            return new User(system.getUserName(), system.getPassword(), new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }
}
